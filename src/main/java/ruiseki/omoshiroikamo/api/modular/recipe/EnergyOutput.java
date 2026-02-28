package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.AbstractEnergyIOPortTE;

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
    public void apply(List<IModularPort> ports) {
        long remaining = amount;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.ENERGY) continue;
            if (port.getPortDirection() != IPortType.Direction.OUTPUT
                && port.getPortDirection() != IPortType.Direction.BOTH) continue;

            if (!(port instanceof AbstractEnergyIOPortTE)) continue;

            AbstractEnergyIOPortTE energyPort = (AbstractEnergyIOPortTE) port;
            int accepted = energyPort.internalReceiveEnergy((int) remaining, false);
            remaining -= accepted;

            if (remaining <= 0) break;
        }
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port instanceof AbstractEnergyIOPortTE;
    }

    @Override
    protected long getPortCapacity(IModularPort port) {
        AbstractEnergyIOPortTE energyPort = (AbstractEnergyIOPortTE) port;
        return energyPort.getMaxEnergyStored() - energyPort.getEnergyStored();
    }

    @Override
    protected long getRequiredAmount() {
        return amount;
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
        return new EnergyOutput(amount, perTick);
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
