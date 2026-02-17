package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.AbstractEnergyIOPortTE;

public class EnergyOutput extends AbstractRecipeOutput {

    private final int amount;
    private final boolean perTick;

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
    public boolean process(List<IModularPort> ports, boolean simulate) {

        long remaining = amount;

        for (IModularPort port : ports) {
            // Check compatibility
            if (port.getPortType() != IPortType.Type.ENERGY) continue;
            // Allow OUTPUT and BOTH
            if (port.getPortDirection() != IPortType.Direction.OUTPUT
                && port.getPortDirection() != IPortType.Direction.BOTH) continue;

            if (!(port instanceof AbstractEnergyIOPortTE)) continue;

            AbstractEnergyIOPortTE energyPort = (AbstractEnergyIOPortTE) port;

            int accepted = energyPort.internalReceiveEnergy((int) remaining, simulate);
            remaining -= accepted;

            if (remaining <= 0) break;
        }

        return remaining <= 0;
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

    public static EnergyOutput fromJson(JsonObject json) {
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
        return new EnergyOutput(amount, perTick);
    }
}
