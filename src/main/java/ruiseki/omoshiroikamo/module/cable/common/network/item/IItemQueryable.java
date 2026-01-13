package ruiseki.omoshiroikamo.module.cable.common.network.item;

import net.minecraft.item.ItemStack;

public interface IItemQueryable {

    void collectItems(ItemIndex index);

    ItemStack extract(ItemStackKey key, int amount);

    ItemStack insert(ItemStack stack);
}
