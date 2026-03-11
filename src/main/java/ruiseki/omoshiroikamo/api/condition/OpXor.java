package ruiseki.omoshiroikamo.api.condition;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Logical XOR operator for conditions.
 * Returns true if exactly one of the conditions is met.
 * Note: While XOR usually takes 2 inputs, this implementation returns true
 * if the number of met conditions is odd (parity check) or exactly one.
 * In IDS module (Integrated Dynamics), XOR is usually binary.
 * Here we implement it as 'exactly one' for more specific recipe control.
 */
public class OpXor implements ICondition {

    private final List<ICondition> children;

    public OpXor(List<ICondition> children) {
        this.children = children;
    }

    @Override
    public boolean isMet(ConditionContext context) {
        int metCount = 0;
        for (ICondition child : children) {
            if (child.isMet(context)) {
                metCount++;
            }
        }
        return metCount == 1; // Exactly one must be met
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder("XOR (");
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
        json.addProperty("type", "xor");
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
        return new OpXor(children);
    }
}
