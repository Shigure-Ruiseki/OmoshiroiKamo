package ruiseki.omoshiroikamo.api.energy.capability.ic2;

import net.minecraftforge.common.util.ForgeDirection;

import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ruiseki.omoshiroikamo.api.energy.capability.EnergyIO;

public class IC2EnergyIO implements EnergyIO {

    private final Object tile;
    private final ForgeDirection side;

    public IC2EnergyIO(Object tile, ForgeDirection side) {
        this.tile = tile;
        this.side = side;
    }

    public int insert(int amount, boolean simulate) {
        if (tile instanceof IEnergySink sink) {
            double eu = amount / 4.0;
            double leftover = simulate ? 0 : sink.injectEnergy(side, eu, 0);
            return (int) Math.round(leftover * 4.0);
        }
        return 0;
    }

    @Override
    public int extract(int amount, boolean simulate) {
        if (tile instanceof IEnergySource source) {
            double euOffered = source.getOfferedEnergy();
            int rf = (int) Math.round(euOffered * 4.0);
            return Math.min(rf, amount);
        }
        return 0;
    }
}
