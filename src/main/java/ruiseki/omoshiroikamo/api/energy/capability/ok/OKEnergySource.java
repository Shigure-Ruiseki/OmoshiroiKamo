package ruiseki.omoshiroikamo.api.energy.capability.ok;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.energy.IOKEnergySource;
import ruiseki.omoshiroikamo.api.energy.capability.EnergySource;

public class OKEnergySource implements EnergySource {

    private final IOKEnergySource provider;
    private final ForgeDirection side;

    public OKEnergySource(IOKEnergySource provider, ForgeDirection side) {
        this.provider = provider;
        this.side = side;
    }

    @Override
    public int extract(int amount, boolean simulate) {
        if (!canConnect()) {
            return 0;
        }
        return provider.extractEnergy(side, amount, simulate);
    }

    @Override
    public boolean canConnect() {
        return provider.canConnectEnergy(side);
    }
}
