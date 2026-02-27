package ruiseki.omoshiroikamo.api.modular.recipe;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.condition.ICondition;

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
}
