package ruiseki.omoshiroikamo.api.recipe.parser.impl;

import com.google.gson.JsonElement;

import ruiseki.omoshiroikamo.api.condition.ConditionParserRegistry;
import ruiseki.omoshiroikamo.api.condition.ICondition;
import ruiseki.omoshiroikamo.api.recipe.core.ModularRecipe;
import ruiseki.omoshiroikamo.api.recipe.parser.IRecipePropertyParser;

public class ConditionPropertyParser implements IRecipePropertyParser {

    @Override
    public void parse(ModularRecipe.Builder builder, JsonElement element) {
        if (element.isJsonArray()) {
            for (JsonElement e : element.getAsJsonArray()) {
                if (e.isJsonObject()) {
                    ICondition condition = ConditionParserRegistry.parse(e.getAsJsonObject());
                    if (condition != null) builder.addCondition(condition);
                }
            }
        } else if (element.isJsonObject()) {
            ICondition condition = ConditionParserRegistry.parse(element.getAsJsonObject());
            if (condition != null) builder.addCondition(condition);
        }
    }
}
