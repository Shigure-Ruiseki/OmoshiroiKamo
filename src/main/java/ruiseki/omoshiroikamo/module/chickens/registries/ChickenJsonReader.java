package ruiseki.omoshiroikamo.module.chickens.registries;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;

import ruiseki.omoshiroikamo.core.json.AbstractJsonReader;

/**
 * Reader for Chicken definitions.
 * Can read a single file or a directory containing multiple files.
 */
public class ChickenJsonReader extends AbstractJsonReader<List<ChickenMaterial>> {

    public ChickenJsonReader(File path) {
        super(path);
    }

    @Override
    public List<ChickenMaterial> read() throws IOException {
        List<ChickenMaterial> materials = new ArrayList<>();
        if (path.isDirectory()) {
            for (File file : listJsonFiles(path)) materials.addAll(readFile(file));
        } else if (path.exists()) {
            materials.addAll(readFile(path));
        }
        this.cache = materials;
        rebuildIndex();
        return materials;
    }

    @Override
    protected List<ChickenMaterial> readFile(JsonElement root, File file) {
        List<ChickenMaterial> list = new ArrayList<>();
        if (root.isJsonArray()) {
            for (JsonElement e : root.getAsJsonArray()) {
                if (e.isJsonObject()) {
                    ChickenMaterial m = new ChickenMaterial();
                    m.setSourceFile(file);
                    m.read(e.getAsJsonObject());
                    list.add(m);
                }
            }
        } else if (root.isJsonObject()) {
            ChickenMaterial m = new ChickenMaterial();
            m.setSourceFile(file);
            m.read(root.getAsJsonObject());
            list.add(m);
        }
        return list;
    }

    @Override
    protected void rebuildIndex() {
        super.rebuildIndex();
        if (cache == null) return;
        for (ChickenMaterial m : cache) {
            if (m.name != null) index.put(m.name, m);
            if (m.id != null) index.put(String.valueOf(m.id), m);
        }
    }
}
