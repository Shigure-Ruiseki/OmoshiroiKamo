package ruiseki.omoshiroikamo.api.structure.io;

import com.google.gson.JsonObject;

/**
 * Interface for objects that can be serialized to JSON.
 */
public interface IStructureSerializable {

    /**
     * Serialize this object to a JsonObject.
     * 
     * @return The serialized JsonObject.
     */
    JsonObject serialize();
}
