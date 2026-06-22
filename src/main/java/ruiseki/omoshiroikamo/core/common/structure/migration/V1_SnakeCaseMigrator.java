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
    public boolean migrate(JsonObject json) {
        boolean changed = false;
        if (json.has("mappings")) {
            changed |= migrateMappings(json.getAsJsonObject("mappings"));
        }
        if (json.has("tierStructures")) {
            JsonElement tierStructures = json.get("tierStructures");
            if (tierStructures.isJsonArray()) {
                for (JsonElement element : tierStructures.getAsJsonArray()) {
                    if (element.isJsonObject()) {
                        changed |= migrate(element.getAsJsonObject());
                    }
                }
            }
        }
        return changed;
    }

    private boolean migrateMappings(JsonObject mappings) {
        boolean changed = false;
        for (Map.Entry<String, JsonElement> entry : mappings.entrySet()) {
            JsonElement element = entry.getValue();

            if (element.isJsonPrimitive() && element.getAsJsonPrimitive()
                .isString()) {
                String oldId = element.getAsString();
                String newId = BlockCompat.camelToSnake(oldId);
                if (!oldId.equals(newId)) {
                    mappings.addProperty(entry.getKey(), newId);
                    changed = true;
                }
            } else if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                JsonArray newArray = new JsonArray();
                boolean arrayChanged = false;
                for (JsonElement item : array) {
                    if (item.isJsonPrimitive() && item.getAsJsonPrimitive()
                        .isString()) {
                        String oldId = item.getAsString();
                        String newId = BlockCompat.camelToSnake(oldId);
                        newArray.add(new JsonPrimitive(newId));
                        if (!oldId.equals(newId)) arrayChanged = true;
                    } else {
                        newArray.add(item);
                    }
                }
                if (arrayChanged) {
                    mappings.add(entry.getKey(), newArray);
                    changed = true;
                }
            } else if (element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();
                if (obj.has("block")) {
                    String oldId = obj.get("block")
                        .getAsString();
                    String newId = BlockCompat.camelToSnake(oldId);
                    if (!oldId.equals(newId)) {
                        obj.addProperty("block", newId);
                        changed = true;
                    }
                }
                if (obj.has("blocks")) {
                    JsonArray blocks = obj.getAsJsonArray("blocks");
                    JsonArray newBlocks = new JsonArray();
                    boolean arrayChanged = false;
                    for (JsonElement item : blocks) {
                        if (item.isJsonPrimitive() && item.getAsJsonPrimitive()
                            .isString()) {
                            String oldId = item.getAsString();
                            String newId = BlockCompat.camelToSnake(oldId);
                            newBlocks.add(new JsonPrimitive(newId));
                            if (!oldId.equals(newId)) arrayChanged = true;
                        } else {
                            newBlocks.add(item);
                        }
                    }
                    if (arrayChanged) {
                        obj.add("blocks", newBlocks);
                        changed = true;
                    }
                }
            }
        }
        return changed;
    }
}
