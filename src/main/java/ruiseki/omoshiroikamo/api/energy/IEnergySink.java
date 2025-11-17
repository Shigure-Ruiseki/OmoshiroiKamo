package ruiseki.omoshiroikamo.api.energy;

import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.common.Optional;

@Optional.Interface(modid = "CoFHLib", iface = "cofh.api.energy.IEnergyReceiver", striprefs = true)
public interface IEnergySink extends IEnergyReceiver, IEnergyTile {

    @Override
    default int getEnergyStored(ForgeDirection forgeDirection) {
        return getEnergyStored();
    }

    @Override
    default int getMaxEnergyStored(ForgeDirection forgeDirection) {
        return getMaxEnergyStored();
    }

}
