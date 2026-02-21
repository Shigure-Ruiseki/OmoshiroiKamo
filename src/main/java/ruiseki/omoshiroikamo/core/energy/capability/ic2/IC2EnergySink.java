package ruiseki.omoshiroikamo.core.energy.capability.ic2;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import ic2.api.energy.tile.IEnergySink;
import ruiseki.omoshiroikamo.config.general.energy.EnergyConfig;
import ruiseki.omoshiroikamo.core.energy.capability.EnergyIO;

public class IC2EnergySink implements EnergyIO {

    private final TileEntity tile;
    private final ForgeDirection side;

    public IC2EnergySink(TileEntity tile, ForgeDirection side) {
        this.tile = tile;
        this.side = side;
    }

    @Override
    public int insert(int amount, boolean simulate) {
        if (!(tile instanceof IEnergySink sink)) {
            return 0;
        }
        double euAmount = (double) amount / EnergyConfig.rftToEU;
        double leftover = euAmount;

        if (!simulate) {
            leftover = sink.injectEnergy(side, euAmount, 0);
        }

        return (int) Math.round(leftover * EnergyConfig.rftToEU);
    }

    @Override
    public boolean canConnect() {
        if (!(tile instanceof IEnergySink sink)) {
            return false;
        }
        return sink.acceptsEnergyFrom(tile, side);
    }

    @Override
    public int extract(int amount, boolean simulate) {
        return 0;
    }
}
