package ruiseki.omoshiroikamo.core.tileentity;

import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import cofh.api.energy.IEnergyStorage;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.energy.EnergyStorage;
import ruiseki.okcore.energy.IOKEnergyHandler;
import ruiseki.okcore.energy.capability.CapabilityEnergy;
import ruiseki.okcore.persist.nbt.NBTPersist;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.config.general.energy.EnergyConfig;
import ruiseki.omoshiroikamo.core.network.PacketEnergy;

/**
 * Abstract base class for tile entities that store and manage energy.
 * Handles energy storage, synchronization with clients, and basic energy API for
 * interaction with other energy-capable tiles.
 */
public abstract class AbstractEnergyTE extends AbstractTE implements IOKEnergyHandler {

    /** Last known energy stored, used for periodic client synchronization. */
    private int lastSyncPowerStored;

    /** Internal energy storage instance. */
    @NBTPersist
    protected EnergyStorage energyStorage;
    protected LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> energyStorage);

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
            OmoshiroiKamo.instance.getPacketHandler()
                .sendToAllAround(new PacketEnergy(this), this);
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
        return energyStorage == null ? 0 : energyStorage.getEnergyStored();
    }

    @Override
    public int receiveEnergy(ForgeDirection side, int amount, boolean simulate) {
        return energyStorage == null ? 0 : energyStorage.receiveEnergy(amount, simulate);
    }

    @Override
    public int extractEnergy(ForgeDirection side, int amount, boolean simulate) {
        return energyStorage == null ? 0 : energyStorage.extractEnergy(amount, simulate);
    }

    @Override
    public void doUpdate() {
        if (worldObj.isRemote) {
            super.doUpdate();
            return;
        }

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
        energyCap.invalidate();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability,
        @Nullable ForgeDirection facing) {
        if (capability == CapabilityEnergy.ENERGY || capability == CapabilityEnergy.ENERGY_SINK_CAPABILITY
            || capability == CapabilityEnergy.ENERGY_SOURCE_CAPABILITY) {
            return energyCap.cast();
        }
        return super.getCapability(capability, facing);
    }
}
