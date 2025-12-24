package ruiseki.omoshiroikamo.common.recipe;

import static ruiseki.omoshiroikamo.api.enums.EnumDye.DYE_ORE_NAMES;
import static ruiseki.omoshiroikamo.common.block.backpack.BackpackHandler.BACKPACK_SLOTS;
import static ruiseki.omoshiroikamo.common.block.backpack.BackpackHandler.UPGRADE_SLOTS;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.util.lib.LibMods;
import ruiseki.omoshiroikamo.config.backport.BackpackConfig;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;

public class BlockRecipes {

    public static void init() {
        if (BackportConfigs.useBackpack) {
            // Leather Backpack
            GameRegistry.addRecipe(
                new NBTShapedOreRecipe(
                    ModBlocks.BACKPACK_BASE.newItemStack(),
                    "SLS",
                    "SCS",
                    "LLL",
                    'S',
                    new ItemStack(Items.string, 1, 0),
                    'L',
                    "itemLeather",
                    'C',
                    new ItemStack(Blocks.chest, 1, 0)));

            // Iron Backpack
            GameRegistry.addRecipe(
                new NBTShapedOreRecipe(
                    ModBlocks.BACKPACK_IRON.newItemStack(),
                    "CCC",
                    "CBC",
                    "CCC",
                    'C',
                    "ingotIron",
                    'B',
                    ModBlocks.BACKPACK_BASE.getItem()).withInt(UPGRADE_SLOTS, BackpackConfig.ironUpgradeSlots)
                        .withInt(BACKPACK_SLOTS, BackpackConfig.ironBackpackSlots)
                        .allowNBTFrom(ModBlocks.BACKPACK_BASE.newItemStack())
                        .allowAllTags());

            // Gold Backpack
            GameRegistry.addRecipe(
                new NBTShapedOreRecipe(
                    ModBlocks.BACKPACK_GOLD.newItemStack(),
                    "CCC",
                    "CBC",
                    "CCC",
                    'C',
                    "ingotGold",
                    'B',
                    ModBlocks.BACKPACK_IRON.getItem()).withInt(UPGRADE_SLOTS, BackpackConfig.goldUpgradeSlots)
                        .withInt(BACKPACK_SLOTS, BackpackConfig.goldBackpackSlots)
                        .allowNBTFrom(ModBlocks.BACKPACK_IRON.newItemStack())
                        .allowAllTags());

            // Diamond Backpack
            GameRegistry.addRecipe(
                new NBTShapedOreRecipe(
                    ModBlocks.BACKPACK_DIAMOND.newItemStack(),
                    "CCC",
                    "CBC",
                    "CCC",
                    'C',
                    "gemDiamond",
                    'B',
                    ModBlocks.BACKPACK_GOLD.getItem()).withInt(UPGRADE_SLOTS, BackpackConfig.diamondUpgradeSlots)
                        .withInt(BACKPACK_SLOTS, BackpackConfig.diamondBackpackSlots)
                        .allowNBTFrom(ModBlocks.BACKPACK_GOLD.newItemStack())
                        .allowAllTags());

            // Obsidian Backpack
            GameRegistry.addRecipe(
                new NBTShapedOreRecipe(
                    ModBlocks.BACKPACK_OBSIDIAN.newItemStack(),
                    "CSC",
                    "SBS",
                    "CSC",
                    'S',
                    "itemNetherStar",
                    'C',
                    "blockObsidian",
                    'B',
                    ModBlocks.BACKPACK_DIAMOND.getItem()).withInt(UPGRADE_SLOTS, BackpackConfig.obsidianUpgradeSlots)
                        .withInt(BACKPACK_SLOTS, BackpackConfig.obsidianBackpackSlots)
                        .allowNBTFrom(ModBlocks.BACKPACK_DIAMOND.newItemStack())
                        .allowAllTags());

            BackpackDyeRecipes recipes = new BackpackDyeRecipes();
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    String accentOre = EnumDye.DYE_ORE_NAMES[i];
                    String mainOre = EnumDye.DYE_ORE_NAMES[j];
                    int accentColor = EnumDye.fromIndex(i)
                        .getColor();
                    int mainColor = EnumDye.fromIndex(j)
                        .getColor();

                    recipes.registerDyeRecipes(
                        ModBlocks.BACKPACK_BASE.newItemStack(),
                        accentOre,
                        mainOre,
                        accentColor,
                        mainColor);
                    recipes.registerDyeRecipes(
                        ModBlocks.BACKPACK_IRON.newItemStack(),
                        accentOre,
                        mainOre,
                        accentColor,
                        mainColor);
                    recipes.registerDyeRecipes(
                        ModBlocks.BACKPACK_GOLD.newItemStack(),
                        accentOre,
                        mainOre,
                        accentColor,
                        mainColor);
                    recipes.registerDyeRecipes(
                        ModBlocks.BACKPACK_DIAMOND.newItemStack(),
                        accentOre,
                        mainOre,
                        accentColor,
                        mainColor);
                    recipes.registerDyeRecipes(
                        ModBlocks.BACKPACK_OBSIDIAN.newItemStack(),
                        accentOre,
                        mainOre,
                        accentColor,
                        mainColor);
                }
            }
        }

        if (BackportConfigs.useEnvironmentalTech) {

            // Hardened Stone
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.BLOCK_HARDENED_STONE.getItem(),
                    "SCS",
                    "CSC",
                    "SCS",
                    'S',
                    "stone",
                    'C',
                    "cobblestone"));

            // Hardened Stone
            GameRegistry.addRecipe(new ShapelessOreRecipe(ModBlocks.BLOCK_HARDENED_STONE.getItem(), "stoneHardened"));

            // Basalt
            GameRegistry.addRecipe(new ShapelessOreRecipe(ModBlocks.BLOCK_BASALT.getItem(), "stoneBasalt"));

            // Alabaster
            GameRegistry.addRecipe(new ShapelessOreRecipe(ModBlocks.BLOCK_ALABASTER.getItem(), "stoneAlabaster"));

            // Structure T1
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.BASALT_STRUCTURE.newItemStack(1, 0),
                    " I ",
                    "CBC",
                    " L ",
                    'C',
                    ModItems.CRYSTAL.newItemStack(1, 0),
                    'I',
                    "ingotIron",
                    'L',
                    "gemLapis",
                    'B',
                    "stoneBasalt"));
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.ALABASTER_STRUCTURE.newItemStack(1, 0),
                    " I ",
                    "CBC",
                    " L ",
                    'C',
                    ModItems.CRYSTAL.newItemStack(1, 0),
                    'I',
                    "ingotIron",
                    'L',
                    "gemLapis",
                    'B',
                    "stoneAlabaster"));
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.HARDENED_STRUCTURE.newItemStack(1, 0),
                    " I ",
                    "CBC",
                    " L ",
                    'C',
                    ModItems.CRYSTAL.newItemStack(1, 0),
                    'I',
                    "ingotIron",
                    'L',
                    "gemLapis",
                    'B',
                    "stoneHardened"));

            // Structure T2
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.BASALT_STRUCTURE.newItemStack(1, 1),
                    " I ",
                    "CBC",
                    " L ",
                    'C',
                    ModItems.CRYSTAL.newItemStack(1, 1),
                    'I',
                    "ingotIron",
                    'L',
                    "gemLapis",
                    'B',
                    ModBlocks.BASALT_STRUCTURE.newItemStack(1, 0)));
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.ALABASTER_STRUCTURE.newItemStack(1, 1),
                    " I ",
                    "CBC",
                    " L ",
                    'C',
                    ModItems.CRYSTAL.newItemStack(1, 1),
                    'I',
                    "ingotIron",
                    'L',
                    "gemLapis",
                    'B',
                    ModBlocks.ALABASTER_STRUCTURE.newItemStack(1, 0)));
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.HARDENED_STRUCTURE.newItemStack(1, 1),
                    " I ",
                    "CBC",
                    " L ",
                    'C',
                    ModItems.CRYSTAL.newItemStack(1, 1),
                    'I',
                    "ingotIron",
                    'L',
                    "gemLapis",
                    'B',
                    ModBlocks.HARDENED_STRUCTURE.newItemStack(1, 0)));

            // Structure T3
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.BASALT_STRUCTURE.newItemStack(1, 2),
                    " I ",
                    "CBC",
                    " L ",
                    'C',
                    ModItems.CRYSTAL.newItemStack(1, 2),
                    'I',
                    "gemDiamond",
                    'L',
                    "gemQuartz",
                    'B',
                    ModBlocks.BASALT_STRUCTURE.newItemStack(1, 1)));
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.ALABASTER_STRUCTURE.newItemStack(1, 2),
                    " I ",
                    "CBC",
                    " L ",
                    'C',
                    ModItems.CRYSTAL.newItemStack(1, 2),
                    'I',
                    "gemDiamond",
                    'L',
                    "gemQuartz",
                    'B',
                    ModBlocks.ALABASTER_STRUCTURE.newItemStack(1, 1)));
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.HARDENED_STRUCTURE.newItemStack(1, 2),
                    " I ",
                    "CBC",
                    " L ",
                    'C',
                    ModItems.CRYSTAL.newItemStack(1, 2),
                    'I',
                    "gemDiamond",
                    'L',
                    "gemQuartz",
                    'B',
                    ModBlocks.HARDENED_STRUCTURE.newItemStack(1, 1)));

            // Structure T4
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.BASALT_STRUCTURE.newItemStack(1, 3),
                    " I ",
                    "CBC",
                    " L ",
                    'C',
                    ModItems.CRYSTAL.newItemStack(1, 3),
                    'I',
                    "gemEmerald",
                    'L',
                    "gemQuartz",
                    'B',
                    ModBlocks.BASALT_STRUCTURE.newItemStack(1, 2)));
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.ALABASTER_STRUCTURE.newItemStack(1, 3),
                    " I ",
                    "CBC",
                    " L ",
                    'C',
                    ModItems.CRYSTAL.newItemStack(1, 3),
                    'I',
                    "gemEmerald",
                    'L',
                    "gemQuartz",
                    'B',
                    ModBlocks.ALABASTER_STRUCTURE.newItemStack(1, 2)));
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.HARDENED_STRUCTURE.newItemStack(1, 3),
                    " I ",
                    "CBC",
                    " L ",
                    'C',
                    ModItems.CRYSTAL.newItemStack(1, 3),
                    'I',
                    "gemEmerald",
                    'L',
                    "gemQuartz",
                    'B',
                    ModBlocks.HARDENED_STRUCTURE.newItemStack(1, 2)));

            // Structure T5
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.BASALT_STRUCTURE.newItemStack(1, 4),
                    " I ",
                    "CBC",
                    " L ",
                    'C',
                    ModItems.CRYSTAL.newItemStack(1, 4),
                    'I',
                    "itemNetherStar",
                    'L',
                    "gemQuartz",
                    'B',
                    ModBlocks.BASALT_STRUCTURE.newItemStack(1, 3)));
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.ALABASTER_STRUCTURE.newItemStack(1, 4),
                    " I ",
                    "CBC",
                    " L ",
                    'C',
                    ModItems.CRYSTAL.newItemStack(1, 4),
                    'I',
                    "itemNetherStar",
                    'L',
                    "gemQuartz",
                    'B',
                    ModBlocks.ALABASTER_STRUCTURE.newItemStack(1, 3)));
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.HARDENED_STRUCTURE.newItemStack(1, 4),
                    " I ",
                    "CBC",
                    " L ",
                    'C',
                    ModItems.CRYSTAL.newItemStack(1, 4),
                    'I',
                    "itemNetherStar",
                    'L',
                    "gemQuartz",
                    'B',
                    ModBlocks.HARDENED_STRUCTURE.newItemStack(1, 3)));

            // Structure T6
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.BASALT_STRUCTURE.newItemStack(1, 5),
                    " I ",
                    "CBC",
                    " I ",
                    'C',
                    ModItems.CRYSTAL.newItemStack(1, 5),
                    'I',
                    "itemNetherStar",
                    'B',
                    ModBlocks.BASALT_STRUCTURE.newItemStack(1, 4)));
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.ALABASTER_STRUCTURE.newItemStack(1, 5),
                    " I ",
                    "CBC",
                    " I ",
                    'C',
                    ModItems.CRYSTAL.newItemStack(1, 5),
                    'I',
                    "itemNetherStar",
                    'B',
                    ModBlocks.ALABASTER_STRUCTURE.newItemStack(1, 4)));
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.HARDENED_STRUCTURE.newItemStack(1, 5),
                    " I ",
                    "CBC",
                    " I ",
                    'C',
                    ModItems.CRYSTAL.newItemStack(1, 5),
                    'I',
                    "itemNetherStar",
                    'B',
                    ModBlocks.HARDENED_STRUCTURE.newItemStack(1, 4)));

            // Solar Cell T1
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.SOLAR_CELL.newItemStack(1, 0),
                    "GGG",
                    "CCC",
                    "RIR",
                    'G',
                    ModItems.CRYSTAL.newItemStack(1, 0),
                    'I',
                    "stoneBasalt",
                    'R',
                    "dustRedstone",
                    'C',
                    ModItems.PHOTOVOLTAIC_CELL.getItem()));
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.SOLAR_CELL.newItemStack(1, 0),
                    "GGG",
                    "CCC",
                    "RIR",
                    'G',
                    ModItems.CRYSTAL.newItemStack(1, 0),
                    'I',
                    "stoneAlabaster",
                    'R',
                    "dustRedstone",
                    'C',
                    ModItems.PHOTOVOLTAIC_CELL.getItem()));
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.SOLAR_CELL.newItemStack(1, 0),
                    "GGG",
                    "CCC",
                    "RIR",
                    'G',
                    ModItems.CRYSTAL.newItemStack(1, 0),
                    'I',
                    "stoneHardened",
                    'R',
                    "dustRedstone",
                    'C',
                    ModItems.PHOTOVOLTAIC_CELL.getItem()));

            // Solar Cell T2
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.SOLAR_CELL.newItemStack(1, 1),
                    "   ",
                    "GGG",
                    "CIC",
                    'G',
                    ModItems.CRYSTAL.newItemStack(1, 1),
                    'I',
                    ModBlocks.SOLAR_CELL.newItemStack(1, 0),
                    'C',
                    ModItems.PHOTOVOLTAIC_CELL.getItem()));

            // Solar Cell T3
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.SOLAR_CELL.newItemStack(1, 2),
                    "   ",
                    "GGG",
                    "CIC",
                    'G',
                    ModItems.CRYSTAL.newItemStack(1, 2),
                    'I',
                    ModBlocks.SOLAR_CELL.newItemStack(1, 1),
                    'C',
                    ModItems.PHOTOVOLTAIC_CELL.getItem()));

            // Solar Cell T4
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.SOLAR_CELL.newItemStack(1, 3),
                    "   ",
                    "GGG",
                    "CIC",
                    'G',
                    ModItems.CRYSTAL.newItemStack(1, 3),
                    'I',
                    ModBlocks.SOLAR_CELL.newItemStack(1, 2),
                    'C',
                    ModItems.PHOTOVOLTAIC_CELL.getItem()));

            // Solar Cell T5
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.SOLAR_CELL.newItemStack(1, 4),
                    "   ",
                    "GGG",
                    "CIC",
                    'G',
                    ModItems.CRYSTAL.newItemStack(1, 4),
                    'I',
                    ModBlocks.SOLAR_CELL.newItemStack(1, 3),
                    'C',
                    ModItems.PHOTOVOLTAIC_CELL.getItem()));

            // Solar Cell T6
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.SOLAR_CELL.newItemStack(1, 5),
                    "   ",
                    "GGG",
                    "CIC",
                    'G',
                    ModItems.CRYSTAL.newItemStack(1, 5),
                    'I',
                    ModBlocks.SOLAR_CELL.newItemStack(1, 4),
                    'C',
                    ModItems.PHOTOVOLTAIC_CELL.getItem()));

            // Solar Array Tier 1
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.SOLAR_ARRAY.newItemStack(1, 0),
                    "GLG",
                    "LCL",
                    "GLG",
                    'G',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 0),
                    'L',
                    "blockLapis",
                    'C',
                    ModBlocks.SOLAR_CELL.newItemStack(1, 0)));

            // Solar Array Tier 2
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.SOLAR_ARRAY.newItemStack(1, 1),
                    "GLG",
                    "LCL",
                    "GLG",
                    'G',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 1),
                    'L',
                    ModBlocks.SOLAR_CELL.newItemStack(1, 1),
                    'C',
                    ModBlocks.SOLAR_ARRAY.newItemStack(1, 0)));

            // Solar Array Tier 3
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.SOLAR_ARRAY.newItemStack(1, 2),
                    "GLG",
                    "LCL",
                    "GLG",
                    'G',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 2),
                    'L',
                    ModBlocks.SOLAR_CELL.newItemStack(1, 2),
                    'C',
                    ModBlocks.SOLAR_ARRAY.newItemStack(1, 1)));

            // Solar Array Tier 4
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.SOLAR_ARRAY.newItemStack(1, 3),
                    "GLG",
                    "LCL",
                    "GLG",
                    'G',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 3),
                    'L',
                    ModBlocks.SOLAR_CELL.newItemStack(1, 3),
                    'C',
                    ModBlocks.SOLAR_ARRAY.newItemStack(1, 2)));

            // Solar Array T5
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.SOLAR_ARRAY.newItemStack(1, 4),
                    "GLG",
                    "LCL",
                    "GLG",
                    'G',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 4),
                    'L',
                    ModBlocks.SOLAR_CELL.newItemStack(1, 4),
                    'C',
                    ModBlocks.SOLAR_ARRAY.newItemStack(1, 3)));

            // Solar Array T6
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.SOLAR_ARRAY.newItemStack(1, 5),
                    "GLG",
                    "LCL",
                    "GLG",
                    'G',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 5),
                    'L',
                    ModBlocks.SOLAR_CELL.newItemStack(1, 5),
                    'C',
                    ModBlocks.SOLAR_ARRAY.newItemStack(1, 4)));

            // Quantum Ore T1
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 0),
                    "COC",
                    "CcC",
                    "BLB",
                    'C',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 0),
                    'L',
                    ModBlocks.LENS.newItemStack(1, 0),
                    'O',
                    "blockDiamond",
                    'B',
                    "stoneBasalt",
                    'c',
                    ModBlocks.LASER_CORE.getItem()));
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 0),
                    "COC",
                    "CcC",
                    "BLB",
                    'C',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 0),
                    'L',
                    ModBlocks.LENS.newItemStack(1, 0),
                    'O',
                    "blockDiamond",
                    'B',
                    "stoneAlabaster",
                    'c',
                    ModBlocks.LASER_CORE.getItem()));
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 0),
                    "COC",
                    "CcC",
                    "BLB",
                    'C',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 0),
                    'L',
                    ModBlocks.LENS.newItemStack(1, 0),
                    'O',
                    "blockDiamond",
                    'B',
                    "stoneHardened",
                    'c',
                    ModBlocks.LASER_CORE.getItem()));

            // Quantum Ore T2
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 1),
                    "COC",
                    "CcC",
                    "CLC",
                    'C',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 1),
                    'L',
                    ModBlocks.LENS.newItemStack(1, 0),
                    'O',
                    "blockDiamond",
                    'c',
                    ModBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 0)));

            // Quantum Ore T3
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 2),
                    "COC",
                    "CcC",
                    "CLC",
                    'C',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 2),
                    'L',
                    ModBlocks.LENS.newItemStack(1, 0),
                    'O',
                    "blockDiamond",
                    'c',
                    ModBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 1)));

            // Quantum Ore T4
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 3),
                    "COC",
                    "CcC",
                    "CLC",
                    'C',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 3),
                    'L',
                    ModBlocks.LENS.newItemStack(1, 0),
                    'O',
                    "blockDiamond",
                    'c',
                    ModBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 2)));

            // Quantum Ore T5
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 4),
                    "COC",
                    "CcC",
                    "CLC",
                    'C',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 4),
                    'L',
                    ModBlocks.LENS.newItemStack(1, 0),
                    'O',
                    "blockDiamond",
                    'c',
                    ModBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 3)));

            // Quantum Ore T6
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 5),
                    "COC",
                    "CcC",
                    "CLC",
                    'C',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 5),
                    'L',
                    ModBlocks.LENS.newItemStack(1, 0),
                    'O',
                    "blockDiamond",
                    'c',
                    ModBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 4)));

            // Quantum Res T1
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 0),
                    "COC",
                    "CcC",
                    "BLB",
                    'C',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 0),
                    'L',
                    ModBlocks.LENS.newItemStack(1, 0),
                    'O',
                    "blockObsidian",
                    'B',
                    "stoneBasalt",
                    'c',
                    ModBlocks.LASER_CORE.getItem()));
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 0),
                    "COC",
                    "CcC",
                    "BLB",
                    'C',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 0),
                    'L',
                    ModBlocks.LENS.newItemStack(1, 0),
                    'O',
                    "blockObsidian",
                    'B',
                    "stoneAlabaster",
                    'c',
                    ModBlocks.LASER_CORE.getItem()));
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 0),
                    "COC",
                    "CcC",
                    "BLB",
                    'C',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 0),
                    'L',
                    ModBlocks.LENS.newItemStack(1, 0),
                    'O',
                    "blockObsidian",
                    'B',
                    "stoneHardened",
                    'c',
                    ModBlocks.LASER_CORE.getItem()));

            // Quantum Res T2
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 1),
                    "COC",
                    "CcC",
                    "CLC",
                    'C',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 1),
                    'L',
                    ModBlocks.LENS.newItemStack(1, 0),
                    'O',
                    "blockObsidian",
                    'c',
                    ModBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 0)));

            // Quantum Res T3
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 2),
                    "COC",
                    "CcC",
                    "CLC",
                    'C',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 2),
                    'L',
                    ModBlocks.LENS.newItemStack(1, 0),
                    'O',
                    "blockObsidian",
                    'c',
                    ModBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 1)));

            // Quantum Res T4
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 3),
                    "COC",
                    "CcC",
                    "CLC",
                    'C',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 3),
                    'L',
                    ModBlocks.LENS.newItemStack(1, 0),
                    'O',
                    "blockObsidian",
                    'c',
                    ModBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 2)));

            // Quantum Res T5
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 4),
                    "COC",
                    "CcC",
                    "CLC",
                    'C',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 4),
                    'L',
                    ModBlocks.LENS.newItemStack(1, 0),
                    'O',
                    "blockObsidian",
                    'c',
                    ModBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 3)));

            // Quantum Res T6
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 5),
                    "COC",
                    "CcC",
                    "CLC",
                    'C',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 5),
                    'L',
                    ModBlocks.LENS.newItemStack(1, 0),
                    'O',
                    "blockObsidian",
                    'c',
                    ModBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 4)));

            // Quantum Beacon T1
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_BEACON.newItemStack(1, 0),
                    "CMC",
                    "CBC",
                    "SNS",
                    'S',
                    "stoneBasalt",
                    'C',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 0),
                    'M',
                    ModBlocks.BLOCK_MICA.getItem(),
                    'N',
                    ModBlocks.MODIFIER_NULL.getItem(),
                    'B',
                    Blocks.beacon));
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_BEACON.newItemStack(1, 0),
                    "CMC",
                    "CBC",
                    "SNS",
                    'S',
                    "stoneAlabaster",
                    'C',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 0),
                    'M',
                    ModBlocks.BLOCK_MICA.getItem(),
                    'N',
                    ModBlocks.MODIFIER_NULL.getItem(),
                    'B',
                    Blocks.beacon));
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_BEACON.newItemStack(1, 0),
                    "CMC",
                    "CBC",
                    "SNS",
                    'S',
                    "stoneHardened",
                    'C',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 0),
                    'M',
                    ModBlocks.BLOCK_MICA.getItem(),
                    'N',
                    ModBlocks.MODIFIER_NULL.getItem(),
                    'B',
                    Blocks.beacon));

            // Quantum Beacon T2
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_BEACON.newItemStack(1, 1),
                    "CMC",
                    "CBC",
                    "MNM",
                    'C',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 1),
                    'M',
                    ModBlocks.BLOCK_MICA.getItem(),
                    'N',
                    ModBlocks.MODIFIER_NULL.getItem(),
                    'B',
                    ModBlocks.QUANTUM_BEACON.newItemStack(1, 0)));

            // Quantum Beacon T3
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_BEACON.newItemStack(1, 2),
                    "CMC",
                    "CBC",
                    "MNM",
                    'C',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 2),
                    'M',
                    ModBlocks.BLOCK_MICA.getItem(),
                    'N',
                    ModBlocks.MODIFIER_NULL.getItem(),
                    'B',
                    ModBlocks.QUANTUM_BEACON.newItemStack(1, 1)));

            // Quantum Beacon T4
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_BEACON.newItemStack(1, 3),
                    "CMC",
                    "CBC",
                    "MNM",
                    'C',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 3),
                    'M',
                    ModBlocks.BLOCK_MICA.getItem(),
                    'N',
                    ModBlocks.MODIFIER_NULL.getItem(),
                    'B',
                    ModBlocks.QUANTUM_BEACON.newItemStack(1, 2)));

            // Quantum Beacon T5
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_BEACON.newItemStack(1, 4),
                    "CMC",
                    "CBC",
                    "MNM",
                    'C',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 4),
                    'M',
                    ModBlocks.BLOCK_MICA.getItem(),
                    'N',
                    ModBlocks.MODIFIER_NULL.getItem(),
                    'B',
                    ModBlocks.QUANTUM_BEACON.newItemStack(1, 3)));

            // Quantum Beacon T6
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_BEACON.newItemStack(1, 5),
                    "CMC",
                    "CBC",
                    "MNM",
                    'C',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 5),
                    'M',
                    ModBlocks.BLOCK_MICA.getItem(),
                    'N',
                    ModBlocks.MODIFIER_NULL.getItem(),
                    'B',
                    ModBlocks.QUANTUM_BEACON.newItemStack(1, 4)));

            // Clear Lens
            GameRegistry
                .addRecipe(new ShapedOreRecipe(ModBlocks.LENS.getItem(), "G G", "GGG", "G G", 'G', "blockGlass"));

            // Color Lens
            for (int i = 0; i < DYE_ORE_NAMES.length; i++) {
                GameRegistry.addRecipe(
                    new ShapelessOreRecipe(
                        ModBlocks.COLORED_LENS.newItemStack(1, i),
                        ModBlocks.LENS.getItem(),
                        DYE_ORE_NAMES[i]));
                GameRegistry.addRecipe(
                    new ShapelessOreRecipe(ModBlocks.LENS.getItem(), ModBlocks.COLORED_LENS.newItemStack(1, i)));
            }

            // Laser Core
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.LASER_CORE.getItem(),
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
                    ModBlocks.MODIFIER_NULL.getItem(),
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
                    ModBlocks.MODIFIER_PIEZO.getItem(),
                    "RMR",
                    "MNM",
                    "IMI",
                    'R',
                    "blockRedstone",
                    'M',
                    ModBlocks.BLOCK_MICA.getItem(),
                    'N',
                    ModBlocks.MODIFIER_NULL.getItem(),
                    'I',
                    "blockIron"));

            // Speed Modifier
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.MODIFIER_SPEED.getItem(),
                    "RCR",
                    "MNM",
                    "RcR",
                    'R',
                    "blockRedstone",
                    'M',
                    ModBlocks.BLOCK_MICA.getItem(),
                    'N',
                    ModBlocks.MODIFIER_NULL.getItem(),
                    'C',
                    ModItems.CRYSTAL.newItemStack(1, 1),
                    'c',
                    ModItems.CRYSTAL.newItemStack(1, 3)));

            // Accuracy Modifier
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.MODIFIER_ACCURACY.getItem(),
                    "DCD",
                    "MNM",
                    "DcD",
                    'C',
                    ModItems.CRYSTAL.newItemStack(1, 2),
                    'c',
                    ModItems.CRYSTAL.newItemStack(1, 3),
                    'M',
                    ModBlocks.BLOCK_MICA.getItem(),
                    'N',
                    ModBlocks.MODIFIER_NULL.getItem(),
                    'D',
                    "blockDiamond"));

            // Flight Modifier
            if (!LibMods.EtFuturum.isLoaded()) {
                GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                        ModBlocks.MODIFIER_FLIGHT.getItem(),
                        "PFP",
                        "MNM",
                        "LfL",
                        'P',
                        Blocks.sticky_piston,
                        'M',
                        ModBlocks.BLOCK_MICA.getItem(),
                        'F',
                        ModItems.CRYSTAL.newItemStack(1, 4),
                        'f',
                        ModItems.CRYSTAL.newItemStack(1, 3),
                        'L',
                        "itemLeather",
                        'N',
                        ModBlocks.MODIFIER_NULL.getItem()));
            }

            // Night Vision Modifier
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.MODIFIER_NIGHT_VISION.getItem(),
                    "ECE",
                    "MNM",
                    "EcE",
                    'E',
                    Items.emerald,
                    'C',
                    ModItems.CRYSTAL.newItemStack(1, 4),
                    'c',
                    ModItems.CRYSTAL.newItemStack(1, 3),
                    'M',
                    ModBlocks.BLOCK_MICA.getItem(),
                    'N',
                    ModBlocks.MODIFIER_NULL.getItem()));

            // Strength Modifier
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.MODIFIER_STRENGTH.getItem(),
                    "SCS",
                    "MNM",
                    "ScS",
                    'C',
                    ModItems.CRYSTAL.newItemStack(1, 2),
                    'c',
                    ModItems.CRYSTAL.newItemStack(1, 3),
                    'S',
                    Items.diamond_sword,
                    'M',
                    ModBlocks.BLOCK_MICA.getItem(),
                    'N',
                    ModBlocks.MODIFIER_NULL.getItem()));

            // Water Breathing Modifier
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.MODIFIER_WATER_BREATHING.getItem(),
                    "PCP",
                    "MNM",
                    "PcP",
                    'C',
                    ModItems.CRYSTAL.newItemStack(1, 1),
                    'c',
                    ModItems.CRYSTAL.newItemStack(1, 3),
                    'P',
                    new ItemStack(Items.potionitem, 1, 8205),
                    'M',
                    ModBlocks.BLOCK_MICA.getItem(),
                    'N',
                    ModBlocks.MODIFIER_NULL.getItem()));

            // Regeneration Modifier
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.MODIFIER_REGENERATION.getItem(),
                    "ACA",
                    "MNM",
                    "ACA",
                    'A',
                    new ItemStack(Items.golden_apple, 1, 0),
                    'C',
                    ModItems.CRYSTAL.newItemStack(1, 3),
                    'M',
                    ModBlocks.BLOCK_MICA.getItem(),
                    'N',
                    ModBlocks.MODIFIER_NULL.getItem()));

            // Fire Resistance Modifier
            ItemStack fireprot4Book = new ItemStack(Items.enchanted_book);
            fireprot4Book.addEnchantment(Enchantment.fireProtection, 4);
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.MODIFIER_FIRE_RESISTANCE.getItem(),
                    "CPC",
                    "PNP",
                    "MPM",
                    'P',
                    fireprot4Book,
                    'C',
                    ModItems.CRYSTAL.newItemStack(1, 2),
                    'M',
                    ModBlocks.BLOCK_MICA.getItem(),
                    'N',
                    ModBlocks.MODIFIER_NULL.getItem()));

            // Jump Boost Modifier
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.MODIFIER_JUMP_BOOST.getItem(),
                    "BCB",
                    "MNM",
                    "BcB",
                    'B',
                    Items.diamond_boots,
                    'C',
                    ModItems.CRYSTAL.newItemStack(1, 1),
                    'c',
                    ModItems.CRYSTAL.newItemStack(1, 3),
                    'M',
                    ModBlocks.BLOCK_MICA.getItem(),
                    'N',
                    ModBlocks.MODIFIER_NULL.getItem()));

            // Resistance Modifier
            ItemStack prot4Book = new ItemStack(Items.enchanted_book);
            prot4Book.addEnchantment(Enchantment.protection, 4);
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.MODIFIER_RESISTANCE.getItem(),
                    "CPC",
                    "ONO",
                    "MPM",
                    'C',
                    ModItems.CRYSTAL.newItemStack(1, 2),
                    'O',
                    "blockObsidian",
                    'P',
                    prot4Book,
                    'M',
                    ModBlocks.BLOCK_MICA.getItem(),
                    'N',
                    ModBlocks.MODIFIER_NULL.getItem()));

            // Haste Modifier
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.MODIFIER_HASTE.getItem(),
                    "PCP",
                    "MNM",
                    "PcP",
                    'C',
                    ModItems.CRYSTAL.newItemStack(1, 2),
                    'c',
                    ModItems.CRYSTAL.newItemStack(1, 3),
                    'P',
                    Items.golden_pickaxe,
                    'M',
                    ModBlocks.BLOCK_MICA.getItem(),
                    'N',
                    ModBlocks.MODIFIER_NULL.getItem()));

            // Saturation Modifier
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.MODIFIER_SATURATION.getItem(),
                    "ACA",
                    "MNM",
                    "AcA",
                    'C',
                    ModItems.CRYSTAL.newItemStack(1, 4),
                    'c',
                    ModItems.CRYSTAL.newItemStack(1, 3),
                    'A',
                    Items.golden_carrot,
                    'M',
                    ModBlocks.BLOCK_MICA.getItem(),
                    'N',
                    ModBlocks.MODIFIER_NULL.getItem()));

            // Machine Base Basalt
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.MACHINE_BASE.newItemStack(1, 0),
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
                    ModBlocks.MACHINE_BASE.newItemStack(1, 1),
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
                    ModBlocks.MACHINE_BASE.newItemStack(1, 2),
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
            for (int i = 0; i < 6; i++) {
                GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                        ModBlocks.BLOCK_CRYSTAL.newItemStack(1, i),
                        "CCC",
                        "CCC",
                        "CCC",
                        'C',
                        ModItems.CRYSTAL.newItemStack(1, i)));

                GameRegistry.addRecipe(
                    new ShapelessOreRecipe(
                        ModItems.CRYSTAL.newItemStack(9, i),
                        ModBlocks.BLOCK_CRYSTAL.newItemStack(1, i)));
            }

            // Crystal Lens
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.LENS.newItemStack(1, 1),
                    "C C",
                    "CLC",
                    "C C",
                    'L',
                    ModBlocks.LENS.newItemStack(1, 0),
                    'C',
                    ModBlocks.BLOCK_CRYSTAL.newItemStack(1, 1)));
        }

        if (BackportConfigs.useChicken) {

            // Roost
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.ROOST.getItem(),
                    "WWW",
                    "W W",
                    "HHH",
                    'W',
                    "plankWood",
                    'H',
                    Blocks.hay_block));

            // Breeder
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.BREEDER.getItem(),
                    "WWW",
                    "WSW",
                    "HHH",
                    'W',
                    "plankWood",
                    'S',
                    Items.wheat_seeds,
                    'H',
                    Blocks.hay_block));

            // Roost Collector
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.ROOST_COLLECTOR.getItem(),
                    "WCW",
                    "WHW",
                    "WOW",
                    'W',
                    "plankWood",
                    'C',
                    Items.egg,
                    'O',
                    "chestWood",
                    'H',
                    "blockHopper"));
        }

        if (BackportConfigs.useCow) {

            // Stall
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.STALL.getItem(),
                    "B B",
                    "BHB",
                    "GGG",
                    'B',
                    Blocks.iron_bars,
                    'H',
                    Blocks.hay_block,
                    'G',
                    new ItemStack(Blocks.stained_hardened_clay, 1, 7)));

        }

        if (BackportConfigs.useDML) {
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.MACHINE_CASING.getItem(),
                    "PIP",
                    "ICI",
                    "PIP",
                    'P',
                    ModItems.SOOT_COVERED_PLATE.getItem(),
                    'I',
                    "ingotIron",
                    'C',
                    ModItems.SOOT_COVERED_REDSTONE.getItem()));

            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.LOOT_FABRICATOR.getItem(),
                    " G ",
                    "DMD",
                    "YCY",
                    'M',
                    ModBlocks.MACHINE_CASING.get(),
                    'G',
                    "ingotGold",
                    'D',
                    "gemDiamond",
                    'Y',
                    "dyeYellow",
                    'C',
                    Items.comparator));

            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.SIMULATION_CHAMBER.getItem(),
                    " G ",
                    "PMP",
                    "DCD",
                    'M',
                    ModBlocks.MACHINE_CASING.get(),
                    'P',
                    "pearlEnder",
                    'G',
                    Blocks.glass_pane,
                    'C',
                    Items.comparator,
                    'D',
                    "gemLapis"));
        }
    }
}
