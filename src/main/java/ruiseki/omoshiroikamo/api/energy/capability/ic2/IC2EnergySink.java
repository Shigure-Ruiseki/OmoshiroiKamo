package ruiseki.omoshiroikamo.api.energy.capability.ic2;

import net.minecraftforge.common.util.ForgeDirection;

import ic2.api.energy.tile.IEnergySink;
import ruiseki.omoshiroikamo.api.energy.capability.EnergyIO;

public class IC2EnergySink implements EnergyIO {

    private final IEnergySink sink;
    private final ForgeDirection side;

    public IC2EnergySink(IEnergySink sink, ForgeDirection side) {
        this.sink = sink;
        this.side = side;
    }

    @Override
    public int insert(int amount, boolean simulate) {
        double euAmount = amount / 4.0;
        double leftover = euAmount;

        if (!simulate) {
            leftover = sink.injectEnergy(side, euAmount, 0);
        }

        return (int) Math.round(leftover * 4.0);
    }

    @Override
    public int extract(int amount, boolean simulate) {
        return 0;
    }
}
