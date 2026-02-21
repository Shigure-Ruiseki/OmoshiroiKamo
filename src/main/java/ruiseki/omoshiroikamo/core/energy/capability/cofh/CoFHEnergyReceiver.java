package ruiseki.omoshiroikamo.core.energy.capability.cofh;

import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyReceiver;
import ruiseki.omoshiroikamo.core.energy.capability.EnergyIO;

public class CoFHEnergyReceiver implements EnergyIO {

    private final IEnergyReceiver handler;
    private final ForgeDirection side;

    public CoFHEnergyReceiver(IEnergyReceiver handler, ForgeDirection side) {
        this.handler = handler;
        this.side = side;
    }

    @Override
    public int extract(int amount, boolean simulate) {
        return 0;
    }

    @Override
    public boolean canConnect() {
        return handler.canConnectEnergy(side);
    }

    @Override
    public int insert(int amount, boolean simulate) {
        if (!canConnect()) {
            return 0;
        }
        return handler.receiveEnergy(side, amount, simulate);
    }
}
