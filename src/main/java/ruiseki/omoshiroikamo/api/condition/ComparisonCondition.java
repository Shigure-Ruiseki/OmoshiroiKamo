package ruiseki.omoshiroikamo.api.condition;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.recipe.expression.EvaluationValue;
import ruiseki.omoshiroikamo.api.recipe.expression.ExpressionsParser;
import ruiseki.omoshiroikamo.api.recipe.expression.IExpression;

/**
 * Condition that compares two expressions.
 */
public class ComparisonCondition implements ICondition {

    private final IExpression left;
    private final IExpression right;
    private final String operator;

    public ComparisonCondition(IExpression left, IExpression right, String operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public boolean isMet(ConditionContext context) {
        EvaluationValue v1 = left.evaluate(context);
        EvaluationValue v2 = right.evaluate(context);

        if (operator.equals("==")) {
            return v1.looseEquals(v2);
        }
        if (operator.equals("!=")) {
            return !v1.looseEquals(v2);
        }

        double d1 = v1.asDouble();
        double d2 = v2.asDouble();

        switch (operator) {
            case ">":
                return d1 > d2;
            case ">=":
                return d1 >= d2;
            case "<":
                return d1 < d2;
            case "<=":
                return d1 <= d2;
            default:
                return false;
        }
    }

    @Override
    public String getDescription() {
        return left.toString() + " " + operator + " " + right.toString();
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("type", "comparison");
        // Serialization not implemented yet
    }

    @Override
    public String toString() {
        return getDescription();
    }

    public static ICondition fromJson(JsonObject json) {
        IExpression left = ExpressionsParser.parse(json.get("left"));
        IExpression right = ExpressionsParser.parse(json.get("right"));
        String op = json.get("operator")
            .getAsString();
        return new ComparisonCondition(left, right, op);
    }
}
