package ruiseki.omoshiroikamo.api.energy;

import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.Optional;

/**
 * Interface for tile entities that can both receive and provide energy.
 * IC2 integration is handled separately by IC2EnergyAdapter.
 */
@Optional.Interface(iface = "cofh.api.energy.IEnergyHandler", modid = "CoFHLib")
public interface IOKEnergyIO extends IEnergyHandler, IOKEnergySink, IOKEnergySource {

    @Override
    int receiveEnergy(ForgeDirection side, int amount, boolean simulate);

    @Override
    int extractEnergy(ForgeDirection side, int amount, boolean simulate);

    @Override
    @Optional.Method(modid = "CoFHLib")
    default int getEnergyStored(ForgeDirection forgeDirection) {
        return getEnergyStored();
    }

    @Override
    @Optional.Method(modid = "CoFHLib")
    default int getMaxEnergyStored(ForgeDirection forgeDirection) {
        return getMaxEnergyStored();
    }
}
