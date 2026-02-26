package ruiseki.omoshiroikamo.module.machinery.common.recipe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;

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
        if (path.isDirectory()) {
            for (File file : listJsonFiles(path)) {
                materials.addAll(readFile(file));
            }
        } else if (path.exists()) {
            materials.addAll(readFile(path));
        }
        this.cache = materials;
        return materials;
    }

    private List<MachineryMaterial> readFile(File file) throws IOException {
        List<MachineryMaterial> list = new ArrayList<>();
        JsonElement root = readJsonElement(file);

        if (root.isJsonObject()) {
            list.add(parseEntry(root, file));
        } else if (root.isJsonArray()) {
            for (JsonElement e : root.getAsJsonArray()) {
                list.add(parseEntry(e, file));
            }
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
