package ruiseki.omoshiroikamo.module.machinery.common.recipe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ruiseki.omoshiroikamo.api.condition.ICondition;
import ruiseki.omoshiroikamo.api.recipe.core.IModularRecipe;
import ruiseki.omoshiroikamo.api.recipe.core.ModularRecipe;
import ruiseki.omoshiroikamo.api.recipe.expression.RecipeScriptException;
import ruiseki.omoshiroikamo.api.recipe.io.IRecipeInput;
import ruiseki.omoshiroikamo.api.recipe.io.IRecipeOutput;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.json.JsonErrorCollector;

/**
 * JSONLoader class for loading recipes from JSON files.
 * TODO: NBT support for recipes
 * TODO: Support No Consume
 */

public class JSONLoader {

    public static List<IModularRecipe> loadRecipes(MachineryJsonReader reader) {
        List<IModularRecipe> recipes = new ArrayList<>();

        try {
            List<MachineryMaterial> materials = reader.read();

            // 1. Map all materials by registry name
            Map<String, MachineryMaterial> materialMap = new HashMap<>();
            for (MachineryMaterial mat : materials) {
                if (mat.registryName != null) {
                    materialMap.put(mat.registryName, mat);
                }
            }

            // 2. Resolve inheritance
            for (MachineryMaterial mat : materials) {
                resolveInheritance(mat, materialMap, new HashSet<>());
            }

            // 3. Convert valid, non-abstract materials to recipes
            for (MachineryMaterial mat : materials) {
                if (mat.isAbstract) continue;
                if (!mat.validate()) continue;

                IModularRecipe recipe = toRecipe(mat);
                if (recipe != null) {
                    recipes.add(recipe);
                }
            }
            Logger.info(
                "Loaded {} recipes from {}",
                recipes.size(),
                reader.getPath()
                    .getName());
        } catch (IOException e) {
            Logger.error(
                "Failed to load recipes from {}: {}",
                reader.getPath()
                    .getName(),
                e.getMessage());
        }

        return recipes;
    }

    private static void resolveInheritance(MachineryMaterial mat, Map<String, MachineryMaterial> map,
        Set<String> resolving) {
        if (mat.parent == null) return;
        if (resolving.contains(mat.registryName)) {
            Logger.error("Circular inheritance detected for recipe: {}", mat.registryName);
            return;
        }

        MachineryMaterial parent = map.get(mat.parent);
        if (parent != null) {
            resolving.add(mat.registryName);
            resolveInheritance(parent, map, resolving);
            mat.mergeParent(parent);
            mat.parent = null; // Mark as merged
        } else {
            Logger.warn("Recipe {} has missing parent: {}", mat.registryName, mat.parent);
        }
    }

    private static IModularRecipe toRecipe(MachineryMaterial mat) {
        try {
            ModularRecipe.Builder builder = ModularRecipe.builder()
                .registryName(mat.registryName)
                .name(mat.localizedName != null ? mat.localizedName : "")
                .recipeGroup(mat.machine) // Assuming machine name is used as group for now or similar
                .duration(mat.time)
                .priority(mat.priority);

            for (IRecipeInput input : mat.inputs) {
                builder.addInput(input);
            }

            for (IRecipeOutput output : mat.outputs) {
                builder.addOutput(output);
            }

            for (ICondition condition : mat.conditions) {
                builder.addCondition(condition);
            }

            IModularRecipe recipe = builder.build();

            // Apply decorators if present in the original JSON
            if (mat.getRawJson() != null) {
                recipe = mat.applyDecorators(recipe, mat.getRawJson());
            }

            return recipe;
        } catch (RecipeScriptException e) {
            JsonErrorCollector.getInstance()
                .collectScriptError(mat.registryName, e);
            return null;
        } catch (Exception e) {
            Logger.warn("Failed to convert material to recipe {}: {}", mat.registryName, e.getMessage());
            return null;
        }
    }
}
