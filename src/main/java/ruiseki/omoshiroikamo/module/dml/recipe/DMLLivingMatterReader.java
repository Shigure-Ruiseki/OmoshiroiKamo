package ruiseki.omoshiroikamo.module.dml.recipe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import ruiseki.omoshiroikamo.api.entity.dml.LivingRegistry;
import ruiseki.omoshiroikamo.api.entity.dml.LivingRegistryItem;
import ruiseki.omoshiroikamo.core.json.AbstractJsonReader;

public class DMLLivingMatterReader extends AbstractJsonReader<List<LivingRegistryItem>> {

    public DMLLivingMatterReader(File path) {
        super(path);
    }

    public List<LivingRegistryItem> readDefault(List<LivingRegistryItem> defaults) {
        this.cache = defaults;
        register();
        // Return cache for writer if needed
        return this.cache;
    }

    private void register() {
        if (this.cache != null) {
            this.cache.forEach(LivingRegistry.INSTANCE::register);
        }
    }

    @Override
    public List<LivingRegistryItem> read() throws IOException {
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

        // Register to the DML Registry
        register();

        return cache;
    }

    @Override
    protected List<LivingRegistryItem> readFile(JsonElement root, File file) {
        List<LivingRegistryItem> results = new ArrayList<>();
        if (root.isJsonArray()) {
            JsonArray array = root.getAsJsonArray();
            for (JsonElement e : array) {
                if (e.isJsonObject()) {
                    LivingRegistryItem item = new LivingRegistryItem();
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
