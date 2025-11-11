package ruiseki.omoshiroikamo.common.init;

import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.block.BlockOK;
import ruiseki.omoshiroikamo.common.block.chicken.BlockBreeder;
import ruiseki.omoshiroikamo.common.block.chicken.BlockRoost;
import ruiseki.omoshiroikamo.common.block.chicken.BlockRoostCollector;
import ruiseki.omoshiroikamo.common.block.multiblock.base.BlockMachineBase;
import ruiseki.omoshiroikamo.common.block.multiblock.base.BlockStructureFrame;
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

public enum ModBlocks {

    BLOCK_MICA(true, new BlockOK(ModObject.blockMica, Material.rock).setTextureName("mica")),
    BLOCK_HARDENED_STONE(true,
        new BlockOK(ModObject.blockHardenedStone, Material.rock).setTextureName("hardened_stone")),
    BLOCK_ALABASTER(true, new BlockOK(ModObject.blockAlabaster, Material.rock).setTextureName("alabaster")),
    BLOCK_BASALT(true, new BlockOK(ModObject.blockBasalt, Material.rock).setTextureName("basalt")),

    QUANTUM_ORE_EXTRACTOR(true, BlockQuantumOreExtractor.create()),
    QUANTUM_RES_EXTRACTOR(true, BlockQuantumResExtractor.create()),
    QUANTUM_BEACON(true, BlockQuantumBeacon.create()),
    LASER_CORE(true, BlockLaserCore.create()),
    COLORED_LENS(true, BlockColoredLens.create()),
    LENS(true, BlockLens.create()),
    SOLAR_ARRAY(true, BlockSolarArray.create()),
    SOLAR_CELL(true, BlockSolarCell.create()),
    STRUCTURE_FRAME(true, BlockStructureFrame.create()),
    MACHINE_BASE(true, BlockMachineBase.create()),

    MODIFIER_PIEZO(true, BlockModifierPiezo.create()),
    MODIFIER_SPEED(true, BlockModifierSpeed.create()),
    MODIFIER_ACCURACY(true, BlockModifierAccuracy.create()),
    MODIFIER_FLIGHT(true, BlockModifierFlight.create()),
    MODIFIER_NIGHT_VISION(true, BlockModifierNightVision.create()),
    MODIFIER_HASTE(true, BlockModifierHaste.create()),
    MODIFIER_STRENGTH(true, BlockModifierStrength.create()),
    MODIFIER_WATER_BREATHING(true, BlockModifierWaterBreathing.create()),
    MODIFIER_REGENERATION(true, BlockModifierRegeneration.create()),
    MODIFIER_SATURATION(true, BlockModifierSaturation.create()),
    MODIFIER_RESISTANCE(true, BlockModifierResistance.create()),
    MODIFIER_JUMP_BOOST(true, BlockModifierJumpBoost.create()),
    MODIFIER_FIRE_RESISTANCE(true, BlockModifierFireResistance.create()),
    MODIFIER_NULL(true, BlockModifierCore.create()),

    ROOST(true, BlockRoost.create()),
    BREEDER(true, BlockBreeder.create()),
    ROOST_COLLECTOR(true, BlockRoostCollector.create()),
    ;

    public static final ModBlocks[] VALUES = values();

    public static void preInit() {
        for (ModBlocks block : VALUES) {
            if (block.isEnabled()) {
                try {
                    block.get()
                        .init();
                    Logger.info("Successfully initialized " + block.name());
                } catch (Exception e) {
                    Logger.error("Failed to initialize block: +" + block.name());
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
