package ruiseki.omoshiroikamo.api.modular.recipe;

import com.google.gson.JsonObject;

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

    public static IExpression fromJson(JsonObject json) {
        return new ConstantExpression(
            json.get("value")
                .getAsDouble());
    }
}
