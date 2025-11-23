package ruiseki.omoshiroikamo.common.block.backpack.syncHandler;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.utils.MouseData;
import com.cleanroommc.modularui.value.sync.PhantomItemSlotSH;

import ruiseki.omoshiroikamo.common.block.backpack.slot.ModularFilterSlot;

public class FilterSlotSH extends PhantomItemSlotSH {

    public FilterSlotSH(ModularFilterSlot slot) {
        super(slot);
    }

    @Override
    public void phantomClick(MouseData mouseData, ItemStack cursorStack) {
        super.phantomClick(mouseData, cursorStack);
        clampStackCount();
    }

    @Override
    public void phantomScroll(MouseData mouseData) {}

    private void clampStackCount() {
        ItemStack stack = getSlot().getStack();
        if (stack != null && stack.stackSize > 1) {
            stack.stackSize = 1;
        }
    }
}
