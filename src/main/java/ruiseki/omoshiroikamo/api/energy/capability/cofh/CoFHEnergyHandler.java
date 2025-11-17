package ruiseki.omoshiroikamo.api.energy.capability.cofh;

import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyHandler;
import ruiseki.omoshiroikamo.api.energy.capability.EnergyIO;

public class CoFHEnergyHandler implements EnergyIO {

    private final IEnergyHandler handler;
    private final ForgeDirection side;

    public CoFHEnergyHandler(IEnergyHandler handler, ForgeDirection side) {
        this.handler = handler;
        this.side = side;
    }

    @Override
    public int extract(int amount, boolean simulate) {
        return handler.extractEnergy(side, amount, simulate);
    }

    @Override
    public int insert(int amount, boolean simulate) {
        return handler.receiveEnergy(side, amount, simulate);
    }
}
