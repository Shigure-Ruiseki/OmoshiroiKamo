package ruiseki.omoshiroikamo.core.json;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.core.common.util.Logger;

/**
 * Abstract base class for materials that wrap a JsonObject.
 */
public abstract class AbstractJsonMaterial implements IJsonMaterial {

    protected File sourceFile;
    protected final Map<String, JsonElement> unknownProperties = new HashMap<>();

    public File getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    /**
     * Reads a string from the json object with a default value.
     */
    protected String getString(JsonObject json, String memberName, String def) {
        return json.has(memberName) && !json.get(memberName)
            .isJsonNull() ? json.get(memberName)
                .getAsString() : def;
    }

    /**
     * Reads an int from the json object with a default value.
     */
    protected int getInt(JsonObject json, String memberName, int def) {
        return json.has(memberName) && !json.get(memberName)
            .isJsonNull() ? json.get(memberName)
                .getAsInt() : def;
    }

    /**
     * Reads a boolean from the json object with a default value.
     */
    protected boolean getBoolean(JsonObject json, String memberName, boolean def) {
        return json.has(memberName) && !json.get(memberName)
            .isJsonNull() ? json.get(memberName)
                .getAsBoolean() : def;
    }

    /**
     * Reads a float from the json object with a default value.
     */
    protected float getFloat(JsonObject json, String memberName, float def) {
        return json.has(memberName) && !json.get(memberName)
            .isJsonNull() ? json.get(memberName)
                .getAsFloat() : def;
    }

    /**
     * Captures properties not handled by this material to prevent data loss.
     */
    protected void captureUnknownProperties(JsonObject json, String... handledKeys) {
        Map<String, Boolean> handled = new HashMap<>();
        for (String key : handledKeys) handled.put(key, true);
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (!handled.containsKey(entry.getKey())) {
                unknownProperties.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Writes back all captured unknown properties to the target JSON.
     */
    protected void writeUnknownProperties(JsonObject json) {
        for (Map.Entry<String, JsonElement> entry : unknownProperties.entrySet()) {
            json.add(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Generic getter for properties. Subclasses should override this if they want
     * to expose fields.
     */
    public Object get(String key) {
        return null;
    }

    /**
     * Generic setter for properties. Subclasses should override this if they want
     * to allow updates.
     */
    public void set(String key, Object value) {}

    /**
     * Validates the material data. Returns true if valid.
     */
    public boolean validate() {
        return true;
    }

    protected void logValidationError(String message) {
        Logger.error(
            "Validation error in {}: {}",
            this.getClass()
                .getSimpleName(),
            message);
    }
}
