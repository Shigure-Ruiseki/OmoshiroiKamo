package ruiseki.omoshiroikamo.api.recipe.expression;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;

/**
 * Expression representing a string literal value.
 */
public class StringLiteralExpression implements IExpression {

    private final String value;

    public StringLiteralExpression(String value) {
        this.value = value;
    }

    @Override
    public EvaluationValue evaluate(ConditionContext context) {
        return new EvaluationValue(value);
    }

    public String getStringValue() {
        return value;
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }

    public static IExpression fromJson(JsonObject json) {
        return new StringLiteralExpression(
            json.get("value")
                .getAsString());
    }
}
