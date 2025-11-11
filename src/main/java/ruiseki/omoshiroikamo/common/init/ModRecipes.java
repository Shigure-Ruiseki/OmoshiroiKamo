package ruiseki.omoshiroikamo.common.init;

import net.minecraftforge.oredict.RecipeSorter;

import ruiseki.omoshiroikamo.common.recipe.BlockRecipes;
import ruiseki.omoshiroikamo.common.recipe.ItemRecipes;
import ruiseki.omoshiroikamo.common.recipe.NBTShapedOreRecipe;
import ruiseki.omoshiroikamo.common.recipe.NBTShapelessOreRecipe;
import ruiseki.omoshiroikamo.common.recipe.machine.MachineRecipeRegistry;
import ruiseki.omoshiroikamo.common.recipe.quantumExtractor.QuantumExtractorRecipes;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class ModRecipes {

    public static void init() {
        initRecipeSorter();

        ItemRecipes.init();
        BlockRecipes.init();
        QuantumExtractorRecipes.init();
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

    public static void loadAllRecipes() {}

    public static void reloadRecipes() {
        MachineRecipeRegistry.clearAll();

        loadAllRecipes();

        System.out.println("[RecipeLoader] Recipes reloaded.");
    }
}
