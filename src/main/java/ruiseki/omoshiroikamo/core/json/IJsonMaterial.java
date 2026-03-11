package ruiseki.omoshiroikamo.core.json;

import com.google.gson.JsonObject;

/**
 * Interface for materials that can be serialized to/from JSON.
 * Represents a logical unit of data (e.g., Item, Condition).
 */
public interface IJsonMaterial {

    /**
     * Populates this material's data from a JsonObject.
     *
     * @param json the source JSON object
     */
    void read(JsonObject json);

    /**
     * Populates this material's data from a JsonElement.
     * Default implementation delegates to read(JsonObject) if the element is an
     * object.
     *
     * @param json the source JSON element
     */
    default void read(com.google.gson.JsonElement json) {
        if (json != null && json.isJsonObject()) {
            read(json.getAsJsonObject());
        }
    }

    /**
     * Serializes this material's data to a JsonObject.
     *
     * @param json the target JSON object
     */
    void write(JsonObject json);

    /**
     * Validates the material data. Returns true if valid.
     */
    default boolean validate() {
        return true;
    }
}
