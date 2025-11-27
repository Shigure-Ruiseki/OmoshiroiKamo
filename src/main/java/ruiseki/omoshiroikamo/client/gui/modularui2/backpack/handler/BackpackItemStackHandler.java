package ruiseki.omoshiroikamo.client.gui.modularui2.backpack.handler;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.utils.item.ItemHandlerHelper;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;

import ruiseki.omoshiroikamo.common.block.backpack.BackpackHandler;
import ruiseki.omoshiroikamo.common.block.backpack.BlockBackpack;

public class BackpackItemStackHandler extends ItemStackHandler {

    private final BackpackHandler handler;

    public BackpackItemStackHandler(int size, BackpackHandler handler) {
        super(size);
        this.handler = handler;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return !(stack.getItem() instanceof BlockBackpack.ItemBackpack) || handler.canNestBackpack();
    }

    @Override
    public int getStackLimit(int slot, ItemStack stack) {
        return stack.getMaxStackSize() * handler.getTotalStackMultiplier();
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64 * handler.getTotalStackMultiplier();
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

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack == null) {
            return null;
        }
        ItemStack existing = stacks.get(slot);

        int limit = getStackLimit(slot, stack);

        if (existing != null) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
                return stack;
            }
            limit -= existing.stackSize;
        }

        if (limit <= 0) {
            return stack;
        }

        boolean reachedLimit = stack.stackSize > limit;

        if (!simulate) {
            if (existing == null) {
                stacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            } else {
                existing.stackSize += (reachedLimit ? limit : stack.stackSize);
            }
            onContentsChanged(slot);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.stackSize - limit) : null;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return null;
        }
        ItemStack existing = getStackInSlot(slot);
        if (existing == null) {
            return null;
        }

        int slotMaxStackSize = existing.getMaxStackSize() * handler.getTotalStackMultiplier();
        int toExtract = Math.min(amount, slotMaxStackSize);

        if (existing.stackSize <= toExtract) {
            if (!simulate) {
                stacks.set(slot, null);
                onContentsChanged(slot);
            }
            return existing;
        } else {
            if (!simulate) {
                stacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.stackSize - toExtract));
                onContentsChanged(slot);
            }
            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    public ItemStack prioritizedInsertion(int slotIndex, ItemStack stack, boolean simulate) {
        return insertItem(slotIndex, stack, simulate);
    }
}
