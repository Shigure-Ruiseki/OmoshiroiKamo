package ruiseki.omoshiroikamo.api.energy.capability.cofh;

import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyReceiver;
import ruiseki.omoshiroikamo.api.energy.capability.EnergyIO;

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
    public int insert(int amount, boolean simulate) {
        return handler.receiveEnergy(side, amount, simulate);
    }
}
