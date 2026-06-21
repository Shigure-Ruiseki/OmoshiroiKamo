package ruiseki.omoshiroikamo.core.common.structure.migration;

import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import ruiseki.omoshiroikamo.api.structure.migration.IDataMigrator;
import ruiseki.omoshiroikamo.core.common.structure.BlockCompat;

/**
 * Migrator that replaces the removed modular_machine_casing block with casing_plain.
 * Target mod version: 2.0.1
 */
public class V2_ModularCasingMigrator implements IDataMigrator {

    @Override
    public String getTargetModVersion() {
        return "2.0.1";
    }

    @Override
    public void migrate(JsonObject json) {
        if (json.has("mappings")) {
            migrateMappings(json.getAsJsonObject("mappings"));
        }

        if (json.has("tierStructures")) {
            JsonElement tierStructures = json.get("tierStructures");
            if (tierStructures.isJsonArray()) {
                for (JsonElement element : tierStructures.getAsJsonArray()) {
                    if (element.isJsonObject()) {
                        migrate(element.getAsJsonObject());
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
                String newId = BlockCompat.remapRemovedBlocks(element.getAsString());
                if (newId != null) {
                    mappings.addProperty(entry.getKey(), newId);
                }
            } else if (element.isJsonArray()) {
                migrateArray(mappings, entry.getKey(), element.getAsJsonArray());
            } else if (element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();
                if (obj.has("block")) {
                    String newId = BlockCompat.remapRemovedBlocks(
                        obj.get("block")
                            .getAsString());
                    if (newId != null) {
                        obj.addProperty("block", newId);
                    }
                }
                if (obj.has("blocks")) {
                    migrateArray(obj, "blocks", obj.getAsJsonArray("blocks"));
                }
            }
        }
    }

    private void migrateArray(JsonObject parent, String key, JsonArray array) {
        JsonArray newArray = new JsonArray();
        boolean changed = false;
        for (JsonElement item : array) {
            if (item.isJsonPrimitive() && item.getAsJsonPrimitive()
                .isString()) {
                String newId = BlockCompat.remapRemovedBlocks(item.getAsString());
                if (newId != null) {
                    newArray.add(new JsonPrimitive(newId));
                    changed = true;
                } else {
                    newArray.add(item);
                }
            } else {
                newArray.add(item);
            }
        }
        if (changed) {
            parent.add(key, newArray);
        }
    }
}
