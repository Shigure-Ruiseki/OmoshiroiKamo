package ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.energy.interfacebus;

import ruiseki.okcore.energy.capability.IEnergySink;

public class InterfaceEnergySink implements IEnergySink {

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
