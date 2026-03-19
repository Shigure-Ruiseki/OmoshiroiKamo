package ruiseki.omoshiroikamo.api.recipe.io;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.gson.JsonObject;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.core.gas.GasTankInfo;
import ruiseki.omoshiroikamo.core.gas.IGasHandler;

public class GasOutput extends AbstractModularRecipeOutput {

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
    public void apply(List<IModularPort> ports, int multiplier) {
        Gas gas = GasRegistry.getGas(gasName);
        if (gas == null) return;

        int remaining = amount * multiplier;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.GAS) continue;
            if (port.getPortDirection() != IPortType.Direction.OUTPUT) continue;

            if (!(port instanceof IGasHandler)) continue;

            IGasHandler gasPort = (IGasHandler) port;
            GasStack insertStack = new GasStack(gas, remaining);
            int accepted = gasPort.receiveGas(ForgeDirection.UNKNOWN, insertStack, true);
            remaining -= accepted;
            if (remaining <= 0) break;
        }
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port.getPortType() == IPortType.Type.GAS && port instanceof IGasHandler;
    }

    @Override
    protected long getPortCapacity(IModularPort port) {
        IGasHandler gasPort = (IGasHandler) port;
        GasTankInfo[] tankInfo = gasPort.getTankInfo(ForgeDirection.UNKNOWN);
        if (tankInfo != null && tankInfo.length > 0) {
            long totalAvailable = 0;
            for (GasTankInfo info : tankInfo) {
                // Calculate available space (capacity - stored amount)
                int stored = info.gas != null ? info.gas.amount : 0;
                int space = info.capacity - stored;
                if (space > 0) totalAvailable += space;
            }
            return totalAvailable;
        }
        return 0;
    }

    @Override
    public long getRequiredAmount() {
        return amount;
    }

    @Override
    public void read(JsonObject json) {
        readPerTick(json, 0);
        this.gasName = json.get("gas")
            .getAsString();
        this.amount = json.has("amount") ? json.get("amount")
            .getAsInt() : 1000;
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("gas", gasName);
        json.addProperty("amount", amount);
        if (interval > 0) json.addProperty("pertick", interval);
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

    @Override
    public IRecipeOutput copy() {
        return copy(1);
    }

    @Override
    public IRecipeOutput copy(int multiplier) {
        GasOutput result = new GasOutput(gasName, amount * multiplier);
        result.interval = this.interval;
        return result;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("id", "gas");
        nbt.setInteger("interval", interval);
        nbt.setString("gas", gasName);
        nbt.setInteger("amount", amount);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.interval = nbt.getInteger("interval");
        this.gasName = nbt.getString("gas");
        this.amount = nbt.getInteger("amount");
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }
}
