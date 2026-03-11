package ruiseki.omoshiroikamo.api.condition;

import com.google.gson.JsonObject;

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
        double lVal = left.evaluate(context);
        double rVal = right.evaluate(context);

        switch (operator) {
            case ">":
                return lVal > rVal;
            case ">=":
                return lVal >= rVal;
            case "<":
                return lVal < rVal;
            case "<=":
                return lVal <= rVal;
            case "==":
                return Math.abs(lVal - rVal) < 0.0001;
            case "!=":
                return Math.abs(lVal - rVal) >= 0.0001;
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

    public static ICondition fromJson(JsonObject json) {
        IExpression left = ExpressionsParser.parse(json.get("left"));
        IExpression right = ExpressionsParser.parse(json.get("right"));
        String op = json.get("operator")
            .getAsString();
        return new ComparisonCondition(left, right, op);
    }
}
