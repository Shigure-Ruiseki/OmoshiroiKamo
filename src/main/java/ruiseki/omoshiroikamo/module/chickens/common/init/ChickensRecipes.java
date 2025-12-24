package ruiseki.omoshiroikamo.module.chickens.common.init;

import static ruiseki.omoshiroikamo.api.enums.EnumDye.DYE_ORE_NAMES;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import cpw.mods.fml.common.registry.GameRegistry;

public class ChickensRecipes {

    public static void init() {
        blockRecipes();
        itemRecipes();
    }

    public static void blockRecipes() {

        // Roost
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                ChickensBlocks.ROOST.getItem(),
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
                ChickensBlocks.BREEDER.getItem(),
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
                ChickensBlocks.ROOST_COLLECTOR.getItem(),
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

    public static void itemRecipes() {

        // Colored Egg
        for (int i = 0; i < DYE_ORE_NAMES.length; i++) {
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ChickensItems.COLORED_EGG.newItemStack(1, i),
                    "DDD",
                    "DED",
                    "DDD",
                    'E',
                    Items.egg,
                    'D',
                    DYE_ORE_NAMES[i]));
        }

        // Analyzer
        GameRegistry.addShapelessRecipe(ChickensItems.ANALYZER.newItemStack(), Items.stick, Items.compass);

        // Chicken Catcher
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                ChickensItems.CHICKEN_CATCHER.newItemStack(),
                " E ",
                " S ",
                " F ",
                'E',
                Items.egg,
                'S',
                "stickWood",
                'F',
                Items.feather));

        GameRegistry.addSmelting(ChickensItems.CHICKEN.getItem(), new ItemStack(Items.cooked_chicken), 0.35f);
    }
}
