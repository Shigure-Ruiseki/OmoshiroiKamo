package ruiseki.omoshiroikamo.api.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyProvider;
import cpw.mods.fml.common.Optional;
import ruiseki.omoshiroikamo.config.general.energy.EnergyConfig;

@Optional.InterfaceList({
    @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySource", modid = "IC2"),
    @Optional.Interface(iface = "cofh.api.energy.IEnergyProvider", modid = "CoFHLib")
})
public interface IEnergySource extends IEnergyProvider, IEnergyTile, ic2.api.energy.tile.IEnergySource {

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

    @Override
    @Optional.Method(modid = "IC2")
    default boolean emitsEnergyTo(TileEntity tileEntity, ForgeDirection forgeDirection) {
        return canConnectEnergy(forgeDirection);
    }
}
