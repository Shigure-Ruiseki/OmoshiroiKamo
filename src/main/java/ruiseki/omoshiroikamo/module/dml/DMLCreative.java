package ruiseki.omoshiroikamo.module.dml;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.core.common.creative.BaseCreativeTab;
import ruiseki.omoshiroikamo.core.common.creative.CreativeTabRegistry;
import ruiseki.omoshiroikamo.core.common.creative.OKCreativeTab;
import ruiseki.omoshiroikamo.module.dml.common.init.DMLBlocks;
import ruiseki.omoshiroikamo.module.dml.common.init.DMLItems;

public class DMLCreative extends BaseCreativeTab {

    public static final DMLCreative INSTANCE = new DMLCreative();

    public static final OKCreativeTab TAB = CreativeTabRegistry.create("okDML");

    public static void preInit() {
        TAB.setIcon(new ItemStack(DMLItems.DEEP_LEARNER.getItem()));
        TAB.registerModule(INSTANCE);
    }

    @Override
    public void fillTab(CreativeTabs tab, List<ItemStack> list) {
        addBlock(tab, list, DMLBlocks.LOOT_FABRICATOR.getBlock());
        addBlock(tab, list, DMLBlocks.SIMULATION_CHAMBER.getBlock());
        addBlock(tab, list, DMLBlocks.MACHINE_CASING.getBlock());

        addItem(tab, list, DMLItems.DEEP_LEARNER.getItem());
        addItem(tab, list, DMLItems.CREATIVE_MODEL_LEARNER.getItem());
        addItem(tab, list, DMLItems.DATA_MODEL_BLANK.getItem());
        addItem(tab, list, DMLItems.DATA_MODEL.getItem());
        addItem(tab, list, DMLItems.PRISTINE_MATTER.getItem());
        addItem(tab, list, DMLItems.LIVING_MATTER.getItem());
        addItem(tab, list, DMLItems.POLYMER_CLAY.getItem());
        addItem(tab, list, DMLItems.SOOT_COVERED_PLATE.getItem());
        addItem(tab, list, DMLItems.SOOT_COVERED_REDSTONE.getItem());
    }
}
