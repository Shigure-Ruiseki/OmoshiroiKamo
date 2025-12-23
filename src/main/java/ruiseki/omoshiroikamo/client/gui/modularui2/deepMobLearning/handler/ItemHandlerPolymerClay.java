package ruiseki.omoshiroikamo.client.gui.modularui2.deepMobLearning.handler;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.client.gui.modularui2.base.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.common.item.dml.ItemPolymerClay;

public class ItemHandlerPolymerClay extends ItemStackHandlerBase {

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return stack != null && stack.getItem() instanceof ItemPolymerClay ? super.insertItem(slot, stack, simulate)
            : stack;
    }
}
