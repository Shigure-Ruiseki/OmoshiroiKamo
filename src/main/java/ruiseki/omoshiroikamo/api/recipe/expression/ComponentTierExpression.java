package ruiseki.omoshiroikamo.api.recipe.expression;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.recipe.context.IRecipeContext;
import ruiseki.omoshiroikamo.api.recipe.core.ITieredMachine;

/**
 * Expression that retrieves the tier of a specific machine component.
 */
public class ComponentTierExpression implements IExpression {

    private final String componentName;

    public ComponentTierExpression(String componentName) {
        if (componentName == null || componentName.isEmpty()) {
            throw new IllegalArgumentException("Component name cannot be null or empty");
        }
        this.componentName = componentName;
    }

    @Override
    public EvaluationValue evaluate(ConditionContext context) {
        if (context == null) return EvaluationValue.ZERO;
        IRecipeContext recipeContext = context.getRecipeContext();
        if (recipeContext == null || !(recipeContext instanceof ITieredMachine)) return EvaluationValue.ZERO;

        ITieredMachine machine = (ITieredMachine) recipeContext;
        return new EvaluationValue((double) machine.getComponentTier(componentName));
    }

    @Override
    public String toString() {
        return "tier." + componentName;
    }

    public static ComponentTierExpression fromJson(JsonObject json) {
        return new ComponentTierExpression(
            json.get("component")
                .getAsString());
    }

    public String getComponentName() {
        return componentName;
    }
}
