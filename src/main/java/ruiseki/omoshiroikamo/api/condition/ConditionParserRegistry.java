package ruiseki.omoshiroikamo.api.condition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.core.common.util.Logger;

/**
 * Registry for parsing ICondition from JSON objects.
 */
public class ConditionParserRegistry {

    private static final Map<String, ConditionEntry> registry = new HashMap<>();
    private static final List<ConditionEntry> entries = new ArrayList<>();

    /**
     * Register a parser for a specific condition type with an inference detector.
     * 
     * @param type     The condition type key (e.g., "dimension", "biome")
     * @param parser   Function that creates ICondition from JsonObject
     * @param detector Predicate to check if a JsonObject matches this condition
     *                 type
     */
    public static void register(String type, Function<JsonObject, ICondition> parser, Predicate<JsonObject> detector) {
        ConditionEntry entry = new ConditionEntry(type, parser, detector);
        registry.put(type, entry);
        entries.add(entry);
    }

    /**
     * Register a parser for a specific condition type (without automatic
     * inference).
     * 
     * @param type   The condition type key
     * @param parser Function that creates ICondition from JsonObject
     */
    public static void register(String type, Function<JsonObject, ICondition> parser) {
        register(type, parser, json -> false);
    }

    /**
     * Parse a JsonObject into an ICondition.
     * 
     * @param json The JsonObject representing the condition.
     * @return The parsed ICondition, or null if parsing failed.
     */
    public static ICondition parse(JsonObject json) {
        ConditionEntry target = null;

        if (json.has("type")) {
            String type = json.get("type")
                .getAsString();
            target = registry.get(type);
        } else {
            // Inference
            for (ConditionEntry entry : entries) {
                if (entry.detector.test(json)) {
                    target = entry;
                    break;
                }
            }
        }

        if (target != null) {
            try {
                return target.parser.apply(json);
            } catch (Exception e) {
                Logger.error("Failed to parse condition of type '{}': {}", target.type, e.getMessage());
                return null;
            }
        } else {
            Logger.warn("Unknown or non-inferable condition type: {}", json);
            return null;
        }
    }

    private static class ConditionEntry {

        final String type;
        final Function<JsonObject, ICondition> parser;
        final Predicate<JsonObject> detector;

        ConditionEntry(String type, Function<JsonObject, ICondition> parser, Predicate<JsonObject> detector) {
            this.type = type;
            this.parser = parser;
            this.detector = detector;
        }
    }
}
