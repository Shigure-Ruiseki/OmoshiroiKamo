package ruiseki.omoshiroikamo.module.machinery.common.recipe;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.recipe.ModularRecipe;
import ruiseki.omoshiroikamo.core.common.util.Logger;

public class RecipeLoader {

    private static RecipeLoader instance;

    private final Map<String, List<ModularRecipe>> recipesByGroup = new HashMap<>();

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
        if (!recipesDir.exists()) {
            recipesDir.mkdirs();
            Logger.info("Created recipe directory: {}", recipesDir.getAbsolutePath());
        }

        recipesByGroup.clear();

        List<ModularRecipe> recipes = JSONLoader.loadRecipes(recipesDir);
        for (ModularRecipe recipe : recipes) {
            String group = recipe.getRecipeGroup();
            recipesByGroup.computeIfAbsent(group, k -> new ArrayList<>())
                .add(recipe);
        }

        // Sort each group
        for (List<ModularRecipe> list : recipesByGroup.values()) {
            Collections.sort(list);
        }

        Logger.info("Loaded {} recipes in {} groups", recipes.size(), recipesByGroup.size());
    }

    public void reload(File configDir) {
        Logger.info("Reloading recipes...");
        recipeVersion++;
        loadAll(configDir);
    }

    public List<ModularRecipe> getRecipes(String... groups) {
        List<ModularRecipe> result = new ArrayList<>();
        for (String group : groups) {
            List<ModularRecipe> list = recipesByGroup.get(group);
            if (list != null) {
                result.addAll(list);
            }
        }
        Collections.sort(result);
        return result;
    }

    public ModularRecipe findMatch(String[] groups, List<IModularPort> inputPorts) {
        List<ModularRecipe> candidates = getRecipes(groups);
        for (ModularRecipe recipe : candidates) {
            if (recipe.matchesInput(inputPorts)) {
                return recipe;
            }
        }
        return null;
    }

    public void addRecipe(String group, ModularRecipe recipe) {
        List<ModularRecipe> list = recipesByGroup.computeIfAbsent(group, k -> new ArrayList<>());
        list.add(recipe);
        Collections.sort(list);
    }

    public void clearGroup(String group) {
        recipesByGroup.remove(group);
    }
}
