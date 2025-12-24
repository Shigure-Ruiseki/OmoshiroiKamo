package ruiseki.omoshiroikamo.module.dml.client.gui.handler;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.core.client.gui.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.module.dml.common.item.ItemPolymerClay;

public class ItemHandlerPolymerClay extends ItemStackHandlerBase {

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return stack != null && stack.getItem() instanceof ItemPolymerClay ? super.insertItem(slot, stack, simulate)
            : stack;
    }
}
