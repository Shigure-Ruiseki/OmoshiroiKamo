package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.json.AbstractJsonMaterial;

public abstract class AbstractRecipeOutput extends AbstractJsonMaterial implements IRecipeOutput {

    @Override
    public boolean checkCapacity(List<IModularPort> ports) {
        long totalCapacity = 0;

        for (IModularPort port : ports) {
            // Common check for all outputs
            if (port.getPortDirection() != IPortType.Direction.OUTPUT
                && port.getPortDirection() != IPortType.Direction.BOTH) continue;

            if (isCorrectPort(port)) {
                totalCapacity += getPortCapacity(port);
            }
        }

        return totalCapacity >= getRequiredAmount();
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

    /**
     * Get the required amount for this output (stack size, fluid amount, etc.)
     */
    protected abstract long getRequiredAmount();
}
