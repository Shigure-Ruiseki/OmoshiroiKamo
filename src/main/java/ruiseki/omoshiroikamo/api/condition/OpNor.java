package ruiseki.omoshiroikamo.api.condition;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Logical NOR operator for conditions.
 * Returns true if none of the conditions are met, false otherwise.
 */
public class OpNor implements ICondition {

    private final List<ICondition> children;

    public OpNor(List<ICondition> children) {
        this.children = children;
    }

    @Override
    public boolean isMet(ConditionContext context) {
        for (ICondition child : children) {
            if (child.isMet(context)) {
                return false; // If any is true, NOR is false
            }
        }
        return true; // None are true, NOR is true
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder("NOR (");
        for (int i = 0; i < children.size(); i++) {
            sb.append(
                children.get(i)
                    .getDescription());
            if (i < children.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("type", "nor");
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
        return new OpNor(children);
    }
}
