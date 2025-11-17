package ruiseki.omoshiroikamo.api.energy;

import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.Optional;

@Optional.Interface(modid = "CoFHLib", iface = "cofh.api.energy.IEnergyHandler", striprefs = true)
public interface IEnergyIO extends IEnergyHandler, IEnergyTile {

    int receiveEnergy(ForgeDirection side, int amount, boolean simulate);

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
