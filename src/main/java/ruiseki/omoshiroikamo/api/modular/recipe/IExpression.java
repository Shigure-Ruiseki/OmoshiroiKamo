package ruiseki.omoshiroikamo.api.modular.recipe;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;

/**
 * Interface for dynamic numeric values that can be evaluated using a context.
 */
public interface IExpression {

    /**
     * Evaluate the expression and return a double value.
     * 
     * @param context The context (machine station, world, etc.)
     * @return The calculated value.
     */
    double evaluate(ConditionContext context);
}
