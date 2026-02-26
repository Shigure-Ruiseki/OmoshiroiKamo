package ruiseki.omoshiroikamo.module.chickens.registries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ICondition;
import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.core.json.AbstractJsonMaterial;
import ruiseki.omoshiroikamo.core.json.ItemMaterial;

/**
 * Material representation of a Chicken in JSON.
 */
public class ChickenMaterial extends AbstractJsonMaterial {

    public Integer id;
    public String name;
    public boolean enabled = true;
    public String texture;
    public String textureOverlay;
    public String tintColor;
    public ItemMaterial layItem;
    public ItemMaterial dropItem;
    public String bgColor;
    public String fgColor;
    public String spawnType = SpawnType.NORMAL.name();
    public String parent1;
    public String parent2;
    public float coefficient = 1.0f;
    public Float mutationChance;
    public List<ItemMaterial> foods = new ArrayList<>();
    public Map<String, String> lang = new HashMap<>();
    public List<JsonElement> conditions = new ArrayList<>();
    public List<ICondition> realConditions = new ArrayList<>();

    @Override
    public void read(JsonObject json) {
        this.id = json.has("id") && !json.get("id")
            .isJsonNull() ? json.get("id")
                .getAsInt() : null;
        this.name = getString(json, "name", null);
        this.enabled = getBoolean(json, "enabled", true);
        this.texture = getString(json, "texture", null);
        this.textureOverlay = getString(json, "textureOverlay", null);
        this.tintColor = getString(json, "tintColor", null);
        if (json.has("layItem")) {
            this.layItem = new ItemMaterial();
            this.layItem.read(json.get("layItem"));
        }
        if (json.has("dropItem")) {
            this.dropItem = new ItemMaterial();
            this.dropItem.read(json.get("dropItem"));
        }
        this.bgColor = getString(json, "bgColor", null);
        this.fgColor = getString(json, "fgColor", null);
        this.spawnType = getString(json, "spawnType", SpawnType.NORMAL.name());
        this.parent1 = getString(json, "parent1", null);
        this.parent2 = getString(json, "parent2", null);
        this.coefficient = getFloat(json, "coefficient", 1.0f);
        this.mutationChance = json.has("mutationChance") && !json.get("mutationChance")
            .isJsonNull() ? json.get("mutationChance")
                .getAsFloat() : null;
        if (json.has("foods")) {
            JsonArray foodArray = json.getAsJsonArray("foods");
            for (JsonElement e : foodArray) {
                ItemMaterial item = new ItemMaterial();
                item.read(e);
                this.foods.add(item);
            }
        }
        if (json.has("lang")) {
            JsonObject langObj = json.getAsJsonObject("lang");
            for (Map.Entry<String, JsonElement> entry : langObj.entrySet()) {
                this.lang.put(
                    entry.getKey(),
                    entry.getValue()
                        .getAsString());
            }
        }
        if (json.has("conditions")) {
            JsonArray condArray = json.getAsJsonArray("conditions");
            for (JsonElement e : condArray) this.conditions.add(e);
        }
        captureUnknownProperties(
            json,
            "id",
            "name",
            "enabled",
            "texture",
            "textureOverlay",
            "tintColor",
            "layItem",
            "dropItem",
            "bgColor",
            "fgColor",
            "spawnType",
            "parent1",
            "parent2",
            "coefficient",
            "mutationChance",
            "foods",
            "lang",
            "conditions");
    }

    @Override
    public void write(JsonObject json) {
        if (id != null) json.addProperty("id", id);
        if (name != null) json.addProperty("name", name);
        json.addProperty("enabled", enabled);
        if (texture != null) json.addProperty("texture", texture);
        if (textureOverlay != null) json.addProperty("textureOverlay", textureOverlay);
        if (tintColor != null) json.addProperty("tintColor", tintColor);
        if (layItem != null) {
            JsonObject layObj = new JsonObject();
            layItem.write(layObj);
            json.add("layItem", layObj);
        }
        if (dropItem != null) {
            JsonObject dropObj = new JsonObject();
            dropItem.write(dropObj);
            json.add("dropItem", dropObj);
        }
        if (bgColor != null) json.addProperty("bgColor", bgColor);
        if (fgColor != null) json.addProperty("fgColor", fgColor);
        json.addProperty("spawnType", spawnType);
        if (parent1 != null) json.addProperty("parent1", parent1);
        if (parent2 != null) json.addProperty("parent2", parent2);
        json.addProperty("coefficient", coefficient);
        if (mutationChance != null) json.addProperty("mutationChance", mutationChance);
        if (!foods.isEmpty()) {
            JsonArray foodArray = new JsonArray();
            for (ItemMaterial m : foods) {
                JsonObject o = new JsonObject();
                m.write(o);
                foodArray.add(o);
            }
            json.add("foods", foodArray);
        }
        if (!lang.isEmpty()) {
            JsonObject langObj = new JsonObject();
            for (Map.Entry<String, String> entry : lang.entrySet())
                langObj.addProperty(entry.getKey(), entry.getValue());
            json.add("lang", langObj);
        }
        if (!conditions.isEmpty() || !realConditions.isEmpty()) {
            JsonArray condArray = new JsonArray();
            for (JsonElement e : conditions) condArray.add(e);
            for (ICondition cond : realConditions) {
                JsonObject o = new JsonObject();
                cond.write(o);
                condArray.add(o);
            }
            json.add("conditions", condArray);
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
            case "texture":
                return texture;
            case "layItem":
                return layItem;
            case "spawnType":
                return spawnType;
            case "parent1":
                return parent1;
            case "parent2":
                return parent2;
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
            case "texture":
                texture = (String) value;
                break;
            case "layItem":
                layItem = (ItemMaterial) value;
                break;
        }
    }

    @Override
    public boolean validate() {
        if (name == null || name.isEmpty()) {
            logValidationError("Chicken name is missing");
            return false;
        }
        if (layItem == null) {
            logValidationError("Lay item is missing for " + name);
            return false;
        }
        return true;
    }
}
