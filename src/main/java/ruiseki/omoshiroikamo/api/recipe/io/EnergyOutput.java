package ruiseki.omoshiroikamo.api.recipe.io;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.core.RecipeTickResult;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.core.energy.IOKEnergySink;

public class EnergyOutput extends AbstractModularRecipeOutput {

    private int amount;

    public EnergyOutput(int amount, boolean perTick) {
        this.amount = amount;
        this.interval = perTick ? 1 : 0;
    }

    public EnergyOutput(int amount) {
        this(amount, true);
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public boolean isPerTick() {
        return interval > 0;
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.ENERGY;
    }

    @Override
    public void apply(List<IModularPort> ports, int multiplier) {
        long remaining = (long) amount * multiplier;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.ENERGY) continue;
            if (port.getPortDirection() != IPortType.Direction.OUTPUT
                && port.getPortDirection() != IPortType.Direction.BOTH) continue;

            if (getIndex() != -1 && port.getAssignedIndex() != getIndex()) continue;

            if (!(port instanceof IOKEnergySink)) continue;

            IOKEnergySink energyPort = (IOKEnergySink) port;
            int accepted = energyPort
                .receiveEnergy(ForgeDirection.UNKNOWN, (int) Math.min(remaining, (long) Integer.MAX_VALUE), false);
            remaining -= accepted;

            if (remaining <= 0) break;
        }
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port instanceof IOKEnergySink;
    }

    @Override
    protected long getPortCapacity(IModularPort port) {
        IOKEnergySink energyPort = (IOKEnergySink) port;
        return (long) (energyPort.getMaxEnergyStored() - energyPort.getEnergyStored());
    }

    @Override
    public long getRequiredAmount() {
        return (long) amount;
    }

    @Override
    public void read(JsonObject json) {
        readPerTick(json, 1);
        if (json.has("index")) this.index = json.get("index")
            .getAsInt();
        this.amount = json.get("energy")
            .getAsInt();
    }

    @Override
    public void write(JsonObject json) {
        if (index != -1) json.addProperty("index", index);
        json.addProperty("energy", amount);
        if (interval != 1) {
            if (isPerTick()) {
                json.addProperty("pertick", interval);
            } else {
                json.addProperty("pertick", false);
            }
        }
    }

    @Override
    public boolean validate() {
        return amount > 0;
    }

    public static EnergyOutput fromJson(JsonObject json) {
        EnergyOutput output = new EnergyOutput(0, true);
        output.read(json);
        return output.validate() ? output : null;
    }

    @Override
    public IRecipeOutput copy() {
        return copy(1);
    }

    @Override
    public IRecipeOutput copy(int multiplier) {
        EnergyOutput result = new EnergyOutput(amount * multiplier, isPerTick());
        result.interval = this.interval;
        result.index = this.index;
        return result;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("id", "energy");
        nbt.setInteger("amount", amount);
        nbt.setInteger("interval", interval);
        nbt.setInteger("index", index);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.amount = nbt.getInteger("amount");
        this.interval = nbt.getInteger("interval");
        this.index = nbt.hasKey("index") ? nbt.getInteger("index") : -1;
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public RecipeTickResult getFailureResult(boolean perTick) {
        return RecipeTickResult.OUTPUT_FULL;
    }
}
