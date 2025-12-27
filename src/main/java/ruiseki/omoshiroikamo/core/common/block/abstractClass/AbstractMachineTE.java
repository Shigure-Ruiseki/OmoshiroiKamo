package ruiseki.omoshiroikamo.core.common.block.abstractClass;

import net.minecraft.nbt.NBTTagCompound;

import lombok.Getter;
import ruiseki.omoshiroikamo.api.crafting.CraftingState;
import ruiseki.omoshiroikamo.api.crafting.ICraftingTile;
import ruiseki.omoshiroikamo.core.common.block.state.BlockStateUtils;
import ruiseki.omoshiroikamo.core.common.network.PacketCraftingState;
import ruiseki.omoshiroikamo.core.common.network.PacketHandler;

/**
 * Abstract base class for machines that consume energy to perform crafting tasks over time.
 * <p>
 * The crafting lifecycle is as follows:
 *
 * <pre>
 * IDLE
 * │
 * │ (canStartCrafting)
 * ▼
 * CRAFTING
 * │ per tick:
 * │ - onCrafting()
 * │ - consume energy (optional)
 * │ - advance crafting progress
 * │
 * │ (progress >= duration)
 * ▼
 * FINISH
 * │
 * ▼
 * RESET → IDLE
 * </pre>
 *
 * This class handles energy consumption, progress tracking, state synchronization with clients,
 * and saving/loading crafting state to NBT.
 */
public abstract class AbstractMachineTE extends AbstractEnergyTE implements ICraftingTile {

    /** Current crafting state (IDLE, CRAFTING, FINISH, etc.). */
    @Getter
    private CraftingState craftingState = CraftingState.IDLE;

    /**
     * Whether the machine is currently crafting.
     * -- GETTER --
     * Returns true if currently crafting.
     */
    @Getter
    protected boolean crafting = false;

    /** Current progress in ticks of the ongoing crafting operation. */
    protected int craftingProgress = 0;

    /** Total duration of the current crafting operation in ticks. */
    private int currentCraftingDuration = 0;

    /** NBT tag name for crafting-related data. */
    public static String CRAFTING_TAG = "crafting";

    /** Default constructor. */
    public AbstractMachineTE() {
        super();
    }

    /**
     * Constructor with custom energy capacity.
     *
     * @param energyCapacity maximum energy storage for this machine
     */
    public AbstractMachineTE(int energyCapacity) {
        super(energyCapacity);
    }

    /**
     * Constructor with custom energy capacity and max energy receive rate.
     *
     * @param energyCapacity   maximum energy storage for this machine
     * @param energyMaxReceive maximum energy per tick that can be received
     */
    public AbstractMachineTE(int energyCapacity, int energyMaxReceive) {
        super(energyCapacity, energyMaxReceive);
    }

    /**
     * Processes the machine's tasks per tick.
     * Starts crafting if possible, continues crafting if already in progress,
     * and synchronizes the crafting state.
     *
     * @param redstoneCheckPassed true if redstone conditions allow operation
     * @return true if any changes were made that require syncing
     */
    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {
        if (this.worldObj.isRemote) {
            return super.processTasks(redstoneCheckPassed);
        }

        if (!crafting && canStartCrafting()) {
            startCrafting();
        }

        if (crafting && canContinueCrafting()) {
            onCrafting();
            advanceCraftingProgress();
        }

        syncCraftingState();

        return super.processTasks(redstoneCheckPassed);
    }

    /**
     * Syncs the crafting state to the block, world, and clients if it changed.
     */
    protected void syncCraftingState() {
        CraftingState newState = updateCraftingState();
        if (craftingState != newState) {
            craftingState = newState;

            if (worldObj != null && !worldObj.isRemote) {
                BlockStateUtils.setCraftingState(worldObj, xCoord, yCoord, zCoord, craftingState);
            }

            PacketHandler.sendToAllAround(new PacketCraftingState(this), this);
            markDirty();
        }
    }

    /** Starts a new crafting operation. */
    protected void startCrafting() {
        crafting = true;
        currentCraftingDuration = getCraftingDuration();
        markDirty();
    }

    /**
     * Determines if crafting can be started.
     *
     * @return true if redstone conditions are met and enough energy is available
     */
    public boolean canStartCrafting() {
        return isRedstoneActive()
            && energyStorage.getEnergyStored() >= getCraftingEnergyCost() * Math.max(1, (getCraftingDuration() - 1));
    }

    /**
     * Determines if crafting can continue.
     *
     * @return true if redstone conditions are met and energy is available
     */
    protected boolean canContinueCrafting() {
        return isRedstoneActive() && hasEnergyForCrafting();
    }

    /** Consumes energy and performs crafting-specific actions each tick. */
    protected void onCrafting() {
        energyStorage.voidEnergy(getCraftingEnergyCost());
    }

    /** Advances the crafting progress and finishes crafting if duration is reached. */
    private void advanceCraftingProgress() {
        craftingProgress++;
        if (craftingProgress >= getCraftingDuration()) {
            finishCrafting();
        }

        if (shouldDoWorkThisTick(20)) {
            markDirty();
        }
    }

    /** Returns the duration in ticks for this crafting operation. */
    protected abstract int getCraftingDuration();

    /** Handles completion of crafting. */
    protected abstract void finishCrafting();

    /** Resets the crafting state to IDLE. */
    protected void resetCrafting() {
        crafting = false;
        craftingProgress = 0;
        markDirty();
    }

    /** Checks if enough energy is available to continue crafting. */
    public boolean hasEnergyForCrafting() {
        return energyStorage.getEnergyStored() >= getCraftingEnergyCost();
    }

    /** Returns the energy cost per tick for crafting. */
    public abstract int getCraftingEnergyCost();

    /** Determines the current crafting state for syncing. */
    protected abstract CraftingState updateCraftingState();

    /** Sets the current crafting state manually. */
    public void setCraftingState(CraftingState newState) {
        craftingState = newState;
        requestRenderUpdate();
    }

    @Override
    public float getProgress() {
        if (!crafting || currentCraftingDuration <= 0) {
            return 0.0f;
        }
        return (float) craftingProgress / (float) currentCraftingDuration;
    }

    @Override
    public void setProgress(float progress) {
        if (worldObj != null && worldObj.isRemote) {
            craftingProgress = Math.round(progress * currentCraftingDuration);
        }
    }

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);

        NBTTagCompound craftingTag = new NBTTagCompound();
        craftingTag.setBoolean("isCrafting", crafting);
        craftingTag.setInteger("progress", craftingProgress);
        craftingTag.setInteger("cDuration", currentCraftingDuration);
        craftingTag.setInteger("craftingState", craftingState.ordinal());
        root.setTag(CRAFTING_TAG, craftingTag);
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);

        NBTTagCompound craftingTag = root.getCompoundTag(CRAFTING_TAG);
        crafting = craftingTag.getBoolean("isCrafting");
        craftingProgress = craftingTag.getInteger("progress");
        currentCraftingDuration = craftingTag.getInteger("cDuration");
        craftingState = CraftingState.values()[craftingTag.getInteger("craftingState")];
    }
}
