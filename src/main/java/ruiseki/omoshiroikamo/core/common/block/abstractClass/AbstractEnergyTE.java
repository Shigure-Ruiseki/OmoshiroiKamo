package ruiseki.omoshiroikamo.core.common.block.abstractClass;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.energy.EnergyStorage;
import ruiseki.omoshiroikamo.api.energy.IEnergyTile;
import ruiseki.omoshiroikamo.core.common.network.PacketEnergy;
import ruiseki.omoshiroikamo.core.common.network.PacketHandler;

/**
 * Abstract base class for tile entities that store and manage energy.
 * <p>
 * Handles energy storage, synchronization with clients, and basic energy API for
 * interaction with other energy-capable tiles.
 */
public abstract class AbstractEnergyTE extends AbstractTE implements IEnergyTile {

    /** Last known energy stored, used for periodic client synchronization. */
    private int lastSyncPowerStored;

    /** Internal energy storage instance. */
    protected EnergyStorage energyStorage;

    /** NBT tag name for energy-related data. */
    public static String ENERGY_TAG = "energy";

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
            PacketHandler.sendToAllAround(new PacketEnergy(this), this);
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
        return energyStorage.getEnergyStored();
    }

    @Override
    public void setEnergyStored(int storedEnergy) {
        energyStorage.setEnergyStored(storedEnergy);
    }

    @Override
    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);
        energyStorage.writeToNBT(root, ENERGY_TAG);
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);
        energyStorage.readFromNBT(root, ENERGY_TAG);
    }
}
