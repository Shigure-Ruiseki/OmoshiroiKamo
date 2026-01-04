package ruiseki.omoshiroikamo.api.energy;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.block.IOKTile;

/**
 * Base interface for tile entities that store and manage energy.
 * IC2 integration is handled separately by IC2EnergyAdapter.
 */
public interface IOKEnergyTile extends IOKTile {

    int getEnergyStored();

    int getMaxEnergyStored();

    void setEnergyStored(int stored);

    int getEnergyTransfer();

    boolean canConnectEnergy(ForgeDirection var1);

    void register();

    void deregister();
}
