package ruiseki.omoshiroikamo.api.recipe.expression;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.core.common.util.Logger;

/**
 * Expression that performs mathematical functions like sin, cos, sqrt, etc.
 */
public class MathFunctionExpression implements IExpression {

    private final String functionName;
    private final List<IExpression> arguments;

    public static final Set<String> SUPPORTED_FUNCTIONS = Collections.unmodifiableSet(
        new HashSet<>(
            Arrays.asList(
                "abs",
                "sqrt",
                "sin",
                "cos",
                "tan",
                "rad",
                "deg",
                "exp",
                "log",
                "log10",
                "asin",
                "acos",
                "atan",
                "atan2",
                "ncr",
                "npr",
                "fact",
                "combi",
                "combination",
                "perm",
                "permu",
                "permutation",
                "sign",
                "clamp",
                "random",
                "floor",
                "ceil",
                "round",
                "chance",
                "pow",
                "min",
                "max")));

    public MathFunctionExpression(String functionName, List<IExpression> arguments) {
        this.functionName = functionName.toLowerCase();
        this.arguments = arguments;
    }

    @Override
    public EvaluationValue evaluate(ConditionContext context) {
        if (functionName.equals("random")) {
            return new EvaluationValue(new Random(context.getEvaluationSeed()).nextDouble());
        }

        if (arguments == null || arguments.isEmpty()) return EvaluationValue.ZERO;

        // Special case: log with 2 arguments
        if (functionName.equals("log") && arguments.size() == 2) {
            double x = arguments.get(0)
                .evaluate(context)
                .asDouble();
            double base = arguments.get(1)
                .evaluate(context)
                .asDouble();
            if (base <= 0 || base == 1 || x <= 0) return EvaluationValue.ZERO;
            return new EvaluationValue(Math.log(x) / Math.log(base));
        }

        double val = arguments.get(0)
            .evaluate(context)
            .asDouble();

        switch (functionName) {
            case "abs":
                return new EvaluationValue(Math.abs(val));
            case "ceil":
            case "ceiling":
                return new EvaluationValue(Math.ceil(val));
            case "floor":
                return new EvaluationValue(Math.floor(val));
            case "sqrt":
                return new EvaluationValue(val < 0 ? 0 : Math.sqrt(val));
            case "cbrt":
                return new EvaluationValue(Math.cbrt(val));
            case "sin":
                return new EvaluationValue(Math.sin(val));
            case "cos":
                return new EvaluationValue(Math.cos(val));
            case "tan":
                return new EvaluationValue(Math.tan(val));
            case "asin":
                return new EvaluationValue((val < -1 || val > 1) ? 0 : Math.asin(val));
            case "acos":
                return new EvaluationValue((val < -1 || val > 1) ? 0 : Math.acos(val));
            case "atan":
                return new EvaluationValue(Math.atan(val));
            case "rad":
                return new EvaluationValue(Math.toRadians(val));
            case "deg":
                return new EvaluationValue(Math.toDegrees(val));
            case "log":
                return new EvaluationValue(val <= 0 ? 0 : Math.log(val));
            case "log10":
                return new EvaluationValue(val <= 0 ? 0 : Math.log10(val));
            case "ln":
                return new EvaluationValue(val <= 0 ? 0 : Math.log(val));
            case "exp":
                return new EvaluationValue(Math.exp(val));
            case "round":
                return new EvaluationValue((double) Math.round(val));
            case "fact":
                return new EvaluationValue(factorial(val));
            case "sign":
                return new EvaluationValue(Math.signum(val));
            case "chance":
                return new EvaluationValue(new Random(context.getEvaluationSeed() + 1).nextDouble() < val);
            case "npr":
            case "perm":
            case "permu":
            case "permutation": {
                if (arguments.size() < 2) return EvaluationValue.ZERO;
                double r = arguments.get(1)
                    .evaluate(context)
                    .asDouble();
                return new EvaluationValue(permutation(val, r));
            }
            case "ncr":
            case "combi":
            case "combination": {
                if (arguments.size() < 2) return EvaluationValue.ZERO;
                double r = arguments.get(1)
                    .evaluate(context)
                    .asDouble();
                return new EvaluationValue(combination(val, r));
            }
            case "atan2": {
                if (arguments.size() < 2) return EvaluationValue.ZERO;
                double x = arguments.get(1)
                    .evaluate(context)
                    .asDouble();
                return new EvaluationValue(Math.atan2(val, x));
            }
            case "pow": {
                if (arguments.size() < 2) return EvaluationValue.ZERO;
                double p = arguments.get(1)
                    .evaluate(context)
                    .asDouble();
                return new EvaluationValue(Math.pow(val, p));
            }
            case "min": {
                if (arguments.size() < 2) return EvaluationValue.ZERO;
                double v2 = arguments.get(1)
                    .evaluate(context)
                    .asDouble();
                return new EvaluationValue(Math.min(val, v2));
            }
            case "max": {
                if (arguments.size() < 2) return EvaluationValue.ZERO;
                double v2 = arguments.get(1)
                    .evaluate(context)
                    .asDouble();
                return new EvaluationValue(Math.max(val, v2));
            }
            case "clamp": {
                if (arguments.size() < 3) return new EvaluationValue(val);
                double min = arguments.get(1)
                    .evaluate(context)
                    .asDouble();
                double max = arguments.get(2)
                    .evaluate(context)
                    .asDouble();
                if (min > max) {
                    Logger.warn(
                        "[Expression] clamp(v, min, max) called with min > max (min=" + min + ", max=" + max + ")");
                    double temp = min;
                    min = max;
                    max = temp;
                }
                return new EvaluationValue(Math.max(min, Math.min(max, val)));
            }
        }
        return EvaluationValue.ZERO;
    }

    private double factorial(double n) {
        if (n <= 0) {
            return 1;
        }
        if (n > 170) {
            return Double.POSITIVE_INFINITY;
        }
        double res = 1;
        for (int i = 1; i <= (int) n; i++) {
            res *= i;
        }
        return res;
    }

    private double permutation(double n, double r) {
        if (n < r || r < 0) {
            return 0;
        }
        return factorial(n) / factorial(n - r);
    }

    private double combination(double n, double r) {
        if (n < r || r < 0) {
            return 0;
        }
        return factorial(n) / (factorial(r) * factorial(n - r));
    }
}
