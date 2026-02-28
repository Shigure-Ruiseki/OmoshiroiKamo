package ruiseki.omoshiroikamo.module.machinery.common.recipe;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.modular.recipe.core.AbstractRecipeProcess;
import ruiseki.omoshiroikamo.api.modular.recipe.core.IModularRecipe;
import ruiseki.omoshiroikamo.api.modular.recipe.io.EnergyInput;
import ruiseki.omoshiroikamo.api.modular.recipe.io.EnergyOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.io.IRecipeOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.io.ManaInput;
import ruiseki.omoshiroikamo.api.modular.recipe.io.ManaOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.parser.OutputNBTRegistry;
import ruiseki.omoshiroikamo.api.modular.recipe.visitor.RecipeExecutionVisitor;

public class ProcessAgent extends AbstractRecipeProcess {

    private List<IModularPort> currentInputPorts; // Temporary holder for visitor usage during start

    public ProcessAgent() {
        reset();
    }

    @Override
    protected void onStart(IModularRecipe recipe, List<IModularPort> inputPorts) {
        this.currentInputPorts = inputPorts;

        // 1. Check inputs (This is still needed here or in start override)
    }

    // Re-implement start to return boolean and handle validation
    public boolean start(IModularRecipe recipe, List<IModularPort> inputPorts, List<IModularPort> energyPorts) {
        if (isRunning()) return false;

        // 1. Check inputs
        RecipeExecutionVisitor checker = new RecipeExecutionVisitor(
            RecipeExecutionVisitor.Mode.CHECK,
            inputPorts,
            this);
        recipe.accept(checker);
        if (!checker.isSatisfied()) return false;

        if (energyPorts != inputPorts) {
            RecipeExecutionVisitor energyChecker = new RecipeExecutionVisitor(
                RecipeExecutionVisitor.Mode.CHECK,
                energyPorts,
                this);
            recipe.accept(energyChecker);
            if (!energyChecker.isSatisfied()) return false;
        }

        // Initialize state via base start logic (simplified here as we already validated)
        super.start(recipe, inputPorts);

        // 2. Consume and setup state (Specific to ProcessAgent)
        recipe.accept(new RecipeExecutionVisitor(RecipeExecutionVisitor.Mode.CONSUME, inputPorts, this));
        if (energyPorts != inputPorts) {
            recipe.accept(new RecipeExecutionVisitor(RecipeExecutionVisitor.Mode.CONSUME, energyPorts, this));
        }

        clearCaches();

        // 3. Cache outputs
        recipe.accept(new RecipeExecutionVisitor(RecipeExecutionVisitor.Mode.CACHE, null, this));

        return true;
    }

    private void clearCaches() {
        cachedOutputs.clear();
    }

    @Override
    protected boolean consumePerTickResources(List<IModularPort> inputPorts) {
        if (energyPerTick > 0) {
            EnergyInput energyReq = new EnergyInput(energyPerTick, true);
            if (!energyReq.process(inputPorts, true)) {
                return false;
            }
            energyReq.process(inputPorts, false);
        }

        if (manaPerTick > 0) {
            ManaInput manaReq = new ManaInput(manaPerTick, true);
            if (!manaReq.process(inputPorts, true)) {
                return false;
            }
            manaReq.process(inputPorts, false);
        }
        return true;
    }

    @Override
    protected void onResourceMissing() {
        // Handled by TickResult in TEMachineController.
        // We could set a status here if needed.
    }

    @Override
    protected void onCompleted() {
        // Transition to waitingForOutput is handled by base
    }

    // Adapt tick to return TickResult for TEMachineController compatibility
    public TickResult tick(List<IModularPort> inputPorts, List<IModularPort> outputPorts, ConditionContext context) {
        if (!isRunning()) return TickResult.IDLE;
        if (isWaitingForOutput()) return TickResult.WAITING_OUTPUT;

        // 1. Resource check (Dry run for energy/mana)
        if (energyPerTick > 0) {
            if (!new EnergyInput(energyPerTick, true).process(inputPorts, true)) return TickResult.NO_ENERGY;
        }
        if (manaPerTick > 0) {
            if (!new ManaInput(manaPerTick, true).process(inputPorts, true)) return TickResult.NO_MANA;
        }

        // 2. Output capacity check for per-tick outputs
        if (energyOutputPerTick > 0 && !new EnergyOutput(energyOutputPerTick, true).process(outputPorts, true))
            return TickResult.OUTPUT_FULL;
        if (manaOutputPerTick > 0 && !new ManaOutput(manaOutputPerTick, true).process(outputPorts, true))
            return TickResult.OUTPUT_FULL;

        // 3. Execute base tick (handles conditions, progress, and actual consumption)
        super.executeTick(inputPorts, outputPorts, context);

        // 4. Handle per-tick energy/mana outputs (since base doesn't know about them specifically)
        if (energyOutputPerTick > 0) new EnergyOutput(energyOutputPerTick, true).process(outputPorts, false);
        if (manaOutputPerTick > 0) new ManaOutput(manaOutputPerTick, true).process(outputPorts, false);

        if (isWaitingForOutput()) return TickResult.READY_OUTPUT;
        if (!isRunning()) return TickResult.IDLE; // Could be aborted by condition

        return TickResult.CONTINUE;
    }

    /**
     * Diagnose why the agent is idle.
     */
    public TickResult diagnoseIdle(List<IModularPort> inputPorts) {
        if (running || waitingForOutput) return TickResult.CONTINUE; // Not idle

        // Check input ports to see if any input is missing
        if (inputPorts == null || inputPorts.isEmpty()) {
            return TickResult.NO_INPUT;
        }

        return TickResult.IDLE;
    }

    @Override
    protected boolean produceOutputs(List<IModularPort> outputPorts) {
        // 1. Check capacity for all
        for (IRecipeOutput output : cachedOutputs) {
            if (!output.process(outputPorts, true)) {
                return false;
            }
        }

        // 2. Apply outputs
        for (IRecipeOutput output : cachedOutputs) {
            output.process(outputPorts, false);
        }

        return true;
    }

    @Override
    protected void reset() {
        super.reset();
        clearCaches();
    }

    public int getEnergyPerTick() {
        return energyPerTick;
    }

    public void setEnergyPerTick(int amount) {
        this.energyPerTick = amount;
    }

    public int getEnergyOutputPerTick() {
        return energyOutputPerTick;
    }

    public void setEnergyOutputPerTick(int amount) {
        this.energyOutputPerTick = amount;
    }

    public int getManaPerTick() {
        return manaPerTick;
    }

    public void setManaPerTick(int amount) {
        this.manaPerTick = amount;
    }

    public int getManaOutputPerTick() {
        return manaOutputPerTick;
    }

    public void setManaOutputPerTick(int amount) {
        this.manaOutputPerTick = amount;
    }

    public void setCurrentRecipeName(String name) {
        this.currentRecipeName = name;
    }

    public String getCurrentRecipeName() {
        return currentRecipeName;
    }

    /**
     * Get a list of output types that are currently cached
     */
    public Set<IPortType.Type> getCachedOutputTypes() {
        Set<IPortType.Type> types = new HashSet<>();
        for (IRecipeOutput output : cachedOutputs) {
            types.add(output.getPortType());
        }
        if (manaOutputPerTick > 0) types.add(IPortType.Type.MANA);
        if (energyOutputPerTick > 0) types.add(IPortType.Type.ENERGY);
        return types;
    }

    public float getProgressPercent() {
        if (maxProgress == 0) return 0;
        return (float) progress / maxProgress;
    }

    public String getStatusMessage(List<IModularPort> outputPorts) {
        if (isRunning() && !isWaitingForOutput()) {
            if (maxProgress <= 0) return "Processing 0 %";
            return "Processing " + (int) ((float) progress / maxProgress * 100) + " %";
        }
        if (isWaitingForOutput()) {
            String blocked = diagnoseBlockedOutputs(outputPorts);
            return blocked + " Output is full";
        }
        return "Idle";
    }

    private String diagnoseBlockedOutputs(List<IModularPort> outputPorts) {
        if (currentRecipe != null) {
            StringBuilder blocked = new StringBuilder();
            for (IRecipeOutput output : currentRecipe.getOutputs()) {
                if (!output.process(outputPorts, true)) {
                    if (blocked.length() > 0) blocked.append(", ");
                    blocked.append(
                        output.getPortType()
                            .name());
                }
            }
            if (blocked.length() > 0) return blocked.toString();
        }
        Set<IPortType.Type> cachedTypes = getCachedOutputTypes();
        if (!cachedTypes.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (IPortType.Type type : cachedTypes) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(type.name());
            }
            return sb.toString();
        }
        return "Unknown";
    }

    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("progress", progress);
        nbt.setInteger("maxProgress", maxProgress);
        nbt.setInteger("energyPerTick", energyPerTick);
        nbt.setInteger("energyOutputPerTick", energyOutputPerTick);
        nbt.setInteger("manaPerTick", manaPerTick);
        nbt.setInteger("manaOutputPerTick", manaOutputPerTick);
        nbt.setBoolean("running", running);
        nbt.setBoolean("waitingForOutput", waitingForOutput);
        if (currentRecipeName != null) nbt.setString("recipeName", currentRecipeName);

        if (running || waitingForOutput) {
            NBTTagList outputList = new NBTTagList();
            for (IRecipeOutput output : cachedOutputs) {
                NBTTagCompound tag = new NBTTagCompound();
                output.writeToNBT(tag);
                outputList.appendTag(tag);
            }
            nbt.setTag("cachedOutputs", outputList);
        }
    }

    public void readFromNBT(NBTTagCompound nbt) {
        progress = nbt.getInteger("progress");
        maxProgress = nbt.getInteger("maxProgress");
        energyPerTick = nbt.getInteger("energyPerTick");
        energyOutputPerTick = nbt.getInteger("energyOutputPerTick");
        manaPerTick = nbt.getInteger("manaPerTick");
        manaOutputPerTick = nbt.getInteger("manaOutputPerTick");
        running = nbt.getBoolean("running");
        waitingForOutput = nbt.getBoolean("waitingForOutput");
        currentRecipeName = nbt.hasKey("recipeName") ? nbt.getString("recipeName") : null;

        clearCaches();

        if (running || waitingForOutput) {
            NBTTagList outputList = nbt.getTagList("cachedOutputs", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < outputList.tagCount(); i++) {
                NBTTagCompound tag = outputList.getCompoundTagAt(i);
                IRecipeOutput output = OutputNBTRegistry.read(tag);
                if (output != null) {
                    cachedOutputs.add(output);
                }
            }
        }
    }

    public enum TickResult {
        IDLE,
        CONTINUE,
        NO_ENERGY,
        READY_OUTPUT,
        WAITING_OUTPUT,
        NO_INPUT,
        NO_MATCHING_RECIPE,
        OUTPUT_FULL,
        PAUSED,
        NO_MANA
    }
}
