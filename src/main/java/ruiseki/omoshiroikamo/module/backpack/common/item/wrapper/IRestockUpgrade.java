package ruiseki.omoshiroikamo.module.backpack.common.item.wrapper;

import net.minecraft.item.ItemStack;

public interface IRestockUpgrade {

    boolean canRestock(ItemStack stack);
}
