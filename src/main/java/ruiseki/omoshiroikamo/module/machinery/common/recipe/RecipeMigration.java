package ruiseki.omoshiroikamo.module.machinery.common.recipe;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;

import ruiseki.omoshiroikamo.core.common.structure.BlockCompat;
import ruiseki.omoshiroikamo.core.common.util.Logger;

/**
 * Migrates recipe JSON files on disk by applying block ID remaps from BlockCompat.
 * Content-based: only rewrites files that actually contain remappable IDs.
 */
public class RecipeMigration {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
        .disableHtmlEscaping()
        .create();

    public static void migrateAll(File recipesDir) {
        if (!recipesDir.exists() || !recipesDir.isDirectory()) return;

        for (File file : listJsonFiles(recipesDir)) {
            try {
                migrateFile(file);
            } catch (IOException e) {
                Logger.warn("[RecipeMigration] Failed to migrate {}: {}", file.getName(), e.getMessage());
            }
        }
    }

    private static void migrateFile(File file) throws IOException {
        JsonElement root;
        try (FileReader fr = new FileReader(file)) {
            JsonReader reader = new JsonReader(fr);
            reader.setLenient(true);
            root = new JsonParser().parse(reader);
        }

        if (root == null) return;

        boolean changed;
        JsonElement toWrite;

        if (root.isJsonObject()) {
            changed = migrateObject(root.getAsJsonObject());
            toWrite = root;
        } else if (root.isJsonArray()) {
            JsonArray migrated = migrateArray(root.getAsJsonArray());
            changed = migrated != null;
            toWrite = changed ? migrated : root;
        } else {
            return;
        }

        if (!changed) return;

        try (FileWriter fw = new FileWriter(file)) {
            GSON.toJson(toWrite, fw);
        }
        Logger.info("[RecipeMigration] Migrated recipe file: {}", file.getName());
    }

    // Modifies the object in-place; returns true if any change was made.
    private static boolean migrateObject(JsonObject obj) {
        boolean changed = false;
        for (Map.Entry<String, JsonElement> entry : new ArrayList<>(obj.entrySet())) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            if (value.isJsonPrimitive() && value.getAsJsonPrimitive()
                .isString()) {
                String newId = BlockCompat.remapRemovedBlocks(value.getAsString());
                if (newId != null) {
                    obj.add(key, new JsonPrimitive(newId));
                    changed = true;
                }
            } else if (value.isJsonObject()) {
                changed |= migrateObject(value.getAsJsonObject());
            } else if (value.isJsonArray()) {
                JsonArray migrated = migrateArray(value.getAsJsonArray());
                if (migrated != null) {
                    obj.add(key, migrated);
                    changed = true;
                }
            }
        }
        return changed;
    }

    // Returns a new JsonArray with replacements applied, or null if nothing changed.
    private static JsonArray migrateArray(JsonArray original) {
        JsonArray result = new JsonArray();
        boolean changed = false;
        for (JsonElement item : original) {
            if (item.isJsonPrimitive() && item.getAsJsonPrimitive()
                .isString()) {
                String newId = BlockCompat.remapRemovedBlocks(item.getAsString());
                if (newId != null) {
                    result.add(new JsonPrimitive(newId));
                    changed = true;
                } else {
                    result.add(item);
                }
            } else if (item.isJsonObject()) {
                JsonObject obj = item.getAsJsonObject();
                changed |= migrateObject(obj);
                result.add(obj);
            } else if (item.isJsonArray()) {
                JsonArray nested = migrateArray(item.getAsJsonArray());
                if (nested != null) {
                    result.add(nested);
                    changed = true;
                } else {
                    result.add(item);
                }
            } else {
                result.add(item);
            }
        }
        return changed ? result : null;
    }

    private static List<File> listJsonFiles(File dir) {
        List<File> files = new ArrayList<>();
        listRecursive(dir, files);
        return files;
    }

    private static void listRecursive(File dir, List<File> files) {
        File[] found = dir.listFiles();
        if (found == null) return;
        for (File f : found) {
            if (f.isDirectory()) {
                listRecursive(f, files);
            } else if (f.getName()
                .endsWith(".json")) {
                    files.add(f);
                }
        }
    }
}
