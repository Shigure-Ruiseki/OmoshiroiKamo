package ruiseki.omoshiroikamo.module.ids.common.cableNet.part.tunnel.energy.interfacebus;

import ruiseki.omoshiroikamo.core.energy.capability.EnergySink;

public class InterfaceEnergySink implements EnergySink {

    private final IEnergyInterface iFace;

    public InterfaceEnergySink(IEnergyInterface iFace) {
        this.iFace = iFace;
    }

    @Override
    public int insert(int amount, boolean simulate) {
        if (!iFace.canConnect()) return 0;
        return iFace.insert(amount, simulate);
    }

    @Override
    public boolean canConnect() {
        return iFace.canConnect();
    }
}
