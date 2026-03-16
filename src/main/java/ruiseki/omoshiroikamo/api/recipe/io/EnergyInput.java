package ruiseki.omoshiroikamo.api.recipe.io;

import net.minecraftforge.common.util.ForgeDirection;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.core.energy.IOKEnergySource;

/**
 * perTick=true: Energy consumed every tick during processing.
 * perTick=false: Energy consumed once at recipe start.
 */
public class EnergyInput extends AbstractRecipeInput {

    private int amount;
    private boolean perTick;

    public EnergyInput(int amount, boolean perTick) {
        this.amount = amount;
        this.perTick = perTick;
    }

    public EnergyInput(int amount) {
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
        if (json.has("consume")) {
            this.consume = json.get("consume")
                .getAsBoolean();
        }

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
        if (!consume) json.addProperty("consume", false);
        json.addProperty("energy", amount);
        json.addProperty("perTick", perTick);
    }

    @Override
    public boolean validate() {
        return amount > 0;
    }

    public static EnergyInput fromJson(JsonObject json) {
        EnergyInput input = new EnergyInput(0, true);
        input.read(json);
        return input.validate() ? input : null;
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }
}
