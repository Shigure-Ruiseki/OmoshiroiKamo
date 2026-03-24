package ruiseki.omoshiroikamo.api.recipe.io;

import java.util.List;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;

/**
 * Abstract base class for recipe inputs that work with the Modular Port system.
 * Extends AbstractRecipeInput with Modular Port-specific processing logic.
 */
public abstract class AbstractModularRecipeInput extends AbstractRecipeInput implements IModularRecipeInput {

    protected int index = -1;

    @Override
    public boolean process(List<IModularPort> ports, int multiplier, boolean simulate) {
        long remaining = getRequiredAmount() * multiplier;
        boolean actualSimulate = simulate || !consume;

        for (IModularPort port : ports) {
            if (port.getPortType() != getPortType()) continue;
            if (port.getPortDirection() != IPortType.Direction.INPUT
                && port.getPortDirection() != IPortType.Direction.BOTH) continue;

            if (getIndex() != -1 && port.getAssignedIndex() != getIndex()) continue;

            if (!isCorrectPort(port)) {
                throw new IllegalStateException(
                    getPortType() + " INPUT port must be compatible implementation, got: "
                        + port.getClass()
                            .getName());
            }

            remaining -= consume(port, remaining, actualSimulate);

            if (remaining <= 0) break;
        }

        return remaining <= 0;
    }

    /**
     * Check if the port is of the correct type and instance for this input.
     */
    protected abstract boolean isCorrectPort(IModularPort port);

    /**
     * Consume from a single port and return the amount consumed.
     */
    protected abstract long consume(IModularPort port, long remaining, boolean simulate);

    @Override
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
