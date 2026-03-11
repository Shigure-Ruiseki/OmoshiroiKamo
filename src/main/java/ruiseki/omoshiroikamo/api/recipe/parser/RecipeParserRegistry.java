package ruiseki.omoshiroikamo.api.recipe.parser;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.recipe.core.ModularRecipe;
import ruiseki.omoshiroikamo.api.recipe.parser.impl.ConditionPropertyParser;
import ruiseki.omoshiroikamo.api.recipe.parser.impl.DurationParser;
import ruiseki.omoshiroikamo.api.recipe.parser.impl.InputPropertyParser;
import ruiseki.omoshiroikamo.api.recipe.parser.impl.OutputPropertyParser;

/**
 * Registry for recipe property parsers.
 */
public class RecipeParserRegistry {

    private static final Map<String, IRecipePropertyParser> parsers = new HashMap<>();

    static {
        // Basic Info
        register(
            "registryName",
            (builder, element) -> { if (element.isJsonPrimitive()) builder.registryName(element.getAsString()); });
        register(
            "localizedName",
            (builder, element) -> { if (element.isJsonPrimitive()) builder.name(element.getAsString()); });
        register("name", (builder, element) -> { if (element.isJsonPrimitive()) builder.name(element.getAsString()); });
        register(
            "machine",
            (builder, element) -> { if (element.isJsonPrimitive()) builder.recipeGroup(element.getAsString()); });
        register(
            "group",
            (builder, element) -> { if (element.isJsonPrimitive()) builder.recipeGroup(element.getAsString()); });
        register(
            "priority",
            (builder, element) -> { if (element.isJsonPrimitive()) builder.priority(element.getAsInt()); });

        // Components
        DurationParser durationParser = new DurationParser();
        register("duration", durationParser);
        register("time", durationParser);

        InputPropertyParser inputParser = new InputPropertyParser();
        register("inputs", inputParser);
        register("input", inputParser);

        OutputPropertyParser outputParser = new OutputPropertyParser();
        register("outputs", outputParser);
        register("output", outputParser);

        ConditionPropertyParser conditionParser = new ConditionPropertyParser();
        register("conditions", conditionParser);
        register("condition", conditionParser);

        // Tiers
        register("tiers", (builder, element) -> {
            if (element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                    builder.addRequiredComponentTier(
                        entry.getKey(),
                        entry.getValue()
                            .getAsInt());
                }
            }
        });
        register("tier", (builder, element) -> {
            if (element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                    builder.addRequiredComponentTier(
                        entry.getKey(),
                        entry.getValue()
                            .getAsInt());
                }
            }
        });
    }

    /**
     * Registers a parser for a specific JSON key.
     * 
     * @param key    The JSON key (e.g., "inputs", "duration").
     * @param parser The parser implementation.
     */
    public static void register(String key, IRecipePropertyParser parser) {
        parsers.put(key, parser);
    }

    /**
     * Gets a parser for a specific JSON key.
     * 
     * @param key The JSON key.
     * @return The parser, or null if not found.
     */
    public static IRecipePropertyParser getParser(String key) {
        return parsers.get(key);
    }

    /**
     * Parses a property and applies it to the builder.
     * 
     * @param builder The recipe builder.
     * @param key     The JSON key.
     * @param element The JSON element.
     * @return true if a parser was found and applied, false otherwise.
     */
    public static boolean parse(ModularRecipe.Builder builder, String key, JsonElement element) {
        IRecipePropertyParser parser = getParser(key);
        if (parser != null) {
            parser.parse(builder, element);
            return true;
        }
        return false;
    }
}
