package ruiseki.omoshiroikamo.core.creative;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class BaseCreativeTab implements ICreativeTabModule {

    public static void addItem(CreativeTabs tab, List<ItemStack> list, Item item) {
        item.getSubItems(item, tab, list);
    }

    public static void addBlock(CreativeTabs tab, List<ItemStack> list, Block block) {
        block.getSubBlocks(Item.getItemFromBlock(block), tab, list);
    }
}
