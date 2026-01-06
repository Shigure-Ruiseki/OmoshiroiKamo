package ruiseki.omoshiroikamo.api.energy;

import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.common.Optional;

/**
 * Interface for tile entities that can receive energy.
 * IC2 integration is handled separately by IC2EnergyAdapter.
 */
@Optional.Interface(iface = "cofh.api.energy.IEnergyReceiver", modid = "CoFHLib")
public interface IOKEnergySink extends IEnergyReceiver, IOKEnergyTile {

    int receiveEnergy(ForgeDirection side, int amount, boolean simulate);

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
