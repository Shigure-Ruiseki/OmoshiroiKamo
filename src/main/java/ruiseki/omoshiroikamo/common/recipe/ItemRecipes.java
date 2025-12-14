package ruiseki.omoshiroikamo.common.recipe;

import static ruiseki.omoshiroikamo.api.enums.EnumDye.DYE_ORE_NAMES;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

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
                    ModItems.BASE_UPGRADE.get(),
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
                    ModItems.BASE_UPGRADE.get()));

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
                    ModItems.CRAFTING_UPGRADE.get(),
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
                    ModItems.BASE_UPGRADE.get()));

            // Magnet Upgrade
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.MAGNET_UPGRADE.get(),
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
                    ModItems.PICKUP_UPGRADE.get()));

            // Advanced Magnet Upgrade
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.ADVANCED_MAGNET_UPGRADE.get(),
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
                    ModItems.ADVANCED_PICKUP_UPGRADE.get()));

            // Advanced Magnet Upgrade
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.ADVANCED_MAGNET_UPGRADE.get(),
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
                    ModItems.ADVANCED_PICKUP_UPGRADE.get()));

            // Void Upgrade
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.VOID_UPGRADE.get(),
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
                    ModItems.BASE_UPGRADE.get()));

            // Advanced Void Upgrade
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.ADVANCED_VOID_UPGRADE.get(),
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
                    ModItems.VOID_UPGRADE.get()));

            // Feeding Upgrade
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.FEEDING_UPGRADE.get(),
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
                    ModItems.BASE_UPGRADE.get()));

            // Advanced Feeding Upgrade
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.ADVANCED_FEEDING_UPGRADE.get(),
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
                    ModItems.FEEDING_UPGRADE.get()));

            // Pickup Upgrade
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.PICKUP_UPGRADE.get(),
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
                    ModItems.BASE_UPGRADE.get()));

            // Advanced Pickup Upgrade
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.ADVANCED_PICKUP_UPGRADE.get(),
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
                    ModItems.PICKUP_UPGRADE.get()));

            // Filter Upgrade
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.FILTER_UPGRADE.get(),
                    "RSR",
                    "SUS",
                    "RSR",
                    'R',
                    "dustRedstone",
                    'S',
                    Items.string,
                    'U',
                    ModItems.BASE_UPGRADE.get()));

            // Advanced Filter Upgrade
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.ADVANCED_FILTER_UPGRADE.get(),
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
                    ModItems.FILTER_UPGRADE.get()));

            // Inception Upgrade
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.INCEPTION_UPGRADE.get(),
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
                    ModItems.BASE_UPGRADE.get()));

            // Battery Upgrade
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.BATTERY_UPGRADE.get(),
                    "GRG",
                    "RUR",
                    "GRG",
                    'G',
                    "ingotGold",
                    'R',
                    "blockRedstone",
                    'U',
                    ModItems.BASE_UPGRADE.get()));

            // Everlasting Upgrade
            if (!LibMods.EtFuturum.isLoaded()) {
                GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                        ModItems.EVERLASTING_UPGRADE.get(),
                        "GRG",
                        "RUR",
                        "GRG",
                        'G',
                        "itemGhastTear",
                        'R',
                        "itemNetherStar",
                        'U',

                        ModItems.BASE_UPGRADE.get()));
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
                    ModItems.PHOTOVOLTAIC_CELL.get(),
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
                    ModItems.STABILIZED_ENDER_PEAR.get(),
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
                    ModItems.ASSEMBLER.get(),
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
                    ModItems.ASSEMBLER.get(),
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
                    ModItems.ASSEMBLER.get(),
                    "  B",
                    " O ",
                    "O  ",
                    'O',
                    "blockObsidian",
                    'B',
                    ModBlocks.HARDENED_STRUCTURE.newItemStack(1, 1)));
        }
        if (BackportConfigs.useChicken) {

            // Colored Egg
            for (int i = 0; i < DYE_ORE_NAMES.length; i++) {
                GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                        ModItems.COLORED_EGG.newItemStack(1, i),
                        "DDD",
                        "DED",
                        "DDD",
                        'E',
                        Items.egg,
                        'D',
                        DYE_ORE_NAMES[i]));
            }

            // Analyzer
            GameRegistry.addShapelessRecipe(ModItems.ANALYZER.newItemStack(), Items.stick, Items.compass);

            // Chicken Catcher
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.CHICKEN_CATCHER.newItemStack(),
                    " E ",
                    " S ",
                    " F ",
                    'E',
                    Items.egg,
                    'S',
                    "stickWood",
                    'F',
                    Items.feather));

            GameRegistry.addSmelting(ModItems.CHICKEN.get(), new ItemStack(Items.cooked_chicken), 0.35f);
        }

        if (BackportConfigs.useCow) {
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.COW_HALTER.get(),
                    "  L",
                    " S ",
                    "S  ",
                    'L',
                    Items.leather,
                    'S',
                    Items.stick));
        }

    }

}
