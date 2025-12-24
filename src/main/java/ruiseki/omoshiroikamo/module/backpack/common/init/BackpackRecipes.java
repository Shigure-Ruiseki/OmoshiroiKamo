package ruiseki.omoshiroikamo.module.backpack.common.init;

import static ruiseki.omoshiroikamo.module.backpack.common.block.BackpackHandler.BACKPACK_SLOTS;
import static ruiseki.omoshiroikamo.module.backpack.common.block.BackpackHandler.UPGRADE_SLOTS;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.config.backport.BackpackConfig;
import ruiseki.omoshiroikamo.core.common.recipe.NBTShapedOreRecipe;
import ruiseki.omoshiroikamo.core.lib.LibMods;

public class BackpackRecipes {

    public static void init() {
        blockRecipes();
        itemRecipes();
    }

    public static void blockRecipes() {
        // Leather Backpack
        GameRegistry.addRecipe(
            new NBTShapedOreRecipe(
                BackpackBlocks.BACKPACK_BASE.newItemStack(),
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
                BackpackBlocks.BACKPACK_IRON.newItemStack(),
                "CCC",
                "CBC",
                "CCC",
                'C',
                "ingotIron",
                'B',
                BackpackBlocks.BACKPACK_BASE.getItem()).withInt(UPGRADE_SLOTS, BackpackConfig.ironUpgradeSlots)
                    .withInt(BACKPACK_SLOTS, BackpackConfig.ironBackpackSlots)
                    .allowNBTFrom(BackpackBlocks.BACKPACK_BASE.newItemStack())
                    .allowAllTags());

        // Gold Backpack
        GameRegistry.addRecipe(
            new NBTShapedOreRecipe(
                BackpackBlocks.BACKPACK_GOLD.newItemStack(),
                "CCC",
                "CBC",
                "CCC",
                'C',
                "ingotGold",
                'B',
                BackpackBlocks.BACKPACK_IRON.getItem()).withInt(UPGRADE_SLOTS, BackpackConfig.goldUpgradeSlots)
                    .withInt(BACKPACK_SLOTS, BackpackConfig.goldBackpackSlots)
                    .allowNBTFrom(BackpackBlocks.BACKPACK_IRON.newItemStack())
                    .allowAllTags());

        // Diamond Backpack
        GameRegistry.addRecipe(
            new NBTShapedOreRecipe(
                BackpackBlocks.BACKPACK_DIAMOND.newItemStack(),
                "CCC",
                "CBC",
                "CCC",
                'C',
                "gemDiamond",
                'B',
                BackpackBlocks.BACKPACK_GOLD.getItem()).withInt(UPGRADE_SLOTS, BackpackConfig.diamondUpgradeSlots)
                    .withInt(BACKPACK_SLOTS, BackpackConfig.diamondBackpackSlots)
                    .allowNBTFrom(BackpackBlocks.BACKPACK_GOLD.newItemStack())
                    .allowAllTags());

        // Obsidian Backpack
        GameRegistry.addRecipe(
            new NBTShapedOreRecipe(
                BackpackBlocks.BACKPACK_OBSIDIAN.newItemStack(),
                "CSC",
                "SBS",
                "CSC",
                'S',
                "itemNetherStar",
                'C',
                "blockObsidian",
                'B',
                BackpackBlocks.BACKPACK_DIAMOND.getItem()).withInt(UPGRADE_SLOTS, BackpackConfig.obsidianUpgradeSlots)
                    .withInt(BACKPACK_SLOTS, BackpackConfig.obsidianBackpackSlots)
                    .allowNBTFrom(BackpackBlocks.BACKPACK_DIAMOND.newItemStack())
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
                    BackpackBlocks.BACKPACK_BASE.newItemStack(),
                    accentOre,
                    mainOre,
                    accentColor,
                    mainColor);
                recipes.registerDyeRecipes(
                    BackpackBlocks.BACKPACK_IRON.newItemStack(),
                    accentOre,
                    mainOre,
                    accentColor,
                    mainColor);
                recipes.registerDyeRecipes(
                    BackpackBlocks.BACKPACK_GOLD.newItemStack(),
                    accentOre,
                    mainOre,
                    accentColor,
                    mainColor);
                recipes.registerDyeRecipes(
                    BackpackBlocks.BACKPACK_DIAMOND.newItemStack(),
                    accentOre,
                    mainOre,
                    accentColor,
                    mainColor);
                recipes.registerDyeRecipes(
                    BackpackBlocks.BACKPACK_OBSIDIAN.newItemStack(),
                    accentOre,
                    mainOre,
                    accentColor,
                    mainColor);
            }
        }

    }

    public static void itemRecipes() {

        // Upgrade Base
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                BackpackItems.BASE_UPGRADE.getItem(),
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
                BackpackItems.STACK_UPGRADE.newItemStack(1, 0),
                "BBB",
                "BUB",
                "BBB",
                'B',
                "blockIron",
                'U',
                BackpackItems.BASE_UPGRADE.getItem()));

        // Stack Upgrade Tier 2
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                BackpackItems.STACK_UPGRADE.newItemStack(1, 1),
                "BBB",
                "BUB",
                "BBB",
                'B',
                "blockGold",
                'U',
                BackpackItems.STACK_UPGRADE.newItemStack(1, 0)));

        // Stack Upgrade Tier 3
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                BackpackItems.STACK_UPGRADE.newItemStack(1, 2),
                "BBB",
                "BUB",
                "BBB",
                'B',
                "blockDiamond",
                'U',
                BackpackItems.STACK_UPGRADE.newItemStack(1, 1)));

        // Stack Upgrade Tier 4
        if (!LibMods.EtFuturum.isLoaded()) {
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    BackpackItems.STACK_UPGRADE.newItemStack(1, 3),
                    "BBB",
                    "BUB",
                    "BBB",
                    'B',
                    "itemNetherStar",
                    'U',
                    BackpackItems.STACK_UPGRADE.newItemStack(1, 2)));
        } else {

            // Stack Upgrade Tier 4
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    BackpackItems.STACK_UPGRADE.newItemStack(1, 3),
                    "BBB",
                    "BUB",
                    "BBB",
                    'B',
                    "blockNetherite",
                    'U',
                    BackpackItems.STACK_UPGRADE.newItemStack(1, 2)));
        }

        // Crafting Upgrade
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                BackpackItems.CRAFTING_UPGRADE.getItem(),
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
                BackpackItems.BASE_UPGRADE.getItem()));

        // Magnet Upgrade
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                BackpackItems.MAGNET_UPGRADE.getItem(),
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
                BackpackItems.PICKUP_UPGRADE.getItem()));

        // Advanced Magnet Upgrade
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                BackpackItems.ADVANCED_MAGNET_UPGRADE.getItem(),
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
                BackpackItems.ADVANCED_PICKUP_UPGRADE.getItem()));

        // Advanced Magnet Upgrade
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                BackpackItems.ADVANCED_MAGNET_UPGRADE.getItem(),
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
                BackpackItems.ADVANCED_PICKUP_UPGRADE.getItem()));

        // Void Upgrade
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                BackpackItems.VOID_UPGRADE.getItem(),
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
                BackpackItems.BASE_UPGRADE.getItem()));

        // Advanced Void Upgrade
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                BackpackItems.ADVANCED_VOID_UPGRADE.getItem(),
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
                BackpackItems.VOID_UPGRADE.getItem()));

        // Feeding Upgrade
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                BackpackItems.FEEDING_UPGRADE.getItem(),
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
                BackpackItems.BASE_UPGRADE.getItem()));

        // Advanced Feeding Upgrade
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                BackpackItems.ADVANCED_FEEDING_UPGRADE.getItem(),
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
                BackpackItems.FEEDING_UPGRADE.getItem()));

        // Pickup Upgrade
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                BackpackItems.PICKUP_UPGRADE.getItem(),
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
                BackpackItems.BASE_UPGRADE.getItem()));

        // Advanced Pickup Upgrade
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                BackpackItems.ADVANCED_PICKUP_UPGRADE.getItem(),
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
                BackpackItems.PICKUP_UPGRADE.getItem()));

        // Filter Upgrade
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                BackpackItems.FILTER_UPGRADE.getItem(),
                "RSR",
                "SUS",
                "RSR",
                'R',
                "dustRedstone",
                'S',
                Items.string,
                'U',
                BackpackItems.BASE_UPGRADE.getItem()));

        // Advanced Filter Upgrade
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                BackpackItems.ADVANCED_FILTER_UPGRADE.getItem(),
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
                BackpackItems.FILTER_UPGRADE.getItem()));

        // Inception Upgrade
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                BackpackItems.INCEPTION_UPGRADE.getItem(),
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
                BackpackItems.BASE_UPGRADE.getItem()));

        // Everlasting Upgrade
        if (!LibMods.EtFuturum.isLoaded()) {
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    BackpackItems.EVERLASTING_UPGRADE.getItem(),
                    "GRG",
                    "RUR",
                    "GRG",
                    'G',
                    "itemGhastTear",
                    'R',
                    "itemNetherStar",
                    'U',

                    BackpackItems.BASE_UPGRADE.getItem()));
        } else {

            // Everlasting Upgrade
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    BackpackItems.EVERLASTING_UPGRADE.getItem(),
                    "GRG",
                    "RUR",
                    "GRG",
                    'G',
                    ganymedes01.etfuturum.ModItems.END_CRYSTAL.get(),
                    'R',
                    "itemNetherStar",
                    'U',

                    BackpackItems.BASE_UPGRADE.getItem()));

        }
    }
}
