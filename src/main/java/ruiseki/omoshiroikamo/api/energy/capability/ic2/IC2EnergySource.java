package ruiseki.omoshiroikamo.api.energy.capability.ic2;

import net.minecraftforge.common.util.ForgeDirection;

import ic2.api.energy.tile.IEnergySource;
import ruiseki.omoshiroikamo.api.energy.capability.EnergyIO;

public class IC2EnergySource implements EnergyIO {

    private final IEnergySource source;
    private final ForgeDirection side;

    public IC2EnergySource(IEnergySource source, ForgeDirection side) {
        this.source = source;
        this.side = side;
    }

    @Override
    public int extract(int amount, boolean simulate) {
        double offeredEU = source.getOfferedEnergy();
        int offeredRF = (int) Math.round(offeredEU * 4.0);

        int toExtract = Math.min(offeredRF, amount);

        if (!simulate) {
            double drawEU = toExtract / 4.0;
            source.drawEnergy(drawEU);
        }

        return toExtract;
    }

    @Override
    public int insert(int amount, boolean simulate) {
        return 0;
    }
}
