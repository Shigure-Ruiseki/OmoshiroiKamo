package ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.energy.interfacebus;

import ruiseki.omoshiroikamo.core.energy.capability.EnergySource;

public class InterfaceEnergySource implements EnergySource {

    private final IEnergyInterface iFace;

    public InterfaceEnergySource(IEnergyInterface iFace) {
        this.iFace = iFace;
    }

    @Override
    public int extract(int amount, boolean simulate) {
        if (!iFace.canConnect()) return 0;
        return iFace.extract(amount, simulate);
    }

    @Override
    public boolean canConnect() {
        return iFace.canConnect();
    }
}
