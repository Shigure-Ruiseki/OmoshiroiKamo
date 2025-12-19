package ruiseki.omoshiroikamo.plugin.model;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import cpw.mods.fml.common.Loader;
import lombok.Getter;
import ruiseki.omoshiroikamo.api.entity.model.ModelRegistryItem;
import ruiseki.omoshiroikamo.api.json.ItemJson;
import ruiseki.omoshiroikamo.api.json.JsonUtils;
import ruiseki.omoshiroikamo.common.util.Logger;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.config.ConfigUpdater;
import ruiseki.omoshiroikamo.config.backport.DeepMobLearningConfig;
import ruiseki.omoshiroikamo.plugin.ModCompatInformation;

// Refactor base on OriginalChicken by Chlorine0808
public abstract class BaseModelHandler {

    @Getter
    protected String modID;
    @Getter
    protected String modName;
    protected String texturesLocation;

    private int startID = 0;
    private int id = 0;
    protected String configFileName;

    private boolean needsMod = true;

    public BaseModelHandler(String modID, String modName, String texturesLocation) {
        this.modID = modID;
        this.modName = modName;
        this.texturesLocation = texturesLocation;
        this.configFileName = modID.toLowerCase() + "_models.json";
    }

    public void setStartID(int startID) {
        this.startID = startID;
        this.id = startID;
    }

    public void setNeedsModPresent(boolean bool) {
        this.needsMod = bool;
    }

    private static class ModelJson {

        Integer id;
        String name;
        boolean enabled;
        String texture;
        DeepLearnerDisplay deepLearnerDisplay;
        ItemJson[] lootItems;
        Map<String, String> lang;
    }

    private static class DeepLearnerDisplay {

        float numberOfHearts;
        float interfaceScale;
        int interfaceOffsetX;
        int interfaceOffsetY;
        String[] mobTrivia;
    }

    private List<ModelJson> loadedCustomModels;

    public List<ModelRegistryItem> tryRegisterModels(List<ModelRegistryItem> allModels) {
        Logger.info("Looking for {} models...", modName);
        if (needsMod && !Loader.isModLoaded(modID)) {
            Logger.info("Skipped {} models → required mod \"{}\" is not loaded.", modName, modID);
            return allModels;
        }
        Logger.info("Loading {} models...", modName);

        File configFile = new File("config/" + LibMisc.MOD_ID + "/model/" + configFileName);
        if (!configFile.exists()) {
            List<ModelRegistryItem> defaultModels = registerModels();
            createDefaultConfig(configFile, defaultModels);
        }

        if (DeepMobLearningConfig.updateMissing) {
            updateConfigWithMissing(configFile, registerModels());
            ConfigUpdater.updateBoolean(DeepMobLearningConfig.class, "updateMissing", false);
        }

        this.id = startID;

        try (FileReader fileReader = new FileReader(configFile)) {
            JsonReader reader = new JsonReader(fileReader);
            reader.setLenient(true); // Allow comments

            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<ModelJson>>() {}.getType();
            List<ModelJson> models = gson.fromJson(reader, listType);
            if (models == null) {
                Logger.info("{} is empty or invalid.", configFileName);
                return allModels;
            }

            this.loadedCustomModels = models;

            for (ModelJson data : models) {
                try {

                    // Migrate
                    int modelID = (data.id != null && data.id >= 0) ? data.id : fixedID(data.name);
                    if (data.id == null || data.id < 0) {
                        data.id = modelID;
                        saveJsonMigration(configFile, loadedCustomModels);
                    }

                    ModelRegistryItem model = addModel(
                        data.name,
                        modelID,
                        data.texture,
                        data.deepLearnerDisplay.numberOfHearts,
                        data.deepLearnerDisplay.interfaceScale,
                        data.deepLearnerDisplay.interfaceOffsetX,
                        data.deepLearnerDisplay.interfaceOffsetY,
                        data.deepLearnerDisplay.mobTrivia);

                    if (model != null) {
                        Logger.debug("Registering ({}) Model: '{}' : {}", this.modID, data.name, model.getId());

                        model.setEnabled(data.enabled);

                        if (data.lootItems != null && data.lootItems.length > 0) {
                            List<ItemStack> loot = ItemJson.resolveListItemStack(data.lootItems);
                            model.setLootItems(loot);
                        }

                        if (data.lang != null) {
                            String langKey = "item.model." + data.name + ".name";
                            JsonUtils.registerLang(langKey, data.lang);
                        }

                        ModCompatInformation.addInformation(
                            model.getId(),
                            new ModCompatInformation(this.getModID(), "", this.getModName()));

                        allModels.add(model);
                    }

                } catch (Exception e) {
                    Logger.error("Error registering model {}: {}", data.name, e.getMessage());
                    e.printStackTrace();
                }
            }
            this.loadedCustomModels = null;
        } catch (IOException e) {
            Logger.error("Failed to read {}: {}", configFileName, e.getMessage());
        }

        return allModels;
    }

    public abstract List<ModelRegistryItem> registerModels();

    protected int nextID() {
        return this.id++;
    }

    protected int fixedID(String name) {
        int hash = (modID + ":" + name).toLowerCase()
            .hashCode();
        return startID + Math.abs(hash % (30000 - startID));
    }

    private void saveJsonMigration(File file, List<ModelJson> models) {
        try (Writer writer = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting()
                .create();
            writer.write(gson.toJson(models));
            Logger.info("Migrated config with new IDs: {}", file.getName());
        } catch (IOException e) {
            Logger.error("Failed to migrate config with IDs: {}", e.getMessage());
        }
    }

    public ModelRegistryItem addModel(String registryName, int id, String texture, float numberOfHearts,
        float interfaceScale, int interfaceOffsetX, int interfaceOffsetY, String[] mobTrivia) {

        return new ModelRegistryItem(
            id,
            registryName,
            new ResourceLocation(LibMisc.MOD_ID, this.texturesLocation + texture),
            numberOfHearts,
            interfaceScale,
            interfaceOffsetX,
            interfaceOffsetY,
            mobTrivia);
    }

    private ModelJson toModelJson(ModelRegistryItem model) {
        if (model == null) return null;

        ModelJson json = new ModelJson();
        json.id = model.getId();
        json.name = model.getEntityName();
        json.enabled = true;
        String fullPath = model.getTexture()
            .getResourcePath();
        json.texture = fullPath.substring(fullPath.lastIndexOf('/') + 1);

        json.deepLearnerDisplay = new DeepLearnerDisplay();
        json.deepLearnerDisplay.numberOfHearts = model.getNumberOfHearts();
        json.deepLearnerDisplay.interfaceScale = model.getInterfaceScale();
        json.deepLearnerDisplay.interfaceOffsetX = model.getInterfaceOffsetX();
        json.deepLearnerDisplay.interfaceOffsetY = model.getInterfaceOffsetY();
        json.deepLearnerDisplay.mobTrivia = model.getMobTrivia();

        List<ItemJson> lootList = new ArrayList<>();
        if (model.getLootStrings() != null) {
            for (String string : model.getLootStrings()) {
                ItemJson item = ItemJson.parseItemString(string);
                if (item != null) {
                    lootList.add(item);
                }
            }
        }

        if (model.getLootItems() != null) {
            for (ItemStack stack : model.getLootItems()) {
                ItemJson item = ItemJson.parseItemStack(stack);
                if (item != null) {
                    lootList.add(item);
                }
            }
        }

        if (!lootList.isEmpty()) {
            json.lootItems = lootList.toArray(new ItemJson[0]);
        }

        json.lang = model.getLang();
        return json;
    }

    public void createDefaultConfig(File file, List<ModelRegistryItem> allModels) {
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();

            List<BaseModelHandler.ModelJson> jsonModels = new ArrayList<>();
            for (ModelRegistryItem model : allModels) {
                BaseModelHandler.ModelJson json = toModelJson(model);
                if (json != null) jsonModels.add(json);
            }

            try (Writer writer = new FileWriter(file)) {
                new GsonBuilder().setPrettyPrinting()
                    .create()
                    .toJson(jsonModels, writer);
            }

            Logger.info("Created default {}", file.getPath());
        } catch (IOException e) {
            Logger.error("Failed to create default config: {} ({})", file.getPath(), e.getMessage());
        }
    }

    private void updateConfigWithMissing(File file, List<ModelRegistryItem> allModels) {
        List<BaseModelHandler.ModelJson> existing = new ArrayList<>();

        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                JsonReader jsonReader = new JsonReader(reader);
                jsonReader.setLenient(true);
                Type listType = new TypeToken<ArrayList<BaseModelHandler.ModelJson>>() {}.getType();
                List<BaseModelHandler.ModelJson> loaded = new Gson().fromJson(jsonReader, listType);
                if (loaded != null) existing.addAll(loaded);
            } catch (Exception e) {
                Logger.error("Failed to read existing model config: {}", e.getMessage());
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
        }

        boolean updated = false;
        List<String> addedModels = new ArrayList<>();
        for (ModelRegistryItem model : allModels) {
            if (model == null) continue;

            boolean exists = existing.stream()
                .anyMatch(m -> m != null && m.name != null && m.name.equalsIgnoreCase(model.getEntityName()));
            if (!exists) {
                BaseModelHandler.ModelJson json = toModelJson(model);
                if (json != null) {
                    existing.add(json);
                    addedModels.add(model.getEntityName());
                    updated = true;
                }
            }
        }

        if (updated) {
            try (Writer writer = new FileWriter(file)) {
                new GsonBuilder().setPrettyPrinting()
                    .create()
                    .toJson(existing, writer);
                Logger.info("Updated model config with missing models: {}", file.getName());
                Logger.info("Added {} model(s): {}", addedModels.size(), String.join(", ", addedModels));
            } catch (IOException e) {
                Logger.error("Failed to update model config: {}", e.getMessage());
            }
        } else {
            Logger.info("No new models to add to config: {}", file.getName());
        }
    }
}
