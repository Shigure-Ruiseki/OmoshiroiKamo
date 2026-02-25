package ruiseki.omoshiroikamo.core.client.gui.handler;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import com.cleanroommc.modularui.utils.item.ItemStackHandler;

import ruiseki.omoshiroikamo.core.item.ItemUtils;
import ruiseki.omoshiroikamo.core.persist.nbt.INBTSerializable;

public class ItemStackHandlerBase extends ItemStackHandler implements INBTSerializable {

    public ItemStackHandlerBase() {
        super(1);
    }

    public ItemStackHandlerBase(int size) {
        super(size);
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        validateSlot(slot);
        return super.getStackInSlot(slot);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        if (stack != null && stack.stackSize <= 0) {
            stack = null;
        }
        super.setStackInSlot(slot, stack);
    }

    protected void validateSlot(int slot) {
        ItemStack stack = super.getStackInSlot(slot);
        if (stack != null && stack.stackSize <= 0) {
            super.setStackInSlot(slot, null);
        }
    }

    public void cleanup() {
        for (int i = 0; i < getSlots(); i++) {
            validateSlot(i);
        }
    }

    public int voidItem(int slot, int amount) {
        if (amount <= 0) return 0;

        ItemStack stack = getStackInSlot(slot);
        if (stack == null) return amount;

        int oldCount = stack.stackSize;
        int toVoid = Math.min(oldCount, amount);

        stack.stackSize -= toVoid;
        if (stack.stackSize <= 0) {
            setStackInSlot(slot, null);
        } else {
            setStackInSlot(slot, stack);
        }

        return amount - toVoid;
    }

    public int voidItem() {
        return voidItem(0, 1);
    }

    public int growItem(int slot, int amount) {
        if (amount <= 0) return 0;

        ItemStack stack = getStackInSlot(slot);
        if (stack == null) return amount;

        int oldCount = stack.stackSize;
        int max = stack.getMaxStackSize();

        int toAdd = Math.min(amount, max - oldCount);
        stack.stackSize += toAdd;

        setStackInSlot(slot, stack);
        return amount - toAdd;
    }

    public int growItem(int amount) {
        return growItem(0, amount);
    }

    public int growItem() {
        return growItem(0, 1);
    }

    public boolean hasRoomForItem(ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) {
            return false;
        }

        int remaining = stack.stackSize;

        for (int i = 0; i < getSlots(); i++) {
            ItemStack slotStack = getStackInSlot(i);

            if (slotStack == null) {
                remaining -= Math.min(stack.getMaxStackSize(), remaining);
            }

            else if (ItemUtils.areStacksEqual(slotStack, stack)) {
                int space = slotStack.getMaxStackSize() - slotStack.stackSize;
                if (space > 0) {
                    remaining -= Math.min(space, remaining);
                }
            }

            if (remaining <= 0) {
                return true;
            }
        }

        return false;
    }

    public boolean hasEmptySlot() {
        for (int i = 0; i < getSlots(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (stack == null) {
                return true;
            }
        }
        return false;
    }

    public int addItemToAvailableSlots(ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) {
            return 0;
        }

        int remaining = stack.stackSize;

        for (int i = 0; i < getSlots() && remaining > 0; i++) {
            ItemStack slotStack = getStackInSlot(i);

            if (slotStack == null) continue;

            if (slotStack.getItem() == stack.getItem() && slotStack.getItemDamage() == stack.getItemDamage()
                && ItemStack.areItemStackTagsEqual(slotStack, stack)) {

                int max = slotStack.getMaxStackSize();
                int canAdd = max - slotStack.stackSize;

                if (canAdd > 0) {
                    int toAdd = Math.min(canAdd, remaining);
                    slotStack.stackSize += toAdd;
                    setStackInSlot(i, slotStack);
                    remaining -= toAdd;
                }
            }
        }

        for (int i = 0; i < getSlots() && remaining > 0; i++) {
            ItemStack slotStack = getStackInSlot(i);

            if (slotStack != null) continue;

            ItemStack newStack = stack.copy();
            int toAdd = Math.min(newStack.getMaxStackSize(), remaining);
            newStack.stackSize = toAdd;

            setStackInSlot(i, newStack);
            remaining -= toAdd;
        }

        return remaining;
    }

    public void resize(int newSize) {
        List<ItemStack> newStacks = new ArrayList<>(newSize);

        for (int i = 0; i < newSize; i++) {
            if (i < stacks.size()) {
                newStacks.add(stacks.get(i));
            } else {
                newStacks.add(null);
            }
        }

        this.stacks = newStacks;
    }

    public void dropAll(World world, int x, int y, int z) {
        for (int i = 0; i < getSlots(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (stack != null) {
                dropStack(world, x, y, z, stack);
            }
        }
    }

    public static void dropStack(World world, int x, int y, int z, ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) {
            return;
        }

        float dx = world.rand.nextFloat() * 0.8F + 0.1F;
        float dy = world.rand.nextFloat() * 0.8F + 0.1F;
        float dz = world.rand.nextFloat() * 0.8F + 0.1F;

        EntityItem entityItem = new EntityItem(world, x + dx, y + dy, z + dz, stack.copy());

        float motion = 0.05F;
        entityItem.motionX = world.rand.nextGaussian() * motion;
        entityItem.motionY = world.rand.nextGaussian() * motion + 0.2F;
        entityItem.motionZ = world.rand.nextGaussian() * motion;

        world.spawnEntityInWorld(entityItem);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagList list = new NBTTagList();

        for (int i = 0; i < stacks.size(); i++) {
            ItemStack stack = stacks.get(i);
            if (stack != null) {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setInteger("Slot", i);
                stack.writeToNBT(itemTag);
                itemTag.setInteger("Count", stack.stackSize);
                list.appendTag(itemTag);
            }
        }

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("Items", list);
        nbt.setInteger("Size", stacks.size());
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        int size = nbt.hasKey("Size", Constants.NBT.TAG_INT) ? nbt.getInteger("Size") : this.stacks.size();

        this.resize(size);

        for (int i = 0; i < stacks.size(); i++) {
            stacks.set(i, null);
        }

        NBTTagList tagList = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < tagList.tagCount(); ++i) {
            NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
            int slot = itemTags.getInteger("Slot");

            if (slot >= 0 && slot < stacks.size()) {
                ItemStack loadedStack = ItemStack.loadItemStackFromNBT(itemTags);
                if (loadedStack != null) {
                    if (itemTags.hasKey("Count", Constants.NBT.TAG_INT)) {
                        loadedStack.stackSize = itemTags.getInteger("Count");
                    }
                }
                stacks.set(slot, loadedStack);
            }
        }

        this.onLoad();
    }
}
