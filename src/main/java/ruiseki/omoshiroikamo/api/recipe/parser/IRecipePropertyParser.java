package ruiseki.omoshiroikamo.api.recipe.parser;

import com.google.gson.JsonElement;

import ruiseki.omoshiroikamo.api.recipe.core.ModularRecipe;

/**
 * Interface for parsing a specific JSON property into a ModularRecipe.Builder.
 */
public interface IRecipePropertyParser {

    /**
     * Parses the given JSON element and applies it to the builder.
     * 
     * @param builder The recipe builder to apply changes to.
     * @param element The JSON element to parse.
     */
    void parse(ModularRecipe.Builder builder, JsonElement element);
}
