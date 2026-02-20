package ruiseki.omoshiroikamo.core.tileentity;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.google.common.collect.Maps;

import ruiseki.omoshiroikamo.core.common.capabilities.CapabilityItemHandler;
import ruiseki.omoshiroikamo.core.inventory.INBTInventory;
import ruiseki.omoshiroikamo.core.item.SidedInvWrapper;

public abstract class AbstractBaseInventoryTE extends AbstractSideCapabilityTE implements ISidedInventory {

    protected boolean sendUpdateOnInventoryChanged = false;
    protected final Map<ForgeDirection, IItemHandler> sidedInventoryHandlers;

    public AbstractBaseInventoryTE() {
        this.sidedInventoryHandlers = Maps.newHashMap();
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            addCapabilitySided(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side, new SidedInvWrapper(this, side));
        }
    }

    /**
     * Get the internal inventory.
     *
     * @return The inventory.
     */
    public abstract INBTInventory getInventory();

    @Override
    public int getSizeInventory() {
        return getInventory().getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slotId) {
        if (slotId >= getSizeInventory() || slotId < 0) return null;
        return getInventory().getStackInSlot(slotId);
    }

    @Override
    public ItemStack decrStackSize(int slotId, int count) {
        ItemStack itemStack = getInventory().decrStackSize(slotId, count);
        onInventoryChanged();
        return itemStack;
    }

    @Override
    public void setInventorySlotContents(int slotId, ItemStack itemstack) {
        getInventory().setInventorySlotContents(slotId, itemstack);
        onInventoryChanged();
    }

    protected void onInventoryChanged() {
        if (isSendUpdateOnInventoryChanged()) sendUpdate();
    }

    @Override
    public String getInventoryName() {
        return getInventory().getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return getInventory().hasCustomInventoryName();
    }

    @Override
    public int getInventoryStackLimit() {
        return getInventory().getInventoryStackLimit();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return getPos().getTileEntity(worldObj) == this && canInteractWith(player);
    }

    @Override
    public void openInventory() {
        getInventory().openInventory();
    }

    @Override
    public void closeInventory() {
        getInventory().closeInventory();
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return getInventory().isItemValidForSlot(index, stack);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return getInventory().getStackInSlotOnClosing(index);
    }

    @Override
    public void readCommon(NBTTagCompound tag) {
        super.readCommon(tag);
        INBTInventory inventory = getInventory();
        if (inventory != null) {
            inventory.readFromNBT(tag);
        }
    }

    @Override
    public void writeCommon(NBTTagCompound tag) {
        super.writeCommon(tag);
        INBTInventory inventory = getInventory();
        if (inventory != null) {
            inventory.writeToNBT(tag);
        }
    }

    protected boolean canAccess(int slot, int side) {
        boolean canAccess = false;
        for (int slotAccess : getAccessibleSlotsFromSide(side)) {
            if (slotAccess == slot) canAccess = true;
        }
        return canAccess;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack itemStack, int side) {
        return canAccess(slot, side) && this.isItemValidForSlot(slot, itemStack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack itemStack, int side) {
        return canAccess(slot, side);
    }

    /**
     * If this tile should send blockState updates when the inventory has changed.
     *
     * @return If it should send blockState updates.
     */
    public boolean isSendUpdateOnInventoryChanged() {
        return sendUpdateOnInventoryChanged;
    }

    /**
     * If this tile should send blockState updates when the inventory has changed.
     *
     * @param sendUpdateOnInventoryChanged If it should send blockState updates.
     */
    public void setSendUpdateOnInventoryChanged(boolean sendUpdateOnInventoryChanged) {
        this.sendUpdateOnInventoryChanged = sendUpdateOnInventoryChanged;
    }
}
