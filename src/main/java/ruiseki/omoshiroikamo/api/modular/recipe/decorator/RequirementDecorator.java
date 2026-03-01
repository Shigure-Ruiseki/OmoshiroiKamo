package ruiseki.omoshiroikamo.api.modular.recipe.decorator;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.condition.ConditionParserRegistry;
import ruiseki.omoshiroikamo.api.condition.ICondition;
import ruiseki.omoshiroikamo.api.modular.recipe.core.IModularRecipe;

/**
 * Decorator that adds an additional condition to a recipe.
 */
public class RequirementDecorator extends RecipeDecorator {

    private final ICondition extraCondition;

    public RequirementDecorator(IModularRecipe internal, ICondition extraCondition) {
        super(internal);
        this.extraCondition = extraCondition;
    }

    @Override
    public boolean isConditionMet(ConditionContext context) {
        // Both the original conditions and this extra condition must be met
        return internal.isConditionMet(context) && extraCondition.isMet(context);
    }

    public ICondition getExtraCondition() {
        return extraCondition;
    }

    public static IModularRecipe fromJson(IModularRecipe recipe, JsonObject json) {
        ICondition condition = ConditionParserRegistry.parse(json.getAsJsonObject("condition"));
        return new RequirementDecorator(recipe, condition);
    }
}
