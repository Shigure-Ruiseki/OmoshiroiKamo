package ruiseki.omoshiroikamo.module.backpack.client.gui.syncHandler;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.module.backpack.client.gui.slot.ModularFilterSlot;

public class FoodFilterSlotSH extends FilterSlotSH {

    public FoodFilterSlotSH(ModularFilterSlot slot) {
        super(slot);
    }

    @Override
    public boolean isItemValid(ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemFood;
    }
}
