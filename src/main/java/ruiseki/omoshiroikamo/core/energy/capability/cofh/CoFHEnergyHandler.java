package ruiseki.omoshiroikamo.core.energy.capability.cofh;

import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyHandler;
import ruiseki.omoshiroikamo.core.energy.capability.EnergyIO;

public class CoFHEnergyHandler implements EnergyIO {

    private final IEnergyHandler handler;
    private final ForgeDirection side;

    public CoFHEnergyHandler(IEnergyHandler handler, ForgeDirection side) {
        this.handler = handler;
        this.side = side;
    }

    @Override
    public int extract(int amount, boolean simulate) {
        if (!canConnect()) {
            return 0;
        }
        return handler.extractEnergy(side, amount, simulate);
    }

    @Override
    public int insert(int amount, boolean simulate) {
        if (!canConnect()) {
            return 0;
        }
        return handler.receiveEnergy(side, amount, simulate);
    }

    @Override
    public boolean canConnect() {
        return handler.canConnectEnergy(side);
    }
}
