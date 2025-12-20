package ruiseki.omoshiroikamo.api.energy.capability.ok;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.energy.IEnergyIO;
import ruiseki.omoshiroikamo.api.energy.capability.EnergyIO;

public class OKEnergyIO implements EnergyIO {

    private final IEnergyIO handler;
    private final ForgeDirection side;

    public OKEnergyIO(IEnergyIO handler, ForgeDirection side) {
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
