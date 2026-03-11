package ruiseki.omoshiroikamo.module.cows.common.registries;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.core.json.AbstractJsonMaterial;
import ruiseki.omoshiroikamo.core.json.FluidJson;

/**
 * Material representation of a Cow in JSON.
 */
public class CowMaterial extends AbstractJsonMaterial {

    public Integer id;
    public String name;
    public boolean enabled = true;
    public FluidJson fluid;
    public String bgColor;
    public String fgColor;
    public String tintColor;
    public String textureOverlay;
    public String spawnType = SpawnType.NORMAL.name();
    public Map<String, String> lang = new HashMap<>();

    @Override
    public void read(JsonObject json) {
        this.id = json.has("id") && !json.get("id")
            .isJsonNull() ? json.get("id")
                .getAsInt() : null;
        this.name = getString(json, "name", null);
        this.enabled = getBoolean(json, "enabled", true);

        if (json.has("fluid")) {
            // Use Gson to parse FluidJson
            Gson gson = new Gson();
            this.fluid = gson.fromJson(json.get("fluid"), FluidJson.class);
        }

        this.bgColor = getString(json, "bgColor", null);
        this.fgColor = getString(json, "fgColor", null);
        this.tintColor = getString(json, "tintColor", null);
        this.textureOverlay = getString(json, "textureOverlay", null);
        this.spawnType = getString(json, "spawnType", SpawnType.NORMAL.name());

        if (json.has("lang")) {
            JsonObject langObj = json.getAsJsonObject("lang");
            for (Map.Entry<String, JsonElement> entry : langObj.entrySet()) {
                this.lang.put(
                    entry.getKey(),
                    entry.getValue()
                        .getAsString());
            }
        }

        captureUnknownProperties(json, "id", "name", "enabled", "fluid", "bgColor", "fgColor", "spawnType", "lang");
    }

    @Override
    public void write(JsonObject json) {
        if (id != null) json.addProperty("id", id);
        if (name != null) json.addProperty("name", name);
        json.addProperty("enabled", enabled);

        if (fluid != null) {
            JsonObject fluidObj = new JsonObject();
            fluidObj.addProperty("name", fluid.name);
            fluidObj.addProperty("amount", fluid.amount);
            json.add("fluid", fluidObj);
        }

        if (bgColor != null) json.addProperty("bgColor", bgColor);
        if (fgColor != null) json.addProperty("fgColor", fgColor);
        if (tintColor != null) json.addProperty("tintColor", tintColor);
        if (textureOverlay != null) json.addProperty("textureOverlay", textureOverlay);
        json.addProperty("spawnType", spawnType);

        if (!lang.isEmpty()) {
            JsonObject langObj = new JsonObject();
            for (Map.Entry<String, String> entry : lang.entrySet()) {
                langObj.addProperty(entry.getKey(), entry.getValue());
            }
            json.add("lang", langObj);
        }

        writeUnknownProperties(json);
    }

    @Override
    public Object get(String key) {
        switch (key) {
            case "id":
                return id;
            case "name":
                return name;
            case "enabled":
                return enabled;
            case "fluid":
                return fluid;
            case "spawnType":
                return spawnType;
            case "tintColor":
                return tintColor;
            case "textureOverlay":
                return textureOverlay;
            default:
                return null;
        }
    }

    @Override
    public void set(String key, Object value) {
        switch (key) {
            case "id":
                id = (Integer) value;
                break;
            case "name":
                name = (String) value;
                break;
            case "enabled":
                enabled = (Boolean) value;
                break;
            case "fluid":
                fluid = (FluidJson) value;
                break;
            case "spawnType":
                spawnType = (String) value;
                break;
            case "tintColor":
                tintColor = (String) value;
                break;
            case "textureOverlay":
                textureOverlay = (String) value;
                break;
        }
    }

    @Override
    public boolean validate() {
        if (name == null || name.isEmpty()) {
            logValidationError("Cow name is missing");
            return false;
        }
        if (fluid == null) {
            logValidationError("Fluid is missing for " + name);
            return false;
        }
        return true;
    }
}
