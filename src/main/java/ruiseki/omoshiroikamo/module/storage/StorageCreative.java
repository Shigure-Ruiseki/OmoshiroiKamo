package ruiseki.omoshiroikamo.module.storage;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.core.creative.BaseCreativeTab;
import ruiseki.omoshiroikamo.core.creative.CreativeTabRegistry;
import ruiseki.omoshiroikamo.core.creative.OKCreativeTab;
import ruiseki.omoshiroikamo.module.storage.common.init.StorageBlocks;

public class StorageCreative extends BaseCreativeTab {

    public static final StorageCreative INSTANCE = new StorageCreative();

    public static final OKCreativeTab TAB = CreativeTabRegistry.create("okStorage");

    public static void preInit() {
        TAB.setIcon(new ItemStack(Items.apple));
        TAB.registerModule(INSTANCE);
    }

    @Override
    public void fillTab(CreativeTabs tab, List<ItemStack> list) {
        addBlock(tab, list, StorageBlocks.BARREL.getBlock());
    }

}
