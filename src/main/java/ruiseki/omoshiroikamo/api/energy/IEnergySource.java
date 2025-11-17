package ruiseki.omoshiroikamo.api.energy;

import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyProvider;
import cpw.mods.fml.common.Optional;

@Optional.Interface(modid = "CoFHLib", iface = "cofh.api.energy.IEnergyProvider", striprefs = true)
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
