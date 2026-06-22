package ruiseki.omoshiroikamo.api.structure.migration;

import com.google.gson.JsonObject;

/**
 * Interface for a data migrator that transforms a structure JSON between versions.
 */
public interface IDataMigrator {

    /**
     * @return The target version this migrator establishes
     */
    String getTargetModVersion();

    /**
     * Performs the migration on the given JSON object.
     *
     * @param json The JSON object to transform
     * @return true if any content was actually changed, false if nothing needed to be done
     */
    boolean migrate(JsonObject json);
}
