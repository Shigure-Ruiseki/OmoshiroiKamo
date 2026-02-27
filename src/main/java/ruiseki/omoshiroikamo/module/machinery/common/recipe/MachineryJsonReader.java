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
public class MachineryJsonReader extends AbstractJsonReader<List<MachineryMaterial>> {

    public MachineryJsonReader(File path) {
        super(path);
    }

    @Override
    public List<MachineryMaterial> read() throws IOException {
        List<MachineryMaterial> materials = new ArrayList<>();
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
    protected List<MachineryMaterial> readFile(JsonElement root, File file) {
        List<MachineryMaterial> list = new ArrayList<>();
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
                    MachineryMaterial m = parseEntry(element, file);
                    if (m != null) {
                        if (m.machine == null) m.machine = group;
                        if (m.validate()) {
                            list.add(m);
                        } else {
                            Logger.warn(
                                "[MachineryJsonReader] Recipe validation failed in " + file.getName()
                                    + " (group="
                                    + group
                                    + ", name="
                                    + m.localizedName
                                    + ", regName="
                                    + m.registryName
                                    + ")");
                        }
                    }
                }
            } else {
                MachineryMaterial m = parseEntry(obj, file);
                if (m != null) {
                    if (m.validate()) {
                        list.add(m);
                    } else {
                        Logger.warn(
                            "[MachineryJsonReader] Single recipe validation failed in " + file
                                .getName() + " (name=" + m.localizedName + ", regName=" + m.registryName + ")");
                    }
                }
            }
        } else if (root.isJsonArray()) {
            JsonArray arr = root.getAsJsonArray();
            Logger.debug(
                "[MachineryJsonReader] Found top-level recipes array with " + arr.size()
                    + " entries in "
                    + file.getName());
            for (JsonElement e : arr) {
                MachineryMaterial m = parseEntry(e, file);
                if (m != null) {
                    if (m.validate()) {
                        list.add(m);
                    } else {
                        Logger.warn(
                            "[MachineryJsonReader] Array recipe validation failed in " + file
                                .getName() + " (name=" + m.localizedName + ", regName=" + m.registryName + ")");
                    }
                }
            }
        }

        if (list.size() > 0) {
            Logger
                .debug("[MachineryJsonReader] Successfully loaded " + list.size() + " recipes from " + file.getName());
        }
        return list;
    }

    private MachineryMaterial parseEntry(JsonElement e, File source) {
        if (!e.isJsonObject()) return null;
        MachineryMaterial m = new MachineryMaterial();
        m.setSourceFile(source);
        m.read(e.getAsJsonObject());
        return m;
    }
}
