package ruiseki.omoshiroikamo.common.init;

import static ruiseki.omoshiroikamo.config.backport.BackportConfigs.useBackpack;
import static ruiseki.omoshiroikamo.config.backport.BackportConfigs.useChicken;
import static ruiseki.omoshiroikamo.config.backport.BackportConfigs.useCow;
import static ruiseki.omoshiroikamo.config.backport.BackportConfigs.useDeepMobLearning;
import static ruiseki.omoshiroikamo.config.backport.BackportConfigs.useEnvironmentalTech;

import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.block.BlockOK;
import ruiseki.omoshiroikamo.common.block.backpack.BlockBackpack;
import ruiseki.omoshiroikamo.common.block.chicken.BlockBreeder;
import ruiseki.omoshiroikamo.common.block.chicken.BlockRoost;
import ruiseki.omoshiroikamo.common.block.chicken.BlockRoostCollector;
import ruiseki.omoshiroikamo.common.block.cow.BlockStall;
import ruiseki.omoshiroikamo.common.block.deepMobLearning.lootFabricator.BlockLootFabricator;
import ruiseki.omoshiroikamo.common.block.deepMobLearning.simulationCharmber.BlockSimulationChamber;
import ruiseki.omoshiroikamo.common.block.deepMobLearning.trialKeystone.BlockTrialKeystone;
import ruiseki.omoshiroikamo.common.block.multiblock.base.BlockAlabasterStructure;
import ruiseki.omoshiroikamo.common.block.multiblock.base.BlockBasaltStructure;
import ruiseki.omoshiroikamo.common.block.multiblock.base.BlockCrystal;
import ruiseki.omoshiroikamo.common.block.multiblock.base.BlockHardenedStructure;
import ruiseki.omoshiroikamo.common.block.multiblock.base.BlockMachineBase;
import ruiseki.omoshiroikamo.common.block.multiblock.modifier.BlockModifierAccuracy;
import ruiseki.omoshiroikamo.common.block.multiblock.modifier.BlockModifierCore;
import ruiseki.omoshiroikamo.common.block.multiblock.modifier.BlockModifierFireResistance;
import ruiseki.omoshiroikamo.common.block.multiblock.modifier.BlockModifierFlight;
import ruiseki.omoshiroikamo.common.block.multiblock.modifier.BlockModifierHaste;
import ruiseki.omoshiroikamo.common.block.multiblock.modifier.BlockModifierJumpBoost;
import ruiseki.omoshiroikamo.common.block.multiblock.modifier.BlockModifierNightVision;
import ruiseki.omoshiroikamo.common.block.multiblock.modifier.BlockModifierPiezo;
import ruiseki.omoshiroikamo.common.block.multiblock.modifier.BlockModifierRegeneration;
import ruiseki.omoshiroikamo.common.block.multiblock.modifier.BlockModifierResistance;
import ruiseki.omoshiroikamo.common.block.multiblock.modifier.BlockModifierSaturation;
import ruiseki.omoshiroikamo.common.block.multiblock.modifier.BlockModifierSpeed;
import ruiseki.omoshiroikamo.common.block.multiblock.modifier.BlockModifierStrength;
import ruiseki.omoshiroikamo.common.block.multiblock.modifier.BlockModifierWaterBreathing;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumBeacon.BlockQuantumBeacon;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.BlockColoredLens;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.BlockLaserCore;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.BlockLens;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.ore.BlockQuantumOreExtractor;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.res.BlockQuantumResExtractor;
import ruiseki.omoshiroikamo.common.block.multiblock.solarArray.BlockSolarArray;
import ruiseki.omoshiroikamo.common.block.multiblock.solarArray.BlockSolarCell;
import ruiseki.omoshiroikamo.common.util.Logger;
import ruiseki.omoshiroikamo.config.backport.BackpackConfig;

public enum ModBlocks {

    // spotless: off

    BACKPACK_BASE(useBackpack,
        BlockBackpack.create(
            ModObject.backpackLeather.unlocalisedName,
            BackpackConfig.leatherBackpackSlots,
            BackpackConfig.leatherUpgradeSlots)),
    BACKPACK_IRON(useBackpack,
        BlockBackpack.create(
            ModObject.blockBackpackIron.unlocalisedName,
            BackpackConfig.ironBackpackSlots,
            BackpackConfig.ironUpgradeSlots)),
    BACKPACK_GOLD(useBackpack,
        BlockBackpack.create(
            ModObject.blockBackpackGold.unlocalisedName,
            BackpackConfig.goldBackpackSlots,
            BackpackConfig.goldUpgradeSlots)),
    BACKPACK_DIAMOND(useBackpack,
        BlockBackpack.create(
            ModObject.blockBackpackDiamond.unlocalisedName,
            BackpackConfig.diamondBackpackSlots,
            BackpackConfig.diamondUpgradeSlots)),
    BACKPACK_OBSIDIAN(useBackpack,
        BlockBackpack.create(
            ModObject.blockBackpackObsidian.unlocalisedName,
            BackpackConfig.obsidianBackpackSlots,
            BackpackConfig.obsidianUpgradeSlots)),

    BLOCK_MICA(useEnvironmentalTech,
        new BlockOK(ModObject.blockMica.unlocalisedName, Material.rock).setTextureName("mica")),
    BLOCK_HARDENED_STONE(useEnvironmentalTech,
        new BlockOK(ModObject.blockHardenedStone.unlocalisedName, Material.rock).setTextureName("hardened_stone")),
    BLOCK_ALABASTER(useEnvironmentalTech,
        new BlockOK(ModObject.blockAlabaster.unlocalisedName, Material.rock).setTextureName("alabaster")),
    BLOCK_BASALT(useEnvironmentalTech,
        new BlockOK(ModObject.blockBasalt.unlocalisedName, Material.rock).setTextureName("basalt")),
    QUANTUM_ORE_EXTRACTOR(useEnvironmentalTech, BlockQuantumOreExtractor.create()),
    QUANTUM_RES_EXTRACTOR(useEnvironmentalTech, BlockQuantumResExtractor.create()),
    QUANTUM_BEACON(useEnvironmentalTech, BlockQuantumBeacon.create()),
    LASER_CORE(useEnvironmentalTech, BlockLaserCore.create()),
    COLORED_LENS(useEnvironmentalTech, BlockColoredLens.create()),
    LENS(useEnvironmentalTech, BlockLens.create()),
    SOLAR_CELL(useEnvironmentalTech, BlockSolarCell.create()),
    SOLAR_ARRAY(useEnvironmentalTech, BlockSolarArray.create()),
    BASALT_STRUCTURE(useEnvironmentalTech, BlockBasaltStructure.create()),
    ALABASTER_STRUCTURE(useEnvironmentalTech, BlockAlabasterStructure.create()),
    HARDENED_STRUCTURE(useEnvironmentalTech, BlockHardenedStructure.create()),
    MACHINE_BASE(useEnvironmentalTech, BlockMachineBase.create()),
    MODIFIER_PIEZO(useEnvironmentalTech, BlockModifierPiezo.create()),
    MODIFIER_SPEED(useEnvironmentalTech, BlockModifierSpeed.create()),
    MODIFIER_ACCURACY(useEnvironmentalTech, BlockModifierAccuracy.create()),
    MODIFIER_FLIGHT(useEnvironmentalTech, BlockModifierFlight.create()),
    MODIFIER_NIGHT_VISION(useEnvironmentalTech, BlockModifierNightVision.create()),
    MODIFIER_HASTE(useEnvironmentalTech, BlockModifierHaste.create()),
    MODIFIER_STRENGTH(useEnvironmentalTech, BlockModifierStrength.create()),
    MODIFIER_WATER_BREATHING(useEnvironmentalTech, BlockModifierWaterBreathing.create()),
    MODIFIER_REGENERATION(useEnvironmentalTech, BlockModifierRegeneration.create()),
    MODIFIER_SATURATION(useEnvironmentalTech, BlockModifierSaturation.create()),
    MODIFIER_RESISTANCE(useEnvironmentalTech, BlockModifierResistance.create()),
    MODIFIER_JUMP_BOOST(useEnvironmentalTech, BlockModifierJumpBoost.create()),
    MODIFIER_FIRE_RESISTANCE(useEnvironmentalTech, BlockModifierFireResistance.create()),
    MODIFIER_NULL(useEnvironmentalTech, BlockModifierCore.create()),
    BLOCK_CRYSTAL(useEnvironmentalTech, BlockCrystal.create()),

    STALL(useCow, BlockStall.create()),
    ROOST(useChicken, BlockRoost.create()),
    BREEDER(useChicken, BlockBreeder.create()),
    ROOST_COLLECTOR(useChicken, BlockRoostCollector.create()),

    LOOT_FABRICATOR(useDeepMobLearning, BlockLootFabricator.create()),
    SIMULATION_CHAMBER(useDeepMobLearning, BlockSimulationChamber.create()),
    TRIAL_KEYSTONE(useDeepMobLearning, BlockTrialKeystone.create()),

    ;
    // spotless: on

    public static final ModBlocks[] VALUES = values();

    public static void preInit() {
        for (ModBlocks block : VALUES) {
            if (block.isEnabled()) {
                try {
                    block.get()
                        .init();
                    Logger.info("Successfully initialized {}", block.name());
                } catch (Exception e) {
                    Logger.error("Failed to initialize block: +{}", block.name());
                }
            }
        }
    }

    private final boolean enabled;
    private final BlockOK block;

    ModBlocks(boolean enabled, BlockOK block) {
        this.enabled = enabled;
        this.block = block;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public BlockOK get() {
        return block;
    }

    public Item getItem() {
        return Item.getItemFromBlock(block);
    }

    public ItemStack newItemStack() {
        return newItemStack(1);
    }

    public ItemStack newItemStack(int count) {
        return newItemStack(count, 0);
    }

    public ItemStack newItemStack(int count, int meta) {
        return new ItemStack(this.get(), count, meta);
    }
}
