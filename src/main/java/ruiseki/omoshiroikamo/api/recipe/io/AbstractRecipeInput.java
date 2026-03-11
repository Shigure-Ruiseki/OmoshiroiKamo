package ruiseki.omoshiroikamo.api.recipe.io;

import java.util.List;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.json.AbstractJsonMaterial;

public abstract class AbstractRecipeInput extends AbstractJsonMaterial implements IRecipeInput {

    protected boolean consume = true;

    @Override
    public boolean process(List<IModularPort> ports, int multiplier, boolean simulate) {
        long remaining = getRequiredAmount() * multiplier;
        boolean actualSimulate = simulate || !consume;

        for (IModularPort port : ports) {
            if (port.getPortType() != getPortType()) continue;
            if (port.getPortDirection() != IPortType.Direction.INPUT
                && port.getPortDirection() != IPortType.Direction.BOTH) continue;

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

    @Override
    public boolean isConsume() {
        return consume;
    }

    public abstract long getRequiredAmount();

    protected abstract boolean isCorrectPort(IModularPort port);

    protected abstract long consume(IModularPort port, long remaining, boolean simulate);
}
