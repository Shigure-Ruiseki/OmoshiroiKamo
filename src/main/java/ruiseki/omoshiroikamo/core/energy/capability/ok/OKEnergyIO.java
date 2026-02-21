package ruiseki.omoshiroikamo.core.energy.capability.ok;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.core.energy.IOKEnergyIO;
import ruiseki.omoshiroikamo.core.energy.capability.EnergyIO;

public class OKEnergyIO implements EnergyIO {

    private final IOKEnergyIO handler;
    private final ForgeDirection side;

    public OKEnergyIO(IOKEnergyIO handler, ForgeDirection side) {
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
