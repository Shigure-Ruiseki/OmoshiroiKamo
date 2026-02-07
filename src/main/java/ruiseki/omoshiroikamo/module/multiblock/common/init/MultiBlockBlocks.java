package ruiseki.omoshiroikamo.module.multiblock.common.init;

import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.common.block.BlockOK;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.multiblock.common.block.base.BlockAlabasterStructure;
import ruiseki.omoshiroikamo.module.multiblock.common.block.base.BlockBasaltStructure;
import ruiseki.omoshiroikamo.module.multiblock.common.block.base.BlockCrystal;
import ruiseki.omoshiroikamo.module.multiblock.common.block.base.BlockHardenedStructure;
import ruiseki.omoshiroikamo.module.multiblock.common.block.base.BlockMachineBase;
import ruiseki.omoshiroikamo.module.multiblock.common.block.modifier.BlockModifierAccuracy;
import ruiseki.omoshiroikamo.module.multiblock.common.block.modifier.BlockModifierCore;
import ruiseki.omoshiroikamo.module.multiblock.common.block.modifier.BlockModifierFireResistance;
import ruiseki.omoshiroikamo.module.multiblock.common.block.modifier.BlockModifierFlight;
import ruiseki.omoshiroikamo.module.multiblock.common.block.modifier.BlockModifierHaste;
import ruiseki.omoshiroikamo.module.multiblock.common.block.modifier.BlockModifierJumpBoost;
import ruiseki.omoshiroikamo.module.multiblock.common.block.modifier.BlockModifierLuck;
import ruiseki.omoshiroikamo.module.multiblock.common.block.modifier.BlockModifierNightVision;
import ruiseki.omoshiroikamo.module.multiblock.common.block.modifier.BlockModifierPiezo;
import ruiseki.omoshiroikamo.module.multiblock.common.block.modifier.BlockModifierRegeneration;
import ruiseki.omoshiroikamo.module.multiblock.common.block.modifier.BlockModifierResistance;
import ruiseki.omoshiroikamo.module.multiblock.common.block.modifier.BlockModifierSaturation;
import ruiseki.omoshiroikamo.module.multiblock.common.block.modifier.BlockModifierSpeed;
import ruiseki.omoshiroikamo.module.multiblock.common.block.modifier.BlockModifierStrength;
import ruiseki.omoshiroikamo.module.multiblock.common.block.modifier.BlockModifierWaterBreathing;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumBeacon.BlockQuantumBeacon;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.BlockColoredLens;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.BlockLaserCore;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.BlockLens;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.ore.BlockQuantumOreExtractor;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.res.BlockQuantumResExtractor;
import ruiseki.omoshiroikamo.module.multiblock.common.block.solarArray.BlockSolarArray;
import ruiseki.omoshiroikamo.module.multiblock.common.block.solarArray.BlockSolarCell;

public enum MultiBlockBlocks {

    // spotless: off

    BLOCK_MICA(new BlockOK(ModObject.blockMica.unlocalisedName, Material.rock).setTextureName("multiblock/mica")),
    BLOCK_HARDENED_STONE(
        new BlockOK(ModObject.blockHardenedStone.unlocalisedName, Material.rock).setTextureName("multiblock/hardened_stone")),
    BLOCK_ALABASTER(new BlockOK(ModObject.blockAlabaster.unlocalisedName, Material.rock).setTextureName("multiblock/alabaster")),
    BLOCK_BASALT(new BlockOK(ModObject.blockBasalt.unlocalisedName, Material.rock).setTextureName("multiblock/basalt")),
    QUANTUM_ORE_EXTRACTOR(BlockQuantumOreExtractor.create()),
    QUANTUM_RES_EXTRACTOR(BlockQuantumResExtractor.create()),
    QUANTUM_BEACON(BlockQuantumBeacon.create()),
    LASER_CORE(BlockLaserCore.create()),
    COLORED_LENS(BlockColoredLens.create()),
    LENS(BlockLens.create()),
    SOLAR_CELL(BlockSolarCell.create()),
    SOLAR_ARRAY(BlockSolarArray.create()),
    BASALT_STRUCTURE(BlockBasaltStructure.create()),
    ALABASTER_STRUCTURE(BlockAlabasterStructure.create()),
    HARDENED_STRUCTURE(BlockHardenedStructure.create()),
    MACHINE_BASE(BlockMachineBase.create()),
    MODIFIER_PIEZO(BlockModifierPiezo.create()),
    MODIFIER_SPEED(BlockModifierSpeed.create()),
    MODIFIER_ACCURACY(BlockModifierAccuracy.create()),
    MODIFIER_FLIGHT(BlockModifierFlight.create()),
    MODIFIER_NIGHT_VISION(BlockModifierNightVision.create()),
    MODIFIER_HASTE(BlockModifierHaste.create()),
    MODIFIER_STRENGTH(BlockModifierStrength.create()),
    MODIFIER_WATER_BREATHING(BlockModifierWaterBreathing.create()),
    MODIFIER_REGENERATION(BlockModifierRegeneration.create()),
    MODIFIER_SATURATION(BlockModifierSaturation.create()),
    MODIFIER_RESISTANCE(BlockModifierResistance.create()),
    MODIFIER_JUMP_BOOST(BlockModifierJumpBoost.create()),
    MODIFIER_FIRE_RESISTANCE(BlockModifierFireResistance.create()),
    MODIFIER_LUCK(BlockModifierLuck.create()),
    MODIFIER_NULL(BlockModifierCore.create()),
    BLOCK_CRYSTAL(BlockCrystal.create()),

    ;
    // spotless: on

    public static final MultiBlockBlocks[] VALUES = values();

    public static void preInit() {
        for (MultiBlockBlocks block : VALUES) {
            try {
                block.getBlock()
                    .init();
                Logger.info("Successfully initialized {}", block.name());
            } catch (Exception e) {
                Logger.error("Failed to initialize block: +{}", block.name());
            }
        }
    }

    private final BlockOK block;

    MultiBlockBlocks(BlockOK block) {
        this.block = block;
    }

    public BlockOK getBlock() {
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
        return new ItemStack(this.getBlock(), count, meta);
    }
}
