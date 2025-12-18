package ruiseki.omoshiroikamo.plugin.cow;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.LanguageRegistry;
import lombok.Getter;
import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;
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
        String[] lang;
    }

    private static class FluidJson {

        String name;
        int amount;
    }

    private List<CowJson> loadedCustomCows;

    public List<CowsRegistryItem> tryRegisterCows(List<CowsRegistryItem> allCows) {
        Logger.info("Looking for " + modName + " cows...");

        if (needsMod && !Loader.isModLoaded(modID)) {
            Logger.info("Skipped " + modName + " cows → required mod \"" + modID + "\" is not loaded.");
            return allCows;
        }

        Logger.info("Loading " + modName + " cows...");

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
                Logger.info(configFileName + " is empty or invalid.");
                return allCows;
            }

            this.id = startID;

            this.loadedCustomCows = cows;

            for (CowJson data : cows) {
                try {
                    FluidStack milk = resolveFluidStack(data.fluid);
                    if (milk == null) {
                        Logger.error(
                            "Error Registering (" + this.modID + ") Cow: '" + data.name + "' It's fluid was null");
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
                        Logger.error("Invalid spawn type for " + data.name + ": " + data.spawnType);
                    }

                    // Migrate
                    int cowID = (data.id != null && data.id >= 0) ? data.id : fixedID(data.name);
                    if (data.id == null || data.id < 0) {
                        data.id = cowID;
                        saveJsonMigration(configFile, loadedCustomCows);
                    }

                    CowsRegistryItem cow = addCow(data.name, cowID, milk, bgColor, fgColor, type, data.lang);

                    if (cow != null) {
                        Logger.debug("Registering (" + this.modID + ") Cow: '" + data.name + "'");

                        cow.setEnabled(data.enabled);
                        if (data.lang != null) {
                            String langKey = "entity." + data.name + ".name";
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
                            cow.getId(),
                            new ModCompatInformation(this.getModID(), "", this.getModName()));

                        allCows.add(cow);
                    }

                } catch (Exception e) {
                    Logger.error("Error registering cow " + data.name + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
            this.loadedCustomCows = null;
        } catch (IOException e) {
            Logger.error("Failed to read " + configFileName + ": " + e.getMessage());
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
        return 2000 + Math.abs(hash % 30000);
    }

    private void saveJsonMigration(File file, List<CowJson> cows) {
        try (Writer writer = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting()
                .create();
            writer.write(gson.toJson(cows));
            Logger.info("Migrated config with new IDs: " + file.getName());
        } catch (IOException e) {
            Logger.error("Failed to migrate config with IDs: " + e.getMessage());
        }
    }

    protected CowsRegistryItem addCow(String cowName, int cowID, FluidStack fluid, int bgColor, int fgColor,
        SpawnType spawntype, String[] lang) {
        if (fluid == null || fluid.getFluid() == null) {
            Logger.error("Error Registering (" + this.modID + ") Cow: '" + cowName + "' It's fluid was null");
            return null;
        }

        return new CowsRegistryItem(
            cowID,
            cowName,
            new ResourceLocation("minecraft", "textures/entity/cow/cow.png"),
            fluid,
            bgColor,
            fgColor,
            lang).setSpawnType(spawntype);
    }

    protected CowsRegistryItem tryAddCow(String name, int id, String fluidName, int primary, int secondary,
        SpawnType type, String[] lang) {
        if (FluidRegistry.getFluid(fluidName) != null) {
            return addCow(
                name,
                id,
                new FluidStack(FluidRegistry.getFluid(fluidName), 1000),
                primary,
                secondary,
                type,
                lang);
        }
        return null;
    }

    private int parseColor(String hex, int def) {
        if (hex == null || hex.isEmpty()) return def;
        try {
            return Integer.decode(hex);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private FluidStack resolveFluidStack(FluidJson data) {
        if (data == null || data.name == null) return null;
        if (FluidRegistry.isFluidRegistered(data.name)) {
            return new FluidStack(FluidRegistry.getFluid(data.name), data.amount > 0 ? data.amount : 1000);
        }
        Logger.error("Fluid not found: " + data.name);
        return null;
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

        if (cow.createMilkFluid() != null) {
            FluidJson f = new FluidJson();
            f.name = cow.createMilkFluid()
                .getFluid()
                .getName();
            f.amount = cow.createMilkFluid().amount;
            json.fluid = f;
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

            Logger.info("Created default " + file.getPath());
        } catch (IOException e) {
            Logger.error("Failed to create default config: " + file.getPath() + " (" + e.getMessage() + ")");
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
                Logger.error("Failed to read existing cow config: " + e.getMessage());
            }
        }

        boolean updated = false;
        for (CowsRegistryItem cow : allCows) {
            if (cow == null) continue;

            boolean exists = existing.stream()
                .anyMatch(c -> c != null && c.name != null && c.name.equalsIgnoreCase(cow.getEntityName()));
            if (!exists) {
                CowJson json = toCowJson(cow);
                if (json != null) {
                    existing.add(json);
                    updated = true;
                }
            }
        }

        if (updated) {
            try (Writer writer = new FileWriter(file)) {
                new GsonBuilder().setPrettyPrinting()
                    .create()
                    .toJson(existing, writer);
                Logger.info("Updated cow config with missing cows: " + file.getName());
            } catch (IOException e) {
                Logger.error("Failed to update cow config: " + e.getMessage());
            }
        }
    }
}
