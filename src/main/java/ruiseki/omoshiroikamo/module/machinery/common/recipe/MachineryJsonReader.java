package ruiseki.omoshiroikamo.module.machinery.common.recipe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.json.AbstractJsonReader;

/**
 * Reader for Modular Machinery recipes.
 */
public class MachineryJsonReader extends AbstractJsonReader<List<JsonObject>> {

    public MachineryJsonReader(File path) {
        super(path);
    }

    @Override
    public List<JsonObject> read() throws IOException {
        List<JsonObject> materials = new ArrayList<>();
        Logger.info("[MachineryJsonReader] Starting scan at: " + path.getAbsolutePath());
        if (path.isDirectory()) {
            List<File> files = listJsonFiles(path);
            Logger.info("[MachineryJsonReader] Found " + files.size() + " JSON files");
            for (File file : files) {
                materials.addAll(readFile(file));
            }
        } else if (path.exists()) {
            materials.addAll(readFile(path));
        }
        Logger.info("[MachineryJsonReader] Total materials read: " + materials.size());
        this.cache = materials;
        return materials;
    }

    @Override
    protected List<JsonObject> readFile(JsonElement root, File file) {
        List<JsonObject> list = new ArrayList<>();
        Logger.debug("[MachineryJsonReader] Reading file: " + file.getName());

        if (root.isJsonObject()) {
            JsonObject obj = root.getAsJsonObject();
            if (obj.has("recipes") && obj.get("recipes")
                .isJsonArray()) {
                // Header format: { "group": "...", "recipes": [...] }
                String group = obj.has("group") ? obj.get("group")
                    .getAsString()
                    : (obj.has("machine") ? obj.get("machine")
                        .getAsString() : null);

                JsonArray recipesArr = obj.getAsJsonArray("recipes");
                Logger.debug(
                    "[MachineryJsonReader] Found nested recipes array with " + recipesArr.size()
                        + " entries in "
                        + file.getName());

                for (JsonElement element : recipesArr) {
                    JsonObject m = parseEntry(element, file);
                    if (m != null) {
                        if (!m.has("machine") && !m.has("group")) {
                            m.addProperty("machine", group);
                        }
                        list.add(m);
                    }
                }
            } else {
                JsonObject m = parseEntry(obj, file);
                if (m != null) {
                    list.add(m);
                }
            }
        } else if (root.isJsonArray()) {
            JsonArray arr = root.getAsJsonArray();
            Logger.debug(
                "[MachineryJsonReader] Found top-level recipes array with " + arr.size()
                    + " entries in "
                    + file.getName());
            for (JsonElement e : arr) {
                JsonObject m = parseEntry(e, file);
                if (m != null) {
                    list.add(m);
                }
            }
        }

        if (list.size() > 0) {
            Logger
                .debug("[MachineryJsonReader] Successfully loaded " + list.size() + " recipes from " + file.getName());
        }
        return list;
    }

    private JsonObject parseEntry(JsonElement e, File source) {
        if (!e.isJsonObject()) return null;
        JsonObject m = e.getAsJsonObject();
        // We can add metadata like source file if needed, but for now just returning
        // the JSON
        return m;
    }
}
