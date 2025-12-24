package ruiseki.omoshiroikamo;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.module.backpack.common.init.BackpackBlocks;
import ruiseki.omoshiroikamo.module.backpack.common.init.BackpackItems;
import ruiseki.omoshiroikamo.module.chickens.common.init.ChickensBlocks;
import ruiseki.omoshiroikamo.module.chickens.common.init.ChickensItems;
import ruiseki.omoshiroikamo.module.cows.common.init.CowsBlocks;
import ruiseki.omoshiroikamo.module.cows.common.init.CowsItems;
import ruiseki.omoshiroikamo.module.dml.common.init.DMLBlocks;
import ruiseki.omoshiroikamo.module.dml.common.init.DMLItems;

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
            return new ItemStack(ModBlocks.QUANTUM_ORE_EXTRACTOR.get());
        }

        @Override
        public void displayAllReleventItems(List<ItemStack> list) {
            this.list = list;

            addBlock(ModBlocks.BLOCK_MICA.get());
            addBlock(ModBlocks.BLOCK_HARDENED_STONE.get());
            addBlock(ModBlocks.BLOCK_ALABASTER.get());
            addBlock(ModBlocks.BLOCK_BASALT.get());
            addBlock(ModBlocks.QUANTUM_ORE_EXTRACTOR.get());
            addBlock(ModBlocks.QUANTUM_RES_EXTRACTOR.get());
            addBlock(ModBlocks.QUANTUM_BEACON.get());
            addBlock(ModBlocks.SOLAR_ARRAY.get());
            addBlock(ModBlocks.SOLAR_CELL.get());
            addBlock(ModBlocks.HARDENED_STRUCTURE.get());
            addBlock(ModBlocks.ALABASTER_STRUCTURE.get());
            addBlock(ModBlocks.BASALT_STRUCTURE.get());
            addBlock(ModBlocks.MACHINE_BASE.get());
            addBlock(ModBlocks.LASER_CORE.get());
            addBlock(ModBlocks.COLORED_LENS.get());
            addBlock(ModBlocks.LENS.get());
            addBlock(ModBlocks.MODIFIER_NULL.get());
            addBlock(ModBlocks.MODIFIER_PIEZO.get());
            addBlock(ModBlocks.MODIFIER_SPEED.get());
            addBlock(ModBlocks.MODIFIER_ACCURACY.get());
            addBlock(ModBlocks.MODIFIER_RESISTANCE.get());
            addBlock(ModBlocks.MODIFIER_REGENERATION.get());
            addBlock(ModBlocks.MODIFIER_NIGHT_VISION.get());
            addBlock(ModBlocks.MODIFIER_FLIGHT.get());
            addBlock(ModBlocks.MODIFIER_JUMP_BOOST.get());
            addBlock(ModBlocks.MODIFIER_HASTE.get());
            addBlock(ModBlocks.MODIFIER_STRENGTH.get());
            addBlock(ModBlocks.MODIFIER_WATER_BREATHING.get());
            addBlock(ModBlocks.MODIFIER_SATURATION.get());
            addBlock(ModBlocks.MODIFIER_FIRE_RESISTANCE.get());
            addBlock(ModBlocks.BLOCK_CRYSTAL.get());

            addItem(ModItems.CRYSTAL.getItem());
            addItem(ModItems.ASSEMBLER.getItem());
            addItem(ModItems.STABILIZED_ENDER_PEAR.getItem());
            addItem(ModItems.PHOTOVOLTAIC_CELL.getItem());
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

        for (ModItems item : ModItems.values()) {
            addItem(item.getItem());
        }

        for (ModBlocks block : ModBlocks.values()) {
            addBlock(block.get());
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
