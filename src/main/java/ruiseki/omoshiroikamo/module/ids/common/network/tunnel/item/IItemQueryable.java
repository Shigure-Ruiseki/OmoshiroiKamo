package ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item;

import net.minecraft.item.ItemStack;

public interface IItemQueryable {

    void collectItems(ItemIndex index);

    ItemStack extract(ItemStack required, int amount);

    ItemStack insert(ItemStack stack);
}
