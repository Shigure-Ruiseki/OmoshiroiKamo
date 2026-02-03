package ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.interfacebus;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.capability.item.ItemSource;
import com.gtnewhorizon.gtnhlib.item.ImmutableItemStack;
import com.gtnewhorizon.gtnhlib.item.ItemStack2IntFunction;
import com.gtnewhorizon.gtnhlib.item.ItemStackPredicate;
import com.gtnewhorizon.gtnhlib.item.StandardInventoryIterator;

public class InterfaceItemSource implements ItemSource {

    private final IItemInterface iface;

    private int[] allowedSlots;

    public InterfaceItemSource(IItemInterface iface) {
        this.iface = iface;
    }

    @Override
    public void resetSource() {
        ItemSource.super.resetSource();
        allowedSlots = null;
    }

    @Override
    public void setAllowedSourceSlots(int[] slots) {
        this.allowedSlots = slots;
    }

    @Override
    public @Nullable ItemStack pull(@Nullable ItemStackPredicate filter, @Nullable ItemStack2IntFunction amount) {
        StandardInventoryIterator iter = sourceIterator();

        while (iter.hasNext()) {
            ImmutableItemStack slot = iter.next();
            if (slot == null || slot.isEmpty()) continue;

            if (filter != null && !filter.test(slot)) continue;

            int toExtract = amount != null ? amount.apply(slot) : slot.getStackSize();
            if (toExtract <= 0) continue;

            ItemStack required = slot.toStack();

            return iface.extract(required, toExtract);
        }

        return null;
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
    public @NotNull StandardInventoryIterator sourceIterator() {
        return new StandardInventoryIterator(
            iface.getInventory(),
            iface.getSide()
                .getOpposite(),
            iface.getSlots(),
            allowedSlots) {

            @Override
            protected boolean canAccess(ItemStack stack, int slot) {
                return canExtract(stack, slot);
            }

            @Override
            protected int getSlotStackLimit(int slot, ItemStack stack) {
                return InterfaceItemSource.this.getSlotStackLimit(slot, stack);
            }
        };
    }
}
