package ruiseki.omoshiroikamo.core.common.structure.migration;

import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import ruiseki.omoshiroikamo.api.structure.migration.IDataMigrator;
import ruiseki.omoshiroikamo.core.common.structure.BlockCompat;

/**
 * Migrator that converts block IDs from camelCase to snake_case.
 * Target mod version: 1.5.4.1 (refactoring release)
 */
public class V1_SnakeCaseMigrator implements IDataMigrator {

    private final String targetVersion = "1.5.4.1";

    @Override
    public String getTargetModVersion() {
        return targetVersion;
    }

    @Override
    public void migrate(JsonObject json) {
        // 1. Process mappings in the current object
        if (json.has("mappings")) {
            migrateMappings(json.getAsJsonObject("mappings"));
        }

        // 2. Process tierStructures if present
        if (json.has("tierStructures")) {
            JsonElement tierStructures = json.get("tierStructures");
            if (tierStructures.isJsonArray()) {
                JsonArray array = tierStructures.getAsJsonArray();
                for (JsonElement element : array) {
                    if (element.isJsonObject()) {
                        migrate(element.getAsJsonObject()); // Recursive call for inner mappings
                    }
                }
            }
        }
    }

    private void migrateMappings(JsonObject mappings) {
        for (Map.Entry<String, JsonElement> entry : mappings.entrySet()) {
            JsonElement element = entry.getValue();

            if (element.isJsonPrimitive() && element.getAsJsonPrimitive()
                .isString()) {
                String oldId = element.getAsString();
                String newId = BlockCompat.camelToSnake(oldId);
                if (!oldId.equals(newId)) {
                    mappings.addProperty(entry.getKey(), newId);
                }
            } else if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                JsonArray newArray = new JsonArray();
                boolean changed = false;

                for (JsonElement item : array) {
                    if (item.isJsonPrimitive() && item.getAsJsonPrimitive()
                        .isString()) {
                        String oldId = item.getAsString();
                        String newId = BlockCompat.camelToSnake(oldId);
                        newArray.add(new JsonPrimitive(newId));
                        if (!oldId.equals(newId)) {
                            changed = true;
                        }
                    } else {
                        newArray.add(item);
                    }
                }

                if (changed) {
                    mappings.add(entry.getKey(), newArray);
                }
            } else if (element.isJsonObject()) {
                // Handle object format: {"block": "mod:name"} or {"blocks": [...]}
                JsonObject obj = element.getAsJsonObject();
                if (obj.has("block")) {
                    String oldId = obj.get("block")
                        .getAsString();
                    String newId = BlockCompat.camelToSnake(oldId);
                    if (!oldId.equals(newId)) {
                        obj.addProperty("block", newId);
                    }
                }
                if (obj.has("blocks")) {
                    JsonArray blocks = obj.getAsJsonArray("blocks");
                    JsonArray newBlocks = new JsonArray();
                    boolean changed = false;
                    for (JsonElement item : blocks) {
                        if (item.isJsonPrimitive() && item.getAsJsonPrimitive()
                            .isString()) {
                            String oldId = item.getAsString();
                            String newId = BlockCompat.camelToSnake(oldId);
                            newBlocks.add(new JsonPrimitive(newId));
                            if (!oldId.equals(newId)) changed = true;
                        } else {
                            newBlocks.add(item);
                        }
                    }
                    if (changed) {
                        obj.add("blocks", newBlocks);
                    }
                }
            }
        }
    }
}
