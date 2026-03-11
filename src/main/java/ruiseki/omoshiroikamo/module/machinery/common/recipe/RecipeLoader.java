package ruiseki.omoshiroikamo.module.machinery.common.recipe;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.recipe.core.IModularRecipe;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.json.JsonErrorCollector;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

public class RecipeLoader {

    private static RecipeLoader instance;

    private final Map<String, List<IModularRecipe>> recipesByGroup = new HashMap<>();

    private MachineryJsonReader reader;

    private int recipeVersion = 0;

    private RecipeLoader() {}

    public static RecipeLoader getInstance() {
        if (instance == null) {
            instance = new RecipeLoader();
        }
        return instance;
    }

    public int getRecipeVersion() {
        return recipeVersion;
    }

    public void loadAll(File configDir) {
        File recipesDir = new File(configDir, "omoshiroikamo/modular/recipes");
        Logger.info("Loading recipes from: " + recipesDir.getAbsolutePath());
        if (!recipesDir.exists()) {
            recipesDir.mkdirs();
            Logger.info("Created recipe directory: {}", recipesDir.getAbsolutePath());
        }

        recipesByGroup.clear();

        if (reader == null || !reader.getPath()
            .equals(recipesDir)) {
            reader = new MachineryJsonReader(recipesDir);
        }

        List<IModularRecipe> recipes = JSONLoader.loadRecipes(reader);
        for (IModularRecipe recipe : recipes) {
            String group = recipe.getRecipeGroup()
                .toLowerCase();
            recipesByGroup.computeIfAbsent(group, k -> new ArrayList<>())
                .add(recipe);
        }

        // Sort each group
        for (List<IModularRecipe> list : recipesByGroup.values()) {
            Collections.sort(list);
        }

        Logger.info("Loaded {} recipes in {} groups", recipes.size(), recipesByGroup.size());
    }

    public void reload(File configDir) {
        // Clear errors before reloading
        JsonErrorCollector.getInstance()
            .clear();
        JsonErrorCollector.getInstance()
            .setConfigDir(new File(configDir, LibMisc.MOD_ID));

        Logger.info("Reloading recipes...");
        recipeVersion++;
        loadAll(configDir);
    }

    public List<IModularRecipe> getRecipes(String... groups) {
        List<IModularRecipe> result = new ArrayList<>();
        for (String group : groups) {
            List<IModularRecipe> list = recipesByGroup.get(group.toLowerCase());
            if (list != null) {
                result.addAll(list);
            }
        }
        Collections.sort(result);
        return result;
    }

    public List<IModularRecipe> getAllRecipes() {
        List<IModularRecipe> result = new ArrayList<>();
        for (List<IModularRecipe> list : recipesByGroup.values()) {
            result.addAll(list);
        }
        Collections.sort(result);
        return result;
    }

    public IModularRecipe findMatch(String[] groups, List<IModularPort> inputPorts) {
        List<IModularRecipe> candidates = getRecipes(groups);
        for (IModularRecipe recipe : candidates) {
            if (recipe.matchesInput(inputPorts)) {
                return recipe;
            }
        }
        return null;
    }

    public IModularRecipe getRecipeByRegistryName(String registryName) {
        if (registryName == null || registryName.isEmpty()) return null;
        for (List<IModularRecipe> list : recipesByGroup.values()) {
            for (IModularRecipe recipe : list) {
                if (registryName.equals(recipe.getRegistryName())) {
                    return recipe;
                }
            }
        }
        return null;
    }

    public void addRecipe(String group, IModularRecipe recipe) {
        List<IModularRecipe> list = recipesByGroup.computeIfAbsent(group, k -> new ArrayList<>());
        list.add(recipe);
        Collections.sort(list);
    }

    public void clearGroup(String group) {
        recipesByGroup.remove(group);
    }

    /**
     * Scan recipe JSON files to collect group names.
     * Uses the internal reader and its cache if available.
     */
    public static List<String> scanGroupNames(File configDir) {
        RecipeLoader instance = getInstance();
        File recipesDir = new File(configDir, LibMisc.MOD_ID + "/modular/recipes");

        if (instance.reader == null || !instance.reader.getPath()
            .equals(recipesDir)) {
            instance.reader = new MachineryJsonReader(recipesDir);
        }

        List<String> groups = new ArrayList<>();
        try {
            // Use existing cache if available, otherwise read
            List<JsonObject> materials = instance.reader.getData();
            if (materials == null) {
                materials = instance.reader.read();
            }

            for (JsonObject mat : materials) {
                String group = mat.has("machine") ? mat.get("machine")
                    .getAsString()
                    : (mat.has("group") ? mat.get("group")
                        .getAsString() : (mat.has("parent") ? null : "default"));
                if (group != null && !groups.contains(group)) {
                    groups.add(group);
                }
            }
        } catch (Exception e) {
            Logger.warn("[scanGroupNames] Failed to scan group names via reader: {}", e.getMessage());
        }

        return groups;
    }
}
