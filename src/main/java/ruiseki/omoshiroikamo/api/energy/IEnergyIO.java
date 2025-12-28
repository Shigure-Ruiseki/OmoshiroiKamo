package ruiseki.omoshiroikamo.api.energy;

import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.Optional;
import ruiseki.omoshiroikamo.config.general.energy.EnergyConfig;

@Optional.InterfaceList({ @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "IC2"),
    @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySource", modid = "IC2"),
    @Optional.Interface(iface = "cofh.api.energy.IEnergyHandler", modid = "CoFHLib") })
public interface IEnergyIO extends IEnergyHandler, IEnergyTile, IEnergySink, IEnergySource {

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

    @Override
    @Optional.Method(modid = "IC2")
    default int getSourceTier() {
        return EnergyConfig.ic2SourceTier;
    }

    @Override
    @Optional.Method(modid = "IC2")
    default double getOfferedEnergy() {
        return getEnergyStored() * EnergyConfig.rftToEU;
    }

    @Override
    @Optional.Method(modid = "IC2")
    default void drawEnergy(double amount) {
        int rf = (int) (amount / EnergyConfig.rftToEU);
        extractEnergy(ForgeDirection.UNKNOWN, rf, false);
    }
}
