package ruiseki.omoshiroikamo.api.modular.recipe;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;

/**
 * A constant numeric value.
 */
public class ConstantExpression implements IExpression {

    private final double value;

    public ConstantExpression(double value) {
        this.value = value;
    }

    @Override
    public double evaluate(ConditionContext context) {
        return value;
    }
}
