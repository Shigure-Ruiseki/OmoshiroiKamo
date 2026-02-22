package ruiseki.omoshiroikamo.core.energy;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.core.tileentity.ITile;

/**
 * Base interface for tile entities that store and manage energy.
 * IC2 integration is handled separately by IC2EnergyAdapter.
 */
public interface IOKEnergyTile extends ITile {

    int getEnergyStored();

    int getMaxEnergyStored();

    void setEnergyStored(int stored);

    int getEnergyTransfer();

    boolean canConnectEnergy(ForgeDirection var1);

    default void register() {}

    default void deregister() {}
}
