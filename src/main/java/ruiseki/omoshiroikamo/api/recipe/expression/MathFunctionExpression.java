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
    public double evaluate(ConditionContext context) {
        if (functionName.equals("random")) {
            return new Random(context.getEvaluationSeed()).nextDouble();
        }

        if (arguments.isEmpty()) {
            return 0;
        }

        double v1 = arguments.get(0)
            .evaluate(context);

        switch (functionName) {
            case "abs":
                return Math.abs(v1);
            case "sqrt":
                return v1 < 0 ? 0 : Math.sqrt(v1);
            case "sin":
                return Math.sin(v1);
            case "cos":
                return Math.cos(v1);
            case "tan":
                return Math.tan(v1);
            case "rad":
                return Math.toRadians(v1);
            case "deg":
                return Math.toDegrees(v1);
            case "exp":
                return Math.exp(v1);
            case "log":
                if (arguments.size() >= 2) {
                    double base = arguments.get(1)
                        .evaluate(context);
                    if (base <= 0 || base == 1 || v1 <= 0) return 0;
                    return Math.log(v1) / Math.log(base);
                }
                return v1 <= 0 ? 0 : Math.log(v1);
            case "log10":
                return v1 <= 0 ? 0 : Math.log10(v1);
            case "asin":
                return (v1 < -1 || v1 > 1) ? 0 : Math.asin(v1);
            case "acos":
                return (v1 < -1 || v1 > 1) ? 0 : Math.acos(v1);
            case "atan":
                return Math.atan(v1);
            case "fact":
                return factorial(v1);
            case "npr":
            case "perm":
            case "permu":
            case "permutation":
                if (arguments.size() < 2) {
                    return 0;
                }
                return permutation(
                    v1,
                    arguments.get(1)
                        .evaluate(context));
            case "ncr":
            case "combi":
            case "combination":
                if (arguments.size() < 2) {
                    return 0;
                }
                return combination(
                    v1,
                    arguments.get(1)
                        .evaluate(context));
            case "sign":
                return Math.signum(v1);
            case "atan2":
                if (arguments.size() < 2) return 0;
                return Math.atan2(
                    v1,
                    arguments.get(1)
                        .evaluate(context));
            case "clamp":
                if (arguments.size() < 3) return v1;
                double min = arguments.get(1)
                    .evaluate(context);
                double max = arguments.get(2)
                    .evaluate(context);
                if (min > max) {
                    Logger.warn(
                        "[Expression] clamp(v, min, max) called with min > max (min=" + min + ", max=" + max + ")");
                    double temp = min;
                    min = max;
                    max = temp;
                }
                return Math.max(min, Math.min(max, v1));
            case "floor":
                return Math.floor(v1);
            case "ceil":
                return Math.ceil(v1);
            case "round":
                return Math.round(v1);
            case "chance":
                // 1.0 if chance succeeds, 0.0 otherwise
                return new Random(context.getEvaluationSeed() + 1).nextDouble() < v1 ? 1.0 : 0.0;
            case "pow":
                if (arguments.size() < 2) return 0;
                return Math.pow(
                    v1,
                    arguments.get(1)
                        .evaluate(context));
            case "min":
                if (arguments.size() < 2) return v1;
                return Math.min(
                    v1,
                    arguments.get(1)
                        .evaluate(context));
            case "max":
                if (arguments.size() < 2) return v1;
                return Math.max(
                    v1,
                    arguments.get(1)
                        .evaluate(context));
            default:
                return 0;
        }
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
