package ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.interfacebus;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.IItemQueryable;

public interface IItemInterface extends IItemQueryable {

    ItemStack extract(ItemStack required, int amount);

    ItemStack insert(ItemStack stack);

    IInventory getInventory();

    int[] getSlots();

    ForgeDirection getSide();
}
