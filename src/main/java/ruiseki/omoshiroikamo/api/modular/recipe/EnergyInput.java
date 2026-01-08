package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.AbstractEnergyIOPortTE;

/**
 * Recipe input requirement for energy.
 * perTick=true: Energy consumed every tick during processing.
 * perTick=false: Energy consumed once at recipe start.
 */
public class EnergyInput implements IRecipeInput {

    private final int amount;
    private final boolean perTick;

    public EnergyInput(int amount, boolean perTick) {
        this.amount = amount;
        this.perTick = perTick;
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
    public boolean process(List<IModularPort> ports, boolean simulate) {
        int remaining = amount;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.ENERGY) continue;
            if (port.getPortDirection() != IPortType.Direction.INPUT) continue;
            if (!(port instanceof AbstractEnergyIOPortTE)) {
                throw new IllegalStateException(
                    "ENERGY INPUT port must be AbstractEnergyIOPortTE, got: " + port.getClass()
                        .getName());
            }
            AbstractEnergyIOPortTE energyPort = (AbstractEnergyIOPortTE) port;

            int stored = energyPort.getEnergyStored();
            if (stored > 0) {
                int extract = Math.min(stored, remaining);
                if (!simulate) {
                    energyPort.extractEnergy(extract);
                }
                remaining -= extract;
            }
            if (remaining <= 0) break;
        }

        return remaining <= 0;
    }

    /**
     * Create EnergyInput from JSON.
     */
    public static EnergyInput fromJson(JsonObject json) {
        int amount = json.get("energy")
            .getAsInt();
        boolean perTick = json.has("perTick") && json.get("perTick")
            .getAsBoolean();
        return new EnergyInput(amount, perTick);
    }
}
