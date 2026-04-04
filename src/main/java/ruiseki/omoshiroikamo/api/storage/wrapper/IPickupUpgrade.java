package ruiseki.omoshiroikamo.api.storage.wrapper;

import net.minecraft.item.ItemStack;

public interface IPickupUpgrade {

    boolean canPickup(ItemStack stack);
}
