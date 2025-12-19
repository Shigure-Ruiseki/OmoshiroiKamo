package ruiseki.omoshiroikamo.api.energy.capability.ic2;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import ic2.api.energy.tile.IEnergySource;
import ruiseki.omoshiroikamo.api.energy.capability.EnergyIO;

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

    @Override
    public boolean canConnect() {
        return true;
    }
}
