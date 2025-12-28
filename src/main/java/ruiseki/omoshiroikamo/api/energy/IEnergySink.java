package ruiseki.omoshiroikamo.api.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.common.Optional;
import ruiseki.omoshiroikamo.config.general.energy.EnergyConfig;

@Optional.InterfaceList({ @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "IC2"),
    @Optional.Interface(iface = "cofh.api.energy.IEnergyReceiver", modid = "CoFHLib") })
public interface IEnergySink extends IEnergyReceiver, IEnergyTile, ic2.api.energy.tile.IEnergySink {

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

    @Override
    @Optional.Method(modid = "IC2")
    default double getDemandedEnergy() {
        int missing = getMaxEnergyStored() - getEnergyStored();
        return Math.max(0, missing * EnergyConfig.rftToEU);
    }

    @Override
    @Optional.Method(modid = "IC2")
    default int getSinkTier() {
        return EnergyConfig.ic2SinkTier;
    }

    @Override
    @Optional.Method(modid = "IC2")
    default double injectEnergy(ForgeDirection direction, double amount, double voltage) {
        int rf = (int) (amount * EnergyConfig.rftToEU);
        int accepted = receiveEnergy(direction, rf, false);
        return amount - ((double) accepted / EnergyConfig.rftToEU);
    }

    @Override
    @Optional.Method(modid = "IC2")
    default boolean acceptsEnergyFrom(TileEntity tileEntity, ForgeDirection forgeDirection) {
        return canConnectEnergy(forgeDirection);
    }
}
