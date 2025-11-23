package ruiseki.omoshiroikamo.client.gui.modularui2.backpack.slot;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.common.block.backpack.BackpackHandler;

public class ModularBackpackSlot extends ModularSlot {

    protected final BackpackHandler handler;

    public ModularBackpackSlot(IItemHandler itemHandler, int index, BackpackHandler handler) {
        super(itemHandler, index);
        this.handler = handler;
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
