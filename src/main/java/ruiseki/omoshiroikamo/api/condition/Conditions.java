package ruiseki.omoshiroikamo.api.condition;

import ruiseki.omoshiroikamo.api.recipe.expression.ExpressionParser;

/**
 * Utility class to register default condition parsers.
 */
public class Conditions {

    public static void registerDefaults() {
        ConditionParserRegistry
            .register("dimension", DimensionCondition::fromJson, json -> json.has("ids") || json.has("dimension"));

        ConditionParserRegistry.register(
            "biome",
            BiomeCondition::fromJson,
            json -> json.has("biomes") || json.has("biome")
                || json.has("tags")
                || json.has("tag")
                || json.has("minTemp")
                || json.has("maxTemp")
                || json.has("minHumid")
                || json.has("maxHumid"));

        ConditionParserRegistry.register(
            "offset",
            OffsetCondition::fromJson,
            json -> (json.has("dx") || json.has("dy") || json.has("dz"))
                && (json.has("condition") || json.has("expression")));

        ConditionParserRegistry.register("pattern", BiomePatternCondition::fromJson, json -> json.has("pattern"));

        ConditionParserRegistry.register("block", BlockCondition::fromJson, json -> json.has("block"));

        ConditionParserRegistry.register("block_below", BlockBelowCondition::fromJson); // Left for backward
                                                                                        // compatibility

        ConditionParserRegistry.register("weather", WeatherCondition::fromJson, json -> json.has("weather"));

        ConditionParserRegistry.register(
            "comparison",
            ComparisonCondition::fromJson,
            json -> json.has("left") && json.has("right") && json.has("operator"));

        ConditionParserRegistry.register(
            "expression",
            json -> ExpressionParser.parseCondition(
                json.get("expression")
                    .getAsString()),
            json -> json.has("expression"));

        ConditionParserRegistry.register(
            "tile_nbt",
            TileNbtCondition::fromJson,
            json -> json.has("key") && json.has("op") && json.has("value"));

        // Logical Operators
        ConditionParserRegistry.register("and", OpAnd::fromJson, json -> json.has("conditions") || json.has("and"));
        ConditionParserRegistry.register("or", OpOr::fromJson, json -> json.has("conditions") || json.has("or"));
        ConditionParserRegistry.register("not", OpNot::fromJson, json -> json.has("condition") || json.has("not"));

        ConditionParserRegistry.register("nand", OpNand::fromJson);
        ConditionParserRegistry.register("nor", OpNor::fromJson);
        ConditionParserRegistry.register("xor", OpXor::fromJson, json -> json.has("xor"));
    }
}
