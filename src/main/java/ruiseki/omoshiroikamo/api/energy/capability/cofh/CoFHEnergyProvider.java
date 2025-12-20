package ruiseki.omoshiroikamo.api.energy.capability.cofh;

import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyProvider;
import ruiseki.omoshiroikamo.api.energy.capability.EnergyIO;

public class CoFHEnergyProvider implements EnergyIO {

    private final IEnergyProvider handler;
    private final ForgeDirection side;

    public CoFHEnergyProvider(IEnergyProvider handler, ForgeDirection side) {
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
    public boolean canConnect() {
        return handler.canConnectEnergy(side);
    }

    @Override
    public int insert(int amount, boolean simulate) {
        return 0;
    }
}
