package ruiseki.omoshiroikamo.core.inventory;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.google.common.collect.Lists;

import ruiseki.omoshiroikamo.core.helper.MinecraftHelpers;
import ruiseki.omoshiroikamo.core.persist.IDirtyMarkListener;

/**
 * A basic inventory implementation.
 *
 * @author rubensworks
 *
 */
public class SimpleInventory implements INBTInventory {

    protected final ItemStack[] contents;
    private final String name;
    private final int stackLimit;
    private final List<IDirtyMarkListener> dirtyMarkListeners = Lists.newLinkedList();

    /**
     * Default constructor for NBT persistence, don't call this yourself.
     */
    public SimpleInventory() {
        this(0, "", 0);
    }

    /**
     * Make a new instance.
     *
     * @param size       The amount of slots in the inventory.
     * @param name       The name of the inventory, used for NBT storage.
     * @param stackLimit The stack limit for each slot.
     */
    public SimpleInventory(int size, String name, int stackLimit) {
        contents = new ItemStack[size];
        this.name = name;
        this.stackLimit = stackLimit;
    }

    /**
     * Add a dirty marking listener.
     *
     * @param dirtyMarkListener The dirty mark listener.
     */
    public synchronized void addDirtyMarkListener(IDirtyMarkListener dirtyMarkListener) {
        this.dirtyMarkListeners.add(dirtyMarkListener);
    }

    /**
     * Remove a dirty marking listener.
     *
     * @param dirtyMarkListener The dirty mark listener.
     */
    public synchronized void removeDirtyMarkListener(IDirtyMarkListener dirtyMarkListener) {
        this.dirtyMarkListeners.remove(dirtyMarkListener);
    }

    @Override
    public int getSizeInventory() {
        return contents.length;
    }

    @Override
    public ItemStack getStackInSlot(int slotId) {
        if (slotId >= 0 && slotId < contents.length) return contents[slotId];
        return null;
    }

    @Override
    public ItemStack decrStackSize(int slotId, int count) {
        if (slotId < contents.length && contents[slotId] != null) {
            if (count <= 0) return null;
            if (contents[slotId].stackSize > count) {
                ItemStack result = contents[slotId].splitStack(count);
                onInventoryChanged();
                return result;
            }
            ItemStack stack = contents[slotId];
            contents[slotId] = null;
            onInventoryChanged();
            return stack;
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        if (contents[index] != null) {
            ItemStack stack = contents[index];
            contents[index] = null;
            return stack;
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int slotId, ItemStack itemstack) {
        if (slotId >= contents.length) {
            return;
        }
        this.contents[slotId] = itemstack;

        if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
            itemstack.stackSize = this.getInventoryStackLimit();
        }
        onInventoryChanged();
    }

    @Override
    public String getInventoryName() {
        return name;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return stackLimit;
    }

    protected void onInventoryChanged() {
        markDirty();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return true;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        readFromNBT(data, "items");
    }

    /**
     * Read inventory data from the given NBT.
     *
     * @param data The NBT data containing inventory data.
     * @param tag  The NBT tag name where the info is located.
     */
    public void readFromNBT(NBTTagCompound data, String tag) {
        NBTTagList nbttaglist = data.getTagList(tag, MinecraftHelpers.NBTTag_Types.NBTTagCompound.getId());

        for (int j = 0; j < contents.length; ++j) contents[j] = null;

        for (int j = 0; j < nbttaglist.tagCount(); ++j) {
            NBTTagCompound slot = nbttaglist.getCompoundTagAt(j);
            int index;
            if (slot.hasKey("index")) {
                index = slot.getInteger("index");
            } else {
                index = slot.getByte("Slot");
            }
            if (index >= 0 && index < contents.length) {
                contents[index] = ItemStack.loadItemStackFromNBT(slot);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        writeToNBT(data, "items");
    }

    /**
     * Write inventory data to the given NBT.
     *
     * @param data The NBT tag that will receive inventory data.
     * @param tag  The NBT tag name where the info must be located.
     */
    public void writeToNBT(NBTTagCompound data, String tag) {
        NBTTagList slots = new NBTTagList();
        for (byte index = 0; index < contents.length; ++index) {
            if (contents[index] != null && contents[index].stackSize > 0) {
                NBTTagCompound slot = new NBTTagCompound();
                slots.appendTag(slot);
                slot.setByte("Slot", index);
                contents[index].writeToNBT(slot);
            }
        }
        data.setTag(tag, slots);
    }

    /**
     * Get the array of {@link net.minecraft.item.ItemStack} inside this inventory.
     *
     * @return The items in this inventory.
     */
    public ItemStack[] getItemStacks() {
        return contents;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return i < contents.length && i >= 0;
    }

    @Override
    public void markDirty() {
        List<IDirtyMarkListener> dirtyMarkListeners;
        synchronized (this) {
            dirtyMarkListeners = Lists.newLinkedList(this.dirtyMarkListeners);
        }
        for (IDirtyMarkListener dirtyMarkListener : dirtyMarkListeners) {
            dirtyMarkListener.onDirty();
        }
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < getSizeInventory(); i++) {
            if (getStackInSlot(i) != null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        readFromNBT(tag);
    }
}
