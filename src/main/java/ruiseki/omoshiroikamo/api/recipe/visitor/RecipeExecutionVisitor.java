package ruiseki.omoshiroikamo.api.recipe.visitor;

import java.util.List;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.recipe.io.BlockInput;
import ruiseki.omoshiroikamo.api.recipe.io.BlockNbtInput;
import ruiseki.omoshiroikamo.api.recipe.io.BlockNbtOutput;
import ruiseki.omoshiroikamo.api.recipe.io.BlockOutput;
import ruiseki.omoshiroikamo.api.recipe.io.EnergyInput;
import ruiseki.omoshiroikamo.api.recipe.io.EnergyOutput;
import ruiseki.omoshiroikamo.api.recipe.io.EssentiaOutput;
import ruiseki.omoshiroikamo.api.recipe.io.FluidInput;
import ruiseki.omoshiroikamo.api.recipe.io.FluidOutput;
import ruiseki.omoshiroikamo.api.recipe.io.GasOutput;
import ruiseki.omoshiroikamo.api.recipe.io.IRecipeInput;
import ruiseki.omoshiroikamo.api.recipe.io.IRecipeOutput;
import ruiseki.omoshiroikamo.api.recipe.io.ItemInput;
import ruiseki.omoshiroikamo.api.recipe.io.ItemOutput;
import ruiseki.omoshiroikamo.api.recipe.io.ManaInput;
import ruiseki.omoshiroikamo.api.recipe.io.ManaOutput;
import ruiseki.omoshiroikamo.api.recipe.io.VisOutput;
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
    private int batchSize = 1;
    private boolean satisfied = true;

    public RecipeExecutionVisitor(Mode mode, List<IModularPort> ports, ProcessAgent agent) {
        this.mode = mode;
        this.ports = ports;
        this.agent = agent;
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

    @Override
    public void visit(ItemInput input) {
        switch (mode) {
            case CHECK:
                if (!input.process(ports, batchSize, true)) satisfied = false;
                break;
            case CONSUME:
                input.process(ports, batchSize, false);
                break;
            default:
                break;
        }
    }

    @Override
    public void visit(FluidInput input) {
        switch (mode) {
            case CHECK:
                if (!input.process(ports, batchSize, true)) satisfied = false;
                break;
            case CONSUME:
                input.process(ports, batchSize, false);
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
                if (!input.process(ports, batchSize, true)) satisfied = false;
                break;
            case CONSUME:
                if (input.isPerTick()) {
                    agent.setEnergyPerTick(agent.getEnergyPerTick() + input.getAmount() * batchSize);
                } else {
                    input.process(ports, batchSize, false);
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
                if (!input.process(ports, batchSize, true)) satisfied = false;
                break;
            case CONSUME:
                if (input.isPerTick()) {
                    agent.setManaPerTick(agent.getManaPerTick() + input.getAmount() * batchSize);
                } else {
                    input.process(ports, batchSize, false);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void visit(BlockInput input) {
        switch (mode) {
            case CHECK:
                if (!input.process(ports, batchSize, true)) satisfied = false;
                break;
            case CONSUME:
                input.process(ports, batchSize, false);
                break;
            default:
                break;
        }
    }

    @Override
    public void visit(BlockNbtInput input) {
        switch (mode) {
            case CHECK:
                if (!input.process(ports, batchSize, true)) satisfied = false;
                break;
            case CONSUME:
                input.process(ports, batchSize, false);
                break;
            default:
                break;
        }
    }

    // --- Outputs ---

    @Override
    public void visit(ItemOutput output) {
        if (mode == Mode.CACHE) {
            agent.addCachedOutput(output.copy(batchSize));
        }
    }

    @Override
    public void visit(FluidOutput output) {
        if (mode == Mode.CACHE) {
            agent.addCachedOutput(output.copy(batchSize));
        }
    }

    @Override
    public void visit(EnergyOutput output) {
        if (mode == Mode.CONSUME && output.isPerTick()) {
            agent.setEnergyOutputPerTick(agent.getEnergyOutputPerTick() + output.getAmount() * batchSize);
        } else if (mode == Mode.CACHE && !output.isPerTick()) {
            agent.addCachedOutput(output.copy(batchSize));
        }
    }

    @Override
    public void visit(ManaOutput output) {
        if (mode == Mode.CONSUME && output.isPerTick()) {
            agent.setManaOutputPerTick(agent.getManaOutputPerTick() + output.getAmount() * batchSize);
        } else if (mode == Mode.CACHE && !output.isPerTick()) {
            agent.addCachedOutput(output.copy(batchSize));
        }
    }

    @Override
    public void visit(GasOutput output) {
        if (mode == Mode.CACHE) {
            agent.addCachedOutput(output.copy(batchSize));
        }
    }

    @Override
    public void visit(EssentiaOutput output) {
        if (mode == Mode.CACHE) {
            agent.addCachedOutput(output.copy(batchSize));
        }
    }

    @Override
    public void visit(VisOutput output) {
        if (mode == Mode.CACHE) {
            agent.addCachedOutput(output.copy(batchSize));
        }
    }

    @Override
    public void visit(BlockNbtOutput output) {
        if (mode == Mode.CACHE) {
            if (!output.checkCapacity(ports, batchSize)) {
                satisfied = false;
            }
            agent.addCachedOutput(output.copy(batchSize));
        }
    }

    @Override
    public void visit(BlockOutput output) {
        if (mode == Mode.CACHE) {
            // BlockOutput acts as a placement check during CACHE mode (checkOutputCapacity)
            if (!output.checkCapacity(ports, batchSize)) {
                satisfied = false;
            }
            agent.addCachedOutput(output.copy(batchSize));
        }
    }

    @Override
    public void visit(IRecipeInput input) {
        switch (this.mode) {
            case CHECK:
                if (!input.process(ports, batchSize, true)) satisfied = false;
                break;
            case CONSUME:
                input.process(ports, batchSize, false);
                break;
            default:
                break;
        }
    }

    @Override
    public void visit(IRecipeOutput output) {
        if (this.mode == Mode.CACHE) {
            agent.addCachedOutput(output.copy(batchSize));
        }
    }
}
