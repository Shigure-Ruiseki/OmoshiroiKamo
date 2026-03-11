package ruiseki.omoshiroikamo.api.condition;

import ruiseki.omoshiroikamo.api.recipe.expression.ExpressionParser;

/**
 * Utility class to register default condition parsers.
 */
public class Conditions {

    public static void registerDefaults() {
        ConditionParserRegistry.register("dimension", DimensionCondition::fromJson);
        ConditionParserRegistry.register("biome", BiomeCondition::fromJson);
        ConditionParserRegistry.register("offset", OffsetCondition::fromJson);
        ConditionParserRegistry.register("pattern", BiomePatternCondition::fromJson);
        ConditionParserRegistry.register("block_below", BlockBelowCondition::fromJson);
        ConditionParserRegistry.register("weather", WeatherCondition::fromJson);
        ConditionParserRegistry.register("comparison", ComparisonCondition::fromJson);
        ConditionParserRegistry.register(
            "expression",
            json -> ExpressionParser.parseCondition(
                json.get("expression")
                    .getAsString()));

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
