package ruiseki.omoshiroikamo.api.recipe.io;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.core.RecipeTickResult;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.core.energy.IOKEnergySource;

/**
 * perTick=true: Energy consumed every tick during processing.
 * perTick=false: Energy consumed once at recipe start.
 */
public class EnergyInput extends AbstractModularRecipeInput {

    private int amount;

    public EnergyInput(int amount, boolean perTick) {
        this.amount = amount;
        this.interval = perTick ? 1 : 0;
    }

    public EnergyInput(int amount) {
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
    public long getRequiredAmount() {
        return (long) amount;
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port instanceof IOKEnergySource;
    }

    @Override
    protected long consume(IModularPort port, long remaining, boolean simulate) {
        IOKEnergySource energyPort = (IOKEnergySource) port;
        int stored = energyPort.getEnergyStored();
        if (stored > 0) {
            int extract = (int) Math.min((long) stored, remaining);
            if (!simulate) {
                energyPort.extractEnergy(ForgeDirection.UNKNOWN, extract, false);
            }
            return (long) extract;
        }
        return 0;
    }

    @Override
    public void read(JsonObject json) {
        readPerTick(json, 1);

        if (json.has("consume")) {
            this.consume = json.get("consume")
                .getAsBoolean();
        }

        this.amount = json.get("energy")
            .getAsInt();
    }

    @Override
    public void write(JsonObject json) {
        if (!consume) json.addProperty("consume", false);
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

    public static EnergyInput fromJson(JsonObject json) {
        EnergyInput input = new EnergyInput(0, false);
        input.read(json);
        return input.validate() ? input : null;
    }

    @Override
    public IRecipeInput copy() {
        return copy(1);
    }

    @Override
    public IRecipeInput copy(int multiplier) {
        EnergyInput result = new EnergyInput(amount * multiplier, isPerTick());
        result.interval = this.interval;
        result.consume = this.consume;
        return result;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("id", "energy");
        nbt.setInteger("amount", amount);
        nbt.setInteger("interval", interval);
        nbt.setBoolean("consume", consume);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.amount = nbt.getInteger("amount");
        this.interval = nbt.getInteger("interval");
        this.consume = nbt.getBoolean("consume");
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public RecipeTickResult getFailureResult(boolean perTick) {
        return RecipeTickResult.NO_ENERGY;
    }
}
