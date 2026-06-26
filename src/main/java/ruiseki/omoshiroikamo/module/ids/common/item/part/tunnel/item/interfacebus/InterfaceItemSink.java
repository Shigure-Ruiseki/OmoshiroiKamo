package ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.item.interfacebus;

import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.item.IImmutableItemStack;
import ruiseki.okcore.item.IInventoryIterator;
import ruiseki.okcore.item.capability.IItemSink;
import ruiseki.okcore.item.capability.minecraft.InventoryIterator;
import ruiseki.omoshiroikamo.OmoshiroiKamo;

public class InterfaceItemSink implements IItemSink {

    private final IItemInterface iface;
    private int[] allowedSlots;

    public InterfaceItemSink(IItemInterface iface) {
        this.iface = iface;
    }

    @Override
    public void resetSink() {
        IItemSink.super.resetSink();
        allowedSlots = null;
    }

    @Override
    public void setAllowedSinkSlots(int @Nullable [] slots) {
        this.allowedSlots = slots;
    }

    @Override
    public int store(IImmutableItemStack stack) {
        if (stack.isEmpty()) return 0;

        ItemStack mcStack = stack.toStack();
        ItemStack remaining = iface.insert(mcStack);

        if (remaining == null || remaining.stackSize <= 0) {
            return 0;
        }

        return remaining.stackSize;
    }

    protected int getSlotStackLimit(int slot, ItemStack stack) {
        int invStackLimit = iface.getInventory()
            .getInventoryStackLimit();

        int existingMaxStack = stack == null ? 64 : stack.getMaxStackSize();

        if (invStackLimit > 64) {
            return invStackLimit / 64 * existingMaxStack;
        } else {
            return Math.min(invStackLimit, existingMaxStack);
        }
    }

    @Override
    public @NotNull InventoryIterator sinkIterator() {
        return new InventoryIterator(
            iface.getInventory(),
            iface.getSide()
                .getOpposite(),
            iface.getSlots(),
            allowedSlots) {

            @Override
            protected boolean canAccess(ItemStack stack, int slot) {
                return canInsert(stack, slot);
            }

            @Override
            protected int getSlotStackLimit(int slot, ItemStack stack) {
                return InterfaceItemSink.this.getSlotStackLimit(slot, stack);
            }
        };
    }

    @Override
    public @Nullable IInventoryIterator simulatedSinkIterator() {
        return new InventoryIterator(iface.getInventory(), iface.getSide(), iface.getSlots(), allowedSlots) {

            @Override
            protected boolean canAccess(ItemStack stack, int slot) {
                return canInsert(stack, slot);
            }

            @Override
            protected int getSlotStackLimit(int slot, ItemStack stack) {
                return InterfaceItemSink.this.getSlotStackLimit(slot, stack);
            }

            @Override
            protected void setInventorySlotContents(int slot, ItemStack stack) {}

            @Override
            protected void markDirty() {}

            @Override
            protected boolean canExtract(ItemStack stack, int slot) {
                return false;
            }

            @Override
            public ItemStack extract(int amount, boolean forced) {
                throw new UnsupportedOperationException();
            }

            @Override
            public IImmutableItemStack previous() {
                OmoshiroiKamo.okLog(Level.ERROR, "This simulated sink iterator doesn't support backward traversal");
                return null;
            }

            @Override
            public boolean rewind() {
                return false;
            }
        };
    }
}
