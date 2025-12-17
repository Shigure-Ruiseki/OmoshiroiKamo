package ruiseki.omoshiroikamo.plugin.deepMobLearning;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.LanguageRegistry;
import lombok.Getter;
import ruiseki.omoshiroikamo.api.entity.model.ModelRegistryItem;
import ruiseki.omoshiroikamo.common.util.Logger;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
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

        String name;
        boolean enabled;
        String texture;
        float numberOfHearts;
        float interfaceScale;
        int interfaceOffsetX;
        int interfaceOffsetY;
        String[] mobTrivia;
        String[] lang;
    }

    public List<ModelRegistryItem> tryRegisterModels(List<ModelRegistryItem> allModels) {
        Logger.info("Looking for " + modName + " models...");
        if (needsMod && !Loader.isModLoaded(modID)) {
            Logger.info("Skipped " + modName + " models → required mod \"" + modID + "\" is not loaded.");
            return allModels;
        }
        Logger.info("Loading " + modName + " models...");

        File configFile = new File("config/" + LibMisc.MOD_ID + "/model/" + configFileName);
        if (!configFile.exists()) {
            List<ModelRegistryItem> defaultModels = registerModels();
            createDefaultConfig(configFile, defaultModels);
        }

        this.id = startID;

        try (FileReader fileReader = new FileReader(configFile)) {
            JsonReader reader = new JsonReader(fileReader);
            reader.setLenient(true); // Allow comments

            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<ModelJson>>() {}.getType();
            List<ModelJson> models = gson.fromJson(reader, listType);
            if (models == null) {
                Logger.info(configFileName + " is empty or invalid.");
                return allModels;
            }

            for (ModelJson data : models) {
                try {

                    ModelRegistryItem model = addModel(
                        data.name,
                        this.nextID(),
                        data.texture,
                        data.numberOfHearts,
                        data.interfaceScale,
                        data.interfaceOffsetX,
                        data.interfaceOffsetY,
                        data.mobTrivia,
                        data.lang);

                    if (model != null) {
                        Logger.debug("Registering (" + this.modID + ") Model: '" + data.name + "':" + model.getId());

                        model.setEnabled(data.enabled);
                        if (data.lang != null) {
                            String langKey = "item.model." + data.name + ".name";
                            for (String entry : data.lang) {
                                int splitIndex = entry.indexOf(':');
                                if (splitIndex > 0) {
                                    String lang = entry.substring(0, splitIndex)
                                        .trim();
                                    String value = entry.substring(splitIndex + 1)
                                        .trim();
                                    LanguageRegistry.instance()
                                        .addStringLocalization(langKey, lang, value);
                                }
                            }
                        }

                        ModCompatInformation.addInformation(
                            model.getId(),
                            new ModCompatInformation(this.getModID(), "", this.getModName()));

                        allModels.add(model);
                    }

                } catch (Exception e) {
                    Logger.error("Error registering model " + data.name + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            Logger.error("Failed to read " + configFileName + ": " + e.getMessage());
        }

        return allModels;
    }

    public abstract List<ModelRegistryItem> registerModels();

    protected int nextID() {
        return this.id++;
    }

    public ModelRegistryItem addModel(String registryName, int id, String texture, float numberOfHearts,
        float interfaceScale, int interfaceOffsetX, int interfaceOffsetY, String[] mobTrivia, String[] lang) {

        return new ModelRegistryItem(
            id,
            registryName,
            new ResourceLocation(LibMisc.MOD_ID, this.texturesLocation + texture),
            numberOfHearts,
            interfaceScale,
            interfaceOffsetX,
            interfaceOffsetY,
            mobTrivia,
            lang);
    }

    public void createDefaultConfig(File file, List<ModelRegistryItem> allModels) {
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try (Writer writer = new FileWriter(file)) {
                List<ModelJson> jsonModels = new ArrayList<>();

                for (ModelRegistryItem model : allModels) {
                    if (model == null) continue;

                    ModelJson m = new ModelJson();
                    m.name = model.getEntityName();
                    m.enabled = true;

                    String fullPath = model.getTexture()
                        .getResourcePath();
                    m.texture = fullPath.substring(fullPath.lastIndexOf('/') + 1);

                    m.numberOfHearts = model.getNumberOfHearts();
                    m.interfaceScale = model.getInterfaceScale();
                    m.interfaceOffsetX = model.getInterfaceOffsetX();
                    m.interfaceOffsetY = model.getInterfaceOffsetY();
                    m.mobTrivia = model.getMobTrivia();
                    m.lang = model.getLang();

                    jsonModels.add(m);
                }

                Gson gson = new GsonBuilder().setPrettyPrinting()
                    .create();
                writer.write(gson.toJson(jsonModels));
            }

            Logger.info("Created default " + file.getPath());
        } catch (IOException e) {
            Logger.error("Failed to create default config: " + file.getPath() + " (" + e.getMessage() + ")");
        }
    }
}
