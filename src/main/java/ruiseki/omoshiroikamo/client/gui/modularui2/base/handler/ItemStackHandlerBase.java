package ruiseki.omoshiroikamo.client.gui.modularui2.base.handler;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.utils.item.ItemStackHandler;

import ruiseki.omoshiroikamo.api.item.ItemUtils;

public class ItemStackHandlerBase extends ItemStackHandler {

    public ItemStackHandlerBase() {
        super(1);
    }

    public ItemStackHandlerBase(int size) {
        super(size);
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
}
