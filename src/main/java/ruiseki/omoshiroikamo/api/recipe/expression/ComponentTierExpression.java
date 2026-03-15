package ruiseki.omoshiroikamo.api.recipe.expression;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.recipe.context.IRecipeContext;
import ruiseki.omoshiroikamo.api.recipe.core.ITieredMachine;

/**
 * Expression that retrieves the tier of a specific machine component.
 * <p>
 * Examples:
 * <ul>
 * <li>tier.glass - Returns the tier of the "glass" component</li>
 * <li>tier.casing - Returns the tier of the "casing" component</li>
 * </ul>
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
    public double evaluate(ConditionContext context) {
        // Null safety checks
        if (context == null) {
            return 0.0;
        }

        IRecipeContext recipeContext = context.getRecipeContext();
        if (recipeContext == null) {
            return 0.0;
        }

        // Check if context implements ITieredMachine
        if (!(recipeContext instanceof ITieredMachine)) {
            return 0.0;
        }

        ITieredMachine machine = (ITieredMachine) recipeContext;
        return (double) machine.getComponentTier(componentName);
    }

    @Override
    public String toString() {
        return "tier." + componentName;
    }

    /**
     * Deserialize from JSON format.
     * <p>
     * Expected format: {"type": "component_tier", "component": "glass"}
     */
    public static ComponentTierExpression fromJson(JsonObject json) {
        String component = json.get("component")
            .getAsString();
        return new ComponentTierExpression(component);
    }

    public String getComponentName() {
        return componentName;
    }
}
