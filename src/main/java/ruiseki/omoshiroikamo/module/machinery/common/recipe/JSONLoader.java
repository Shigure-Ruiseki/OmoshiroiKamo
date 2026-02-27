package ruiseki.omoshiroikamo.module.machinery.common.recipe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.modular.recipe.IModularRecipe;
import ruiseki.omoshiroikamo.api.modular.recipe.IRecipeInput;
import ruiseki.omoshiroikamo.api.modular.recipe.IRecipeOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.ModularRecipe;
import ruiseki.omoshiroikamo.core.common.util.Logger;

/**
 * JSONLoader class for loading recipes from JSON files.
 * TODO: NBT support for recipes
 * TODO: Support No Consume
 */

public class JSONLoader {

    public static List<IModularRecipe> loadRecipes(File recipesDir) {
        List<IModularRecipe> recipes = new ArrayList<>();

        if (!recipesDir.exists() || !recipesDir.isDirectory()) {
            Logger.warn("Recipe directory does not exist: {}", recipesDir.getAbsolutePath());
            return recipes;
        }

        try {
            MachineryJsonReader reader = new MachineryJsonReader(recipesDir);
            List<MachineryMaterial> materials = reader.read();
            for (MachineryMaterial mat : materials) {
                IModularRecipe recipe = toRecipe(mat);
                if (recipe != null) {
                    recipes.add(recipe);
                }
            }
            Logger.info("Loaded {} recipes from {}", recipes.size(), recipesDir.getName());
        } catch (IOException e) {
            Logger.error("Failed to load recipes from {}: {}", recipesDir.getName(), e.getMessage());
        }

        return recipes;
    }

    private static IModularRecipe toRecipe(MachineryMaterial mat) {
        try {
            ModularRecipe.Builder builder = ModularRecipe.builder()
                .registryName(mat.registryName)
                .name(mat.localizedName != null ? mat.localizedName : "")
                .recipeGroup(mat.machine) // Assuming machine name is used as group for now or similar
                .duration(mat.time)
                .priority(0); // Default priority

            for (IRecipeInput input : mat.inputs) {
                builder.addInput(input);
            }

            for (IRecipeOutput output : mat.outputs) {
                builder.addOutput(output);
            }

            IModularRecipe recipe = builder.build();

            // Apply decorators if present in the original JSON
            if (mat.getRawJson() != null) {
                recipe = mat.applyDecorators(recipe, mat.getRawJson());
            }

            return recipe;
        } catch (Exception e) {
            Logger.warn("Failed to convert material to recipe: {}", e.getMessage());
            return null;
        }
    }
}
