package ruiseki.omoshiroikamo.module.cows;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.core.creative.BaseCreativeTab;
import ruiseki.omoshiroikamo.core.creative.CreativeTabRegistry;
import ruiseki.omoshiroikamo.core.creative.OKCreativeTab;
import ruiseki.omoshiroikamo.module.cows.common.init.CowsBlocks;
import ruiseki.omoshiroikamo.module.cows.common.init.CowsItems;

public class CowsCreative extends BaseCreativeTab {

    public static final CowsCreative INSTANCE = new CowsCreative();

    public static final OKCreativeTab TAB = CreativeTabRegistry.create("okCow");

    public static void preInit() {
        TAB.setIcon(new ItemStack(CowsItems.COW_SPAWN_EGG.getItem()));
        TAB.registerModule(INSTANCE);
    }

    @Override
    public void fillTab(CreativeTabs tab, List<ItemStack> list) {
        addBlock(tab, list, CowsBlocks.STALL.getBlock());

        addItem(tab, list, CowsItems.COW_HALTER.getItem());
        addItem(tab, list, CowsItems.COW_SPAWN_EGG.getItem());
    }
}
