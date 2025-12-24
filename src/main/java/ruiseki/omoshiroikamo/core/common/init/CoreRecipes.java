package ruiseki.omoshiroikamo.core.common.init;

import net.minecraftforge.oredict.RecipeSorter;

import ruiseki.omoshiroikamo.core.common.recipe.NBTShapedOreRecipe;
import ruiseki.omoshiroikamo.core.common.recipe.NBTShapelessOreRecipe;
import ruiseki.omoshiroikamo.core.lib.LibResources;

public class CoreRecipes {

    public static void init() {
        initRecipeSorter();
    }

    public static void initRecipeSorter() {
        RecipeSorter.register(
            LibResources.PREFIX_MOD + "nbtshaped",
            NBTShapedOreRecipe.class,
            RecipeSorter.Category.SHAPED,
            "after:minecraft:shaped");
        RecipeSorter.register(
            LibResources.PREFIX_MOD + "nbtshapeless",
            NBTShapelessOreRecipe.class,
            RecipeSorter.Category.SHAPELESS,
            "after:minecraft:shapeless");
    }
}
