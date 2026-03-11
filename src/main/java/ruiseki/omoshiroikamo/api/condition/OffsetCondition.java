package ruiseki.omoshiroikamo.api.condition;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.recipe.expression.ExpressionParser;

/**
 * A condition that evaluates another condition at a relative offset.
 */
public class OffsetCondition implements ICondition {

    private final ICondition condition;
    private final int dx, dy, dz;

    public OffsetCondition(ICondition condition, int dx, int dy, int dz) {
        this.condition = condition;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }

    @Override
    public boolean isMet(ConditionContext context) {
        ConditionContext offsetContext = new ConditionContext(
            context.getWorld(),
            context.getX() + dx,
            context.getY() + dy,
            context.getZ() + dz,
            context.getRecipeContext());
        return condition.isMet(offsetContext);
    }

    @Override
    public String getDescription() {
        return String.format("offset(%d, %d, %d): %s", dx, dy, dz, condition.getDescription());
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("type", "offset");
        json.addProperty("dx", dx);
        json.addProperty("dy", dy);
        json.addProperty("dz", dz);
        JsonObject condJson = new JsonObject();
        condition.write(condJson);
        json.add("condition", condJson);
    }

    public static ICondition fromJson(JsonObject json) {
        int dx = json.has("dx") ? json.get("dx")
            .getAsInt() : 0;
        int dy = json.has("dy") ? json.get("dy")
            .getAsInt() : 0;
        int dz = json.has("dz") ? json.get("dz")
            .getAsInt() : 0;

        ICondition condition;
        if (json.has("condition")) {
            condition = ConditionParserRegistry.parse(json.getAsJsonObject("condition"));
        } else if (json.has("expression")) {
            condition = ExpressionParser.parseCondition(
                json.get("expression")
                    .getAsString());
        } else {
            throw new RuntimeException("OffsetCondition requires a 'condition' or 'expression'");
        }

        return new OffsetCondition(condition, dx, dy, dz);
    }
}
