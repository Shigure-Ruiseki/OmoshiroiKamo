package ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.item.interfacebus;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.GTNHLib;
import com.gtnewhorizon.gtnhlib.capability.item.ItemSink;
import com.gtnewhorizon.gtnhlib.item.ImmutableItemStack;
import com.gtnewhorizon.gtnhlib.item.InventoryIterator;
import com.gtnewhorizon.gtnhlib.item.StandardInventoryIterator;

public class InterfaceItemSink implements ItemSink {

    private final IItemInterface iface;
    private int[] allowedSlots;

    public InterfaceItemSink(IItemInterface iface) {
        this.iface = iface;
    }

    @Override
    public void resetSink() {
        ItemSink.super.resetSink();
        allowedSlots = null;
    }

    @Override
    public void setAllowedSinkSlots(int @Nullable [] slots) {
        this.allowedSlots = slots;
    }

    @Override
    public int store(ImmutableItemStack stack) {
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
    public @NotNull StandardInventoryIterator sinkIterator() {
        return new StandardInventoryIterator(
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
    public @Nullable InventoryIterator simulatedSinkIterator() {
        return new StandardInventoryIterator(iface.getInventory(), iface.getSide(), iface.getSlots(), allowedSlots) {

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
            public ImmutableItemStack previous() {
                GTNHLib.LOG.warn("This simulated sink iterator doesn't support backward traversal");
                return null;
            }

            @Override
            public boolean rewind() {
                return false;
            }
        };
    }
}
