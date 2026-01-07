package ruiseki.omoshiroikamo.module.machinery.common.recipe;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ruiseki.omoshiroikamo.api.modular.recipe.IRecipeInput;
import ruiseki.omoshiroikamo.api.modular.recipe.IRecipeOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.InputParserRegistry;
import ruiseki.omoshiroikamo.api.modular.recipe.ModularRecipe;
import ruiseki.omoshiroikamo.api.modular.recipe.OutputParserRegistry;
import ruiseki.omoshiroikamo.core.common.util.Logger;

/**
 * Loads recipe definitions from JSON files.
 * Uses Registry pattern for extensible input/output parsing.
 */
public class JSONLoader {

    /**
     * Load all recipe files from a directory.
     * Each file contains a recipe group with multiple recipes.
     * 
     * @param recipesDir the directory containing recipe JSON files
     * @return list of loaded recipes
     */
    public static List<ModularRecipe> loadRecipes(File recipesDir) {
        List<ModularRecipe> recipes = new ArrayList<>();

        if (!recipesDir.exists() || !recipesDir.isDirectory()) {
            Logger.warn("Recipe directory does not exist: {}", recipesDir.getAbsolutePath());
            return recipes;
        }

        File[] files = recipesDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) {
            return recipes;
        }

        for (File file : files) {
            try {
                List<ModularRecipe> loaded = loadRecipeFile(file);
                recipes.addAll(loaded);
                Logger.info("Loaded {} recipes from {}", loaded.size(), file.getName());
            } catch (Exception e) {
                Logger.error("Failed to load recipe file: {}", file.getName());
                Logger.error("Error: {}", e.getMessage());
            }
        }

        return recipes;
    }

    /**
     * Load recipes from a single JSON file.
     */
    public static List<ModularRecipe> loadRecipeFile(File file) throws IOException {
        List<ModularRecipe> recipes = new ArrayList<>();

        try (FileReader reader = new FileReader(file)) {
            JsonParser parser = new JsonParser();
            JsonObject root = parser.parse(reader)
                .getAsJsonObject();

            String group = root.get("group")
                .getAsString();
            JsonArray recipeArray = root.getAsJsonArray("recipes");

            for (JsonElement elem : recipeArray) {
                ModularRecipe recipe = parseRecipe(elem.getAsJsonObject(), group);
                if (recipe != null) {
                    recipes.add(recipe);
                }
            }
        }

        return recipes;
    }

    /**
     * Parse a single recipe from JSON.
     */
    public static ModularRecipe parseRecipe(JsonObject json, String group) {
        try {
            // TODO: name field for future use
            // String name = json.has("name") ? json.get("name").getAsString() : null;
            int duration = json.get("duration")
                .getAsInt();
            int priority = json.has("priority") ? json.get("priority")
                .getAsInt() : 0;

            ModularRecipe.Builder builder = ModularRecipe.builder()
                .machineType(group)
                .duration(duration)
                .priority(priority);

            // Parse inputs
            JsonArray inputs = json.getAsJsonArray("inputs");
            for (JsonElement inputElem : inputs) {
                IRecipeInput input = InputParserRegistry.parse(inputElem.getAsJsonObject());
                if (input != null) {
                    builder.addInput(input);
                }
            }

            // Parse outputs
            JsonArray outputs = json.getAsJsonArray("outputs");
            for (JsonElement outputElem : outputs) {
                IRecipeOutput output = OutputParserRegistry.parse(outputElem.getAsJsonObject());
                if (output != null) {
                    builder.addOutput(output);
                }
            }

            return builder.build();
        } catch (Exception e) {
            Logger.warn("Failed to parse recipe: {}", e.getMessage());
            return null;
        }
    }
}
