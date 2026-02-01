package ruiseki.omoshiroikamo.module.backpack;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.core.common.creative.BaseCreativeTab;
import ruiseki.omoshiroikamo.core.common.creative.CreativeTabRegistry;
import ruiseki.omoshiroikamo.core.common.creative.OKCreativeTab;
import ruiseki.omoshiroikamo.module.backpack.common.init.BackpackBlocks;
import ruiseki.omoshiroikamo.module.backpack.common.init.BackpackItems;

public class BackpackCreative extends BaseCreativeTab {

    public static final BackpackCreative INSTANCE = new BackpackCreative();

    public static final OKCreativeTab TAB = CreativeTabRegistry.create("okBackpack");

    public static void preInit() {
        TAB.setIcon(new ItemStack(BackpackBlocks.BACKPACK_BASE.getBlock()));
        TAB.registerModule(INSTANCE);
    }

    @Override
    public void fillTab(CreativeTabs tab, List<ItemStack> list) {
        addBlock(tab, list, BackpackBlocks.BACKPACK_BASE.getBlock());
        addBlock(tab, list, BackpackBlocks.BACKPACK_IRON.getBlock());
        addBlock(tab, list, BackpackBlocks.BACKPACK_GOLD.getBlock());
        addBlock(tab, list, BackpackBlocks.BACKPACK_DIAMOND.getBlock());
        addBlock(tab, list, BackpackBlocks.BACKPACK_OBSIDIAN.getBlock());

        addItem(tab, list, BackpackItems.BASE_UPGRADE.getItem());
        addItem(tab, list, BackpackItems.STACK_UPGRADE.getItem());
        addItem(tab, list, BackpackItems.MAGNET_UPGRADE.getItem());
        addItem(tab, list, BackpackItems.ADVANCED_MAGNET_UPGRADE.getItem());
        addItem(tab, list, BackpackItems.FEEDING_UPGRADE.getItem());
        addItem(tab, list, BackpackItems.ADVANCED_FEEDING_UPGRADE.getItem());
        addItem(tab, list, BackpackItems.PICKUP_UPGRADE.getItem());
        addItem(tab, list, BackpackItems.ADVANCED_PICKUP_UPGRADE.getItem());
        addItem(tab, list, BackpackItems.EVERLASTING_UPGRADE.getItem());
        addItem(tab, list, BackpackItems.INCEPTION_UPGRADE.getItem());
        addItem(tab, list, BackpackItems.FILTER_UPGRADE.getItem());
        addItem(tab, list, BackpackItems.ADVANCED_FILTER_UPGRADE.getItem());
        addItem(tab, list, BackpackItems.VOID_UPGRADE.getItem());
        addItem(tab, list, BackpackItems.ADVANCED_VOID_UPGRADE.getItem());
        addItem(tab, list, BackpackItems.CRAFTING_UPGRADE.getItem());
    }
}
