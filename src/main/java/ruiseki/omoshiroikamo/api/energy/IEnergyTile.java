package ruiseki.omoshiroikamo.api.energy;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.block.BlockPos;

public interface IEnergyTile {

    int getEnergyStored();

    int getMaxEnergyStored();

    void setEnergyStored(int stored);

    boolean canConnectEnergy(ForgeDirection var1);

    BlockPos getLocation();
}
