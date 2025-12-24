package ruiseki.omoshiroikamo;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.backpack.common.init.BackpackBlocks;
import ruiseki.omoshiroikamo.module.backpack.common.init.BackpackItems;
import ruiseki.omoshiroikamo.module.chickens.common.init.ChickensBlocks;
import ruiseki.omoshiroikamo.module.chickens.common.init.ChickensItems;
import ruiseki.omoshiroikamo.module.cows.common.init.CowsBlocks;
import ruiseki.omoshiroikamo.module.cows.common.init.CowsItems;
import ruiseki.omoshiroikamo.module.dml.common.init.DMLBlocks;
import ruiseki.omoshiroikamo.module.dml.common.init.DMLItems;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockBlocks;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockItems;

public abstract class OKCreativeTab extends CreativeTabs {

    public static final CreativeTabs BACKPACK = new OKCreativeTab("okBackpack") {

        @Override
        public ItemStack getIconItemStack() {
            return new ItemStack(BackpackBlocks.BACKPACK_BASE.getBlock());
        }

        @Override
        public void displayAllReleventItems(List<ItemStack> list) {
            this.list = list;

            addBlock(BackpackBlocks.BACKPACK_BASE.getBlock());
            addBlock(BackpackBlocks.BACKPACK_IRON.getBlock());
            addBlock(BackpackBlocks.BACKPACK_GOLD.getBlock());
            addBlock(BackpackBlocks.BACKPACK_DIAMOND.getBlock());
            addBlock(BackpackBlocks.BACKPACK_OBSIDIAN.getBlock());

            addItem(BackpackItems.BASE_UPGRADE.getItem());
            addItem(BackpackItems.STACK_UPGRADE.getItem());
            addItem(BackpackItems.MAGNET_UPGRADE.getItem());
            addItem(BackpackItems.ADVANCED_MAGNET_UPGRADE.getItem());
            addItem(BackpackItems.FEEDING_UPGRADE.getItem());
            addItem(BackpackItems.ADVANCED_FEEDING_UPGRADE.getItem());
            addItem(BackpackItems.PICKUP_UPGRADE.getItem());
            addItem(BackpackItems.ADVANCED_PICKUP_UPGRADE.getItem());
            addItem(BackpackItems.EVERLASTING_UPGRADE.getItem());
            addItem(BackpackItems.INCEPTION_UPGRADE.getItem());
            addItem(BackpackItems.FILTER_UPGRADE.getItem());
            addItem(BackpackItems.ADVANCED_FILTER_UPGRADE.getItem());
            addItem(BackpackItems.VOID_UPGRADE.getItem());
            addItem(BackpackItems.ADVANCED_VOID_UPGRADE.getItem());
            addItem(BackpackItems.CRAFTING_UPGRADE.getItem());
        }
    };

    public static final CreativeTabs MULTIBLOCK = new OKCreativeTab("okMultiblock") {

        @Override
        public ItemStack getIconItemStack() {
            return new ItemStack(MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.getBlock());
        }

        @Override
        public void displayAllReleventItems(List<ItemStack> list) {
            this.list = list;

            addBlock(MultiBlockBlocks.BLOCK_MICA.getBlock());
            addBlock(MultiBlockBlocks.BLOCK_HARDENED_STONE.getBlock());
            addBlock(MultiBlockBlocks.BLOCK_ALABASTER.getBlock());
            addBlock(MultiBlockBlocks.BLOCK_BASALT.getBlock());
            addBlock(MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.getBlock());
            addBlock(MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.getBlock());
            addBlock(MultiBlockBlocks.QUANTUM_BEACON.getBlock());
            addBlock(MultiBlockBlocks.SOLAR_ARRAY.getBlock());
            addBlock(MultiBlockBlocks.SOLAR_CELL.getBlock());
            addBlock(MultiBlockBlocks.HARDENED_STRUCTURE.getBlock());
            addBlock(MultiBlockBlocks.ALABASTER_STRUCTURE.getBlock());
            addBlock(MultiBlockBlocks.BASALT_STRUCTURE.getBlock());
            addBlock(MultiBlockBlocks.MACHINE_BASE.getBlock());
            addBlock(MultiBlockBlocks.LASER_CORE.getBlock());
            addBlock(MultiBlockBlocks.COLORED_LENS.getBlock());
            addBlock(MultiBlockBlocks.LENS.getBlock());
            addBlock(MultiBlockBlocks.MODIFIER_NULL.getBlock());
            addBlock(MultiBlockBlocks.MODIFIER_PIEZO.getBlock());
            addBlock(MultiBlockBlocks.MODIFIER_SPEED.getBlock());
            addBlock(MultiBlockBlocks.MODIFIER_ACCURACY.getBlock());
            addBlock(MultiBlockBlocks.MODIFIER_RESISTANCE.getBlock());
            addBlock(MultiBlockBlocks.MODIFIER_REGENERATION.getBlock());
            addBlock(MultiBlockBlocks.MODIFIER_NIGHT_VISION.getBlock());
            addBlock(MultiBlockBlocks.MODIFIER_FLIGHT.getBlock());
            addBlock(MultiBlockBlocks.MODIFIER_JUMP_BOOST.getBlock());
            addBlock(MultiBlockBlocks.MODIFIER_HASTE.getBlock());
            addBlock(MultiBlockBlocks.MODIFIER_STRENGTH.getBlock());
            addBlock(MultiBlockBlocks.MODIFIER_WATER_BREATHING.getBlock());
            addBlock(MultiBlockBlocks.MODIFIER_SATURATION.getBlock());
            addBlock(MultiBlockBlocks.MODIFIER_FIRE_RESISTANCE.getBlock());
            addBlock(MultiBlockBlocks.BLOCK_CRYSTAL.getBlock());

            addItem(MultiBlockItems.CRYSTAL.getItem());
            addItem(MultiBlockItems.ASSEMBLER.getItem());
            addItem(MultiBlockItems.STABILIZED_ENDER_PEAR.getItem());
            addItem(MultiBlockItems.PHOTOVOLTAIC_CELL.getItem());
        }
    };

    public static final CreativeTabs CHICKEN_COW = new OKCreativeTab("okChickenCow") {

        @Override
        public ItemStack getIconItemStack() {
            return new ItemStack(ChickensItems.CHICKEN.getItem());
        }

        @Override
        public void displayAllReleventItems(List<ItemStack> list) {
            this.list = list;

            addBlock(ChickensBlocks.ROOST.getBlock());
            addBlock(ChickensBlocks.BREEDER.getBlock());
            addBlock(ChickensBlocks.ROOST_COLLECTOR.getBlock());
            addBlock(CowsBlocks.STALL.getBlock());

            addItem(ChickensItems.ANALYZER.getItem());
            addItem(ChickensItems.CHICKEN_CATCHER.getItem());
            addItem(ChickensItems.CHICKEN.getItem());
            addItem(ChickensItems.COLORED_EGG.getItem());
            addItem(ChickensItems.CHICKEN_SPAWN_EGG.getItem());
            addItem(ChickensItems.LIQUID_EGG.getItem());
            addItem(ChickensItems.SOLID_XP.getItem());
            addItem(CowsItems.COW_HALTER.getItem());
            addItem(CowsItems.COW_SPAWN_EGG.getItem());
        }
    };

    public static final CreativeTabs DEEP_MOB_LEARNING = new OKCreativeTab("okDeepMobLearner") {

        @Override
        public ItemStack getIconItemStack() {
            return new ItemStack(DMLItems.DEEP_LEARNER.getItem());
        }

        @Override
        public void displayAllReleventItems(List<ItemStack> list) {
            this.list = list;

            addBlock(DMLBlocks.LOOT_FABRICATOR.getBlock());
            addBlock(DMLBlocks.SIMULATION_CHAMBER.getBlock());
            addBlock(DMLBlocks.MACHINE_CASING.getBlock());

            addItem(DMLItems.DEEP_LEARNER.getItem());
            addItem(DMLItems.CREATIVE_MODEL_LEARNER.getItem());
            addItem(DMLItems.DATA_MODEL_BLANK.getItem());
            addItem(DMLItems.DATA_MODEL.getItem());
            addItem(DMLItems.PRISTINE_MATTER.getItem());
            addItem(DMLItems.LIVING_MATTER.getItem());
            addItem(DMLItems.POLYMER_CLAY.getItem());
            addItem(DMLItems.SOOT_COVERED_PLATE.getItem());
            addItem(DMLItems.SOOT_COVERED_REDSTONE.getItem());
        }
    };

    List<ItemStack> list;

    public OKCreativeTab(String label) {
        super(label);
    }

    @Override
    public boolean hasSearchBar() {
        return false;
    }

    @Override
    public void displayAllReleventItems(List<ItemStack> list) {
        this.list = list;

        for (MultiBlockItems item : MultiBlockItems.values()) {
            addItem(item.getItem());
        }

        for (MultiBlockBlocks block : MultiBlockBlocks.values()) {
            addBlock(block.getBlock());
        }
    }

    public void addItem(Item item) {
        item.getSubItems(item, this, list);
    }

    public void addBlock(Block block) {
        ItemStack stack = new ItemStack(block);
        block.getSubBlocks(stack.getItem(), this, list);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getTranslatedTabLabel() {
        return LibMisc.LANG.localize("creativetab." + getTabLabel());
    }

    @Override
    public Item getTabIconItem() {
        return getIconItemStack().getItem();
    }
}
