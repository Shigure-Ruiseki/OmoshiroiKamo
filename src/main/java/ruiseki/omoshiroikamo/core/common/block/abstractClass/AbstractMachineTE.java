package ruiseki.omoshiroikamo.core.common.block.abstractClass;

import net.minecraft.nbt.NBTTagCompound;

import lombok.Getter;
import ruiseki.omoshiroikamo.api.crafting.CraftingState;
import ruiseki.omoshiroikamo.api.crafting.ICraftingTile;
import ruiseki.omoshiroikamo.core.common.network.PacketCraftingState;
import ruiseki.omoshiroikamo.core.common.network.PacketHandler;

/*
 * IDLE
 * │
 * │ (canStartCrafting)
 * ▼
 * CRAFTING
 * │ per tick:
 * │ - consume energy
 * │ - +progress
 * │
 * │ (progress >= duration)
 * ▼
 * FINISH
 * │
 * ▼
 * RESET → IDLE
 */
public abstract class AbstractMachineTE extends AbstractEnergyTE implements ICraftingTile {

    @Getter
    private CraftingState craftingState = CraftingState.IDLE;
    protected boolean crafting = false;
    protected int craftingProgress = 0;

    public static String CRAFTING_TAG = "crafting";

    public AbstractMachineTE() {
        super();
    }

    public AbstractMachineTE(int energyCapacity) {
        super(energyCapacity);
    }

    public AbstractMachineTE(int energyCapacity, int energyMaxReceive) {
        super(energyCapacity, energyMaxReceive);
    }

    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {
        if (this.worldObj.isRemote) {
            return super.processTasks(redstoneCheckPassed);
        }

        if (!crafting && canStartCrafting()) {
            startCrafting();
        }

        if (crafting && canContinueCrafting()) {
            energyStorage.voidEnergy(getCraftingEnergyCost());
            advanceCraftingProgress();
        }

        syncCraftingState();

        return super.processTasks(redstoneCheckPassed);
    }

    protected void syncCraftingState() {
        CraftingState newState = updateCraftingState();
        if (craftingState != newState) {
            craftingState = newState;
            PacketHandler.sendToAllAround(new PacketCraftingState(this), this);
            markDirty();
        }
    }

    protected void startCrafting() {
        crafting = true;
        markDirty();
    }

    public boolean canStartCrafting() {
        return isRedstoneActive()
            && energyStorage.getEnergyStored() >= getCraftingEnergyCost() * Math.max(1, (getCraftingDuration() - 1));
    }

    protected boolean canContinueCrafting() {
        return isRedstoneActive() && hasEnergyForCrafting();
    }

    private void advanceCraftingProgress() {
        craftingProgress++;
        if (craftingProgress >= getCraftingDuration()) {
            finishCrafting();
        }

        markDirty();
    }

    protected abstract int getCraftingDuration();

    protected abstract void finishCrafting();

    protected void resetCrafting() {
        crafting = false;
        craftingProgress = 0;
        markDirty();
    }

    public float getRelativeCraftingProgress() {
        return (float) craftingProgress / getCraftingDuration();
    }

    public boolean hasEnergyForCrafting() {
        return energyStorage.getEnergyStored() >= getCraftingEnergyCost();
    }

    public abstract int getCraftingEnergyCost();

    protected abstract CraftingState updateCraftingState();

    public void setCraftingState(CraftingState newState) {
        craftingState = newState;
        requestRenderUpdate();
    }

    public boolean isCrafting() {
        return crafting;
    }

    @Override
    public float getProgress() {
        if (!crafting || getCraftingDuration() <= 0) {
            return 0.0f;
        }
        return (float) craftingProgress / (float) getCraftingDuration();
    }

    @Override
    public void setProgress(float progress) {
        if (worldObj != null && worldObj.isRemote) {
            craftingProgress = Math.round(progress * getCraftingDuration());
        }
    }

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);

        NBTTagCompound craftingTag = new NBTTagCompound();
        craftingTag.setBoolean("isCrafting", crafting);
        craftingTag.setInteger("progress", craftingProgress);
        root.setTag(CRAFTING_TAG, craftingTag);
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);

        NBTTagCompound craftingTag = root.getCompoundTag(CRAFTING_TAG);
        crafting = craftingTag.getBoolean("isCrafting");
        craftingProgress = craftingTag.getInteger("progress");
    }
}
