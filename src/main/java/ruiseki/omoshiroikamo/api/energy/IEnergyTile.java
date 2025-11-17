package ruiseki.omoshiroikamo.api.energy;

import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

public interface IEnergyTile {

    int getEnergyStored();

    int getMaxEnergyStored();

    void setEnergyStored(int stored);

    boolean canConnectEnergy(ForgeDirection var1);

    BlockPos getLocation();
}
