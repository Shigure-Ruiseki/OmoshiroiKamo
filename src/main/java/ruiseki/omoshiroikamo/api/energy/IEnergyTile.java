package ruiseki.omoshiroikamo.api.energy;

import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.Optional;
import ic2.api.tile.IEnergyStorage;
import ruiseki.omoshiroikamo.api.block.IOKTile;
import ruiseki.omoshiroikamo.config.general.energy.EnergyConfig;

@Optional.InterfaceList({ @Optional.Interface(iface = "ic2.api.energy.tile.IEnergyStorage", modid = "IC2"),
    @Optional.Interface(iface = "ic2.api.energy.tile.IEnergyTile", modid = "IC2") })
public interface IEnergyTile extends IOKTile, IEnergyStorage, ic2.api.energy.tile.IEnergyTile {

    int getEnergyStored();

    int getMaxEnergyStored();

    void setEnergyStored(int stored);

    int getEnergyTransfer();

    boolean canConnectEnergy(ForgeDirection var1);

    @Optional.Method(modid = "IC2")
    void register();

    @Optional.Method(modid = "IC2")
    void deregister();

    @Override
    @Optional.Method(modid = "IC2")
    default int getStored() {
        return (int) (getEnergyStored() * EnergyConfig.rftToEU);
    }

    @Override
    @Optional.Method(modid = "IC2")
    default void setStored(int eu) {
        setEnergyStored((int) (eu / EnergyConfig.rftToEU));
    }

    @Override
    @Optional.Method(modid = "IC2")
    default int addEnergy(int eu) {
        int before = getEnergyStored();
        int addedRF = (int) (eu / EnergyConfig.rftToEU);
        setEnergyStored(before + addedRF);
        return (int) ((getEnergyStored() - before) * EnergyConfig.rftToEU);
    }

    @Override
    @Optional.Method(modid = "IC2")
    default int getCapacity() {
        return (int) (getMaxEnergyStored() * EnergyConfig.rftToEU);
    }

    @Override
    @Optional.Method(modid = "IC2")
    default int getOutput() {
        // IC2 wants max EU/tick output
        return (int) (getMaxEnergyStored() * EnergyConfig.rftToEU);
    }

    @Override
    @Optional.Method(modid = "IC2")
    default double getOutputEnergyUnitsPerTick() {
        return getOutput();
    }

    @Override
    @Optional.Method(modid = "IC2")
    default boolean isTeleporterCompatible(ForgeDirection side) {
        return canConnectEnergy(side);
    }
}
