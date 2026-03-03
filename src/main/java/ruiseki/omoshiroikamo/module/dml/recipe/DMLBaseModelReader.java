package ruiseki.omoshiroikamo.module.dml.recipe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import ruiseki.omoshiroikamo.api.entity.dml.ModelRegistry;
import ruiseki.omoshiroikamo.api.entity.dml.ModelRegistryItem;
import ruiseki.omoshiroikamo.core.json.AbstractJsonReader;

public class DMLBaseModelReader extends AbstractJsonReader<List<ModelRegistryItem>> {

    public DMLBaseModelReader(File path) {
        super(path);
    }

    @Override
    public List<ModelRegistryItem> read() throws IOException {
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

        // Register to the DML Registry and Generate Recipes
        this.cache.forEach(model -> {
            ModelRegistry.INSTANCE.register(model);
            if (model.isEnabled()) {
                // Generate for Simulation Chamber
                SimulationChamberRecipe simRecipe = new SimulationChamberRecipe(model);
                DMLRecipeRegistry.INSTANCE.addSimulationRecipe(simRecipe);

                // Generate for Loot Fabricator
                LootFabricatorRecipe fabricRecipe = new LootFabricatorRecipe(model);
                DMLRecipeRegistry.INSTANCE.addFabricationRecipe(fabricRecipe);
            }
        });

        return cache;
    }

    @Override
    protected List<ModelRegistryItem> readFile(JsonElement root, File file) {
        List<ModelRegistryItem> results = new ArrayList<>();
        if (root.isJsonArray()) {
            JsonArray array = root.getAsJsonArray();
            for (JsonElement e : array) {
                if (e.isJsonObject()) {
                    ModelRegistryItem item = new ModelRegistryItem();
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
