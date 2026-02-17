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

    private final int amount;
    private final boolean perTick;

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

    public static EnergyInput fromJson(JsonObject json) {
        int amount = json.get("energy")
            .getAsInt();
        boolean perTick = true;
        if (json.has("perTick")) {
            perTick = json.get("perTick")
                .getAsBoolean();
        } else if (json.has("pertick")) {
            perTick = json.get("pertick")
                .getAsBoolean();
        }
        return new EnergyInput(amount, perTick);
    }
}
