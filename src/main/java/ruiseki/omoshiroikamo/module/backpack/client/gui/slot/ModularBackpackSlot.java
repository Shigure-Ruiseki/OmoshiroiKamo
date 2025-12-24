package ruiseki.omoshiroikamo.module.backpack.client.gui.slot;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.module.backpack.common.block.BackpackHandler;

public class ModularBackpackSlot extends ModularSlot {

    protected final BackpackHandler handler;

    public ModularBackpackSlot(BackpackHandler handler, int index) {
        super(handler.getBackpackHandler(), index);
        this.handler = handler;
    }

    public ItemStack getMemoryStack() {
        return handler.getMemorizedStack(getSlotIndex());
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        int multiplier = handler.getTotalStackMultiplier();
        return stack.getMaxStackSize() * multiplier;
    }

    @Override
    public int getSlotStackLimit() {
        int multiplier = handler.getTotalStackMultiplier();
        return 64 * multiplier;
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        handler.writeToItem();
    }
}
