package ruiseki.omoshiroikamo.module.storage.common.item.wrapper;

import net.minecraft.item.ItemStack;

public interface IPickupUpgrade {

    boolean canPickup(ItemStack stack);
}
