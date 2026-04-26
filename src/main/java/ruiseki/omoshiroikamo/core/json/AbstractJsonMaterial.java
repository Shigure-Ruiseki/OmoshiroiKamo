package ruiseki.omoshiroikamo.core.json;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import ruiseki.omoshiroikamo.core.common.util.Logger;

/**
 * Abstract base class for materials that wrap a JsonObject.
 *
 * TODO: Future Architectural Improvement - Data Mapper Pattern
 * Currently, domain models (entities) are responsible for their own JSON
 * serialization/deserialization by extending this class (Active Record pattern).
 * To improve separation of concerns and remove GSON dependency from the API layer,
 * consider moving JSON logic to dedicated Reader/Writer classes.
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
     * Reads a Map from the json object.
     */
    protected Map<String, String> getMap(JsonObject json, String memberName) {
        Map<String, String> map = new HashMap<>();
        if (json.has(memberName) && json.get(memberName)
            .isJsonObject()) {
            JsonObject obj = json.getAsJsonObject(memberName);
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                if (entry.getValue()
                    .isJsonPrimitive()) {
                    map.put(
                        entry.getKey(),
                        entry.getValue()
                            .getAsString());
                }
            }
        }
        return map;
    }

    /**
     * Writes a Map to the json object.
     */
    protected void writeMap(JsonObject json, String memberName, Map<String, String> map) {
        if (map == null || map.isEmpty()) return;
        JsonObject obj = new JsonObject();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            obj.addProperty(entry.getKey(), entry.getValue());
        }
        json.add(memberName, obj);
    }

    /**
     * Reads a String array from the json object.
     */
    protected String[] getStringArray(JsonObject json, String memberName) {
        if (json.has(memberName) && json.get(memberName)
            .isJsonArray()) {
            JsonArray array = json.getAsJsonArray(memberName);
            String[] result = new String[array.size()];
            for (int i = 0; i < array.size(); i++) {
                result[i] = array.get(i)
                    .getAsString();
            }
            return result;
        }
        return new String[0];
    }

    /**
     * Reads a Map of String arrays from the json object.
     */
    protected Map<String, String[]> getStringArrayMap(JsonObject json, String memberName) {
        Map<String, String[]> map = new HashMap<>();
        if (!json.has(memberName) || !json.get(memberName)
            .isJsonObject()) return map;
        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject(memberName)
            .entrySet()) {
            if (entry.getValue()
                .isJsonArray()) {
                map.put(
                    entry.getKey(),
                    getStringArray(
                        entry.getValue()
                            .getAsJsonArray()));
            }
        }
        return map.isEmpty() ? null : map;
    }

    private String[] getStringArray(JsonArray array) {
        String[] result = new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            result[i] = array.get(i)
                .getAsString();
        }
        return result;
    }

    /**
     * Writes a Map of String arrays to the json object.
     */
    protected void writeStringArrayMap(JsonObject json, String memberName, Map<String, String[]> map) {
        if (map == null || map.isEmpty()) return;
        JsonObject obj = new JsonObject();
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            JsonArray array = new JsonArray();
            for (String s : entry.getValue()) {
                array.add(new JsonPrimitive(s));
            }
            obj.add(entry.getKey(), array);
        }
        json.add(memberName, obj);
    }

    /**
     * Writes a String array to the json object.
     */
    protected void writeStringArray(JsonObject json, String memberName, String[] array) {
        if (array == null || array.length == 0) return;
        JsonArray jsonArray = new JsonArray();
        for (String s : array) {
            jsonArray.add(new JsonPrimitive(s));
        }
        json.add(memberName, jsonArray);
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
        String fileName = ParsingContext.getCurrentFileName();
        String className = this.getClass()
            .getSimpleName();

        // Log to terminal
        Logger.error("Validation error in {} ({}): {}", className, fileName, message);

        // Collect to unified error system
        JsonErrorCollector.getInstance()
            .collect(className, message);
    }
}
