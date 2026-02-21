package ruiseki.omoshiroikamo.core.energy.capability.ic2;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import ic2.api.energy.tile.IEnergySource;
import ruiseki.omoshiroikamo.config.general.energy.EnergyConfig;
import ruiseki.omoshiroikamo.core.energy.capability.EnergyIO;

public class IC2EnergySource implements EnergyIO {

    private final TileEntity tile;
    private final ForgeDirection side;

    public IC2EnergySource(TileEntity tile, ForgeDirection side) {
        this.tile = tile;
        this.side = side;
    }

    @Override
    public int extract(int amount, boolean simulate) {
        if (!(tile instanceof IEnergySource source)) {
            return 0;
        }
        if (!canConnect()) {
            return 0;
        }
        double offeredEU = source.getOfferedEnergy();
        int offeredRF = (int) Math.round(offeredEU * EnergyConfig.rftToEU);

        int toExtract = Math.min(offeredRF, amount);

        if (!simulate) {
            double drawEU = (double) toExtract / EnergyConfig.rftToEU;
            source.drawEnergy(drawEU);
        }

        return toExtract;
    }

    @Override
    public int insert(int amount, boolean simulate) {
        return 0;
    }

    @Override
    public boolean canConnect() {
        return true;
    }
}
