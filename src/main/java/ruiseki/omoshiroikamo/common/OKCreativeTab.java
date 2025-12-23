package ruiseki.omoshiroikamo.common;

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

public abstract class OKCreativeTab extends CreativeTabs {

    public static final CreativeTabs BACKPACK = new OKCreativeTab("okBackpack") {

        @Override
        public ItemStack getIconItemStack() {
            return new ItemStack(ModBlocks.BACKPACK_BASE.get());
        }

        @Override
        public void displayAllReleventItems(List<ItemStack> list) {
            this.list = list;

            addBlock(ModBlocks.BACKPACK_BASE.get());
            addBlock(ModBlocks.BACKPACK_IRON.get());
            addBlock(ModBlocks.BACKPACK_GOLD.get());
            addBlock(ModBlocks.BACKPACK_DIAMOND.get());
            addBlock(ModBlocks.BACKPACK_OBSIDIAN.get());

            addItem(ModItems.BASE_UPGRADE.getItem());
            addItem(ModItems.STACK_UPGRADE.getItem());
            addItem(ModItems.MAGNET_UPGRADE.getItem());
            addItem(ModItems.ADVANCED_MAGNET_UPGRADE.getItem());
            addItem(ModItems.FEEDING_UPGRADE.getItem());
            addItem(ModItems.ADVANCED_FEEDING_UPGRADE.getItem());
            addItem(ModItems.PICKUP_UPGRADE.getItem());
            addItem(ModItems.ADVANCED_PICKUP_UPGRADE.getItem());
            addItem(ModItems.EVERLASTING_UPGRADE.getItem());
            addItem(ModItems.INCEPTION_UPGRADE.getItem());
            addItem(ModItems.FILTER_UPGRADE.getItem());
            addItem(ModItems.ADVANCED_FILTER_UPGRADE.getItem());
            addItem(ModItems.VOID_UPGRADE.getItem());
            addItem(ModItems.ADVANCED_VOID_UPGRADE.getItem());
            addItem(ModItems.CRAFTING_UPGRADE.getItem());
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
            return new ItemStack(ModItems.CHICKEN.getItem());
        }

        @Override
        public void displayAllReleventItems(List<ItemStack> list) {
            this.list = list;

            addBlock(ModBlocks.ROOST.get());
            addBlock(ModBlocks.BREEDER.get());
            addBlock(ModBlocks.ROOST_COLLECTOR.get());
            addBlock(ModBlocks.STALL.get());

            addItem(ModItems.ANALYZER.getItem());
            addItem(ModItems.CHICKEN_CATCHER.getItem());
            addItem(ModItems.CHICKEN.getItem());
            addItem(ModItems.COLORED_EGG.getItem());
            addItem(ModItems.CHICKEN_SPAWN_EGG.getItem());
            addItem(ModItems.LIQUID_EGG.getItem());
            addItem(ModItems.SOLID_XP.getItem());
            addItem(ModItems.COW_HALTER.getItem());
            addItem(ModItems.COW_SPAWN_EGG.getItem());
        }
    };

    public static final CreativeTabs DEEP_MOB_LEARNING = new OKCreativeTab("okDeepMobLearner") {

        @Override
        public ItemStack getIconItemStack() {
            return new ItemStack(ModItems.DEEP_LEARNER.getItem());
        }

        @Override
        public void displayAllReleventItems(List<ItemStack> list) {
            this.list = list;

            addBlock(ModBlocks.LOOT_FABRICATOR.get());
            addBlock(ModBlocks.SIMULATION_CHAMBER.get());
            addBlock(ModBlocks.TRIAL_KEYSTONE.get());

            addItem(ModItems.DEEP_LEARNER.getItem());
            addItem(ModItems.CREATIVE_MODEL_LEARNER.getItem());
            addItem(ModItems.DATA_MODEL_BLANK.getItem());
            addItem(ModItems.DATA_MODEL.getItem());
            addItem(ModItems.PRISTINE_MATTER.getItem());
            addItem(ModItems.LIVING_MATTER.getItem());
            addItem(ModItems.POLYMER_CLAY.getItem());
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
