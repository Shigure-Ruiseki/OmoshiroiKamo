package ruiseki.omoshiroikamo.api.energy;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.block.IOKTile;

public interface IEnergyTile extends IOKTile {

    int getEnergyStored();

    int getMaxEnergyStored();

    void setEnergyStored(int stored);

    boolean canConnectEnergy(ForgeDirection var1);
}
