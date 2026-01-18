package ruiseki.omoshiroikamo.module.cable.common.network.item;

import net.minecraft.item.ItemStack;

public interface IItemQueryable {

    void collectItems(ItemIndex index);

    ItemStack extract(ItemStack required, int amount);

    ItemStack insert(ItemStack stack);
}
