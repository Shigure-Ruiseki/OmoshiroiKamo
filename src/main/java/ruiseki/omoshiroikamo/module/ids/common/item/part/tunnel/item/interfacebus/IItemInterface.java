package ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.item.interfacebus;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.item.IItemQueryable;

public interface IItemInterface extends IItemQueryable {

    ItemStack extract(ItemStack required, int amount);

    ItemStack insert(ItemStack stack);

    IInventory getInventory();

    int[] getSlots();

    ForgeDirection getSide();
}
