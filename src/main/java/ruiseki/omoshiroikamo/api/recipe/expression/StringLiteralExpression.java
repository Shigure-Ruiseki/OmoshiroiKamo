package ruiseki.omoshiroikamo.api.recipe.expression;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;

/**
 * Expression representing a string literal value.
 * Returns 0 when evaluated as a number (for compatibility).
 */
public class StringLiteralExpression implements IExpression {

    private final String value;

    public StringLiteralExpression(String value) {
        this.value = value;
    }

    @Override
    public double evaluate(ConditionContext context) {
        // String literals evaluate to 0 for numeric contexts
        return 0;
    }

    /**
     * Get the string value.
     */
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
