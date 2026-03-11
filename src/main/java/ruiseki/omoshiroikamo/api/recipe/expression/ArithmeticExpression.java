package ruiseki.omoshiroikamo.api.recipe.expression;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;

/**
 * Expression that performs arithmetic operations between two expressions.
 */
public class ArithmeticExpression implements IExpression {

    private final IExpression left;
    private final IExpression right;
    private final String operation;

    public ArithmeticExpression(IExpression left, IExpression right, String operation) {
        this.left = left;
        this.right = right;
        this.operation = operation;
    }

    @Override
    public double evaluate(ConditionContext context) {
        double lVal = left.evaluate(context);
        double rVal = right.evaluate(context);

        switch (operation) {
            case "+":
                return lVal + rVal;
            case "-":
                return lVal - rVal;
            case "*":
                return lVal * rVal;
            case "/":
                return rVal != 0 ? lVal / rVal : 0;
            case "%":
                return rVal != 0 ? lVal % rVal : 0;
            default:
                return 0;
        }
    }

    public static IExpression fromJson(JsonObject json) {
        IExpression left = ExpressionsParser.parse(json.get("left"));
        IExpression right = ExpressionsParser.parse(json.get("right"));
        String op = json.get("operation")
            .getAsString();
        return new ArithmeticExpression(left, right, op);
    }
}
