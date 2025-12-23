package ruiseki.omoshiroikamo.plugin.dml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import lombok.Getter;
import ruiseki.omoshiroikamo.api.entity.model.LivingRegistry;
import ruiseki.omoshiroikamo.api.entity.model.LivingRegistryItem;
import ruiseki.omoshiroikamo.api.entity.model.ModelRegistryItem;
import ruiseki.omoshiroikamo.api.json.ItemJson;
import ruiseki.omoshiroikamo.api.json.JsonUtils;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.util.Logger;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.config.ConfigUpdater;
import ruiseki.omoshiroikamo.config.backport.DMLConfig;
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

    public static class ModelJson {

        Integer id;
        String displayName;
        boolean enabled;
        String texture;
        String pristineMatterTexture;
        int simulationRFCost;
        String livingMatter;
        DeepLearnerDisplay deepLearnerDisplay;
        ItemJson[] lootItems;
        ItemJson[] craftingIngredients;
        String[] associatedMobs;
        String extraTooltip;
        Map<String, String> lang;
        Map<String, String> pristineLang;
    }

    private static class DeepLearnerDisplay {

        String entityDisplay;
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

        if (DMLConfig.updateMissing) {
            updateConfigWithMissing(configFile, registerModels());
            ConfigUpdater.updateBoolean(DMLConfig.class, "updateMissing", false);
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
                    if (data.associatedMobs == null) {
                        Logger.error(
                            "Error registering ({}) Model '{}' : associatedMobs was null",
                            this.modID,
                            data.displayName);
                        continue;
                    }

                    List<Class<? extends Entity>> associatedEntityClasses;
                    associatedEntityClasses = JsonUtils.resolveEntityClasses(data.associatedMobs);
                    if (associatedEntityClasses.isEmpty()) {
                        Logger.error(
                            "Error registering ({}) Model '{}' : associatedEntityClasses was null",
                            this.modID,
                            data.displayName);
                        continue;
                    }

                    if (data.livingMatter == null || data.livingMatter.isEmpty()) {
                        Logger.error(
                            "Error registering ({}) Model '{}' : livingMatter is missing or empty",
                            this.modID,
                            data.displayName);
                        continue;
                    }

                    LivingRegistryItem livingMatter = LivingRegistry.INSTANCE.getByName(data.livingMatter);
                    if (livingMatter == null) {
                        Logger.error(
                            "Error registering ({}) Model '{}' : livingMatter '{}' not found in LivingRegistry",
                            this.modID,
                            data.displayName,
                            data.livingMatter);
                        continue;
                    }

                    // Migrate
                    int modelID = (data.id != null && data.id >= 0) ? data.id : fixedID(data.displayName);
                    if (data.id == null || data.id < 0) {
                        data.id = modelID;
                        saveJsonMigration(configFile, loadedCustomModels);
                    }

                    ModelRegistryItem model = addModel(
                        data.displayName,
                        modelID,
                        data.texture,
                        data.deepLearnerDisplay.entityDisplay,
                        data.deepLearnerDisplay.numberOfHearts,
                        data.deepLearnerDisplay.interfaceScale,
                        data.deepLearnerDisplay.interfaceOffsetX,
                        data.deepLearnerDisplay.interfaceOffsetY,
                        data.deepLearnerDisplay.mobTrivia);

                    if (model != null) {
                        Logger.debug("Registering ({}) Model: '{}' : {}", this.modID, data.displayName, model.getId());

                        model.setEnabled(data.enabled);

                        if (data.pristineMatterTexture != null) {
                            model.setPristineTexture(
                                LibResources.PREFIX_MOD + "pristine/"
                                    + this.texturesLocation
                                    + data.pristineMatterTexture);
                        } else {
                            model.setPristineTexture(
                                LibResources.PREFIX_MOD + "pristine/"
                                    + this.texturesLocation
                                    + "pristine_matter_"
                                    + data.texture);
                        }

                        if (data.lootItems != null && data.lootItems.length > 0) {
                            List<ItemStack> loot = ItemJson.resolveListItemStack(data.lootItems);
                            model.setLootItems(loot);
                        }

                        if (data.craftingIngredients != null && data.craftingIngredients.length > 0) {
                            List<ItemStack> crafting = ItemJson.resolveListItemStack(data.craftingIngredients);

                            Object[] recipe = new Object[crafting.size() + 1];
                            recipe[0] = ModItems.DATA_MODEL_BLANK.newItemStack(1);

                            for (int i = 0; i < crafting.size(); i++) {
                                recipe[i + 1] = crafting.get(i);
                            }

                            GameRegistry.addShapelessRecipe(ModItems.DATA_MODEL.newItemStack(1, data.id), recipe);
                        }

                        model.setLivingMatter(livingMatter);

                        model.setPristineMatter(ModItems.PRISTINE_MATTER.newItemStack(1, model.getId()));

                        model.setAssociatedMobs(data.associatedMobs);
                        model.setAssociatedMobsClasses(associatedEntityClasses);

                        model.setSimulationRFCost(data.simulationRFCost);

                        if (data.extraTooltip != null) {
                            model.setExtraTooltip(data.extraTooltip);
                        }

                        if (data.lang != null) {
                            String langKey = "item.model." + data.displayName + ".name";
                            JsonUtils.registerLang(langKey, data.lang);
                        }

                        if (data.pristineLang != null) {
                            String langKey = "item.pristine." + data.displayName + ".name";
                            JsonUtils.registerLang(langKey, data.pristineLang);
                        }

                        ModCompatInformation.addInformation(
                            model.getId(),
                            new ModCompatInformation(this.getModID(), "", this.getModName()));

                        allModels.add(model);
                    }

                } catch (Exception e) {
                    Logger.error("Error registering model {}: {}", data.displayName, e.getMessage());
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

    public void saveJsonMigration(File file, List<ModelJson> models) {
        try (Writer writer = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting()
                .create();
            writer.write(gson.toJson(models));
            Logger.info("Migrated config with new IDs: {}", file.getName());
        } catch (IOException e) {
            Logger.error("Failed to migrate config with IDs: {}", e.getMessage());
        }
    }

    public ModelRegistryItem addModel(String displayName, int id, String texture, String entityDisplay,
        float numberOfHearts, float interfaceScale, int interfaceOffsetX, int interfaceOffsetY, String[] mobTrivia) {

        return new ModelRegistryItem(
            id,
            displayName,
            LibResources.PREFIX_MOD + "model/" + this.texturesLocation + texture,
            entityDisplay,
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
        json.displayName = model.getDisplayName();
        json.enabled = true;
        String fullPath = model.getTexture();
        json.texture = fullPath.substring(fullPath.lastIndexOf('/') + 1);

        json.simulationRFCost = model.getSimulationRFCost() > 0 ? model.getSimulationRFCost() : 256;

        json.livingMatter = LivingRegistry.INSTANCE.getByType(
            model.getLivingMatter()
                .getItemDamage())
            .getDisplayName();

        json.deepLearnerDisplay = new DeepLearnerDisplay();
        json.deepLearnerDisplay.entityDisplay = model.getEntityDisplay();
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

        List<ItemJson> craftingList = new ArrayList<>();
        if (model.getCraftingStrings() != null) {
            for (String string : model.getCraftingStrings()) {
                ItemJson item = ItemJson.parseItemString(string);
                if (item != null) {
                    craftingList.add(item);
                }
            }
        }

        if (!craftingList.isEmpty()) {
            json.craftingIngredients = craftingList.toArray(new ItemJson[0]);
        }

        if (!lootList.isEmpty()) {
            json.lootItems = lootList.toArray(new ItemJson[0]);
        }

        json.associatedMobs = model.getAssociatedMobs();

        if (model.getExtraTooltip() != null) {
            json.extraTooltip = model.getExtraTooltip();
        }

        json.lang = model.getLang();
        json.pristineLang = model.getPristineLang();
        return json;
    }

    public void createDefaultConfig(File file, List<ModelRegistryItem> allModels) {
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();

            List<ModelJson> jsonModels = new ArrayList<>();
            for (ModelRegistryItem model : allModels) {
                ModelJson json = toModelJson(model);
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
        List<ModelJson> existing = new ArrayList<>();

        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                JsonReader jsonReader = new JsonReader(reader);
                jsonReader.setLenient(true);
                Type listType = new TypeToken<ArrayList<ModelJson>>() {}.getType();
                List<ModelJson> loaded = new Gson().fromJson(jsonReader, listType);
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
                .anyMatch(m -> m != null && m.id != null && m.id == model.getId());
            if (!exists) {
                ModelJson json = toModelJson(model);
                if (json != null) {
                    existing.add(json);
                    addedModels.add(model.getDisplayName());
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
