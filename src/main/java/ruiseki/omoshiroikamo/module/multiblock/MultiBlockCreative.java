package ruiseki.omoshiroikamo.module.multiblock;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.core.creative.BaseCreativeTab;
import ruiseki.omoshiroikamo.core.creative.CreativeTabRegistry;
import ruiseki.omoshiroikamo.core.creative.OKCreativeTab;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockBlocks;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockItems;

public class MultiBlockCreative extends BaseCreativeTab {

    public static final MultiBlockCreative INSTANCE = new MultiBlockCreative();

    public static final OKCreativeTab TAB = CreativeTabRegistry.create("okMultiblock");

    public static void preInit() {
        TAB.setIcon(new ItemStack(MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.getBlock()));
        TAB.registerModule(INSTANCE);
    }

    @Override
    public void fillTab(CreativeTabs tab, List<ItemStack> list) {
        addBlock(tab, list, MultiBlockBlocks.BLOCK_MICA.getBlock());
        addBlock(tab, list, MultiBlockBlocks.BLOCK_HARDENED_STONE.getBlock());
        addBlock(tab, list, MultiBlockBlocks.BLOCK_ALABASTER.getBlock());
        addBlock(tab, list, MultiBlockBlocks.BLOCK_BASALT.getBlock());
        addBlock(tab, list, MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.getBlock());
        addBlock(tab, list, MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.getBlock());
        addBlock(tab, list, MultiBlockBlocks.QUANTUM_BEACON.getBlock());
        addBlock(tab, list, MultiBlockBlocks.SOLAR_ARRAY.getBlock());
        addBlock(tab, list, MultiBlockBlocks.SOLAR_CELL.getBlock());
        addBlock(tab, list, MultiBlockBlocks.HARDENED_STRUCTURE.getBlock());
        addBlock(tab, list, MultiBlockBlocks.ALABASTER_STRUCTURE.getBlock());
        addBlock(tab, list, MultiBlockBlocks.BASALT_STRUCTURE.getBlock());
        addBlock(tab, list, MultiBlockBlocks.MACHINE_BASE.getBlock());
        addBlock(tab, list, MultiBlockBlocks.LASER_CORE.getBlock());
        addBlock(tab, list, MultiBlockBlocks.COLORED_LENS.getBlock());
        addBlock(tab, list, MultiBlockBlocks.LENS.getBlock());
        addBlock(tab, list, MultiBlockBlocks.MODIFIER_NULL.getBlock());
        addBlock(tab, list, MultiBlockBlocks.MODIFIER_PIEZO.getBlock());
        addBlock(tab, list, MultiBlockBlocks.MODIFIER_SPEED.getBlock());
        addBlock(tab, list, MultiBlockBlocks.MODIFIER_ACCURACY.getBlock());
        addBlock(tab, list, MultiBlockBlocks.MODIFIER_RESISTANCE.getBlock());
        addBlock(tab, list, MultiBlockBlocks.MODIFIER_REGENERATION.getBlock());
        addBlock(tab, list, MultiBlockBlocks.MODIFIER_NIGHT_VISION.getBlock());
        addBlock(tab, list, MultiBlockBlocks.MODIFIER_FLIGHT.getBlock());
        addBlock(tab, list, MultiBlockBlocks.MODIFIER_JUMP_BOOST.getBlock());
        addBlock(tab, list, MultiBlockBlocks.MODIFIER_HASTE.getBlock());
        addBlock(tab, list, MultiBlockBlocks.MODIFIER_STRENGTH.getBlock());
        addBlock(tab, list, MultiBlockBlocks.MODIFIER_WATER_BREATHING.getBlock());
        addBlock(tab, list, MultiBlockBlocks.MODIFIER_SATURATION.getBlock());
        addBlock(tab, list, MultiBlockBlocks.MODIFIER_FIRE_RESISTANCE.getBlock());
        addBlock(tab, list, MultiBlockBlocks.BLOCK_CRYSTAL.getBlock());

        addItem(tab, list, MultiBlockItems.CRYSTAL.getItem());
        addItem(tab, list, MultiBlockItems.ASSEMBLER.getItem());
        addItem(tab, list, MultiBlockItems.STABILIZED_ENDER_PEAR.getItem());
        addItem(tab, list, MultiBlockItems.PHOTOVOLTAIC_CELL.getItem());
    }
}
