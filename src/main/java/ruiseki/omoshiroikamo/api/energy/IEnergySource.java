package ruiseki.omoshiroikamo.api.energy;

import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyProvider;
import cpw.mods.fml.common.Optional;

/**
 * Interface for tile entities that can provide/extract energy.
 * IC2 integration is handled separately by IC2EnergyAdapter.
 */
@Optional.Interface(iface = "cofh.api.energy.IEnergyProvider", modid = "CoFHLib")
public interface IEnergySource extends IEnergyProvider, IEnergyTile {

    int extractEnergy(ForgeDirection side, int amount, boolean simulate);

    @Override
    @Optional.Method(modid = "CoFHLib")
    default int getEnergyStored(ForgeDirection side) {
        return getEnergyStored();
    }

    @Override
    @Optional.Method(modid = "CoFHLib")
    default int getMaxEnergyStored(ForgeDirection side) {
        return getMaxEnergyStored();
    }
}
