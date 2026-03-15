package ruiseki.omoshiroikamo.api.recipe.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.recipe.core.IModularRecipe;
import ruiseki.omoshiroikamo.api.recipe.decorator.BonusBlockOutputDecorator;
import ruiseki.omoshiroikamo.api.recipe.decorator.BonusOutputDecorator;
import ruiseki.omoshiroikamo.api.recipe.decorator.ChanceRecipeDecorator;
import ruiseki.omoshiroikamo.api.recipe.decorator.HarvestBlockDecorator;
import ruiseki.omoshiroikamo.api.recipe.decorator.PerPositionProbabilityDecorator;
import ruiseki.omoshiroikamo.api.recipe.decorator.RandomBlockOutputDecorator;
import ruiseki.omoshiroikamo.api.recipe.decorator.RequirementDecorator;
import ruiseki.omoshiroikamo.api.recipe.decorator.WeightedRandomDecorator;

public class DecoratorParser {

    private static final Map<String, DecoratorEntry> registry = new HashMap<>();
    private static final List<DecoratorEntry> entries = new ArrayList<>();

    static {
        register("requirement", RequirementDecorator::fromJson, json -> json.has("condition"));

        register("weighted_random", WeightedRandomDecorator::fromJson, json -> json.has("rolls") || json.has("pool"));

        register(
            "randomBlockOutput",
            RandomBlockOutputDecorator::fromJson,
            json -> json.has("count") || json.has("selections"));

        register(
            "harvest",
            HarvestBlockDecorator::fromJson,
            json -> json.has("fortune") || json.has("silkTouch") || json.has("shear") || json.has("harvestLevel"));

        register(
            "perPositionProbability",
            PerPositionProbabilityDecorator::fromJson,
            json -> json.has("chance") && json.has("symbol") && json.has("output"));

        register(
            "bonusBlockOutput",
            BonusBlockOutputDecorator::fromJson,
            json -> json.has("chance") && json.has("outputs") && isFirstOutputBlock(json.getAsJsonArray("outputs")));

        register("bonus", BonusOutputDecorator::fromJson, json -> json.has("chance") && json.has("outputs"));

        register("chance", ChanceRecipeDecorator::fromJson, json -> json.has("chance"));
    }

    public static void register(String type, BiFunction<IModularRecipe, JsonObject, IModularRecipe> parser,
        Predicate<JsonObject> detector) {
        DecoratorEntry entry = new DecoratorEntry(type, parser, detector);
        registry.put(type, entry);
        entries.add(entry);
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
        DecoratorEntry target = null;

        if (json.has("type")) {
            String type = json.get("type")
                .getAsString();
            target = registry.get(type);
        } else {
            for (DecoratorEntry entry : entries) {
                if (entry.detector.test(json)) {
                    target = entry;
                    break;
                }
            }
        }

        if (target != null) {
            return target.parser.apply(recipe, json);
        }

        String msg = json.has("type") ? "Unknown decorator type: " + json.get("type")
            .getAsString() : "Could not infer decorator type from properties: " + json.entrySet();
        throw new IllegalArgumentException(msg);
    }

    private static boolean isFirstOutputBlock(JsonArray outputs) {
        if (outputs.size() == 0) return false;
        JsonElement first = outputs.get(0);
        if (!first.isJsonObject()) return false;
        JsonObject obj = first.getAsJsonObject();
        return obj.has("type") && "block".equals(
            obj.get("type")
                .getAsString());
    }

    private static class DecoratorEntry {

        final String type;
        final BiFunction<IModularRecipe, JsonObject, IModularRecipe> parser;
        final Predicate<JsonObject> detector;

        DecoratorEntry(String type, BiFunction<IModularRecipe, JsonObject, IModularRecipe> parser,
            Predicate<JsonObject> detector) {
            this.type = type;
            this.parser = parser;
            this.detector = detector;
        }
    }
}
