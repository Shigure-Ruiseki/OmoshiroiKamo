package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.modular.IModularPort;

/**
 * Abstract base class for recipe processing logic.
 * Implements the Template Method pattern for the recipe execution flow.
 */
public abstract class AbstractRecipeProcess {

    protected IModularRecipe currentRecipe;
    protected String currentRecipeName;
    protected int progress;
    protected int maxProgress;
    protected boolean running;
    protected boolean waitingForOutput;

    // Process state
    protected int energyPerTick;
    protected int energyOutputPerTick;
    protected int manaPerTick;
    protected int manaOutputPerTick;

    // Cached outputs to be produced upon completion
    protected final List<IRecipeOutput> cachedOutputs = new ArrayList<>();

    public void start(IModularRecipe recipe, List<IModularPort> inputPorts) {
        this.currentRecipe = recipe;
        this.currentRecipeName = recipe.getRegistryName();
        this.maxProgress = recipe.getDuration();
        this.progress = 0;
        this.running = true;
        this.waitingForOutput = false;

        onStart(recipe, inputPorts);
    }

    /**
     * Hook called when a recipe starts. Subclasses can use this to initialize state
     * or cache results.
     */
    protected abstract void onStart(IModularRecipe recipe, List<IModularPort> inputPorts);

    /**
     * Main tick method (Template Method).
     * Defines the skeleton of the recipe processing algorithm.
     */
    public void executeTick(List<IModularPort> inputPorts, List<IModularPort> outputPorts, ConditionContext context) {
        if (!running || waitingForOutput) return;

        // 1. Tick-based resource consumption (Energy, Mana, etc.)
        if (!consumePerTickResources(inputPorts)) {
            onResourceMissing();
            return;
        }

        // 2. Continuous condition check
        if (!checkContinuousConditions(context)) {
            abort();
            return;
        }

        // 3. Per-tick recipe logic
        currentRecipe.onTick(context);

        // 4. Progress update
        progress++;
        onProgressUpdate(progress, maxProgress);

        // 5. Completion check
        if (progress >= maxProgress) {
            handleCompletion();
        }
    }

    protected abstract boolean consumePerTickResources(List<IModularPort> inputPorts);

    protected abstract void onResourceMissing();

    protected boolean checkContinuousConditions(ConditionContext context) {
        return currentRecipe.isConditionMet(context);
    }

    protected void onProgressUpdate(int current, int max) {
        // Can be overridden for syncing or particles
    }

    protected void handleCompletion() {
        this.running = false;
        this.waitingForOutput = true;
        onCompleted();
    }

    protected abstract void onCompleted();

    public boolean tryOutput(List<IModularPort> outputPorts) {
        if (!waitingForOutput) return false;

        if (produceOutputs(outputPorts)) {
            this.waitingForOutput = false;
            reset();
            return true;
        }
        return false;
    }

    protected abstract boolean produceOutputs(List<IModularPort> outputPorts);

    public void abort() {
        reset();
    }

    protected void reset() {
        currentRecipe = null;
        currentRecipeName = null;
        progress = 0;
        maxProgress = 0;
        running = false;
        waitingForOutput = false;
        energyPerTick = 0;
        energyOutputPerTick = 0;
        manaPerTick = 0;
        manaOutputPerTick = 0;
        cachedOutputs.clear();
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isWaitingForOutput() {
        return waitingForOutput;
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setProgress(int progress) {
        this.progress = Math.max(0, progress);
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = Math.max(0, maxProgress);
    }

    public IModularRecipe getCurrentRecipe() {
        return currentRecipe;
    }

    public void addCachedOutput(IRecipeOutput output) {
        if (output != null) {
            this.cachedOutputs.add(output);
        }
    }

    public List<IRecipeOutput> getCachedOutputs() {
        return cachedOutputs;
    }
}
