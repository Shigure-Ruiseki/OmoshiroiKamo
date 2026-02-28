package ruiseki.omoshiroikamo.api.modular.recipe;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.AbstractEnergyIOPortTE;

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
    protected long getRequiredAmount() {
        return amount;
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port instanceof AbstractEnergyIOPortTE;
    }

    @Override
    protected long consume(IModularPort port, long remaining, boolean simulate) {
        AbstractEnergyIOPortTE energyPort = (AbstractEnergyIOPortTE) port;
        int stored = energyPort.getEnergyStored();
        if (stored > 0) {
            int extract = (int) Math.min(stored, remaining);
            if (!simulate) {
                energyPort.extractEnergy(extract);
            }
            return extract;
        }
        return 0;
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
