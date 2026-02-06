package ruiseki.omoshiroikamo.module.multiblock.common.init;

import static ruiseki.omoshiroikamo.api.enums.EnumDye.DYE_ORE_NAMES;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.core.lib.LibMods;
import ruiseki.omoshiroikamo.module.multiblock.common.block.base.BlockCrystal;

public class MultiblockRecipes {

    public static void init() {
        QuantumExtractorRecipes.init();
        blockRecipes();
        itemRecipes();
    }

    public static void blockRecipes() {

        // Hardened Stone
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.BLOCK_HARDENED_STONE.getItem(),
                "SCS",
                "CSC",
                "SCS",
                'S',
                "stone",
                'C',
                "cobblestone"));

        // Hardened Stone
        GameRegistry
            .addRecipe(new ShapelessOreRecipe(MultiBlockBlocks.BLOCK_HARDENED_STONE.getItem(), "stoneHardened"));

        // Basalt
        GameRegistry.addRecipe(new ShapelessOreRecipe(MultiBlockBlocks.BLOCK_BASALT.getItem(), "stoneBasalt"));

        // Alabaster
        GameRegistry.addRecipe(new ShapelessOreRecipe(MultiBlockBlocks.BLOCK_ALABASTER.getItem(), "stoneAlabaster"));

        // Structure T1
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.BASALT_STRUCTURE.newItemStack(1, 0),
                " I ",
                "CBC",
                " L ",
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 0),
                'I',
                "ingotIron",
                'L',
                "gemLapis",
                'B',
                "stoneBasalt"));
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.ALABASTER_STRUCTURE.newItemStack(1, 0),
                " I ",
                "CBC",
                " L ",
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 0),
                'I',
                "ingotIron",
                'L',
                "gemLapis",
                'B',
                "stoneAlabaster"));
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.HARDENED_STRUCTURE.newItemStack(1, 0),
                " I ",
                "CBC",
                " L ",
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 0),
                'I',
                "ingotIron",
                'L',
                "gemLapis",
                'B',
                "stoneHardened"));

        // Structure T2
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.BASALT_STRUCTURE.newItemStack(1, 1),
                " I ",
                "CBC",
                " L ",
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 1),
                'I',
                "ingotIron",
                'L',
                "gemLapis",
                'B',
                MultiBlockBlocks.BASALT_STRUCTURE.newItemStack(1, 0)));
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.ALABASTER_STRUCTURE.newItemStack(1, 1),
                " I ",
                "CBC",
                " L ",
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 1),
                'I',
                "ingotIron",
                'L',
                "gemLapis",
                'B',
                MultiBlockBlocks.ALABASTER_STRUCTURE.newItemStack(1, 0)));
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.HARDENED_STRUCTURE.newItemStack(1, 1),
                " I ",
                "CBC",
                " L ",
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 1),
                'I',
                "ingotIron",
                'L',
                "gemLapis",
                'B',
                MultiBlockBlocks.HARDENED_STRUCTURE.newItemStack(1, 0)));

        // Structure T3
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.BASALT_STRUCTURE.newItemStack(1, 2),
                " I ",
                "CBC",
                " L ",
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 2),
                'I',
                "gemDiamond",
                'L',
                "gemQuartz",
                'B',
                MultiBlockBlocks.BASALT_STRUCTURE.newItemStack(1, 1)));
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.ALABASTER_STRUCTURE.newItemStack(1, 2),
                " I ",
                "CBC",
                " L ",
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 2),
                'I',
                "gemDiamond",
                'L',
                "gemQuartz",
                'B',
                MultiBlockBlocks.ALABASTER_STRUCTURE.newItemStack(1, 1)));
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.HARDENED_STRUCTURE.newItemStack(1, 2),
                " I ",
                "CBC",
                " L ",
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 2),
                'I',
                "gemDiamond",
                'L',
                "gemQuartz",
                'B',
                MultiBlockBlocks.HARDENED_STRUCTURE.newItemStack(1, 1)));

        // Structure T4
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.BASALT_STRUCTURE.newItemStack(1, 3),
                " I ",
                "CBC",
                " L ",
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 3),
                'I',
                "gemEmerald",
                'L',
                "gemQuartz",
                'B',
                MultiBlockBlocks.BASALT_STRUCTURE.newItemStack(1, 2)));
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.ALABASTER_STRUCTURE.newItemStack(1, 3),
                " I ",
                "CBC",
                " L ",
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 3),
                'I',
                "gemEmerald",
                'L',
                "gemQuartz",
                'B',
                MultiBlockBlocks.ALABASTER_STRUCTURE.newItemStack(1, 2)));
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.HARDENED_STRUCTURE.newItemStack(1, 3),
                " I ",
                "CBC",
                " L ",
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 3),
                'I',
                "gemEmerald",
                'L',
                "gemQuartz",
                'B',
                MultiBlockBlocks.HARDENED_STRUCTURE.newItemStack(1, 2)));

        // Structure T5
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.BASALT_STRUCTURE.newItemStack(1, 4),
                " I ",
                "CBC",
                " L ",
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 4),
                'I',
                "itemNetherStar",
                'L',
                "gemQuartz",
                'B',
                MultiBlockBlocks.BASALT_STRUCTURE.newItemStack(1, 3)));
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.ALABASTER_STRUCTURE.newItemStack(1, 4),
                " I ",
                "CBC",
                " L ",
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 4),
                'I',
                "itemNetherStar",
                'L',
                "gemQuartz",
                'B',
                MultiBlockBlocks.ALABASTER_STRUCTURE.newItemStack(1, 3)));
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.HARDENED_STRUCTURE.newItemStack(1, 4),
                " I ",
                "CBC",
                " L ",
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 4),
                'I',
                "itemNetherStar",
                'L',
                "gemQuartz",
                'B',
                MultiBlockBlocks.HARDENED_STRUCTURE.newItemStack(1, 3)));

        // Structure T6
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.BASALT_STRUCTURE.newItemStack(1, 5),
                " I ",
                "CBC",
                " I ",
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 5),
                'I',
                "itemNetherStar",
                'B',
                MultiBlockBlocks.BASALT_STRUCTURE.newItemStack(1, 4)));
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.ALABASTER_STRUCTURE.newItemStack(1, 5),
                " I ",
                "CBC",
                " I ",
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 5),
                'I',
                "itemNetherStar",
                'B',
                MultiBlockBlocks.ALABASTER_STRUCTURE.newItemStack(1, 4)));
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.HARDENED_STRUCTURE.newItemStack(1, 5),
                " I ",
                "CBC",
                " I ",
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 5),
                'I',
                "itemNetherStar",
                'B',
                MultiBlockBlocks.HARDENED_STRUCTURE.newItemStack(1, 4)));

        // Solar Cell T1
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.SOLAR_CELL.newItemStack(1, 0),
                "GGG",
                "CCC",
                "RIR",
                'G',
                MultiBlockItems.CRYSTAL.newItemStack(1, 0),
                'I',
                "stoneBasalt",
                'R',
                "dustRedstone",
                'C',
                MultiBlockItems.PHOTOVOLTAIC_CELL.getItem()));
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.SOLAR_CELL.newItemStack(1, 0),
                "GGG",
                "CCC",
                "RIR",
                'G',
                MultiBlockItems.CRYSTAL.newItemStack(1, 0),
                'I',
                "stoneAlabaster",
                'R',
                "dustRedstone",
                'C',
                MultiBlockItems.PHOTOVOLTAIC_CELL.getItem()));
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.SOLAR_CELL.newItemStack(1, 0),
                "GGG",
                "CCC",
                "RIR",
                'G',
                MultiBlockItems.CRYSTAL.newItemStack(1, 0),
                'I',
                "stoneHardened",
                'R',
                "dustRedstone",
                'C',
                MultiBlockItems.PHOTOVOLTAIC_CELL.getItem()));

        // Solar Cell T2
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.SOLAR_CELL.newItemStack(1, 1),
                "   ",
                "GGG",
                "CIC",
                'G',
                MultiBlockItems.CRYSTAL.newItemStack(1, 1),
                'I',
                MultiBlockBlocks.SOLAR_CELL.newItemStack(1, 0),
                'C',
                MultiBlockItems.PHOTOVOLTAIC_CELL.getItem()));

        // Solar Cell T3
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.SOLAR_CELL.newItemStack(1, 2),
                "   ",
                "GGG",
                "CIC",
                'G',
                MultiBlockItems.CRYSTAL.newItemStack(1, 2),
                'I',
                MultiBlockBlocks.SOLAR_CELL.newItemStack(1, 1),
                'C',
                MultiBlockItems.PHOTOVOLTAIC_CELL.getItem()));

        // Solar Cell T4
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.SOLAR_CELL.newItemStack(1, 3),
                "   ",
                "GGG",
                "CIC",
                'G',
                MultiBlockItems.CRYSTAL.newItemStack(1, 3),
                'I',
                MultiBlockBlocks.SOLAR_CELL.newItemStack(1, 2),
                'C',
                MultiBlockItems.PHOTOVOLTAIC_CELL.getItem()));

        // Solar Cell T5
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.SOLAR_CELL.newItemStack(1, 4),
                "   ",
                "GGG",
                "CIC",
                'G',
                MultiBlockItems.CRYSTAL.newItemStack(1, 4),
                'I',
                MultiBlockBlocks.SOLAR_CELL.newItemStack(1, 3),
                'C',
                MultiBlockItems.PHOTOVOLTAIC_CELL.getItem()));

        // Solar Cell T6
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.SOLAR_CELL.newItemStack(1, 5),
                "   ",
                "GGG",
                "CIC",
                'G',
                MultiBlockItems.CRYSTAL.newItemStack(1, 5),
                'I',
                MultiBlockBlocks.SOLAR_CELL.newItemStack(1, 4),
                'C',
                MultiBlockItems.PHOTOVOLTAIC_CELL.getItem()));

        // Solar Array Tier 1
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.SOLAR_ARRAY.newItemStack(1, 0),
                "GLG",
                "LCL",
                "GLG",
                'G',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 0),
                'L',
                "blockLapis",
                'C',
                MultiBlockBlocks.SOLAR_CELL.newItemStack(1, 0)));

        // Solar Array Tier 2
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.SOLAR_ARRAY.newItemStack(1, 1),
                "GLG",
                "LCL",
                "GLG",
                'G',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 1),
                'L',
                MultiBlockBlocks.SOLAR_CELL.newItemStack(1, 1),
                'C',
                MultiBlockBlocks.SOLAR_ARRAY.newItemStack(1, 0)));

        // Solar Array Tier 3
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.SOLAR_ARRAY.newItemStack(1, 2),
                "GLG",
                "LCL",
                "GLG",
                'G',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 2),
                'L',
                MultiBlockBlocks.SOLAR_CELL.newItemStack(1, 2),
                'C',
                MultiBlockBlocks.SOLAR_ARRAY.newItemStack(1, 1)));

        // Solar Array Tier 4
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.SOLAR_ARRAY.newItemStack(1, 3),
                "GLG",
                "LCL",
                "GLG",
                'G',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 3),
                'L',
                MultiBlockBlocks.SOLAR_CELL.newItemStack(1, 3),
                'C',
                MultiBlockBlocks.SOLAR_ARRAY.newItemStack(1, 2)));

        // Solar Array T5
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.SOLAR_ARRAY.newItemStack(1, 4),
                "GLG",
                "LCL",
                "GLG",
                'G',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 4),
                'L',
                MultiBlockBlocks.SOLAR_CELL.newItemStack(1, 4),
                'C',
                MultiBlockBlocks.SOLAR_ARRAY.newItemStack(1, 3)));

        // Solar Array T6
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.SOLAR_ARRAY.newItemStack(1, 5),
                "GLG",
                "LCL",
                "GLG",
                'G',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 5),
                'L',
                MultiBlockBlocks.SOLAR_CELL.newItemStack(1, 5),
                'C',
                MultiBlockBlocks.SOLAR_ARRAY.newItemStack(1, 4)));

        // Quantum Ore T1
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 0),
                "COC",
                "CcC",
                "BLB",
                'C',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 0),
                'L',
                MultiBlockBlocks.LENS.newItemStack(1, 0),
                'O',
                "blockDiamond",
                'B',
                "stoneBasalt",
                'c',
                MultiBlockBlocks.LASER_CORE.getItem()));
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 0),
                "COC",
                "CcC",
                "BLB",
                'C',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 0),
                'L',
                MultiBlockBlocks.LENS.newItemStack(1, 0),
                'O',
                "blockDiamond",
                'B',
                "stoneAlabaster",
                'c',
                MultiBlockBlocks.LASER_CORE.getItem()));
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 0),
                "COC",
                "CcC",
                "BLB",
                'C',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 0),
                'L',
                MultiBlockBlocks.LENS.newItemStack(1, 0),
                'O',
                "blockDiamond",
                'B',
                "stoneHardened",
                'c',
                MultiBlockBlocks.LASER_CORE.getItem()));

        // Quantum Ore T2
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 1),
                "COC",
                "CcC",
                "CLC",
                'C',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 1),
                'L',
                MultiBlockBlocks.LENS.newItemStack(1, 0),
                'O',
                "blockDiamond",
                'c',
                MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 0)));

        // Quantum Ore T3
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 2),
                "COC",
                "CcC",
                "CLC",
                'C',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 2),
                'L',
                MultiBlockBlocks.LENS.newItemStack(1, 0),
                'O',
                "blockDiamond",
                'c',
                MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 1)));

        // Quantum Ore T4
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 3),
                "COC",
                "CcC",
                "CLC",
                'C',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 3),
                'L',
                MultiBlockBlocks.LENS.newItemStack(1, 0),
                'O',
                "blockDiamond",
                'c',
                MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 2)));

        // Quantum Ore T5
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 4),
                "COC",
                "CcC",
                "CLC",
                'C',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 4),
                'L',
                MultiBlockBlocks.LENS.newItemStack(1, 0),
                'O',
                "blockDiamond",
                'c',
                MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 3)));

        // Quantum Ore T6
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 5),
                "COC",
                "CcC",
                "CLC",
                'C',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 5),
                'L',
                MultiBlockBlocks.LENS.newItemStack(1, 0),
                'O',
                "blockDiamond",
                'c',
                MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 4)));

        // Quantum Res T1
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 0),
                "COC",
                "CcC",
                "BLB",
                'C',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 0),
                'L',
                MultiBlockBlocks.LENS.newItemStack(1, 0),
                'O',
                "blockObsidian",
                'B',
                "stoneBasalt",
                'c',
                MultiBlockBlocks.LASER_CORE.getItem()));
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 0),
                "COC",
                "CcC",
                "BLB",
                'C',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 0),
                'L',
                MultiBlockBlocks.LENS.newItemStack(1, 0),
                'O',
                "blockObsidian",
                'B',
                "stoneAlabaster",
                'c',
                MultiBlockBlocks.LASER_CORE.getItem()));
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 0),
                "COC",
                "CcC",
                "BLB",
                'C',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 0),
                'L',
                MultiBlockBlocks.LENS.newItemStack(1, 0),
                'O',
                "blockObsidian",
                'B',
                "stoneHardened",
                'c',
                MultiBlockBlocks.LASER_CORE.getItem()));

        // Quantum Res T2
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 1),
                "COC",
                "CcC",
                "CLC",
                'C',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 1),
                'L',
                MultiBlockBlocks.LENS.newItemStack(1, 0),
                'O',
                "blockObsidian",
                'c',
                MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 0)));

        // Quantum Res T3
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 2),
                "COC",
                "CcC",
                "CLC",
                'C',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 2),
                'L',
                MultiBlockBlocks.LENS.newItemStack(1, 0),
                'O',
                "blockObsidian",
                'c',
                MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 1)));

        // Quantum Res T4
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 3),
                "COC",
                "CcC",
                "CLC",
                'C',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 3),
                'L',
                MultiBlockBlocks.LENS.newItemStack(1, 0),
                'O',
                "blockObsidian",
                'c',
                MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 2)));

        // Quantum Res T5
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 4),
                "COC",
                "CcC",
                "CLC",
                'C',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 4),
                'L',
                MultiBlockBlocks.LENS.newItemStack(1, 0),
                'O',
                "blockObsidian",
                'c',
                MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 3)));

        // Quantum Res T6
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 5),
                "COC",
                "CcC",
                "CLC",
                'C',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 5),
                'L',
                MultiBlockBlocks.LENS.newItemStack(1, 0),
                'O',
                "blockObsidian",
                'c',
                MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 4)));

        // Quantum Beacon T1
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.QUANTUM_BEACON.newItemStack(1, 0),
                "CMC",
                "CBC",
                "SNS",
                'S',
                "stoneBasalt",
                'C',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 0),
                'M',
                MultiBlockBlocks.BLOCK_MICA.getItem(),
                'N',
                MultiBlockBlocks.MODIFIER_NULL.getItem(),
                'B',
                Blocks.beacon));
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.QUANTUM_BEACON.newItemStack(1, 0),
                "CMC",
                "CBC",
                "SNS",
                'S',
                "stoneAlabaster",
                'C',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 0),
                'M',
                MultiBlockBlocks.BLOCK_MICA.getItem(),
                'N',
                MultiBlockBlocks.MODIFIER_NULL.getItem(),
                'B',
                Blocks.beacon));
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.QUANTUM_BEACON.newItemStack(1, 0),
                "CMC",
                "CBC",
                "SNS",
                'S',
                "stoneHardened",
                'C',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 0),
                'M',
                MultiBlockBlocks.BLOCK_MICA.getItem(),
                'N',
                MultiBlockBlocks.MODIFIER_NULL.getItem(),
                'B',
                Blocks.beacon));

        // Quantum Beacon T2
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.QUANTUM_BEACON.newItemStack(1, 1),
                "CMC",
                "CBC",
                "MNM",
                'C',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 1),
                'M',
                MultiBlockBlocks.BLOCK_MICA.getItem(),
                'N',
                MultiBlockBlocks.MODIFIER_NULL.getItem(),
                'B',
                MultiBlockBlocks.QUANTUM_BEACON.newItemStack(1, 0)));

        // Quantum Beacon T3
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.QUANTUM_BEACON.newItemStack(1, 2),
                "CMC",
                "CBC",
                "MNM",
                'C',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 2),
                'M',
                MultiBlockBlocks.BLOCK_MICA.getItem(),
                'N',
                MultiBlockBlocks.MODIFIER_NULL.getItem(),
                'B',
                MultiBlockBlocks.QUANTUM_BEACON.newItemStack(1, 1)));

        // Quantum Beacon T4
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.QUANTUM_BEACON.newItemStack(1, 3),
                "CMC",
                "CBC",
                "MNM",
                'C',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 3),
                'M',
                MultiBlockBlocks.BLOCK_MICA.getItem(),
                'N',
                MultiBlockBlocks.MODIFIER_NULL.getItem(),
                'B',
                MultiBlockBlocks.QUANTUM_BEACON.newItemStack(1, 2)));

        // Quantum Beacon T5
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.QUANTUM_BEACON.newItemStack(1, 4),
                "CMC",
                "CBC",
                "MNM",
                'C',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 4),
                'M',
                MultiBlockBlocks.BLOCK_MICA.getItem(),
                'N',
                MultiBlockBlocks.MODIFIER_NULL.getItem(),
                'B',
                MultiBlockBlocks.QUANTUM_BEACON.newItemStack(1, 3)));

        // Quantum Beacon T6
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.QUANTUM_BEACON.newItemStack(1, 5),
                "CMC",
                "CBC",
                "MNM",
                'C',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 5),
                'M',
                MultiBlockBlocks.BLOCK_MICA.getItem(),
                'N',
                MultiBlockBlocks.MODIFIER_NULL.getItem(),
                'B',
                MultiBlockBlocks.QUANTUM_BEACON.newItemStack(1, 4)));

        // Clear Lens
        GameRegistry
            .addRecipe(new ShapedOreRecipe(MultiBlockBlocks.LENS.getItem(), "G G", "GGG", "G G", 'G', "blockGlass"));

        // Color Lens
        for (int i = 0; i < DYE_ORE_NAMES.length; i++) {
            GameRegistry.addRecipe(
                new ShapelessOreRecipe(
                    MultiBlockBlocks.COLORED_LENS.newItemStack(1, i),
                    MultiBlockBlocks.LENS.getItem(),
                    DYE_ORE_NAMES[i]));
            GameRegistry.addRecipe(
                new ShapelessOreRecipe(
                    MultiBlockBlocks.LENS.getItem(),
                    MultiBlockBlocks.COLORED_LENS.newItemStack(1, i)));
        }

        // Laser Core
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.LASER_CORE.getItem(),
                "GRG",
                "I I",
                "GRG",
                'G',
                "blockGlass",
                'R',
                "dustRedstone",
                'I',
                "ingotIron"));

        // Null Modifier
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.MODIFIER_NULL.getItem(),
                "IGI",
                "GSG",
                "IGI",
                'S',
                "stone",
                'G',
                "blockGlass",
                'I',
                "blockIron"));

        // Piezo Modifier
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.MODIFIER_PIEZO.getItem(),
                "RMR",
                "MNM",
                "IMI",
                'R',
                "blockRedstone",
                'M',
                MultiBlockBlocks.BLOCK_MICA.getItem(),
                'N',
                MultiBlockBlocks.MODIFIER_NULL.getItem(),
                'I',
                "blockIron"));

        // Speed Modifier
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.MODIFIER_SPEED.getItem(),
                "RCR",
                "MNM",
                "RcR",
                'R',
                "blockRedstone",
                'M',
                MultiBlockBlocks.BLOCK_MICA.getItem(),
                'N',
                MultiBlockBlocks.MODIFIER_NULL.getItem(),
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 1),
                'c',
                MultiBlockItems.CRYSTAL.newItemStack(1, 3)));

        // Accuracy Modifier
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.MODIFIER_ACCURACY.getItem(),
                "DCD",
                "MNM",
                "DcD",
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 2),
                'c',
                MultiBlockItems.CRYSTAL.newItemStack(1, 3),
                'M',
                MultiBlockBlocks.BLOCK_MICA.getItem(),
                'N',
                MultiBlockBlocks.MODIFIER_NULL.getItem(),
                'D',
                "blockDiamond"));

        // Luck Modifier
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.MODIFIER_LUCK.getItem(),
                "LCL",
                "MNM",
                "LcL",
                'M',
                MultiBlockBlocks.BLOCK_MICA.getItem(),
                'N',
                MultiBlockBlocks.MODIFIER_NULL.getItem(),
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 7),
                'c',
                MultiBlockItems.CRYSTAL.newItemStack(1, 3),
                'L',
                "blockLapis"));

        // Flight Modifier
        if (!LibMods.EtFuturum.isLoaded()) {
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    MultiBlockBlocks.MODIFIER_FLIGHT.getItem(),
                    "PFP",
                    "MNM",
                    "LfL",
                    'P',
                    Blocks.sticky_piston,
                    'M',
                    MultiBlockBlocks.BLOCK_MICA.getItem(),
                    'F',
                    MultiBlockItems.CRYSTAL.newItemStack(1, 4),
                    'f',
                    MultiBlockItems.CRYSTAL.newItemStack(1, 3),
                    'L',
                    "itemLeather",
                    'N',
                    MultiBlockBlocks.MODIFIER_NULL.getItem()));
        } else {

            // Flight Modifier
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    MultiBlockBlocks.MODIFIER_FLIGHT.getBlock(),
                    "EFE",
                    "MNM",
                    "LfL",
                    'E',
                    ganymedes01.etfuturum.ModItems.ELYTRA.get(),
                    'F',
                    MultiBlockItems.CRYSTAL.newItemStack(1, 4),
                    'f',
                    MultiBlockItems.CRYSTAL.newItemStack(1, 3),
                    'M',
                    MultiBlockBlocks.BLOCK_MICA.getBlock(),
                    'L',
                    "itemLeather",
                    'N',
                    MultiBlockBlocks.MODIFIER_NULL.getBlock()));
        }

        // Night Vision Modifier
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.MODIFIER_NIGHT_VISION.getItem(),
                "ECE",
                "MNM",
                "EcE",
                'E',
                Items.emerald,
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 4),
                'c',
                MultiBlockItems.CRYSTAL.newItemStack(1, 3),
                'M',
                MultiBlockBlocks.BLOCK_MICA.getItem(),
                'N',
                MultiBlockBlocks.MODIFIER_NULL.getItem()));

        // Strength Modifier
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.MODIFIER_STRENGTH.getItem(),
                "SCS",
                "MNM",
                "ScS",
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 2),
                'c',
                MultiBlockItems.CRYSTAL.newItemStack(1, 3),
                'S',
                Items.diamond_sword,
                'M',
                MultiBlockBlocks.BLOCK_MICA.getItem(),
                'N',
                MultiBlockBlocks.MODIFIER_NULL.getItem()));

        // Water Breathing Modifier
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.MODIFIER_WATER_BREATHING.getItem(),
                "PCP",
                "MNM",
                "PcP",
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 1),
                'c',
                MultiBlockItems.CRYSTAL.newItemStack(1, 3),
                'P',
                new ItemStack(Items.potionitem, 1, 8205),
                'M',
                MultiBlockBlocks.BLOCK_MICA.getItem(),
                'N',
                MultiBlockBlocks.MODIFIER_NULL.getItem()));

        // Regeneration Modifier
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.MODIFIER_REGENERATION.getItem(),
                "ACA",
                "MNM",
                "ACA",
                'A',
                new ItemStack(Items.golden_apple, 1, 0),
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 3),
                'M',
                MultiBlockBlocks.BLOCK_MICA.getItem(),
                'N',
                MultiBlockBlocks.MODIFIER_NULL.getItem()));

        // Fire Resistance Modifier
        ItemStack fireprot4Book = new ItemStack(Items.enchanted_book);
        fireprot4Book.addEnchantment(Enchantment.fireProtection, 4);
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.MODIFIER_FIRE_RESISTANCE.getItem(),
                "CPC",
                "PNP",
                "MPM",
                'P',
                fireprot4Book,
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 2),
                'M',
                MultiBlockBlocks.BLOCK_MICA.getItem(),
                'N',
                MultiBlockBlocks.MODIFIER_NULL.getItem()));

        // Jump Boost Modifier
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.MODIFIER_JUMP_BOOST.getItem(),
                "BCB",
                "MNM",
                "BcB",
                'B',
                Items.diamond_boots,
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 1),
                'c',
                MultiBlockItems.CRYSTAL.newItemStack(1, 3),
                'M',
                MultiBlockBlocks.BLOCK_MICA.getItem(),
                'N',
                MultiBlockBlocks.MODIFIER_NULL.getItem()));

        // Resistance Modifier
        ItemStack prot4Book = new ItemStack(Items.enchanted_book);
        prot4Book.addEnchantment(Enchantment.protection, 4);
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.MODIFIER_RESISTANCE.getItem(),
                "CPC",
                "ONO",
                "MPM",
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 2),
                'O',
                "blockObsidian",
                'P',
                prot4Book,
                'M',
                MultiBlockBlocks.BLOCK_MICA.getItem(),
                'N',
                MultiBlockBlocks.MODIFIER_NULL.getItem()));

        // Haste Modifier
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.MODIFIER_HASTE.getItem(),
                "PCP",
                "MNM",
                "PcP",
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 2),
                'c',
                MultiBlockItems.CRYSTAL.newItemStack(1, 3),
                'P',
                Items.golden_pickaxe,
                'M',
                MultiBlockBlocks.BLOCK_MICA.getItem(),
                'N',
                MultiBlockBlocks.MODIFIER_NULL.getItem()));

        // Saturation Modifier
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.MODIFIER_SATURATION.getItem(),
                "ACA",
                "MNM",
                "AcA",
                'C',
                MultiBlockItems.CRYSTAL.newItemStack(1, 4),
                'c',
                MultiBlockItems.CRYSTAL.newItemStack(1, 3),
                'A',
                Items.golden_carrot,
                'M',
                MultiBlockBlocks.BLOCK_MICA.getItem(),
                'N',
                MultiBlockBlocks.MODIFIER_NULL.getItem()));

        // Machine Base Basalt
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.MACHINE_BASE.newItemStack(1, 0),
                "RBR",
                "BQB",
                "NBN",
                'R',
                "dustRedstone",
                'B',
                "barsIron",
                'N',
                "nuggetGold",
                'Q',
                "stoneBasalt"));

        // Machine Base Hardened Stone
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.MACHINE_BASE.newItemStack(1, 1),
                "RBR",
                "BQB",
                "NBN",
                'R',
                "dustRedstone",
                'B',
                "barsIron",
                'N',
                "nuggetGold",
                'Q',
                "stoneHardened"));

        // Machine Base Alabaster
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.MACHINE_BASE.newItemStack(1, 2),
                "RBR",
                "BQB",
                "NBN",
                'R',
                "dustRedstone",
                'B',
                "barsIron",
                'N',
                "nuggetGold",
                'Q',
                "stoneAlabaster"));

        // Crystal
        for (int i = 0; i < BlockCrystal.VARIATIONS; i++) {
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, i),
                    "CCC",
                    "CCC",
                    "CCC",
                    'C',
                    MultiBlockItems.CRYSTAL.newItemStack(1, i)));

            GameRegistry.addRecipe(
                new ShapelessOreRecipe(
                    MultiBlockItems.CRYSTAL.newItemStack(9, i),
                    MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, i)));
        }

        // Crystal Lens
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockBlocks.LENS.newItemStack(1, 1),
                "C C",
                "CLC",
                "C C",
                'L',
                MultiBlockBlocks.LENS.newItemStack(1, 0),
                'C',
                MultiBlockBlocks.BLOCK_CRYSTAL.newItemStack(1, 1)));
    }

    public static void itemRecipes() {

        // Crystal T1
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockItems.CRYSTAL.newItemStack(1, 0),
                "GFG",
                "FDF",
                "GFG",
                'G',
                "dyeCyan",
                'F',
                Items.flint,
                'D',
                "gemDiamond"));

        // Photovoltaic Cell
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockItems.PHOTOVOLTAIC_CELL.getItem(),
                " L ",
                "LQL",
                " L ",
                'L',
                "gemLapis",
                'Q',
                "gemQuartz"));

        // Stabilized Ender Pear
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockItems.STABILIZED_ENDER_PEAR.getItem(),
                " P ",
                "PIP",
                " P ",
                'P',
                "pearlEnder",
                'I',
                "blockIron"));

        // Assembler
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockItems.ASSEMBLER.getItem(),
                "  B",
                " O ",
                "O  ",
                'O',
                "blockObsidian",
                'B',
                MultiBlockBlocks.BASALT_STRUCTURE.newItemStack(1, 1)));

        // Assembler
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockItems.ASSEMBLER.getItem(),
                "  B",
                " O ",
                "O  ",
                'O',
                "blockObsidian",
                'B',
                MultiBlockBlocks.ALABASTER_STRUCTURE.newItemStack(1, 1)));

        // Assembler
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                MultiBlockItems.ASSEMBLER.getItem(),
                "  B",
                " O ",
                "O  ",
                'O',
                "blockObsidian",
                'B',
                MultiBlockBlocks.HARDENED_STRUCTURE.newItemStack(1, 1)));
    }
}
