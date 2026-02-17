package ruiseki.omoshiroikamo.api.modular.recipe;

import com.google.gson.JsonObject;

import mekanism.api.gas.GasStack;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.AbstractGasPortTE;

public class GasInput extends AbstractRecipeInput {

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
    protected long getRequiredAmount() {
        return amount;
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port instanceof AbstractGasPortTE;
    }

    @Override
    protected long consume(IModularPort port, long remaining, boolean simulate) {
        AbstractGasPortTE gasPort = (AbstractGasPortTE) port;
        // Use internalDrawGas to bypass side IO checks
        GasStack drawn = gasPort.internalDrawGas((int) remaining, false);
        if (drawn != null && drawn.amount > 0) {
            // Check if gas type matches (null gasName = any gas)
            if (gasName == null || gasName.isEmpty()
                || drawn.getGas()
                    .getName()
                    .equals(gasName)) {
                if (!simulate) {
                    gasPort.internalDrawGas(drawn.amount, true);
                }
                return drawn.amount;
            }
        }
        return 0;
    }

    public static GasInput fromJson(JsonObject json) {
        String gasName = json.has("gas") ? json.get("gas")
            .getAsString() : null;
        int amount = json.get("amount")
            .getAsInt();
        return new GasInput(gasName, amount);
    }
}
