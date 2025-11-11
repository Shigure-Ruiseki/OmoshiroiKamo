package ruiseki.omoshiroikamo.common.recipe;

import static com.enderio.core.common.util.DyeColor.DYE_ORE_NAMES;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.enderio.core.common.util.DyeColor;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistry;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.util.lib.LibMods;

public class ItemRecipes {

    public static void init() {

        // Starter Backpack
        GameRegistry.addRecipe(
            new NBTShapedOreRecipe(
                ModItems.BACKPACK.newItemStack(1, 0),
                "SLS",
                "SCS",
                "LLL",
                'S',
                new ItemStack(Items.string, 1, 1),
                'L',
                "itemLeather",
                'C',
                new ItemStack(Blocks.chest, 1, 1)).withInt("BackpackColor", DyeColor.BROWN.getColor()));

        // Copper Backpack
        GameRegistry.addRecipe(
            new NBTShapedOreRecipe(
                ModItems.BACKPACK.newItemStack(1, 1),
                "CCC",
                "CBC",
                "CCC",
                'C',
                "ingotCopper",
                'B',
                ModItems.BACKPACK.newItemStack(1, 0)).allowNBTFrom(ModItems.BACKPACK.newItemStack(1, 0))
                    .allowAllTags());

        // Iron Backpack
        GameRegistry.addRecipe(
            new NBTShapedOreRecipe(
                ModItems.BACKPACK.newItemStack(1, 2),
                "CCC",
                "CBC",
                "CCC",
                'C',
                "ingotIron",
                'B',
                ModItems.BACKPACK.newItemStack(1, 1)).allowNBTFrom(ModItems.BACKPACK.newItemStack(1, 1))
                    .allowAllTags());

        // Gold Backpack
        GameRegistry.addRecipe(
            new NBTShapedOreRecipe(
                ModItems.BACKPACK.newItemStack(1, 3),
                "CCC",
                "CBC",
                "CCC",
                'C',
                "ingotGold",
                'B',
                ModItems.BACKPACK.newItemStack(1, 2)).allowNBTFrom(ModItems.BACKPACK.newItemStack(1, 2))
                    .allowAllTags());

        // Diamond Backpack
        GameRegistry.addRecipe(
            new NBTShapedOreRecipe(
                ModItems.BACKPACK.newItemStack(1, 4),
                "CCC",
                "CBC",
                "CCC",
                'C',
                "gemDiamond",
                'B',
                ModItems.BACKPACK.newItemStack(1, 3)).allowNBTFrom(ModItems.BACKPACK.newItemStack(1, 3))
                    .allowAllTags());

        if (!LibMods.EtFuturum.isLoaded()) {
            GameRegistry.addRecipe(
                new NBTShapedOreRecipe(
                    ModItems.BACKPACK.newItemStack(1, 5),
                    "CSC",
                    "SBS",
                    "CSC",
                    'S',
                    "itemNetherStar",
                    'C',
                    "blockObsidian",
                    'B',
                    ModItems.BACKPACK.newItemStack(1, 4)).allowNBTFrom(ModItems.BACKPACK.newItemStack(1, 4))
                        .allowAllTags());
        }

        for (int i = 0; i < DYE_ORE_NAMES.length; i++) {
            String dyeOreName = DYE_ORE_NAMES[i];
            GameRegistry.addRecipe(
                new NBTShapelessOreRecipe(
                    ModItems.BACKPACK.newItemStack(1, 0),
                    ModItems.BACKPACK.newItemStack(1, 0),
                    dyeOreName).withInt(
                        "BackpackColor",
                        DyeColor.fromIndex(i)
                            .getColor()));
            GameRegistry.addRecipe(
                new NBTShapelessOreRecipe(
                    ModItems.BACKPACK.newItemStack(1, 1),
                    ModItems.BACKPACK.newItemStack(1, 1),
                    dyeOreName).withInt(
                        "BackpackColor",
                        DyeColor.fromIndex(i)
                            .getColor()));
            GameRegistry.addRecipe(
                new NBTShapelessOreRecipe(
                    ModItems.BACKPACK.newItemStack(1, 2),
                    ModItems.BACKPACK.newItemStack(1, 2),
                    dyeOreName).withInt(
                        "BackpackColor",
                        DyeColor.fromIndex(i)
                            .getColor()));
            GameRegistry.addRecipe(
                new NBTShapelessOreRecipe(
                    ModItems.BACKPACK.newItemStack(1, 3),
                    ModItems.BACKPACK.newItemStack(1, 3),
                    dyeOreName).withInt(
                        "BackpackColor",
                        DyeColor.fromIndex(i)
                            .getColor()));
            GameRegistry.addRecipe(
                new NBTShapelessOreRecipe(
                    ModItems.BACKPACK.newItemStack(1, 4),
                    ModItems.BACKPACK.newItemStack(1, 4),
                    dyeOreName).withInt(
                        "BackpackColor",
                        DyeColor.fromIndex(i)
                            .getColor()));
            GameRegistry.addRecipe(
                new NBTShapelessOreRecipe(
                    ModItems.BACKPACK.newItemStack(1, 5),
                    ModItems.BACKPACK.newItemStack(1, 5),
                    dyeOreName).withInt(
                        "BackpackColor",
                        DyeColor.fromIndex(i)
                            .getColor()));
        }

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
                ModItems.BASE_UPGRADE.get()));

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
                ModBlocks.STRUCTURE_FRAME.newItemStack(1, 1)));

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
                ModBlocks.STRUCTURE_FRAME.newItemStack(1, 5)));

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
                ModBlocks.STRUCTURE_FRAME.newItemStack(1, 9)));

        // Colored Egg
        for (ChickensRegistryItem chicken : ChickensRegistry.INSTANCE.getItems()) {

            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModItems.COLORED_EGG.newItemStack(1, chicken.getDyeMetadata()),
                    "DDD",
                    "DED",
                    "DDD",
                    'E',
                    Items.egg,
                    'D',
                    new ItemStack(Items.dye, 1, chicken.getDyeMetadata())));
        }

        // Analyzer
        GameRegistry.addShapelessRecipe(ModItems.ANALYZER.newItemStack(), Items.egg, Items.compass);

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

        GameRegistry.addSmelting(ModItems.CHICKEN_SPAWN_EGG.get(), new ItemStack(Items.cooked_chicken), 0.35f);

    }

}
