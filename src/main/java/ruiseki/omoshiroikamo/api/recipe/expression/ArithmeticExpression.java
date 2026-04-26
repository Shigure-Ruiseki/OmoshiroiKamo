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
    public EvaluationValue evaluate(ConditionContext context) {
        EvaluationValue val1 = left.evaluate(context);
        EvaluationValue val2 = right.evaluate(context);

        // String concatenation support
        if (operation.equals("+") && (val1.isString() || val2.isString())) {
            return new EvaluationValue(val1.asString() + val2.asString());
        }

        double d1 = val1.asDouble();
        double d2 = val2.asDouble();

        switch (operation) {
            case "+":
                return new EvaluationValue(d1 + d2);
            case "-":
                return new EvaluationValue(d1 - d2);
            case "*":
                return new EvaluationValue(d1 * d2);
            case "/":
                return d2 != 0 ? new EvaluationValue(d1 / d2) : EvaluationValue.ZERO;
            case "%":
                return d2 != 0 ? new EvaluationValue(d1 % d2) : EvaluationValue.ZERO;
        }
        return EvaluationValue.ZERO;
    }

    public static IExpression fromJson(JsonObject json) {
        IExpression left = ExpressionsParser.parse(json.get("left"));
        IExpression right = ExpressionsParser.parse(json.get("right"));
        String op = json.get("operation")
            .getAsString();
        return new ArithmeticExpression(left, right, op);
    }
}
