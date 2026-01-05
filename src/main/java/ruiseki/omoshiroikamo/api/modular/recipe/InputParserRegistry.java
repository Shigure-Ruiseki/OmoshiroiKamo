package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.JsonObject;

/**
 * Registry for input parsers.
 * Uses Factory + Registry pattern for extensibility.
 */
public class InputParserRegistry {

    private static final Map<String, Function<JsonObject, IRecipeInput>> parsers = new HashMap<>();

    static {
        register("item", ItemInput::fromJson);
        register("fluid", FluidInput::fromJson);
        register("energy", EnergyInput::fromJson);
    }

    /**
     * Register a parser for a specific JSON key.
     * 
     * @param key    The JSON key that identifies this input type (e.g., "item",
     *               "fluid")
     * @param parser Function that creates IRecipeInput from JsonObject
     */
    public static void register(String key, Function<JsonObject, IRecipeInput> parser) {
        parsers.put(key, parser);
    }

    /**
     * Parse a JsonObject into an IRecipeInput.
     * Determines type by checking which key is present.
     */
    public static IRecipeInput parse(JsonObject json) {
        for (Map.Entry<String, Function<JsonObject, IRecipeInput>> entry : parsers.entrySet()) {
            if (json.has(entry.getKey())) {
                return entry.getValue()
                    .apply(json);
            }
        }
        throw new IllegalArgumentException("Unknown input type in JSON: " + json);
    }
}
