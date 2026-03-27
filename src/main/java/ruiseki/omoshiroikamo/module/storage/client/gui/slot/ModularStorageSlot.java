package ruiseki.omoshiroikamo.module.storage.client.gui.slot;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.module.storage.common.handler.StorageWrapper;

public class ModularStorageSlot extends ModularSlot {

    protected final StorageWrapper wrapper;

    public ModularStorageSlot(StorageWrapper wrapper, int index) {
        super(wrapper.storageItemStackHandler, index);
        this.wrapper = wrapper;
    }

    public ItemStack getMemoryStack() {
        return wrapper.getMemorizedStack(getSlotIndex());
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        int multiplier = wrapper.getTotalStackMultiplier();
        return stack.getMaxStackSize() * multiplier;
    }

    @Override
    public int getSlotStackLimit() {
        int multiplier = wrapper.getTotalStackMultiplier();
        return 64 * multiplier;
    }
}
