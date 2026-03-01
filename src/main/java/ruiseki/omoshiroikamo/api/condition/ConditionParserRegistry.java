package ruiseki.omoshiroikamo.api.condition;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.core.common.util.Logger;

/**
 * Registry for parsing ICondition from JSON objects.
 * Similar to InputParserRegistry in modular module.
 */
public class ConditionParserRegistry {

    private static final Map<String, Function<JsonObject, ICondition>> parsers = new HashMap<>();

    /**
     * Register a parser for a specific condition type.
     * 
     * @param type   The condition type key (e.g., "dimension", "biome")
     * @param parser Function that creates ICondition from JsonObject
     */
    public static void register(String type, Function<JsonObject, ICondition> parser) {
        parsers.put(type, parser);
    }

    /**
     * Parse a JsonObject into an ICondition.
     * 
     * @param json The JsonObject representing the condition. Must have a "type"
     *             field.
     * @return The parsed ICondition, or null if parsing failed.
     */
    public static ICondition parse(JsonObject json) {
        if (!json.has("type")) {
            Logger.warn("Condition JSON missing 'type' field: {}", json);
            return null;
        }

        String type = json.get("type")
            .getAsString();
        Function<JsonObject, ICondition> parser = parsers.get(type);

        if (parser != null) {
            try {
                return parser.apply(json);
            } catch (Exception e) {
                Logger.error("Failed to parse condition of type '{}': {}", type, e.getMessage());
                return null;
            }
        } else {
            Logger.warn("Unknown condition type: {}", type);
            return null;
        }
    }
}
