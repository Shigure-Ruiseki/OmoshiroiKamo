package ruiseki.omoshiroikamo.api.structure.migration;

import com.google.gson.JsonObject;

/**
 * Interface for a data migrator that transforms a structure JSON between versions.
 */
public interface IDataMigrator {

    /**
     * @return The target version this migrator establishes (e.g., "1.5.4").
     */
    String getTargetModVersion();

    /**
     * Performs the migration on the given JSON object.
     * This should be idempotent if possible.
     *
     * @param json The JSON object to transform
     */
    void migrate(JsonObject json);
}
