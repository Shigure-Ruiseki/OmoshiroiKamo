package ruiseki.omoshiroikamo.module.chickens;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.core.creative.BaseCreativeTab;
import ruiseki.omoshiroikamo.core.creative.CreativeTabRegistry;
import ruiseki.omoshiroikamo.core.creative.OKCreativeTab;
import ruiseki.omoshiroikamo.module.chickens.common.init.ChickensBlocks;
import ruiseki.omoshiroikamo.module.chickens.common.init.ChickensItems;

public class ChickensCreative extends BaseCreativeTab {

    public static final ChickensCreative INSTANCE = new ChickensCreative();

    public static final OKCreativeTab TAB = CreativeTabRegistry.create("okChicken");

    public static void preInit() {
        TAB.setIcon(new ItemStack(ChickensItems.CHICKEN.getItem()));
        TAB.registerModule(INSTANCE);
    }

    @Override
    public void fillTab(CreativeTabs tab, List<ItemStack> list) {
        addBlock(tab, list, ChickensBlocks.ROOST.getBlock());
        addBlock(tab, list, ChickensBlocks.BREEDER.getBlock());
        addBlock(tab, list, ChickensBlocks.ROOST_COLLECTOR.getBlock());

        addItem(tab, list, ChickensItems.ANALYZER.getItem());
        addItem(tab, list, ChickensItems.CHICKEN_CATCHER.getItem());
        addItem(tab, list, ChickensItems.CHICKEN.getItem());
        addItem(tab, list, ChickensItems.COLORED_EGG.getItem());
        addItem(tab, list, ChickensItems.CHICKEN_SPAWN_EGG.getItem());
        addItem(tab, list, ChickensItems.LIQUID_EGG.getItem());
        addItem(tab, list, ChickensItems.SOLID_XP.getItem());
    }
}
