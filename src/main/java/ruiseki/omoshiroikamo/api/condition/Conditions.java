package ruiseki.omoshiroikamo.api.condition;

/**
 * Utility class to register default condition parsers.
 */
public class Conditions {

    public static void registerDefaults() {
        ConditionParserRegistry.register("dimension", DimensionCondition::fromJson);
        ConditionParserRegistry.register("biome", BiomeCondition::fromJson);
        ConditionParserRegistry.register("block_below", BlockBelowCondition::fromJson);
    }
}
