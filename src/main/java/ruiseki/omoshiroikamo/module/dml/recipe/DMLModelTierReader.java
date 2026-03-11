package ruiseki.omoshiroikamo.module.dml.recipe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import ruiseki.omoshiroikamo.api.entity.dml.ModelTierRegistryItem;
import ruiseki.omoshiroikamo.core.json.AbstractJsonReader;

public class DMLModelTierReader extends AbstractJsonReader<List<ModelTierRegistryItem>> {

    public DMLModelTierReader(File path) {
        super(path);
    }

    public List<ModelTierRegistryItem> readDefault(List<ModelTierRegistryItem> defaults) {
        this.cache = defaults;
        return this.cache;
    }

    @Override
    public List<ModelTierRegistryItem> read() throws IOException {
        this.cache = new ArrayList<>();
        if (path.exists()) {
            if (path.isDirectory()) {
                for (File f : listJsonFiles(path)) {
                    this.cache.addAll(readFile(f));
                }
            } else {
                this.cache.addAll(readFile(path));
            }
        }

        return cache;
    }

    @Override
    protected List<ModelTierRegistryItem> readFile(JsonElement root, File file) {
        List<ModelTierRegistryItem> results = new ArrayList<>();
        if (root.isJsonArray()) {
            JsonArray array = root.getAsJsonArray();
            for (JsonElement e : array) {
                if (e.isJsonObject()) {
                    ModelTierRegistryItem item = new ModelTierRegistryItem();
                    item.setSourceFile(file);
                    item.read(e.getAsJsonObject());
                    if (item.validate()) {
                        results.add(item);
                    }
                }
            }
        }
        return results;
    }
}
