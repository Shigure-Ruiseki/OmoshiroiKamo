package ruiseki.omoshiroikamo.api.item;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;

public class SidedInvWrapper implements IItemHandlerModifiable {

    protected final ISidedInventory inv;
    protected final ForgeDirection side;

    public SidedInvWrapper(ISidedInventory inv, ForgeDirection side) {
        this.inv = inv;
        this.side = side;
    }

    public static int getSlot(ISidedInventory inv, int slot, ForgeDirection side) {
        int[] slots = inv.getAccessibleSlotsFromSide(side.ordinal());
        if (slot < slots.length) return slots[slot];
        return -1;
    }

    @Override
    public int getSlots() {
        return inv.getAccessibleSlotsFromSide(side.ordinal()).length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        int realSlot = getSlot(inv, slot, side);
        return realSlot == -1 ? null : inv.getStackInSlot(realSlot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {

        if (stack == null) return null;

        int realSlot = getSlot(inv, slot, side);
        if (realSlot == -1) return stack;

        if (!inv.canInsertItem(realSlot, stack, side.ordinal()) || !inv.isItemValidForSlot(realSlot, stack))
            return stack;

        ItemStack stackInSlot = inv.getStackInSlot(realSlot);
        int limit = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));

        if (stackInSlot == null) {

            if (stack.stackSize <= limit) {
                if (!simulate) setInventorySlotContents(realSlot, stack.copy());
                return null;
            } else {
                ItemStack remainder = stack.copy();
                ItemStack toInsert = remainder.splitStack(limit);

                if (!simulate) setInventorySlotContents(realSlot, toInsert);

                return remainder;
            }

        } else {

            if (!stackInSlot.isItemEqual(stack) || !ItemStack.areItemStackTagsEqual(stackInSlot, stack)) return stack;

            if (stackInSlot.stackSize >= limit) return stack;

            int canInsert = limit - stackInSlot.stackSize;

            if (stack.stackSize <= canInsert) {
                if (!simulate) {
                    stackInSlot.stackSize += stack.stackSize;
                    inv.markDirty();
                }
                return null;
            } else {
                ItemStack remainder = stack.copy();
                remainder.stackSize -= canInsert;

                if (!simulate) {
                    stackInSlot.stackSize += canInsert;
                    inv.markDirty();
                }
                return remainder;
            }
        }
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        int realSlot = getSlot(inv, slot, side);
        if (realSlot != -1) setInventorySlotContents(realSlot, stack);
    }

    private void setInventorySlotContents(int slot, ItemStack stack) {
        inv.setInventorySlotContents(slot, stack);
        inv.markDirty();
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {

        if (amount <= 0) return null;

        int realSlot = getSlot(inv, slot, side);
        if (realSlot == -1) return null;

        ItemStack stackInSlot = inv.getStackInSlot(realSlot);
        if (stackInSlot == null) return null;

        if (!inv.canExtractItem(realSlot, stackInSlot, side.ordinal())) return null;

        int extracted = Math.min(amount, stackInSlot.stackSize);

        if (simulate) {
            ItemStack copy = stackInSlot.copy();
            copy.stackSize = extracted;
            return copy;
        } else {
            ItemStack ret = inv.decrStackSize(realSlot, extracted);
            inv.markDirty();
            return ret;
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return inv.getInventoryStackLimit();
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        int realSlot = getSlot(inv, slot, side);
        return realSlot != -1 && inv.isItemValidForSlot(realSlot, stack);
    }
}
