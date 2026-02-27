package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DecoratorParser {

    private static final Map<String, BiFunction<IModularRecipe, JsonObject, IModularRecipe>> parsers = new HashMap<>();

    static {
        register("chance", (recipe, json) -> ChanceRecipeDecorator.fromJson(recipe, json));
        register("bonus", (recipe, json) -> BonusOutputDecorator.fromJson(recipe, json));
        register("requirement", (recipe, json) -> RequirementDecorator.fromJson(recipe, json));
        register("weighted_random", (recipe, json) -> WeightedRandomDecorator.fromJson(recipe, json));
    }

    public static void register(String type, BiFunction<IModularRecipe, JsonObject, IModularRecipe> parser) {
        parsers.put(type, parser);
    }

    public static IModularRecipe parse(IModularRecipe recipe, JsonElement element) {
        if (element == null || element.isJsonNull()) return recipe;

        if (element.isJsonArray()) {
            JsonArray arr = element.getAsJsonArray();
            IModularRecipe current = recipe;
            for (JsonElement e : arr) {
                current = parseSingle(current, e.getAsJsonObject());
            }
            return current;
        }

        return parseSingle(recipe, element.getAsJsonObject());
    }

    private static IModularRecipe parseSingle(IModularRecipe recipe, JsonObject json) {
        String type = json.get("type")
            .getAsString();
        BiFunction<IModularRecipe, JsonObject, IModularRecipe> parser = parsers.get(type);
        if (parser != null) {
            return parser.apply(recipe, json);
        }
        throw new IllegalArgumentException("Unknown decorator type: " + type);
    }
}
