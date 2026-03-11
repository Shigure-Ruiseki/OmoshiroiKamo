package ruiseki.omoshiroikamo.api.recipe.expression;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ExpressionsParser {

    private static final Map<String, Function<JsonObject, IExpression>> parsers = new HashMap<>();

    static {
        register("constant", json -> ConstantExpression.fromJson(json));
        register("nbt", json -> NbtExpression.fromJson(json));
        register("map_range", json -> MapRangeExpression.fromJson(json));
        register("arithmetic", json -> ArithmeticExpression.fromJson(json));
        register("world_property", json -> WorldPropertyExpression.fromJson(json));
    }

    public static void register(String type, Function<JsonObject, IExpression> parser) {
        parsers.put(type, parser);
    }

    public static IExpression parse(JsonElement element) {
        if (element == null || element.isJsonNull()) return null;

        if (element.isJsonPrimitive()) {
            if (element.getAsJsonPrimitive()
                .isNumber()) {
                return new ConstantExpression(element.getAsDouble());
            } else if (element.getAsJsonPrimitive()
                .isString()) {
                    return ExpressionParser.parseExpression(element.getAsString());
                }
        }

        if (element.isJsonObject()) {
            JsonObject json = element.getAsJsonObject();
            String type = json.has("type") ? json.get("type")
                .getAsString() : "constant";

            Function<JsonObject, IExpression> parser = parsers.get(type);
            if (parser != null) {
                return parser.apply(json);
            }
        }

        throw new IllegalArgumentException("Unknown expression type: " + element);
    }
}
