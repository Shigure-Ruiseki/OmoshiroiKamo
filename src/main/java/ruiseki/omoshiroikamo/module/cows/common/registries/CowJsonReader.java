package ruiseki.omoshiroikamo.module.cows.common.registries;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;

import ruiseki.omoshiroikamo.core.json.AbstractJsonReader;

/**
 * Reader for Cow definitions.
 */
public class CowJsonReader extends AbstractJsonReader<List<CowMaterial>> {

    public CowJsonReader(File path) {
        super(path);
    }

    @Override
    public List<CowMaterial> read() throws IOException {
        List<CowMaterial> materials = new ArrayList<>();
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
    protected List<CowMaterial> readFile(JsonElement root, File file) {
        List<CowMaterial> list = new ArrayList<>();
        if (root.isJsonArray()) {
            for (JsonElement e : root.getAsJsonArray()) {
                if (e.isJsonObject()) {
                    CowMaterial m = new CowMaterial();
                    m.setSourceFile(file);
                    m.read(e.getAsJsonObject());
                    list.add(m);
                }
            }
        } else if (root.isJsonObject()) {
            CowMaterial m = new CowMaterial();
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
        for (CowMaterial m : cache) {
            if (m.name != null) index.put(m.name, m);
            if (m.id != null) index.put(String.valueOf(m.id), m);
        }
    }
}
