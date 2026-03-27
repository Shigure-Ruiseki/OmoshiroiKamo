package ruiseki.omoshiroikamo.api.recipe.visitor;

import java.util.List;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.recipe.io.BlockOutput;
import ruiseki.omoshiroikamo.api.recipe.io.IModularRecipeInput;
import ruiseki.omoshiroikamo.api.recipe.io.IModularRecipeOutput;
import ruiseki.omoshiroikamo.module.machinery.common.recipe.ProcessAgent;

/**
 * Visitor that handles the actual execution of a recipe.
 * Dispatches logic for checking, consuming, and caching outputs based on the
 * mode.
 */
public class RecipeExecutionVisitor implements IRecipeVisitor {

    public enum Mode {
        CHECK, // Check if inputs are available
        CONSUME, // Consume inputs and setup per-tick processing
        CACHE // Cache outputs for later production
    }

    private final Mode mode;
    private final List<IModularPort> ports;
    private final ProcessAgent agent;
    private final ConditionContext context;
    private int batchSize = 1;
    private boolean satisfied = true;

    public RecipeExecutionVisitor(Mode mode, List<IModularPort> ports, ProcessAgent agent, ConditionContext context) {
        this.mode = mode;
        this.ports = ports;
        this.agent = agent;
        this.context = context;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public ProcessAgent getAgent() {
        return agent;
    }

    public boolean isSatisfied() {
        return satisfied;
    }

    // --- Modular Inputs ---

    @Override
    public void visit(IModularRecipeInput input) {
        if (mode == Mode.CHECK) {
            // Check even if per-tick, to ensure initial availability
            if (!input.process(ports, batchSize, true, context)) satisfied = false;
        } else if (mode == Mode.CONSUME) {
            if (input.isPerTick()) {
                agent.addPerTickInput((IModularRecipeInput) input.copy(batchSize));
            } else {
                input.process(ports, batchSize, false, context);
            }
        }
    }

    // --- Modular Outputs ---

    @Override
    public void visit(IModularRecipeOutput output) {
        if (mode == Mode.CACHE) {
            if (output.isPerTick()) {
                agent.addPerTickOutput((IModularRecipeOutput) output.copy(batchSize));
            } else {
                agent.addCachedOutput(output.copy(batchSize));
            }
        }
    }

    /**
     * BlockOutput has special capacity checking during CACHE mode.
     */
    @Override
    public void visit(BlockOutput output) {
        if (mode == Mode.CACHE) {
            // BlockOutput acts as a placement check during CACHE mode (checkOutputCapacity)
            if (!output.checkCapacity(ports, batchSize, context)) {
                satisfied = false;
            }
            if (output.isPerTick()) {
                agent.addPerTickOutput((IModularRecipeOutput) output.copy(batchSize));
            } else {
                agent.addCachedOutput(output.copy(batchSize));
            }
        }
    }
}
