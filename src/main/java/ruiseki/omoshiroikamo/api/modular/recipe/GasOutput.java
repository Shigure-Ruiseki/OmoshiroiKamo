package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import net.minecraftforge.common.util.ForgeDirection;

import com.google.gson.JsonObject;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.AbstractGasPortTE;

/**
 * Recipe output for Gas.
 */
public class GasOutput implements IRecipeOutput {

    private final String gasName;
    private final int amount;

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
    public boolean process(List<IModularPort> ports, boolean simulate) {
        Gas gas = GasRegistry.getGas(gasName);
        if (gas == null) return false;

        int remaining = amount;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.GAS) continue;
            if (port.getPortDirection() != IPortType.Direction.OUTPUT) continue;
            if (!(port instanceof AbstractGasPortTE)) continue;

            AbstractGasPortTE gasPort = (AbstractGasPortTE) port;
            GasStack insertStack = new GasStack(gas, remaining);
            int accepted = gasPort.receiveGas(ForgeDirection.UNKNOWN, insertStack, !simulate);
            remaining -= accepted;
            if (remaining <= 0) break;
        }

        return remaining <= 0;
    }

    public static GasOutput fromJson(JsonObject json) {
        String gasName = json.get("gas")
            .getAsString();
        int amount = json.has("amount") ? json.get("amount")
            .getAsInt() : 1000;
        Gas gas = GasRegistry.getGas(gasName);
        if (gas == null) {
            Logger.warn("Unknown gas in recipe: {}", gasName);
            return null;
        }
        return new GasOutput(gasName, amount);
    }
}
