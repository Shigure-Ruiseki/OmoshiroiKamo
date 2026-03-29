package ruiseki.omoshiroikamo.api.recipe.expression;

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

    /**
     * Evaluate without a context. For constant expressions, returns the value.
     * For dynamic expressions that require context, returns 1.0 as a neutral default.
     */
    default double evaluateSafe() {
        return 1.0;
    }

    /**
     * Evaluate the expression as a string.
     * Useful for literal strings in function arguments.
     */
    default String evaluateString(ConditionContext context) {
        return null;
    }
}
