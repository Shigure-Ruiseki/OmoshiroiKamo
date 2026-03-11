package ruiseki.omoshiroikamo.api.entity.dml;

import java.util.Map;

import com.google.gson.JsonObject;

import lombok.Getter;
import lombok.Setter;
import ruiseki.omoshiroikamo.core.json.AbstractJsonMaterial;

public class LivingRegistryItem extends AbstractJsonMaterial {

    @Getter
    protected int id;
    @Getter
    protected String displayName;
    @Getter
    @Setter
    protected String texture;
    @Getter
    protected int xpValue;

    @Getter
    protected Map<String, String> lang;

    @Getter
    @Setter
    protected boolean enabled;

    public LivingRegistryItem() {
        this.enabled = true;
    }

    public LivingRegistryItem(int id, String displayName, String texture, int xpValue) {
        this();
        this.id = id;
        this.displayName = displayName;
        this.texture = texture;
        this.xpValue = xpValue;
    }

    @Override
    public void read(JsonObject json) {
        this.id = getInt(json, "id", 0);
        this.displayName = getString(json, "displayName", "Unknown");
        this.texture = getString(json, "texture", displayName.toLowerCase());
        this.xpValue = getInt(json, "xpValue", 10);
        this.enabled = getBoolean(json, "enabled", true);
        this.lang = getMap(json, "lang");
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("id", id);
        json.addProperty("displayName", displayName);
        json.addProperty("texture", texture);
        json.addProperty("xpValue", xpValue);
        json.addProperty("enabled", enabled);
        writeMap(json, "lang", lang);
    }

    @Override
    public boolean validate() {
        if (id < 0) {
            logValidationError("ID must be non-negative");
            return false;
        }
        return true;
    }

    public String getItemName() {
        String name = displayName != null ? displayName : "Unknown";
        return "item.living_matter." + name + ".name";
    }
}
