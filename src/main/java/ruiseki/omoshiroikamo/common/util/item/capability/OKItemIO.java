package ruiseki.omoshiroikamo.common.util.item.capability;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.item.AbstractInventoryIterator;
import com.gtnewhorizon.gtnhlib.item.ImmutableItemStack;
import com.gtnewhorizon.gtnhlib.item.InventoryIterator;
import com.gtnewhorizon.gtnhlib.item.SimpleItemIO;

import ruiseki.omoshiroikamo.common.util.item.ItemUtils;

public class OKItemIO extends SimpleItemIO {

    private final IInventory inv;
    private final ForgeDirection side;

    private boolean markedDirty = false;

    public OKItemIO(IInventory inv, ForgeDirection side) {
        this.inv = inv;
        this.side = side;
    }

    public OKItemIO(IInventory inv) {
        this(inv, ForgeDirection.UNKNOWN);
    }

    public static int[] getInventorySlotIndices(IInventory inv, ForgeDirection side) {
        if (inv instanceof ISidedInventory sided) {
            return sided.getAccessibleSlotsFromSide(side.ordinal());
        } else {
            int[] slots = new int[inv.getSizeInventory()];

            for (int i = 0; i < slots.length; i++) {
                slots[i] = i;
            }

            return slots;
        }
    }

    @Override
    protected @NotNull InventoryIterator iterator(int[] allowedSlots) {
        return new AbstractInventoryIterator(getInventorySlotIndices(inv, side)) {

            @Override
            protected ItemStack getStackInSlot(int slot) {
                return inv.getStackInSlot(slot);
            }

            private void setInventorySlotContents(int slot, ItemStack stack) {
                inv.setInventorySlotContents(slot, stack);
            }

            protected boolean canExtract(ItemStack stack, int slot) {

                if (inv instanceof ISidedInventory sided) {
                    return sided.canExtractItem(slot, stack, side.ordinal());
                }

                return inv.isItemValidForSlot(slot, stack);
            }

            private boolean canInsert(ItemStack stack, int slot) {

                if (inv instanceof ISidedInventory sided) {
                    return sided.canInsertItem(slot, stack, side.ordinal());
                }

                return inv.isItemValidForSlot(slot, stack);
            }

            @Override
            protected boolean canAccess(ItemStack stack, int slot) {
                return canExtract(stack, slot) || canInsert(stack, slot);
            }

            @Override
            public ItemStack extract(int amount, boolean forced) {
                int slotIndex = getCurrentSlot();

                ItemStack inSlot = getStackInSlot(slotIndex);

                if (ItemUtils.isStackEmpty(inSlot)) {
                    return null;
                }
                if (!forced && !canExtract(inSlot, slotIndex)) {
                    return null;
                }

                int toExtract = Math.min(inSlot.stackSize, amount);

                ItemStack extracted = inSlot.splitStack(toExtract);

                setInventorySlotContents(slotIndex, inSlot.stackSize == 0 ? null : inSlot);

                markDirty();

                return extracted;
            }

            @Override
            public int insert(ImmutableItemStack stack, boolean forced) {
                if (stack.isEmpty()) {
                    return 0;
                }

                int slotIndex = getCurrentSlot();

                ItemStack inSlot = getStackInSlot(slotIndex);

                if (!ItemUtils.isStackEmpty(inSlot) && !stack.matches(inSlot)) {
                    return stack.getStackSize();
                }

                ItemStack partialCopy = stack.toStackFast();

                if (!forced && !canInsert(partialCopy, slotIndex)) {
                    return stack.getStackSize();
                }

                if (!ItemUtils.isStackEmpty(inSlot)) {
                    int maxStack = getSlotStackLimit(slotIndex, partialCopy);
                    int toInsert = forced ? stack.getStackSize()
                        : Math.min(maxStack - inSlot.stackSize, stack.getStackSize());

                    inSlot.stackSize += toInsert;

                    setInventorySlotContents(slotIndex, inSlot);

                    markDirty();

                    return stack.getStackSize() - toInsert;
                } else {
                    setInventorySlotContents(slotIndex, stack.toStack());

                    markDirty();

                    return 0;
                }
            }

            private int getSlotStackLimit(int slot, ItemStack stack) {
                int invStackLimit = inv.getInventoryStackLimit();

                int existingMaxStack = stack == null ? 64 : stack.getMaxStackSize();

                if (invStackLimit > 64) {
                    return invStackLimit / 64 * existingMaxStack;
                } else {
                    return Math.min(invStackLimit, existingMaxStack);
                }
            }

            private void markDirty() {
                if (markedDirty) {
                    return;
                }

                // Only mark the inv dirty once, iterators should never last longer than one tick.
                inv.markDirty();
                markedDirty = true;
            }
        };
    }
}
