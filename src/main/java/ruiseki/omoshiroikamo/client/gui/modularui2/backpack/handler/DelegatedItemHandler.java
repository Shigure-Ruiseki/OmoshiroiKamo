package ruiseki.omoshiroikamo.client.gui.modularui2.backpack.handler;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;

public class DelegatedItemHandler implements IItemHandlerModifiable {

    private IDelegatedSupplier delegated;

    public DelegatedItemHandler(IDelegatedSupplier delegated) {
        this.delegated = delegated;
    }

    public void setDelegated(IDelegatedSupplier delegated) {
        this.delegated = delegated;
    }

    @Override
    public int getSlots() {
        return delegated.get()
            .getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return delegated.get()
            .getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return delegated.get()
            .insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return delegated.get()
            .extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return delegated.get()
            .getSlotLimit(slot);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        IItemHandler handler = delegated.get();
        if (handler instanceof IItemHandlerModifiable modifiable) {
            modifiable.setStackInSlot(slot, stack);
        }
    }

    @FunctionalInterface
    public interface IDelegatedSupplier {

        IItemHandler get();
    }
}
