package ruiseki.omoshiroikamo.api.recipe.expression;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;

/**
 * Interface for dynamic numeric values that can be evaluated using a context.
 */
public interface IExpression {

    /**
     * Evaluate the expression and return an EvaluationValue.
     *
     * @param context The context (machine station, world, etc.)
     * @return The calculated value wrapper.
     */
    EvaluationValue evaluate(ConditionContext context);

    /**
     * Evaluate without a context. For constant expressions, returns the value.
     * For dynamic expressions that require context, returns VOID.
     */
    default EvaluationValue evaluateSafe() {
        return EvaluationValue.VOID;
    }

    /**
     * Helper to get double value directly.
     */
    default double evaluateDouble(ConditionContext context) {
        return evaluate(context).asDouble();
    }
}
