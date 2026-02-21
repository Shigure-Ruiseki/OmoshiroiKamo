package ruiseki.omoshiroikamo.module.ids;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.core.creative.BaseCreativeTab;
import ruiseki.omoshiroikamo.core.creative.CreativeTabRegistry;
import ruiseki.omoshiroikamo.core.creative.OKCreativeTab;
import ruiseki.omoshiroikamo.module.ids.common.init.IDsBlocks;
import ruiseki.omoshiroikamo.module.ids.common.init.IDsItems;

public class IDsCreative extends BaseCreativeTab {

    public static final IDsCreative INSTANCE = new IDsCreative();

    public static final OKCreativeTab TAB = CreativeTabRegistry.create("okIDs");

    public static void preInit() {
        TAB.setIcon(new ItemStack(IDsItems.LOGIC_CARD.getItem()));
        TAB.registerModule(INSTANCE);
    }

    @Override
    public void fillTab(CreativeTabs tab, List<ItemStack> list) {
        addBlock(tab, list, IDsBlocks.CABLE.getBlock());
        addBlock(tab, list, IDsBlocks.PROGRAMMER.getBlock());

        addItem(tab, list, IDsItems.LOGIC_CARD.getItem());

        addItem(tab, list, IDsItems.REDSTONE_READER.getItem());
        addItem(tab, list, IDsItems.BLOCK_READER.getItem());
        addItem(tab, list, IDsItems.INVENTORY_READER.getItem());
        addItem(tab, list, IDsItems.FLUID_READER.getItem());

        addItem(tab, list, IDsItems.ITEM_IMPORTER.getItem());
        addItem(tab, list, IDsItems.ITEM_EXPORTER.getItem());
        addItem(tab, list, IDsItems.ITEM_INTERFACE.getItem());
        addItem(tab, list, IDsItems.ITEM_FILTER_INTERFACE.getItem());

        addItem(tab, list, IDsItems.ENERGY_IMPORTER.getItem());
        addItem(tab, list, IDsItems.ENERGY_EXPORTER.getItem());
        addItem(tab, list, IDsItems.ENERGY_INTERFACE.getItem());
        addItem(tab, list, IDsItems.ENERGY_FILTER_INTERFACE.getItem());

        addItem(tab, list, IDsItems.CRAFTING_INTERFACE.getItem());
        addItem(tab, list, IDsItems.STORAGE_TERMINAL.getItem());
    }
}
