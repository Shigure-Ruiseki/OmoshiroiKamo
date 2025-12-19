package ruiseki.omoshiroikamo.plugin.cow;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import cpw.mods.fml.common.Loader;
import lombok.Getter;
import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;
import ruiseki.omoshiroikamo.api.json.FluidJson;
import ruiseki.omoshiroikamo.api.json.JsonUtils;
import ruiseki.omoshiroikamo.common.util.Logger;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.config.ConfigUpdater;
import ruiseki.omoshiroikamo.config.backport.CowConfig;
import ruiseki.omoshiroikamo.plugin.ModCompatInformation;

// Refactor base on OriginalChicken by Chlorine0808
public abstract class BaseCowHandler {

    @Getter
    protected String modID;
    @Getter
    protected String modName;
    protected String texturesLocation;

    private int startID = 0;
    private int id = 0;
    protected String configFileName;

    private boolean needsMod = true;

    public BaseCowHandler(String modID, String modName, String texturesLocation) {
        this.modID = modID;
        this.modName = modName;
        this.texturesLocation = texturesLocation;
        this.configFileName = modID.toLowerCase() + "_cows.json";
    }

    public void setStartID(int startID) {
        this.startID = startID;
        this.id = startID;
    }

    public void setNeedsModPresent(boolean bool) {
        this.needsMod = bool;
    }

    private static class CowJson {

        Integer id;
        String name;
        boolean enabled;
        FluidJson fluid;
        String fgColor; // Hex string
        String bgColor; // Hex string
        String spawnType;
        Map<String, String> lang;
    }

    private List<CowJson> loadedCustomCows;

    public List<CowsRegistryItem> tryRegisterCows(List<CowsRegistryItem> allCows) {
        Logger.info("Looking for {} cows...", modName);

        if (needsMod && !Loader.isModLoaded(modID)) {
            Logger.info("Skipped {} cows → required mod \"{}\" is not loaded.", modName, modID);
            return allCows;
        }

        Logger.info("Loading {} cows...", modName);

        File configFile = new File("config/" + LibMisc.MOD_ID + "/cow/" + configFileName);
        if (!configFile.exists()) {
            List<CowsRegistryItem> defaultCows = registerCows();
            createDefaultConfig(configFile, defaultCows);
        }

        if (CowConfig.updateMissing) {
            updateConfigWithMissing(configFile, registerCows());
            ConfigUpdater.updateBoolean(CowConfig.class, "updateMissing", false);
        }

        try (FileReader fileReader = new FileReader(configFile)) {
            JsonReader reader = new JsonReader(fileReader);
            reader.setLenient(true);

            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<CowJson>>() {}.getType();
            List<CowJson> cows = gson.fromJson(reader, listType);
            if (cows == null) {
                Logger.info("{} is empty or invalid.", configFileName);
                return allCows;
            }

            this.id = startID;

            this.loadedCustomCows = cows;

            for (CowJson data : cows) {
                try {
                    FluidStack milk = FluidJson.resolveFluidStack(data.fluid);
                    if (milk == null) {
                        Logger.error("Error registering ({}) Cow '{}' : fluid was null", this.modID, data.name);
                        continue;
                    }
                    int bgColor = parseColor(data.bgColor, 0xFFFFFF);
                    int fgColor = parseColor(data.fgColor, 0xAAAAAA);

                    SpawnType type = SpawnType.NORMAL;
                    try {
                        if (data.spawnType != null && !data.spawnType.isEmpty()) {
                            type = SpawnType.valueOf(data.spawnType.toUpperCase());
                        }
                    } catch (IllegalArgumentException e) {
                        Logger.error("Invalid spawn type for cow {}: {}", data.name, data.spawnType);
                    }

                    // Migrate
                    int cowID = (data.id != null && data.id >= 0) ? data.id : fixedID(data.name);
                    if (data.id == null || data.id < 0) {
                        data.id = cowID;
                        saveJsonMigration(configFile, loadedCustomCows);
                    }

                    CowsRegistryItem cow = addCow(data.name, cowID, bgColor, fgColor, type);

                    if (cow != null) {
                        Logger.debug("Registering ({}) Cow '{}'", this.modID, data.name);

                        cow.setEnabled(data.enabled);
                        cow.setFluid(milk);
                        if (data.lang != null) {
                            String langKey = "entity." + data.name + ".name";
                            JsonUtils.registerLang(langKey, data.lang);
                        }

                        ModCompatInformation.addInformation(
                            cow.getId(),
                            new ModCompatInformation(this.getModID(), "", this.getModName()));

                        allCows.add(cow);
                    }

                } catch (Exception e) {
                    Logger.error("Error registering cow {}", data.name, e);
                }
            }
            this.loadedCustomCows = null;
        } catch (IOException e) {
            Logger.error("Failed to read {}: {}", configFileName, e.getMessage());
        }

        return allCows;
    }

    public abstract List<CowsRegistryItem> registerCows();

    protected int nextID() {
        return this.id++;
    }

    protected int fixedID(String name) {
        int hash = (modID + ":" + name).toLowerCase()
            .hashCode();
        return startID + Math.abs(hash % (30000 - startID));
    }

    private void saveJsonMigration(File file, List<CowJson> cows) {
        try (Writer writer = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting()
                .create();
            writer.write(gson.toJson(cows));
            Logger.info("Migrated config with new IDs: {}", file.getName());
        } catch (IOException e) {
            Logger.error("Failed to migrate config with IDs: {}", e.getMessage());
        }
    }

    protected CowsRegistryItem addCow(String cowName, int cowID, int bgColor, int fgColor, SpawnType spawntype) {

        return new CowsRegistryItem(
            cowID,
            cowName,
            new ResourceLocation("minecraft", "textures/entity/cow/cow.png"),
            bgColor,
            fgColor).setSpawnType(spawntype);
    }

    private int parseColor(String hex, int def) {
        if (hex == null || hex.isEmpty()) return def;
        try {
            return Integer.decode(hex);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private CowJson toCowJson(CowsRegistryItem cow) {
        if (cow == null) return null;

        CowJson json = new CowJson();
        json.id = cow.getId();
        json.name = cow.getEntityName();
        json.enabled = true;
        json.bgColor = String.format("0x%06X", cow.getBgColor() & 0xFFFFFF);
        json.fgColor = String.format("0x%06X", cow.getFgColor() & 0xFFFFFF);
        json.spawnType = cow.getSpawnType() != null ? cow.getSpawnType()
            .name() : "NORMAL";

        if (cow.getFluidString() != null) {
            json.fluid = FluidJson.parseFluidString(cow.getFluidString());
        }

        if (cow.getFluid() != null) {
            json.fluid = FluidJson.parseFluidStack(cow.getFluid());
        }

        json.lang = cow.getLang();
        return json;
    }

    public void createDefaultConfig(File file, List<CowsRegistryItem> allCows) {
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();

            List<CowJson> jsonModels = new ArrayList<>();
            for (CowsRegistryItem cow : allCows) {
                CowJson json = toCowJson(cow);
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

    private void updateConfigWithMissing(File file, List<CowsRegistryItem> allCows) {
        List<CowJson> existing = new ArrayList<>();

        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                JsonReader jsonReader = new JsonReader(reader);
                jsonReader.setLenient(true);
                Type listType = new TypeToken<ArrayList<CowJson>>() {}.getType();
                List<CowJson> loaded = new Gson().fromJson(jsonReader, listType);
                if (loaded != null) existing.addAll(loaded);
            } catch (Exception e) {
                Logger.error("Failed to read existing cow config: {}", e.getMessage());
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
        }

        boolean updated = false;
        List<String> addedCows = new ArrayList<>();
        for (CowsRegistryItem cow : allCows) {
            if (cow == null) continue;

            boolean exists = existing.stream()
                .anyMatch(c -> c != null && c.name != null && c.name.equalsIgnoreCase(cow.getEntityName()));
            if (!exists) {
                CowJson json = toCowJson(cow);
                if (json != null) {
                    existing.add(json);
                    addedCows.add(cow.getEntityName());
                    updated = true;
                }
            }
        }

        if (updated) {
            try (Writer writer = new FileWriter(file)) {
                new GsonBuilder().setPrettyPrinting()
                    .create()
                    .toJson(existing, writer);
                Logger.info("Updated cow config with missing cows: {}", file.getName());
                Logger.info("Added {} cow(s): {}", addedCows.size(), String.join(", ", addedCows));
            } catch (IOException e) {
                Logger.error("Failed to update cow config: {}", e.getMessage());
            }
        } else {
            Logger.info("No new cows to add to config: {}", file.getName());
        }
    }
}
