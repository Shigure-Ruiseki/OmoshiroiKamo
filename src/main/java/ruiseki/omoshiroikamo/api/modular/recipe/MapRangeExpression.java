package ruiseki.omoshiroikamo.api.modular.recipe;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;

/**
 * An expression that maps an input value from one range to another.
 */
public class MapRangeExpression implements IExpression {

    private final IExpression input;
    private final double minIn;
    private final double maxIn;
    private final double minOut;
    private final double maxOut;
    private final boolean clamp;

    public MapRangeExpression(IExpression input, double minIn, double maxIn, double minOut, double maxOut,
        boolean clamp) {
        this.input = input;
        this.minIn = minIn;
        this.maxIn = maxIn;
        this.minOut = minOut;
        this.maxOut = maxOut;
        this.clamp = clamp;
    }

    @Override
    public double evaluate(ConditionContext context) {
        double val = input.evaluate(context);

        // Linear interpolation
        double result = minOut + (val - minIn) * (maxOut - minOut) / (maxIn - minIn);

        if (clamp) {
            double lower = Math.min(minOut, maxOut);
            double upper = Math.max(minOut, maxOut);
            result = Math.max(lower, Math.min(upper, result));
        }

        return result;
    }

    public static IExpression fromJson(JsonObject json) {
        IExpression input = ExpressionsParser.parse(json.get("input"));
        double minIn = json.get("minIn")
            .getAsDouble();
        double maxIn = json.get("maxIn")
            .getAsDouble();
        double minOut = json.get("minOut")
            .getAsDouble();
        double maxOut = json.get("maxOut")
            .getAsDouble();
        boolean clamp = json.has("clamp") && json.get("clamp")
            .getAsBoolean();
        return new MapRangeExpression(input, minIn, maxIn, minOut, maxOut, clamp);
    }
}
