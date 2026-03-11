package ruiseki.omoshiroikamo.api.recipe.expression;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;

/**
 * Expression that retrieves properties from the world (time, days, moon phase).
 */
public class WorldPropertyExpression implements IExpression {

    private final String property;

    public WorldPropertyExpression(String property) {
        this.property = property;
    }

    @Override
    public double evaluate(ConditionContext context) {
        if (context == null || context.getWorld() == null) return 0;

        switch (property.toLowerCase()) {
            case "time":
                // 0 to 23999
                return context.getWorld()
                    .getWorldTime() % 24000;
            case "total_days":
                // Days since world creation
                return context.getWorld()
                    .getTotalWorldTime() / 24000;
            case "day": // Alias for total_days
                return context.getWorld()
                    .getTotalWorldTime() / 24000;
            case "moon_phase":
                // 0 to 7 (0 is full moon)
                return context.getWorld().provider.getMoonPhase(
                    context.getWorld()
                        .getWorldTime());
            default:
                return 0;
        }
    }

    public static IExpression fromJson(JsonObject json) {
        String prop = json.get("property")
            .getAsString();
        return new WorldPropertyExpression(prop);
    }
}
