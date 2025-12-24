package ruiseki.omoshiroikamo.common.recipe;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.util.lib.LibMods;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;

public class ItemRecipes {

    public static void init() {
        if (BackportConfigs.useBackpack) {

            // Upgrade Base
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.BASE_UPGRADE.getItem(),
                    "SIS",
                    "ILI",
                    "SIS",
                    'S',
                    new ItemStack(Items.string, 1, 0),
                    'I',
                    "ingotIron",
                    'L',
                    new ItemStack(Items.leather, 1, 0)));

            // Stack Upgrade Tier 1
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.STACK_UPGRADE.newItemStack(1, 0),
                    "BBB",
                    "BUB",
                    "BBB",
                    'B',
                    "blockIron",
                    'U',
                    ModItems.BASE_UPGRADE.getItem()));

            // Stack Upgrade Tier 2
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.STACK_UPGRADE.newItemStack(1, 1),
                    "BBB",
                    "BUB",
                    "BBB",
                    'B',
                    "blockGold",
                    'U',
                    ModItems.STACK_UPGRADE.newItemStack(1, 0)));

            // Stack Upgrade Tier 3
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.STACK_UPGRADE.newItemStack(1, 2),
                    "BBB",
                    "BUB",
                    "BBB",
                    'B',
                    "blockDiamond",
                    'U',
                    ModItems.STACK_UPGRADE.newItemStack(1, 1)));

            // Stack Upgrade Tier 4
            if (!LibMods.EtFuturum.isLoaded()) {
                GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                        ModItems.STACK_UPGRADE.newItemStack(1, 3),
                        "BBB",
                        "BUB",
                        "BBB",
                        'B',
                        "itemNetherStar",
                        'U',
                        ModItems.STACK_UPGRADE.newItemStack(1, 2)));
            }

            // Crafting Upgrade
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.CRAFTING_UPGRADE.getItem(),
                    " c ",
                    "IUI",
                    " C ",
                    'c',
                    new ItemStack(Blocks.crafting_table, 1, 0),
                    'C',
                    new ItemStack(Blocks.chest, 1, 0),
                    'I',
                    "ingotIron",
                    'U',
                    ModItems.BASE_UPGRADE.getItem()));

            // Magnet Upgrade
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.MAGNET_UPGRADE.getItem(),
                    "EIE",
                    "IUI",
                    "R L",
                    'E',
                    "pearlEnder",
                    'R',
                    "dustRedstone",
                    'L',
                    "gemLapis",
                    'I',
                    "ingotIron",
                    'U',
                    ModItems.PICKUP_UPGRADE.getItem()));

            // Advanced Magnet Upgrade
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.ADVANCED_MAGNET_UPGRADE.getItem(),
                    "EIE",
                    "IUI",
                    "R L",
                    'E',
                    "pearlEnder",
                    'R',
                    "dustRedstone",
                    'L',
                    "gemLapis",
                    'I',
                    "ingotIron",
                    'U',
                    ModItems.ADVANCED_PICKUP_UPGRADE.getItem()));

            // Advanced Magnet Upgrade
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.ADVANCED_MAGNET_UPGRADE.getItem(),
                    " D ",
                    "GUG",
                    "RRR",
                    'D',
                    "gemDiamond",
                    'R',
                    "dustRedstone",
                    'G',
                    "ingotGold",
                    'U',
                    ModItems.ADVANCED_PICKUP_UPGRADE.getItem()));

            // Void Upgrade
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.VOID_UPGRADE.getItem(),
                    " E ",
                    "OUO",
                    "ROR",
                    'E',
                    "pearlEnder",
                    'O',
                    "blockObsidian",
                    'A',
                    new ItemStack(Items.golden_apple, 1, 0),
                    'R',
                    "dustRedstone",
                    'U',
                    ModItems.BASE_UPGRADE.getItem()));

            // Advanced Void Upgrade
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.ADVANCED_VOID_UPGRADE.getItem(),
                    " D ",
                    "GUG",
                    "RRR",
                    'D',
                    "gemDiamond",
                    'R',
                    "dustRedstone",
                    'G',
                    "ingotGold",
                    'U',
                    ModItems.VOID_UPGRADE.getItem()));

            // Feeding Upgrade
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.FEEDING_UPGRADE.getItem(),
                    " C ",
                    "AUM",
                    " E ",
                    'E',
                    "pearlEnder",
                    'C',
                    new ItemStack(Items.golden_carrot, 1, 0),
                    'A',
                    new ItemStack(Items.golden_apple, 1, 0),
                    'M',
                    new ItemStack(Items.speckled_melon, 1, 0),
                    'U',
                    ModItems.BASE_UPGRADE.getItem()));

            // Advanced Feeding Upgrade
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.ADVANCED_FEEDING_UPGRADE.getItem(),
                    " D ",
                    "GUG",
                    "RRR",
                    'D',
                    "gemDiamond",
                    'R',
                    "dustRedstone",
                    'G',
                    "ingotGold",
                    'U',
                    ModItems.FEEDING_UPGRADE.getItem()));

            // Pickup Upgrade
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.PICKUP_UPGRADE.getItem(),
                    " D ",
                    "GUG",
                    "RRR",
                    'D',
                    Blocks.sticky_piston,
                    'R',
                    "dustRedstone",
                    'G',
                    Items.string,
                    'U',
                    ModItems.BASE_UPGRADE.getItem()));

            // Advanced Pickup Upgrade
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.ADVANCED_PICKUP_UPGRADE.getItem(),
                    " D ",
                    "GUG",
                    "RRR",
                    'D',
                    "gemDiamond",
                    'R',
                    "dustRedstone",
                    'G',
                    "ingotGold",
                    'U',
                    ModItems.PICKUP_UPGRADE.getItem()));

            // Filter Upgrade
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.FILTER_UPGRADE.getItem(),
                    "RSR",
                    "SUS",
                    "RSR",
                    'R',
                    "dustRedstone",
                    'S',
                    Items.string,
                    'U',
                    ModItems.BASE_UPGRADE.getItem()));

            // Advanced Filter Upgrade
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.ADVANCED_FILTER_UPGRADE.getItem(),
                    " D ",
                    "GUG",
                    "RRR",
                    'D',
                    "gemDiamond",
                    'R',
                    "dustRedstone",
                    'G',
                    "ingotGold",
                    'U',
                    ModItems.FILTER_UPGRADE.getItem()));

            // Inception Upgrade
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.INCEPTION_UPGRADE.getItem(),
                    "ESE",
                    "DUD",
                    "EDE",
                    'D',
                    "gemDiamond",
                    'S',
                    "itemNetherStar",
                    'E',
                    Items.ender_eye,
                    'U',
                    ModItems.BASE_UPGRADE.getItem()));

            // Everlasting Upgrade
            if (!LibMods.EtFuturum.isLoaded()) {
                GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                        ModItems.EVERLASTING_UPGRADE.getItem(),
                        "GRG",
                        "RUR",
                        "GRG",
                        'G',
                        "itemGhastTear",
                        'R',
                        "itemNetherStar",
                        'U',

                        ModItems.BASE_UPGRADE.getItem()));
            }
        }
        if (BackportConfigs.useEnvironmentalTech) {

            // Crystal T1
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.CRYSTAL.newItemStack(1, 0),
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
                    ModItems.PHOTOVOLTAIC_CELL.getItem(),
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
                    ModItems.STABILIZED_ENDER_PEAR.getItem(),
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
                    ModItems.ASSEMBLER.getItem(),
                    "  B",
                    " O ",
                    "O  ",
                    'O',
                    "blockObsidian",
                    'B',
                    ModBlocks.BASALT_STRUCTURE.newItemStack(1, 1)));

            // Assembler
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.ASSEMBLER.getItem(),
                    "  B",
                    " O ",
                    "O  ",
                    'O',
                    "blockObsidian",
                    'B',
                    ModBlocks.ALABASTER_STRUCTURE.newItemStack(1, 1)));

            // Assembler
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.ASSEMBLER.getItem(),
                    "  B",
                    " O ",
                    "O  ",
                    'O',
                    "blockObsidian",
                    'B',
                    ModBlocks.HARDENED_STRUCTURE.newItemStack(1, 1)));
        }

        if (BackportConfigs.useCow) {
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.COW_HALTER.getItem(),
                    "  L",
                    " S ",
                    "S  ",
                    'L',
                    Items.leather,
                    'S',
                    Items.stick));
        }

        if (BackportConfigs.useDML) {

            // Deep Learner
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.DEEP_LEARNER.newItemStack(),
                    "ORO",
                    "RSR",
                    "ODO",
                    'O',
                    ModItems.SOOT_COVERED_PLATE.getItem(),
                    'D',
                    ModItems.SOOT_COVERED_REDSTONE.getItem(),
                    'S',
                    Blocks.glass_pane,
                    'R',
                    Items.repeater));

            // Data Model Blank
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.DATA_MODEL_BLANK.newItemStack(),
                    "CEC",
                    "RSR",
                    "CGC",
                    'E',
                    Items.repeater,
                    'R',
                    ModItems.SOOT_COVERED_REDSTONE.getItem(),
                    'G',
                    "ingotGold",
                    'C',
                    "gemLapis",
                    'S',
                    "stone"));

            // Polymer Clay
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.POLYMER_CLAY.newItemStack(16),
                    "GC ",
                    "CDC",
                    " CI",
                    'I',
                    "ingotIron",
                    'G',
                    "ingotGold",
                    'D',
                    "gemLapis",
                    'C',
                    "itemClay"));

            // Soot Covered Plate
            GameRegistry.addRecipe(
                new ShapelessOreRecipe(
                    ModItems.SOOT_COVERED_PLATE.newItemStack(8),
                    ModItems.SOOT_COVERED_REDSTONE.getItem(),
                    "blockObsidian",
                    "blockObsidian",
                    "blockObsidian"));
        }
    }

}
