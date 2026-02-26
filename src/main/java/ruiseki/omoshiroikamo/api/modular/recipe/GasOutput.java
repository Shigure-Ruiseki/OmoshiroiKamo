package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import net.minecraftforge.common.util.ForgeDirection;

import com.google.gson.JsonObject;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.gas.GasTankInfo;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.AbstractGasPortTE;

public class GasOutput extends AbstractRecipeOutput {

    private String gasName;
    private int amount;

    public GasOutput(String gasName, int amount) {
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
    public void apply(List<IModularPort> ports) {
        Gas gas = GasRegistry.getGas(gasName);
        if (gas == null) return;

        int remaining = amount;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.GAS) continue;
            if (port.getPortDirection() != IPortType.Direction.OUTPUT) continue;

            if (!(port instanceof AbstractGasPortTE)) continue;

            AbstractGasPortTE gasPort = (AbstractGasPortTE) port;
            GasStack insertStack = new GasStack(gas, remaining);
            int accepted = gasPort.internalReceiveGas(insertStack, true);
            remaining -= accepted;
            if (remaining <= 0) break;
        }
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port.getPortType() == IPortType.Type.GAS && port instanceof AbstractGasPortTE;
    }

    @Override
    protected long getPortCapacity(IModularPort port) {
        AbstractGasPortTE gasPort = (AbstractGasPortTE) port;
        GasTankInfo[] tankInfo = gasPort.getTankInfo(ForgeDirection.UNKNOWN);
        if (tankInfo != null && tankInfo.length > 0) {
            long total = 0;
            for (GasTankInfo info : tankInfo) {
                total += info.capacity;
            }
            return total;
        }
        return 0;
    }

    @Override
    protected long getRequiredAmount() {
        return amount;
    }

    @Override
    public void read(JsonObject json) {
        this.gasName = json.get("gas")
            .getAsString();
        this.amount = json.has("amount") ? json.get("amount")
            .getAsInt() : 1000;
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("gas", gasName);
        json.addProperty("amount", amount);
    }

    @Override
    public boolean validate() {
        return gasName != null && !gasName.isEmpty() && amount > 0;
    }

    public static GasOutput fromJson(JsonObject json) {
        GasOutput output = new GasOutput("", 0);
        output.read(json);
        return output.validate() ? output : null;
    }
}
