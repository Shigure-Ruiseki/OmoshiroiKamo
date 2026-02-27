package ruiseki.omoshiroikamo.api.condition;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Logical NAND operator for conditions.
 * Returns false if all conditions are met, true otherwise.
 */
public class OpNand implements ICondition {

    private final List<ICondition> children;

    public OpNand(List<ICondition> children) {
        this.children = children;
    }

    @Override
    public boolean isMet(ConditionContext context) {
        for (ICondition child : children) {
            if (!child.isMet(context)) {
                return true; // If any is false, NAND is true
            }
        }
        return false; // All are true, NAND is false
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder("NAND (");
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
        json.addProperty("type", "nand");
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
        return new OpNand(children);
    }
}
