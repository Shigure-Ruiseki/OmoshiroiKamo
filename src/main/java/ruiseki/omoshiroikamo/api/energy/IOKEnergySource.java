package ruiseki.omoshiroikamo.api.energy;

import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyProvider;

/**
 * Interface for tile entities that can provide/extract energy.
 * IC2 integration is handled separately by IC2EnergyAdapter.
 */
public interface IOKEnergySource extends IEnergyProvider, IOKEnergyTile {

    int extractEnergy(ForgeDirection side, int amount, boolean simulate);

    @Override
    default int getEnergyStored(ForgeDirection side) {
        return getEnergyStored();
    }

    @Override
    default int getMaxEnergyStored(ForgeDirection side) {
        return getMaxEnergyStored();
    }
}
