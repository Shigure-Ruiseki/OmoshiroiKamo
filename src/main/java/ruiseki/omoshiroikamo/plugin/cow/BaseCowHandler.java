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
import ruiseki.omoshiroikamo.api.entity.model.ModelRegistryItem;
import ruiseki.omoshiroikamo.common.util.Logger;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.plugin.ModCompatInformation;
import ruiseki.omoshiroikamo.plugin.deepMobLearning.BaseModelHandler;

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

                    CowsRegistryItem cow = addCow(data.name, this.nextID(), milk, bgColor, fgColor, type, data.lang);

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

        } catch (IOException e) {
            Logger.error("Failed to read " + configFileName + ": " + e.getMessage());
        }

        return allCows;
    }

    public abstract List<CowsRegistryItem> registerCows();

    protected int nextID() {
        return this.id++;
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
            fluid.copy(),
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

    public void createDefaultConfig(File file, List<CowsRegistryItem> allCows) {
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try (Writer writer = new FileWriter(file)) {
                List<CowJson> jsonModels = new ArrayList<>();

                for (CowsRegistryItem cow : allCows) {
                    CowJson m = new CowJson();
                    m.name = cow.getEntityName();
                    m.enabled = true;
                    m.bgColor = String.format("0x%06X", cow.getBgColor() & 0xFFFFFF);
                    m.fgColor = String.format("0x%06X", cow.getFgColor() & 0xFFFFFF);
                    m.spawnType = cow.getSpawnType() != null ? cow.getSpawnType()
                        .name() : "NORMAL";
                    if (cow.createMilkFluid() != null) {
                        FluidJson f = new FluidJson();
                        f.name = cow.createMilkFluid()
                            .getFluid()
                            .getName();
                        f.amount = cow.createMilkFluid().amount;
                        m.fluid = f;
                    }
                    m.lang = cow.getLang();
                    jsonModels.add(m);
                }
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                writer.write(gson.toJson(jsonModels));
            }

            Logger.info("Created default " + file.getPath());
        } catch (IOException e) {
            Logger.error("Failed to create default config: " + file.getPath() + " (" + e.getMessage() + ")");
        }
    }
}
