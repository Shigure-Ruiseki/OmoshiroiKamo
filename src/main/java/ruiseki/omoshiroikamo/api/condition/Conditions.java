package ruiseki.omoshiroikamo.api.condition;

/**
 * Utility class to register default condition parsers.
 */
public class Conditions {

    public static void registerDefaults() {
        ConditionParserRegistry.register("dimension", DimensionCondition::fromJson);
        ConditionParserRegistry.register("biome", BiomeCondition::fromJson);
        ConditionParserRegistry.register("block_below", BlockBelowCondition::fromJson);

        // Logical Operators
        ConditionParserRegistry.register("and", OpAnd::fromJson);
        ConditionParserRegistry.register("or", OpOr::fromJson);
        ConditionParserRegistry.register("not", OpNot::fromJson);
        ConditionParserRegistry.register("nand", OpNand::fromJson);
        ConditionParserRegistry.register("nor", OpNor::fromJson);
        ConditionParserRegistry.register("xor", OpXor::fromJson);
        ConditionParserRegistry.register("tile_nbt", TileNbtCondition::fromJson);
    }
}
