package ruiseki.omoshiroikamo.api.modular.recipe.io;

import com.google.gson.JsonObject;

import mekanism.api.gas.GasStack;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.modular.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.AbstractGasPortTE;

public class GasInput extends AbstractRecipeInput {

    private String gasName;
    private int amount;

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
    public long getRequiredAmount() {
        return amount;
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port instanceof AbstractGasPortTE;
    }

    @Override
    protected long consume(IModularPort port, long remaining, boolean simulate) {
        AbstractGasPortTE gasPort = (AbstractGasPortTE) port;
        GasStack drawn = gasPort.internalDrawGas((int) remaining, false);
        if (drawn != null && drawn.amount > 0) {
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

    @Override
    public void read(JsonObject json) {
        this.gasName = json.has("gas") ? json.get("gas")
            .getAsString() : null;
        this.amount = json.get("amount")
            .getAsInt();
    }

    @Override
    public void write(JsonObject json) {
        if (gasName != null) json.addProperty("gas", gasName);
        json.addProperty("amount", amount);
    }

    @Override
    public boolean validate() {
        return amount > 0;
    }

    public static GasInput fromJson(JsonObject json) {
        GasInput input = new GasInput(null, 0);
        input.read(json);
        return input.validate() ? input : null;
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }
}
