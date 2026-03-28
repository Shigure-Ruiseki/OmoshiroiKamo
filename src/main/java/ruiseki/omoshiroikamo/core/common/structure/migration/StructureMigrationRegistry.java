package ruiseki.omoshiroikamo.core.common.structure.migration;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.structure.migration.IDataMigrator;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.common.util.VersionComparator;

/**
 * Central registry for structure data migrators.
 */
public class StructureMigrationRegistry {

    private static final List<IDataMigrator> MIGRATORS = new ArrayList<>();

    static {
        MIGRATORS.add(new V1_SnakeCaseMigrator()); // v1.5.4.1
    }

    /**
     * Applies migrations to the given structure JSON if its version is older than
     * the current mod version.
     *
     * @param root              The structure JSON element (Object or Array)
     * @param currentModVersion The current version of the mod (Tags.VERSION)
     * @return true if any migration was applied, false otherwise
     */
    public static boolean migrate(JsonElement root, String currentModVersion) {
        if (root == null || (!root.isJsonObject() && !root.isJsonArray())) {
            return false;
        }

        boolean[] migrated = { false };

        if (root.isJsonObject()) {
            migrateObject(root.getAsJsonObject(), currentModVersion, migrated);
        } else if (root.isJsonArray()) {
            for (JsonElement element : root.getAsJsonArray()) {
                if (element.isJsonObject()) {
                    migrateObject(element.getAsJsonObject(), currentModVersion, migrated);
                }
            }
        }

        return migrated[0];
    }

    private static void migrateObject(JsonObject json, String currentModVersion, boolean[] migratedFlag) {
        String fileVersion = "0.0.0";
        if (json.has("modVersion")) {
            fileVersion = json.get("modVersion")
                .getAsString();
        }

        boolean thisObjectMigrated = false;

        for (IDataMigrator migrator : MIGRATORS) {
            String targetVer = migrator.getTargetModVersion();

            // If file is older than the target version of this migrator
            if (VersionComparator.compare(fileVersion, targetVer) < 0) {
                Logger.info(
                    "[Migration] Applying migrator to version " + targetVer
                        + " for structure: "
                        + (json.has("name") ? json.get("name")
                            .getAsString() : "unknown"));
                migrator.migrate(json);
                fileVersion = targetVer;
                thisObjectMigrated = true;
                migratedFlag[0] = true;
            }
        }

        // Final update to the latest mod version if migrated
        if (thisObjectMigrated) {
            json.addProperty("modVersion", VersionComparator.toBaseVersion(currentModVersion));
        }
    }
}
