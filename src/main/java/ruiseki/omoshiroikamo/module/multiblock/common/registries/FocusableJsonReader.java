package ruiseki.omoshiroikamo.module.multiblock.common.registries;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.core.json.AbstractJsonReader;

/**
 * Reader for Multiblock Focusable definitions.
 */
public class FocusableJsonReader extends AbstractJsonReader<List<FocusableMaterial>> {

    public FocusableJsonReader(File path) {
        super(path);
    }

    @Override
    public List<FocusableMaterial> read() throws IOException {
        List<FocusableMaterial> materials = new ArrayList<>();
        if (path.isDirectory()) {
            for (File file : listJsonFiles(path)) {
                materials.addAll(readFile(file));
            }
        } else if (path.exists()) {
            materials.addAll(readFile(path));
        }
        this.cache = materials;
        // Search index could be added here if needed, e.g., by ID
        return materials;
    }

    @Override
    protected List<FocusableMaterial> readFile(JsonElement root, File file) {
        List<FocusableMaterial> list = new ArrayList<>();

        if (root.isJsonObject()) {
            JsonObject obj = root.getAsJsonObject();
            // Support both single entry and FocusableList wrapper
            if (obj.has("entries") && obj.get("entries")
                .isJsonArray()) {
                for (JsonElement e : obj.getAsJsonArray("entries")) {
                    list.add(parseEntry(e, file));
                }
            } else {
                list.add(parseEntry(root, file));
            }
        } else if (root.isJsonArray()) {
            for (JsonElement e : root.getAsJsonArray()) {
                list.add(parseEntry(e, file));
            }
        }

        return list;
    }

    private FocusableMaterial parseEntry(JsonElement e, File source) {
        if (!e.isJsonObject()) return null;
        FocusableMaterial m = new FocusableMaterial();
        m.setSourceFile(source);
        m.read(e.getAsJsonObject());
        return m;
    }
}
