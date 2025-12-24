package ruiseki.omoshiroikamo.module.dml.client.gui.handler;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.core.client.gui.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.module.dml.common.item.ItemDataModel;

public class ItemHandlerDataModel extends ItemStackHandlerBase {

    public ItemHandlerDataModel() {
        super();
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return stack != null && stack.getItem() instanceof ItemDataModel ? super.insertItem(slot, stack, simulate)
            : stack;
    }
}
