package ruiseki.omoshiroikamo.core.init;

import net.minecraftforge.oredict.RecipeSorter;

import ruiseki.omoshiroikamo.Reference;
import ruiseki.omoshiroikamo.core.recipe.NBTShapedOreRecipe;
import ruiseki.omoshiroikamo.core.recipe.NBTShapelessOreRecipe;

public class CoreRecipes {

    public static void init() {
        initRecipeSorter();
    }

    public static void initRecipeSorter() {
        RecipeSorter.register(
            Reference.PREFIX_MOD + "nbtshaped",
            NBTShapedOreRecipe.class,
            RecipeSorter.Category.SHAPED,
            "after:minecraft:shaped");
        RecipeSorter.register(
            Reference.PREFIX_MOD + "nbtshapeless",
            NBTShapelessOreRecipe.class,
            RecipeSorter.Category.SHAPELESS,
            "after:minecraft:shapeless");
    }
}
