package ruiseki.omoshiroikamo.module.machinery.common.recipe;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.recipe.IModularRecipe;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.json.JsonErrorCollector;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

public class RecipeLoader {

    private static RecipeLoader instance;

    private final Map<String, List<IModularRecipe>> recipesByGroup = new HashMap<>();

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

        List<IModularRecipe> recipes = JSONLoader.loadRecipes(recipesDir);
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

    public void addRecipe(String group, IModularRecipe recipe) {
        List<IModularRecipe> list = recipesByGroup.computeIfAbsent(group, k -> new ArrayList<>());
        list.add(recipe);
        Collections.sort(list);
    }

    public void clearGroup(String group) {
        recipesByGroup.remove(group);
    }

    /**
     * Scan recipe JSON files to collect group names without full recipe parsing.
     * This is safe to call at any time (even before loadAll) since it only reads
     * the "group" field from each JSON file.
     */
    public static List<String> scanGroupNames(File configDir) {
        List<String> groups = new ArrayList<>();
        File recipesDir = new File(configDir, LibMisc.MOD_ID + "/modular/recipes");
        Logger.info("[scanGroupNames] Scanning dir: {} (exists={})", recipesDir.getAbsolutePath(), recipesDir.exists());
        if (!recipesDir.exists() || !recipesDir.isDirectory()) {
            Logger.warn("[scanGroupNames] Directory not found or not a directory!");
            return groups;
        }

        File[] files = recipesDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) return groups;
        Logger.info("[scanGroupNames] Found {} JSON files", files.length);

        JsonParser parser = new JsonParser();
        for (File file : files) {
            try (FileReader reader = new FileReader(file)) {
                JsonObject root = parser.parse(reader)
                    .getAsJsonObject();
                if (root.has("group")) {
                    String group = root.get("group")
                        .getAsString();
                    Logger.info("[scanGroupNames] File '{}' -> group '{}'", file.getName(), group);
                    if (!groups.contains(group)) {
                        groups.add(group);
                    }
                } else {
                    Logger.warn("[scanGroupNames] File '{}' has no 'group' field", file.getName());
                }
            } catch (Exception e) {
                Logger.warn("[scanGroupNames] Failed to scan group from: {} - {}", file.getName(), e.getMessage());
            }
        }
        Logger.info("[scanGroupNames] Total groups found: {}", groups);
        return groups;
    }
}
