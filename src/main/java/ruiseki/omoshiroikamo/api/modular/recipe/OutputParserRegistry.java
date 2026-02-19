package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.JsonObject;

public class OutputParserRegistry {

    private static final Map<String, Function<JsonObject, IRecipeOutput>> parsers = new HashMap<>();

    static {
        register("item", ItemOutput::fromJson);
        register("fluid", FluidOutput::fromJson);
        register("mana", ManaOutput::fromJson);
        register("gas", GasOutput::fromJson);
        register("essentia", EssentiaOutput::fromJson);
        register("vis", VisOutput::fromJson);
        register("energy", EnergyOutput::fromJson);
    }

    /**
     * Register a parser for a specific JSON key.
     * 
     * @param key    The JSON key that identifies this output type (e.g., "item",
     *               "fluid")
     * @param parser Function that creates IRecipeOutput from JsonObject
     */
    public static void register(String key, Function<JsonObject, IRecipeOutput> parser) {
        parsers.put(key, parser);
    }

    public static IRecipeOutput parse(JsonObject json) {
        for (Map.Entry<String, Function<JsonObject, IRecipeOutput>> entry : parsers.entrySet()) {
            if (json.has(entry.getKey())) {
                return entry.getValue()
                    .apply(json);
            }
        }
        throw new IllegalArgumentException("Unknown output type in JSON: " + json);
    }
}
