package ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.item.interfacebus;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.item.IImmutableItemStack;
import ruiseki.okcore.item.IInventoryIterator;
import ruiseki.okcore.item.IItemStack2IntFunction;
import ruiseki.okcore.item.IItemStackPredicate;
import ruiseki.okcore.item.capability.IItemSource;
import ruiseki.okcore.item.capability.minecraft.InventoryIterator;

public class InterfaceItemSource implements IItemSource {

    private final IItemInterface iface;

    private int[] allowedSlots;

    public InterfaceItemSource(IItemInterface iface) {
        this.iface = iface;
    }

    @Override
    public void resetSource() {
        IItemSource.super.resetSource();
        allowedSlots = null;
    }

    @Override
    public void setAllowedSourceSlots(int[] slots) {
        this.allowedSlots = slots;
    }

    @Override
    public @Nullable ItemStack pull(@Nullable IItemStackPredicate filter, @Nullable IItemStack2IntFunction amount) {
        IInventoryIterator iter = sourceIterator();

        while (iter.hasNext()) {
            IImmutableItemStack slot = iter.next();
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
    public @NotNull IInventoryIterator sourceIterator() {
        return new InventoryIterator(
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
