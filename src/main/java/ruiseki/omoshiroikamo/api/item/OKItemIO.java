package ruiseki.omoshiroikamo.api.item;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.item.AbstractInventoryIterator;
import com.gtnewhorizon.gtnhlib.item.ImmutableItemStack;
import com.gtnewhorizon.gtnhlib.item.InventoryIterator;
import com.gtnewhorizon.gtnhlib.item.SimpleItemIO;
import com.gtnewhorizon.gtnhlib.util.ItemUtil;

import ruiseki.omoshiroikamo.api.io.SlotDefinition;

public class OKItemIO extends SimpleItemIO {

    private final ISidedInventory inv;
    private final ForgeDirection side;
    private final int[] allSlots;

    private boolean markedDirty = false;

    public OKItemIO(ISidedInventory inv, ForgeDirection side, int[] allSlots) {
        this.inv = inv;
        this.side = side;
        this.allSlots = allSlots;
    }

    public OKItemIO(ISidedInventory inv, int[] allSlots) {
        this(inv, ForgeDirection.UNKNOWN, allSlots);
    }

    public OKItemIO(ISidedInventory inv, ForgeDirection side, SlotDefinition definition) {
        this(inv, side, definition.getAllItemSlots());
    }

    public OKItemIO(ISidedInventory inv, SlotDefinition definition) {
        this(inv, ForgeDirection.UNKNOWN, definition);
    }

    @Override
    protected @NotNull InventoryIterator iterator(int[] allowedSlots) {
        return new AbstractInventoryIterator(allSlots) {

            @Override
            protected ItemStack getStackInSlot(int slot) {
                return inv.getStackInSlot(slot);
            }

            private void setInventorySlotContents(int slot, ItemStack stack) {
                inv.setInventorySlotContents(slot, stack);
            }

            @Override
            protected boolean canExtract(ItemStack stack, int slot) {
                if (side == ForgeDirection.UNKNOWN) {
                    return true;
                }
                return inv.canExtractItem(slot, stack, side.ordinal());
            }

            private boolean canInsert(ItemStack stack, int slot) {
                if (side == ForgeDirection.UNKNOWN) {
                    return true;
                }
                return inv.canInsertItem(slot, stack, side.ordinal());
            }

            @Override
            public ItemStack extract(int amount, boolean forced) {
                int slotIndex = getCurrentSlot();

                ItemStack inSlot = getStackInSlot(slotIndex);

                if (ItemUtil.isStackEmpty(inSlot)) {
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

                if (!ItemUtil.isStackEmpty(inSlot) && !stack.matches(inSlot)) {
                    return stack.getStackSize();
                }

                ItemStack partialCopy = stack.toStackFast();

                if (!forced && !canInsert(partialCopy, slotIndex)) {
                    return stack.getStackSize();
                }

                if (!ItemUtil.isStackEmpty(inSlot)) {
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
