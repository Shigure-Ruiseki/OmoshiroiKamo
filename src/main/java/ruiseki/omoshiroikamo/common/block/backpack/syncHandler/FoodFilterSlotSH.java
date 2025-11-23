package ruiseki.omoshiroikamo.common.block.backpack.syncHandler;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.common.block.backpack.slot.ModularFilterSlot;

public class FoodFilterSlotSH extends FilterSlotSH {

    public FoodFilterSlotSH(ModularFilterSlot slot) {
        super(slot);
    }

    @Override
    public boolean isItemValid(ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemFood;
    }
}
