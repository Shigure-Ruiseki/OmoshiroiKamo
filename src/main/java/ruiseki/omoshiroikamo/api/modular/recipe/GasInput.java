package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import com.google.gson.JsonObject;

import mekanism.api.gas.GasStack;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.AbstractGasPortTE;

/**
 * Recipe input requirement for Gas.
 */
public class GasInput implements IRecipeInput {

    private final String gasName;
    private final int amount;

    public GasInput(String gasName, int amount) {
        this.gasName = gasName;
        this.amount = amount;
    }

    public String getGasName() {
        return gasName;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.GAS;
    }

    @Override
    public boolean process(List<IModularPort> ports, boolean simulate) {
        int remaining = amount;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.GAS) continue;
            if (port.getPortDirection() != IPortType.Direction.INPUT) continue;
            if (!(port instanceof AbstractGasPortTE)) continue;

            AbstractGasPortTE gasPort = (AbstractGasPortTE) port;
            // Use internalDrawGas to bypass side IO checks
            GasStack drawn = gasPort.internalDrawGas(remaining, false);
            if (drawn != null && drawn.amount > 0) {
                // Check if gas type matches (null gasName = any gas)
                if (gasName == null || gasName.isEmpty()
                    || drawn.getGas()
                        .getName()
                        .equals(gasName)) {
                    if (!simulate) {
                        gasPort.internalDrawGas(drawn.amount, true);
                    }
                    remaining -= drawn.amount;
                }
            }
            if (remaining <= 0) break;
        }

        return remaining <= 0;
    }

    public static GasInput fromJson(JsonObject json) {
        String gasName = json.has("gas") ? json.get("gas")
            .getAsString() : null;
        int amount = json.get("amount")
            .getAsInt();
        return new GasInput(gasName, amount);
    }
}
