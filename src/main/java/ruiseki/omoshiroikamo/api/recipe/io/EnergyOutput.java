package ruiseki.omoshiroikamo.api.recipe.io;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.core.energy.IOKEnergySink;

public class EnergyOutput extends AbstractRecipeOutput {

    private int amount;
    private boolean perTick;

    public EnergyOutput(int amount, boolean perTick) {
        this.amount = amount;
        this.perTick = perTick;
    }

    public EnergyOutput(int amount) {
        this(amount, true);
    }

    public int getAmount() {
        return amount;
    }

    public boolean isPerTick() {
        return perTick;
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
        this.amount = json.get("energy")
            .getAsInt();
        this.perTick = true;
        if (json.has("perTick")) {
            this.perTick = json.get("perTick")
                .getAsBoolean();
        } else if (json.has("pertick")) {
            this.perTick = json.get("pertick")
                .getAsBoolean();
        }
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("energy", amount);
        if (!perTick) json.addProperty("perTick", false);
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
        return new EnergyOutput(amount * multiplier, perTick);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("id", "energy");
        nbt.setInteger("amount", amount);
        nbt.setBoolean("perTick", perTick);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.amount = nbt.getInteger("amount");
        this.perTick = nbt.getBoolean("perTick");
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }
}
