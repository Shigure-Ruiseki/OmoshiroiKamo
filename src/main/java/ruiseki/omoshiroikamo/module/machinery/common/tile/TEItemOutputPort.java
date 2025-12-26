package ruiseki.omoshiroikamo.module.machinery.common.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * Item Output Port TileEntity.
 * Holds a single slot for outputting processed items from machines.
 */
public class TEItemOutputPort extends TileEntity implements IInventory {

    private ItemStack inventory;

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot == 0 ? inventory : null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (inventory == null) {
            return null;
        }
        if (inventory.stackSize <= amount) {
            ItemStack result = inventory;
            inventory = null;
            return result;
        }
        return inventory.splitStack(amount);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (slot == 0) {
            inventory = stack;
        }
    }

    @Override
    public String getInventoryName() {
        return "itemOutputPort";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this
            && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) <= 64.0;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
    }

    /**
     * Try to insert an item into the output slot.
     * 
     * @return true if successful, false if slot is full
     */
    public boolean insertItem(ItemStack stack) {
        if (inventory == null) {
            inventory = stack.copy();
            return true;
        }
        if (inventory.isItemEqual(stack) && inventory.stackSize + stack.stackSize <= getInventoryStackLimit()) {
            inventory.stackSize += stack.stackSize;
            return true;
        }
        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (inventory != null) {
            NBTTagCompound itemNbt = new NBTTagCompound();
            inventory.writeToNBT(itemNbt);
            nbt.setTag("inventory", itemNbt);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.hasKey("inventory")) {
            inventory = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("inventory"));
        }
    }
}
