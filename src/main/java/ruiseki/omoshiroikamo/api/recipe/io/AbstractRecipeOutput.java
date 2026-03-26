package ruiseki.omoshiroikamo.api.recipe.io;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import ruiseki.omoshiroikamo.core.json.AbstractJsonMaterial;

public abstract class AbstractRecipeOutput extends AbstractJsonMaterial implements IRecipeOutput {

    protected int interval = 0;

    @Override
    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = Math.max(0, interval);
    }

    protected void readPerTick(JsonObject json, int defaultInterval) {
        this.interval = defaultInterval;
        JsonElement element = json.has("pertick") ? json.get("pertick")
            : (json.has("perTick") ? json.get("perTick") : null);
        if (element != null && element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                this.interval = primitive.getAsBoolean() ? 1 : 0;
            } else if (primitive.isNumber()) {
                this.interval = primitive.getAsInt();
            }
        }
    }
}
