package ruiseki.omoshiroikamo.api.condition;

import com.google.gson.JsonObject;

/**
 * Logical NOT operator for conditions.
 * Mimics IDs module naming style.
 */
public class OpNot implements ICondition {

    private final ICondition child;

    public OpNot(ICondition child) {
        this.child = child;
    }

    @Override
    public boolean isMet(ConditionContext context) {
        return !child.isMet(context);
    }

    @Override
    public String getDescription() {
        return "NOT (" + child.getDescription() + ")";
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("type", "not");
        JsonObject childJson = new JsonObject();
        child.write(childJson);
        json.add("condition", childJson);
    }

    public static ICondition fromJson(JsonObject json) {
        if (json.has("condition")) {
            ICondition parsed = ConditionParserRegistry.parse(json.getAsJsonObject("condition"));
            if (parsed != null) {
                return new OpNot(parsed);
            }
        }
        return null;
    }
}
