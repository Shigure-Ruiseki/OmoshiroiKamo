package ruiseki.omoshiroikamo.api.condition;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Logical AND operator for conditions.
 * Mimics IDs module naming style.
 */
public class OpAnd implements ICondition {

    private final List<ICondition> children;

    public OpAnd(List<ICondition> children) {
        this.children = children;
    }

    @Override
    public boolean isMet(ConditionContext context) {
        for (ICondition child : children) {
            if (!child.isMet(context)) {
                return false; // Short-circuit
            }
        }
        return true;
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < children.size(); i++) {
            sb.append(
                children.get(i)
                    .getDescription());
            if (i < children.size() - 1) {
                sb.append(" AND ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("type", "and");
        JsonArray array = new JsonArray();
        for (ICondition child : children) {
            JsonObject childJson = new JsonObject();
            child.write(childJson);
            array.add(childJson);
        }
        json.add("conditions", array);
    }

    public static ICondition fromJson(JsonObject json) {
        List<ICondition> children = new ArrayList<>();
        if (json.has("conditions")) {
            JsonArray array = json.getAsJsonArray("conditions");
            for (JsonElement e : array) {
                ICondition parsed = ConditionParserRegistry.parse(e.getAsJsonObject());
                if (parsed != null) {
                    children.add(parsed);
                }
            }
        }
        return new OpAnd(children);
    }
}
