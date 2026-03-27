package ruiseki.omoshiroikamo.module.storage.common.item.wrapper;

import net.minecraft.item.ItemStack;

public interface IRestockUpgrade {

    boolean canRestock(ItemStack stack);
}
