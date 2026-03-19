package ruiseki.omoshiroikamo.api.recipe.io;

import net.minecraftforge.common.util.ForgeDirection;

import com.google.gson.JsonObject;

import mekanism.api.gas.GasStack;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.core.gas.IGasHandler;

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
        return port.getPortType() == IPortType.Type.GAS && port instanceof IGasHandler;
    }

    @Override
    protected long consume(IModularPort port, long remaining, boolean simulate) {
        IGasHandler gasPort = (IGasHandler) port;
        GasStack drawn = gasPort.drawGas(ForgeDirection.UNKNOWN, (int) remaining, false);
        if (drawn != null && drawn.amount > 0) {
            if (gasName == null || gasName.isEmpty()
                || drawn.getGas()
                    .getName()
                    .equals(gasName)) {
                if (!simulate) {
                    gasPort.drawGas(ForgeDirection.UNKNOWN, drawn.amount, true);
                }
                return drawn.amount;
            }
        }
        return 0;
    }

    @Override
    public void read(JsonObject json) {
        if (json.has("consume")) {
            this.consume = json.get("consume")
                .getAsBoolean();
        }

        this.gasName = json.has("gas") ? json.get("gas")
            .getAsString() : null;
        this.amount = json.get("amount")
            .getAsInt();
    }

    @Override
    public void write(JsonObject json) {
        if (!consume) json.addProperty("consume", false);
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
