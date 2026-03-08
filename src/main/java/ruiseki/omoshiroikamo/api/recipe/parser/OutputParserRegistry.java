package ruiseki.omoshiroikamo.api.recipe.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.recipe.io.BlockNbtOutput;
import ruiseki.omoshiroikamo.api.recipe.io.BlockOutput;
import ruiseki.omoshiroikamo.api.recipe.io.EnergyOutput;
import ruiseki.omoshiroikamo.api.recipe.io.EssentiaOutput;
import ruiseki.omoshiroikamo.api.recipe.io.FluidOutput;
import ruiseki.omoshiroikamo.api.recipe.io.GasOutput;
import ruiseki.omoshiroikamo.api.recipe.io.IRecipeOutput;
import ruiseki.omoshiroikamo.api.recipe.io.ItemOutput;
import ruiseki.omoshiroikamo.api.recipe.io.ManaOutput;
import ruiseki.omoshiroikamo.api.recipe.io.VisOutput;

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
        register("symbol", BlockOutput::fromJson);
        register("block_nbt", BlockNbtOutput::fromJson);
    }

    /**
     * Register a parser for a specific type or identifying key.
     * 
     * @param key    The JSON key or "type" value
     * @param parser Function that creates IRecipeOutput from JsonObject
     */
    public static void register(String key, Function<JsonObject, IRecipeOutput> parser) {
        parsers.put(key, parser);
    }

    public static IRecipeOutput parse(JsonObject json) {
        // 1. Try "type" field first
        if (json.has("type")) {
            String type = json.get("type")
                .getAsString();
            if (parsers.containsKey(type)) {
                return parsers.get(type)
                    .apply(json);
            }
        }

        // 2. Fallback to identifying keys
        for (Map.Entry<String, Function<JsonObject, IRecipeOutput>> entry : parsers.entrySet()) {
            if (json.has(entry.getKey())) {
                return entry.getValue()
                    .apply(json);
            }
        }
        throw new IllegalArgumentException("Unknown output type in JSON: " + json);
    }
}
