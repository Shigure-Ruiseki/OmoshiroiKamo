package ruiseki.omoshiroikamo.module.machinery.common.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.recipe.core.IModularRecipe;
import ruiseki.omoshiroikamo.api.recipe.core.ModularRecipe;
import ruiseki.omoshiroikamo.api.recipe.parser.DecoratorParser;
import ruiseki.omoshiroikamo.api.recipe.parser.RecipeParserRegistry;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.json.JsonErrorCollector;
import ruiseki.omoshiroikamo.core.json.ParsingContext;

/**
 * Loader class that converts JSON objects into ModularRecipe instances.
 * Uses RecipeParserRegistry for extensible property parsing.
 */
public class MachineryRecipeLoader {

    public static List<IModularRecipe> load(List<JsonObject> recipeJsons) {
        List<IModularRecipe> recipes = new ArrayList<>();
        Map<String, JsonObject> recipeMap = new HashMap<>();

        // 1. Index recipes by registryName and validate basic info
        for (JsonObject json : recipeJsons) {
            String registryName = getOrGenerateRegistryName(json);
            if (registryName != null) {
                recipeMap.put(registryName, json);
                // Ensure registryName is set in JSON for inheritance/parsing
                json.addProperty("registryName", registryName);
            }
        }

        // 2. Resolve inheritance
        Set<String> resolved = new HashSet<>();
        for (String name : recipeMap.keySet()) {
            resolveInheritance(name, recipeMap, resolved, new HashSet<>());
        }

        // 3. Parse JSONs to recipes
        for (JsonObject json : recipeJsons) {
            if (json.has("abstract") && json.get("abstract")
                .getAsBoolean()) {
                continue;
            }

            IModularRecipe recipe = parseRecipe(json);
            if (recipe != null) {
                recipes.add(recipe);
            }
        }

        return recipes;
    }

    private static String getOrGenerateRegistryName(JsonObject json) {
        if (json.has("registryName")) {
            return json.get("registryName")
                .getAsString();
        }

        String name = json.has("localizedName") ? json.get("localizedName")
            .getAsString()
            : (json.has("name") ? json.get("name")
                .getAsString() : null);

        if (name != null) {
            return name.toLowerCase()
                .replaceAll("\\s+", "_")
                .replaceAll("[^a-z0-9_]", "");
        }
        return null;
    }

    private static void resolveInheritance(String name, Map<String, JsonObject> map, Set<String> resolved,
        Set<String> resolving) {
        if (resolved.contains(name)) return;

        JsonObject json = map.get(name);
        if (json == null || !json.has("parent")) {
            resolved.add(name);
            return;
        }

        String parentName = json.get("parent")
            .getAsString();
        if (resolving.contains(name)) {
            Logger.error("Circular inheritance detected for recipe: {}", name);
            return;
        }

        JsonObject parentJson = map.get(parentName);
        if (parentJson != null) {
            resolving.add(name);
            resolveInheritance(parentName, map, resolved, resolving);
            RecipeJsonMergeUtil.merge(json, parentJson);
            json.remove("parent"); // Mark as merged
        } else {
            Logger.warn("Recipe {} has missing parent: {}", name, parentName);
        }
        resolved.add(name);
    }

    private static IModularRecipe parseRecipe(JsonObject json) {
        String registryName = json.has("registryName") ? json.get("registryName")
            .getAsString() : "unknown";
        try {
            ModularRecipe.Builder builder = ModularRecipe.builder();

            // Set default recipeGroup (required by builder)
            builder.recipeGroup("default");

            // Use registry to parse all properties
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                RecipeParserRegistry.parse(builder, entry.getKey(), entry.getValue());
            }

            IModularRecipe recipe = builder.build();

            // Apply decorators (decorators usually wrap the recipe instance)
            if (json.has("decorators")) {
                recipe = DecoratorParser.parse(recipe, json.get("decorators"));
            }

            return recipe;
        } catch (Exception e) {
            String fileName = ParsingContext.getCurrentFileName();
            String errorMsg = String
                .format("Failed to parse recipe '%s' in %s: %s", registryName, fileName, e.getMessage());
            Logger.error(errorMsg);
            JsonErrorCollector.getInstance()
                .collect("MachineryRecipeLoader", errorMsg);
            return null;
        }
    }
}
