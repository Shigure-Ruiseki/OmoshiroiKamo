package ruiseki.omoshiroikamo.core.common.block.abstractClass;

import static ruiseki.omoshiroikamo.CommonProxy.NETWORK;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ruiseki.omoshiroikamo.api.capabilities.Capability;
import ruiseki.omoshiroikamo.api.energy.EnergyStorage;
import ruiseki.omoshiroikamo.api.energy.IOKEnergyTile;
import ruiseki.omoshiroikamo.api.persist.nbt.NBTPersist;
import ruiseki.omoshiroikamo.config.general.energy.EnergyConfig;
import ruiseki.omoshiroikamo.core.common.capabilities.CapabilityEnergy;
import ruiseki.omoshiroikamo.core.common.network.PacketEnergy;

/**
 * Abstract base class for tile entities that store and manage energy.
 * Handles energy storage, synchronization with clients, and basic energy API for
 * interaction with other energy-capable tiles.
 * IC2 integration is handled via @Optional.Interface annotations, which allow
 * the class to implement IC2 interfaces only when IC2 is present at runtime.
 */
@Optional.InterfaceList({ @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "IC2"),
    @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySource", modid = "IC2") })
public abstract class AbstractEnergyTE extends AbstractTE implements IOKEnergyTile, IEnergySink, IEnergySource {

    /** Last known energy stored, used for periodic client synchronization. */
    private int lastSyncPowerStored;

    /** Internal energy storage instance. */
    @NBTPersist
    protected EnergyStorage energyStorage;

    /** Flag indicating if IC2 registration was completed. */
    public boolean ic2Registered = false;

    /**
     * Constructor specifying capacity and max receive rate.
     *
     * @param energyCapacity   maximum energy the tile can store
     * @param energyMaxReceive maximum energy the tile can receive per tick
     */
    public AbstractEnergyTE(int energyCapacity, int energyMaxReceive) {
        energyStorage = new EnergyStorage(energyCapacity, energyMaxReceive) {

            @Override
            public void onEnergyChanged() {
                super.onEnergyChanged();
                markDirty();
            }
        };
    }

    /**
     * Constructor specifying only capacity; max receive defaults to capacity.
     *
     * @param energyCapacity maximum energy the tile can store
     */
    public AbstractEnergyTE(int energyCapacity) {
        this(energyCapacity, energyCapacity);
    }

    /** Default constructor with default capacity 100,000 and max receive 100,000. */
    public AbstractEnergyTE() {
        this(100000, 100000);
    }

    /**
     * Processes energy-related tasks each tick.
     * Sends energy synchronization packets to clients periodically when stored energy changes.
     *
     * @param redstoneCheckPassed true if redstone conditions allow operation
     * @return false (energy processing does not affect base processing result)
     */
    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {
        boolean powerChanged = (lastSyncPowerStored != getEnergyStored() && shouldDoWorkThisTick(5));
        if (powerChanged) {
            lastSyncPowerStored = getEnergyStored();
            NETWORK.sendToAllAround(new PacketEnergy(this), this);
        }

        return false;
    }

    /**
     * Determines whether energy can be accepted from the given direction.
     *
     * @param from the side of the block attempting to connect
     * @return true if connection is allowed
     */
    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    @Override
    public int getEnergyStored() {
        return energyStorage == null ? 0 : energyStorage.getEnergyStored();
    }

    @Override
    public void setEnergyStored(int storedEnergy) {
        if (energyStorage == null) {
            return;
        }
        energyStorage.setEnergyStored(storedEnergy);
    }

    @Override
    public int getMaxEnergyStored() {
        return energyStorage == null ? 0 : energyStorage.getMaxEnergyStored();
    }

    @Override
    public int getEnergyTransfer() {
        return energyStorage == null ? 0 : energyStorage.getMaxReceive();
    }

    @Override
    public void register() {
        if (!worldObj.isRemote && Loader.isModLoaded("IC2") && EnergyConfig.ic2Capability) {
            registerIC2();
        }
    }

    @Override
    public void deregister() {
        if (!worldObj.isRemote && Loader.isModLoaded("IC2") && ic2Registered) {
            deregisterIC2();
        }
    }

    @Optional.Method(modid = "IC2")
    private void registerIC2() {
        if (ic2Registered) return;
        MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
        ic2Registered = true;
    }

    @Optional.Method(modid = "IC2")
    private void deregisterIC2() {
        MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
        ic2Registered = false;
    }

    @Override
    public void doUpdate() {
        if (!ic2Registered && EnergyConfig.ic2Capability) {
            register();
        }
        super.doUpdate();
    }

    @Override
    public void onChunkUnload() {
        if (EnergyConfig.ic2Capability) {
            deregister();
        }
        super.onChunkUnload();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (EnergyConfig.ic2Capability) {
            deregister();
        }
    }

    @Override
    @Optional.Method(modid = "IC2")
    public double getDemandedEnergy() {
        int missing = getMaxEnergyStored() - getEnergyStored();
        return Math.max(0, (double) missing / EnergyConfig.rftToEU);
    }

    @Override
    @Optional.Method(modid = "IC2")
    public int getSinkTier() {
        return EnergyConfig.ic2SinkTier;
    }

    @Override
    @Optional.Method(modid = "IC2")
    public double injectEnergy(ForgeDirection direction, double amount, double voltage) {
        int rf = (int) (amount * EnergyConfig.rftToEU);
        int accepted = energyStorage.receiveEnergy(rf, false);
        return amount - ((double) accepted / EnergyConfig.rftToEU);
    }

    @Override
    @Optional.Method(modid = "IC2")
    public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
        return canConnectEnergy(direction);
    }

    @Override
    @Optional.Method(modid = "IC2")
    public double getOfferedEnergy() {
        return (double) getEnergyStored() / EnergyConfig.rftToEU;
    }

    @Override
    @Optional.Method(modid = "IC2")
    public void drawEnergy(double amount) {
        int rf = (int) (amount * EnergyConfig.rftToEU);
        energyStorage.extractEnergy(rf, false);
    }

    @Override
    @Optional.Method(modid = "IC2")
    public int getSourceTier() {
        return EnergyConfig.ic2SourceTier;
    }

    @Override
    @Optional.Method(modid = "IC2")
    public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
        return canConnectEnergy(direction);
    }

    @Override
    public boolean hasCapability(@NotNull Capability<?> capability, @Nullable ForgeDirection facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@NotNull Capability<T> capability, @Nullable ForgeDirection facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return (T) energyStorage;
        }
        return super.getCapability(capability, facing);
    }
}
