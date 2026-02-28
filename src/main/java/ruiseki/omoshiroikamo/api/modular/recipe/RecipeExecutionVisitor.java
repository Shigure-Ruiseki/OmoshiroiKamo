package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.module.machinery.common.recipe.ProcessAgent;

/**
 * Visitor that handles the actual execution of a recipe.
 * Dispatches logic for checking, consuming, and caching outputs based on the mode.
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
    private boolean satisfied = true;

    public RecipeExecutionVisitor(Mode mode, List<IModularPort> ports, ProcessAgent agent) {
        this.mode = mode;
        this.ports = ports;
        this.agent = agent;
    }

    public boolean isSatisfied() {
        return satisfied;
    }

    @Override
    public void visit(ItemInput input) {
        switch (mode) {
            case CHECK:
                if (!input.process(ports, true)) satisfied = false;
                break;
            case CONSUME:
                input.process(ports, false);
                break;
            default:
                break;
        }
    }

    @Override
    public void visit(FluidInput input) {
        switch (mode) {
            case CHECK:
                if (!input.process(ports, true)) satisfied = false;
                break;
            case CONSUME:
                input.process(ports, false);
                break;
            default:
                break;
        }
    }

    @Override
    public void visit(EnergyInput input) {
        switch (mode) {
            case CHECK:
                if (input.isPerTick()) return; // Skip per-tick for start check
                if (!input.process(ports, true)) satisfied = false;
                break;
            case CONSUME:
                if (input.isPerTick()) {
                    agent.setEnergyPerTick(agent.getEnergyPerTick() + input.getAmount());
                } else {
                    input.process(ports, false);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void visit(ManaInput input) {
        switch (mode) {
            case CHECK:
                if (input.isPerTick()) return;
                if (!input.process(ports, true)) satisfied = false;
                break;
            case CONSUME:
                if (input.isPerTick()) {
                    agent.setManaPerTick(agent.getManaPerTick() + input.getAmount());
                } else {
                    input.process(ports, false);
                }
                break;
            default:
                break;
        }
    }

    // --- Outputs ---

    @Override
    public void visit(ItemOutput output) {
        if (mode == Mode.CACHE) {
            agent.getCachedItemOutputs()
                .add(
                    output.getOutput()
                        .copy());
        }
    }

    @Override
    public void visit(FluidOutput output) {
        if (mode == Mode.CACHE) {
            agent.getCachedFluidOutputs()
                .add(
                    output.getOutput()
                        .copy());
        }
    }

    @Override
    public void visit(EnergyOutput output) {
        if (mode == Mode.CONSUME && output.isPerTick()) {
            agent.setEnergyOutputPerTick(agent.getEnergyOutputPerTick() + output.getAmount());
        } else if (mode == Mode.CACHE && !output.isPerTick()) {
            agent.getCachedEnergyOutputs()
                .add(output.getAmount());
        }
    }

    @Override
    public void visit(ManaOutput output) {
        if (mode == Mode.CONSUME && output.isPerTick()) {
            agent.setManaOutputPerTick(agent.getManaOutputPerTick() + output.getAmount());
        } else if (mode == Mode.CACHE && !output.isPerTick()) {
            agent.getCachedManaOutputs()
                .add(output.getAmount());
        }
    }

    @Override
    public void visit(GasOutput output) {
        if (mode == Mode.CACHE) {
            agent.getCachedGasOutputs()
                .add(new String[] { output.getGasName(), String.valueOf(output.getAmount()) });
        }
    }

    @Override
    public void visit(EssentiaOutput output) {
        if (mode == Mode.CACHE) {
            agent.getCachedEssentiaOutputs()
                .add(new String[] { output.getAspectTag(), String.valueOf(output.getAmount()) });
        }
    }

    @Override
    public void visit(VisOutput output) {
        if (mode == Mode.CACHE) {
            agent.getCachedVisOutputs()
                .add(new String[] { output.getAspectTag(), String.valueOf(output.getAmountCentiVis()) });
        }
    }
}
