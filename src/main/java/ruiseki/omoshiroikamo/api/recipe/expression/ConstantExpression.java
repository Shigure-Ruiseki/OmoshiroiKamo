package ruiseki.omoshiroikamo.api.recipe.expression;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;

/**
 * A constant numeric value.
 */
public class ConstantExpression implements IExpression {

    public static final ConstantExpression PI = new ConstantExpression(Math.PI);
    public static final ConstantExpression E = new ConstantExpression(Math.E);
    public static final ConstantExpression ZERO = new ConstantExpression(0);
    public static final ConstantExpression ONE = new ConstantExpression(1);

    private final double value;

    public ConstantExpression(double value) {
        this.value = value;
    }

    @Override
    public EvaluationValue evaluate(ConditionContext context) {
        return new EvaluationValue(value);
    }

    @Override
    public EvaluationValue evaluateSafe() {
        return new EvaluationValue(value);
    }

    @Override
    public String toString() {
        if (value == (long) value) return String.valueOf((long) value);
        return String.valueOf(value);
    }

    public static IExpression fromJson(JsonObject json) {
        return new ConstantExpression(
            json.get("value")
                .getAsDouble());
    }
}
