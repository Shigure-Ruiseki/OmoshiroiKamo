package ruiseki.omoshiroikamo.module.backpack.client.gui.slot;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.module.backpack.common.handler.BackpackWrapper;

public class ModularBackpackSlot extends ModularSlot {

    protected final BackpackWrapper wrapper;

    public ModularBackpackSlot(BackpackWrapper handler, int index) {
        super(handler.getBackpackHandler(), index);
        this.wrapper = handler;
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
