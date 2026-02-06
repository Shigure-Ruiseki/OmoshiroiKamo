package ruiseki.omoshiroikamo.module.dml.common.registries;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import ruiseki.omoshiroikamo.api.entity.dml.LivingRegistryItem;
import ruiseki.omoshiroikamo.api.json.JsonUtils;
import ruiseki.omoshiroikamo.config.ConfigUpdater;
import ruiseki.omoshiroikamo.config.backport.DMLConfig;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;

public class ModLivingMatters {

    private int id = 0;
    protected String configFileName;

    public ModLivingMatters() {
        this.configFileName = "living_matter.json";
    }

    public static class LivingJson {

        Integer id;
        String displayName;
        String texture;
        boolean enable;
        int xpValue;
        Map<String, String> lang;
    }

    private List<LivingJson> loadedCustomModels;

    public List<LivingRegistryItem> tryRegisterLivings() {
        List<LivingRegistryItem> allLivings = new ArrayList<>();

        File configFile = new File("config/" + LibMisc.MOD_ID + "/dml/" + configFileName);
        if (!configFile.exists()) {
            List<LivingRegistryItem> defaultModels = registerLivings();
            createDefaultConfig(configFile, defaultModels);
        }

        if (DMLConfig.updateMissing) {
            updateConfigWithMissing(configFile, registerLivings());
            ConfigUpdater.updateBoolean(DMLConfig.class, "updateMissing", false);
        }

        this.id = 0;

        try (FileReader fileReader = new FileReader(configFile)) {
            JsonReader reader = new JsonReader(fileReader);
            reader.setLenient(true); // Allow comments

            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<LivingJson>>() {}.getType();
            List<LivingJson> models = gson.fromJson(reader, listType);
            if (models == null) {
                Logger.info("{} is empty or invalid.", configFileName);
            }

            this.loadedCustomModels = models;

            for (LivingJson data : models) {
                try {

                    int modelID = (data.id != null && data.id >= 0) ? data.id : fixedID(data.displayName);
                    if (data.id == null || data.id < 0) {
                        data.id = modelID;
                        saveJsonMigration(configFile, loadedCustomModels);
                    }

                    LivingRegistryItem model = addLiving(data.id, data.displayName, data.texture, data.xpValue);

                    if (model != null) {
                        Logger.debug("Registering Model Tier: {}", data.displayName);

                        model.setEnabled(data.enable);

                        if (data.lang != null) {
                            String langKey = "item.living_matter." + data.displayName + ".name";
                            JsonUtils.registerLang(langKey, data.lang);
                        }

                        allLivings.add(model);
                    }

                } catch (Exception e) {
                    Logger.error("Error registering model tier {}: {}", data.displayName, e.getMessage());
                    e.printStackTrace();
                }
            }
            this.loadedCustomModels = null;
        } catch (IOException e) {
            Logger.error("Failed to read : ", configFileName, e.getMessage());
        }

        return allLivings;
    }

    public List<LivingRegistryItem> registerLivings() {
        List<LivingRegistryItem> allLivings = new ArrayList<>();

        LivingRegistryItem overworldian = addLiving(this.nextID(), "overworldian", "overworldian", 10);
        allLivings.add(overworldian);

        LivingRegistryItem hellish = addLiving(this.nextID(), "hellish", "hellish", 14);
        allLivings.add(hellish);

        LivingRegistryItem extraterrestrial = addLiving(this.nextID(), "extraterrestrial", "extraterrestrial", 20);
        allLivings.add(extraterrestrial);

        return allLivings;
    }

    public LivingRegistryItem addLiving(int id, String displayName, String texture, int xpValue) {
        return new LivingRegistryItem(id, displayName, LibResources.PREFIX_MOD + "living/" + texture, xpValue);
    }

    protected int nextID() {
        return this.id++;
    }

    protected int fixedID(String name) {
        int hash = (LibMisc.MOD_ID + ":" + name).toLowerCase()
            .hashCode();
        return Math.abs(hash % (30000));
    }

    public void saveJsonMigration(File file, List<LivingJson> models) {
        try (Writer writer = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting()
                .create();
            writer.write(gson.toJson(models));
            Logger.info("Migrated config with new IDs: {}", file.getName());
        } catch (IOException e) {
            Logger.error("Failed to migrate config with IDs: {}", e.getMessage());
        }
    }

    private LivingJson toModelJson(LivingRegistryItem living) {
        if (living == null) return null;

        LivingJson json = new LivingJson();
        json.id = living.getId();
        json.displayName = living.getDisplayName();
        String fullPath = living.getTexture();
        json.texture = fullPath.substring(fullPath.lastIndexOf('/') + 1);
        json.xpValue = living.getXpValue();
        json.enable = true;
        json.lang = living.getLang();

        return json;
    }

    public void createDefaultConfig(File file, List<LivingRegistryItem> allModels) {
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();

            List<LivingJson> jsonModels = new ArrayList<>();
            for (LivingRegistryItem model : allModels) {
                LivingJson json = toModelJson(model);
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

    private void updateConfigWithMissing(File file, List<LivingRegistryItem> allModels) {
        List<LivingJson> existing = new ArrayList<>();

        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                JsonReader jsonReader = new JsonReader(reader);
                jsonReader.setLenient(true);
                Type listType = new TypeToken<ArrayList<LivingJson>>() {}.getType();
                List<LivingJson> loaded = new Gson().fromJson(jsonReader, listType);
                if (loaded != null) existing.addAll(loaded);
            } catch (Exception e) {
                Logger.error("Failed to read existing tier config: {}", e.getMessage());
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
        }

        boolean updated = false;
        List<String> addedLivings = new ArrayList<>();
        for (LivingRegistryItem model : allModels) {
            if (model == null) continue;

            boolean exists = existing.stream()
                .anyMatch(m -> m != null && m.id != model.getId());
            if (!exists) {
                LivingJson json = toModelJson(model);
                if (json != null) {
                    existing.add(json);
                    addedLivings.add(model.getDisplayName());
                    updated = true;
                }
            }
        }

        if (updated) {
            try (Writer writer = new FileWriter(file)) {
                new GsonBuilder().setPrettyPrinting()
                    .create()
                    .toJson(existing, writer);
                Logger.info("Updated model tier config with missing model tiers: {}", file.getName());
                Logger.info("Added {} model tier(s): {}", addedLivings.size(), String.join(", ", addedLivings));
            } catch (IOException e) {
                Logger.error("Failed to update model tier config: {}", e.getMessage());
            }
        } else {
            Logger.info("No new model tiers to add to config: {}", file.getName());
        }
    }

}
