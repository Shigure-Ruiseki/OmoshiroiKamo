package ruiseki.omoshiroikamo.api.energy;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

public interface IPowerContainer {

    int getEnergyStored();

    void setEnergyStored(int storedEnergy);

    int getMaxEnergyStored();

    BlockPos getLocation();
}
