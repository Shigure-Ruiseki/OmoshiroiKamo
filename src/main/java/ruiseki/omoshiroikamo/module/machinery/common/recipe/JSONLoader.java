package ruiseki.omoshiroikamo.module.machinery.common.recipe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.recipe.core.IModularRecipe;
import ruiseki.omoshiroikamo.core.common.util.Logger;

/**
 * JSONLoader class for loading recipes from JSON files.
 */
public class JSONLoader {

    public static List<IModularRecipe> loadRecipes(MachineryJsonReader reader) {
        try {
            List<JsonObject> recipeJsons = reader.read();
            List<IModularRecipe> recipes = MachineryRecipeLoader.load(recipeJsons);

            Logger.info(
                "Loaded {} recipes from {}",
                recipes.size(),
                reader.getPath()
                    .getName());

            return recipes;
        } catch (IOException e) {
            Logger.error(
                "Failed to load recipes from {}: {}",
                reader.getPath()
                    .getName(),
                e.getMessage());
            return new ArrayList<>();
        }
    }
}
