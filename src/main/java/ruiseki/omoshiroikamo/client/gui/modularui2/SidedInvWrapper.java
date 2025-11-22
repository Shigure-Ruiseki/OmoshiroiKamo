package ruiseki.omoshiroikamo.client.gui.modularui2;

import java.util.Objects;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.cleanroommc.modularui.utils.item.ItemHandlerHelper;

public class SidedInvWrapper implements IItemHandlerModifiable {

    private final ISidedInventory inv;
    private final ForgeDirection direction;

    public SidedInvWrapper(ISidedInventory inv, ForgeDirection direction) {
        this.inv = Objects.requireNonNull(inv);
        this.direction = direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SidedInvWrapper that = (SidedInvWrapper) o;

        return inv.equals(that.inv);
    }

    @Override
    public int hashCode() {
        return inv.hashCode();
    }

    @Override
    public void setStackInSlot(int slot, @Nullable ItemStack stack) {
        inv.setInventorySlotContents(slot, stack);
    }

    @Override
    public int getSlots() {
        return inv.getSizeInventory();
    }

    @Override
    public @Nullable ItemStack getStackInSlot(int slot) {
        return inv.getStackInSlot(slot);
    }

    @Override
    public @Nullable ItemStack insertItem(int slot, @Nullable ItemStack stack, boolean simulate) {
        if (stack == null) {
            return null;
        }

        if (!inv.canInsertItem(slot, stack, direction.ordinal())) {
            return stack;
        }

        ItemStack stackInSlot = inv.getStackInSlot(slot);

        int m;
        if (stackInSlot != null) {
            if (stackInSlot.stackSize >= Math.min(stackInSlot.getMaxStackSize(), getSlotLimit(slot))) {
                return stack;
            }

            if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot)) {
                return stack;
            }

            if (!inv.isItemValidForSlot(slot, stack)) {
                return stack;
            }

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot)) - stackInSlot.stackSize;

            if (stack.stackSize <= m) {
                if (!simulate) {
                    ItemStack copy = stack.copy();
                    copy.stackSize += stackInSlot.stackSize;
                    inv.setInventorySlotContents(slot, copy);
                    inv.markDirty();
                }

                return null;
            } else {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    ItemStack copy = stack.splitStack(m);
                    copy.stackSize += stackInSlot.stackSize;
                    inv.setInventorySlotContents(slot, copy);
                    inv.markDirty();
                } else {
                    stack.stackSize -= m;
                }
                return stack;
            }
        } else {
            if (!inv.isItemValidForSlot(slot, stack)) {
                return stack;
            }

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
            if (m < stack.stackSize) {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    inv.setInventorySlotContents(slot, stack.splitStack(m));
                    inv.markDirty();
                } else {
                    stack.stackSize -= m;
                }
                return stack;
            } else {
                if (!simulate) {
                    inv.setInventorySlotContents(slot, stack);
                    inv.markDirty();
                }
                return null;
            }
        }
    }

    @Override
    public @Nullable ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return null;
        }

        ItemStack stackInSlot = inv.getStackInSlot(slot);

        if (stackInSlot == null) {
            return null;
        }

        if (!inv.canExtractItem(slot, stackInSlot, direction.ordinal())) {
            return null;
        }

        if (simulate) {
            if (stackInSlot.stackSize < amount) {
                return stackInSlot.copy();
            } else {
                ItemStack copy = stackInSlot.copy();
                copy.stackSize = amount;
                return copy;
            }
        } else {
            int m = Math.min(stackInSlot.stackSize, amount);

            ItemStack decrStackSize = inv.decrStackSize(slot, m);
            inv.markDirty();
            return decrStackSize;
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return inv.getInventoryStackLimit();
    }

    @Override
    public boolean isItemValid(int slot, @Nullable ItemStack stack) {
        return inv.isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean isSlotFromInventory(int index, IInventory inventory, int invIndex) {
        return inventory == this.inv && index == invIndex && invIndex >= 0 && invIndex < inv.getSizeInventory();
    }
}
