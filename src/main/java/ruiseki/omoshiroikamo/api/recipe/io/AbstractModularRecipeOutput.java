package ruiseki.omoshiroikamo.api.recipe.io;

import java.util.List;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;

/**
 * Abstract base class for recipe outputs that work with the Modular Port system.
 * Extends AbstractRecipeOutput with Modular Port-specific capacity checking logic.
 */
public abstract class AbstractModularRecipeOutput extends AbstractRecipeOutput implements IModularRecipeOutput {

    protected int index = -1;

    @Override
    public boolean checkCapacity(List<IModularPort> ports, int multiplier) {
        long totalCapacity = 0;

        for (IModularPort port : ports) {
            // Common check for all outputs
            if (port.getPortDirection() != IPortType.Direction.OUTPUT
                && port.getPortDirection() != IPortType.Direction.BOTH) continue;

            if (getIndex() != -1 && port.getAssignedIndex() != getIndex()) continue;

            if (isCorrectPort(port)) {
                totalCapacity += getPortCapacity(port);
            }
        }

        return totalCapacity >= getRequiredAmount() * multiplier;
    }

    /**
     * Check if the port is of the correct type and instance for this output.
     * Also checks IPortType.Type.
     */
    protected abstract boolean isCorrectPort(IModularPort port);

    /**
     * Calculate the capacity of a single valid port for this output type.
     */
    protected abstract long getPortCapacity(IModularPort port);

    @Override
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
