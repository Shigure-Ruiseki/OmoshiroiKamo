package ruiseki.omoshiroikamo.api.condition;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.StatCollector;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Condition that checks the current dimension ID.
 */
public class DimensionCondition implements ICondition {

    private final List<Integer> allowedDimensions;

    public DimensionCondition(List<Integer> allowedDimensions) {
        this.allowedDimensions = allowedDimensions;
    }

    @Override
    public boolean isMet(ConditionContext context) {
        return allowedDimensions.contains(context.getWorld().provider.dimensionId);
    }

    @Override
    public String getDescription() {
        return StatCollector
            .translateToLocalFormatted("omoshiroikamo.condition.dimension", allowedDimensions.toString());
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("type", "dimension");
        JsonArray array = new JsonArray();
        for (Integer id : allowedDimensions) {
            array.add(new JsonPrimitive(id));
        }
        json.add("ids", array);
    }

    public static ICondition fromJson(JsonObject json) {
        List<Integer> ids = new ArrayList<>();
        if (json.has("ids")) {
            JsonArray array = json.getAsJsonArray("ids");
            for (JsonElement e : array) {
                ids.add(e.getAsInt());
            }
        }
        return new DimensionCondition(ids);
    }
}
