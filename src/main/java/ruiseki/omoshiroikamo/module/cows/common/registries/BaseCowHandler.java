package ruiseki.omoshiroikamo.module.cows.common.registries;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.common.Loader;
import lombok.Getter;
import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;
import ruiseki.omoshiroikamo.config.ConfigUpdater;
import ruiseki.omoshiroikamo.config.backport.CowConfig;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.integration.ModCompatInformation;
import ruiseki.omoshiroikamo.core.json.FluidJson;
import ruiseki.omoshiroikamo.core.json.JsonUtils;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

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
    protected final List<CowsRegistryItem> registeredCows = new ArrayList<>();

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

    private List<CowMaterial> loadedCustomMaterials;

    public List<CowsRegistryItem> tryRegisterCows(List<CowsRegistryItem> allCows) {
        Logger.info("Looking for {} cows...", modName);

        if (needsMod && !Loader.isModLoaded(modID)) {
            Logger.info("Skipped {} cows → required mod \"{}\" is not loaded.", modName, modID);
            return allCows;
        }

        Logger.info("Loading {} cows...", modName);

        File configDir = new File("config/" + LibMisc.MOD_ID + "/cow/");
        File configFile = new File(configDir, configFileName);
        if (!configFile.exists()) {
            List<CowsRegistryItem> defaultCows = registerCows();
            Logger.info("Generated {} default cows for {}", defaultCows.size(), configFileName);
            createDefaultConfig(configFile, defaultCows);
        }

        if (CowConfig.updateMissing) {
            updateConfigWithMissing(configFile, registerCows());
            ConfigUpdater.updateBoolean(CowConfig.class, "updateMissing", false);
        }

        this.id = startID;

        try {
            CowJsonReader reader = new CowJsonReader(configFile);
            List<CowMaterial> materials = reader.read();
            Logger.info("Read {} materials from {}", materials != null ? materials.size() : 0, configFileName);

            if (materials == null || materials.isEmpty()) {
                Logger.info("{} is empty or invalid.", configFileName);
                return allCows;
            }

            this.loadedCustomMaterials = materials;
            boolean migrated = false;

            for (CowMaterial data : materials) {
                try {
                    if (!data.validate()) continue;

                    FluidStack milk = FluidJson.resolveFluidStack(data.fluid);
                    if (milk == null) {
                        Logger.error("Error registering ({}) Cow '{}' : fluid was null", this.modID, data.name);
                        continue;
                    }

                    int bgColor = JsonUtils.resolveColor(data.bgColor, 0xFFFFFF);
                    int fgColor = JsonUtils.resolveColor(data.fgColor, 0xAAAAAA);

                    SpawnType type = SpawnType.NORMAL;
                    try {
                        if (data.spawnType != null && !data.spawnType.isEmpty()) {
                            type = SpawnType.valueOf(data.spawnType.toUpperCase());
                        }
                    } catch (IllegalArgumentException e) {
                        Logger.error("Invalid spawn type for cow {}: {}", data.name, data.spawnType);
                    }

                    // Migrate
                    if (data.id == null || data.id < 0) {
                        data.id = fixedID(data.name);
                        migrated = true;
                    }

                    CowsRegistryItem cow = addCow(data.name, data.id, bgColor, fgColor, type);

                    if (cow != null) {
                        Logger.debug("Registering ({}) Cow '{}'", this.modID, data.name);

                        cow.setEnabled(data.enabled);
                        cow.setFluid(milk);
                        cow.setFluidString(data.fluid.name);

                        if (data.tintColor != null) {
                            cow.setTintColor(JsonUtils.resolveColor(data.tintColor, 0xFFFFFF));
                        }
                        if (data.textureOverlay != null) {
                            cow.setTextureOverlay(new ResourceLocation(data.textureOverlay));
                        }

                        if (data.lang != null) {
                            String langKey = "entity." + data.name + ".name";
                            JsonUtils.registerLang(langKey, data.lang);

                            // Also set lang on the cow object so getLang() returns the correct data
                            for (Map.Entry<String, String> entry : data.lang.entrySet()) {
                                cow.setLang(entry.getKey(), entry.getValue());
                            }
                        }

                        ModCompatInformation.addInformation(
                            cow.getId(),
                            new ModCompatInformation(this.getModID(), "", this.getModName()));

                        allCows.add(cow);
                        registeredCows.add(cow);
                    }

                } catch (Exception e) {
                    Logger.error("Error registering cow {}", data.name, e);
                }
            }

            if (migrated) performMigration(configFile, materials);

            this.loadedCustomMaterials = null;
        } catch (IOException e) {
            Logger.error("Failed to read {}: {}", configFileName, e.getMessage());
        }

        return allCows;
    }

    protected void performMigration(File file, List<CowMaterial> materials) {
        try {
            new CowJsonWriter(file).write(materials);
            Logger.info("Migrated config with new IDs: {}", file.getName());
        } catch (IOException e) {
            Logger.error("Failed to migrate config with IDs: {}", e.getMessage());
        }
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

    protected CowsRegistryItem addCow(String cowName, int cowID, int bgColor, int fgColor, SpawnType spawntype) {

        return new CowsRegistryItem(
            cowID,
            cowName,
            new ResourceLocation("minecraft", "textures/entity/cow/cow.png"),
            bgColor,
            fgColor).setSpawnType(spawntype);
    }

    private CowMaterial toCowMaterial(CowsRegistryItem cow) {
        if (cow == null) return null;

        CowMaterial mat = new CowMaterial();
        mat.id = cow.getId();
        mat.name = cow.getEntityName();
        mat.enabled = true;
        mat.bgColor = String.format("0x%06X", cow.getBgColor() & 0xFFFFFF);
        mat.fgColor = String.format("0x%06X", cow.getFgColor() & 0xFFFFFF);
        mat.tintColor = String.format("0x%06X", cow.getTintColor() & 0xFFFFFF);
        if (cow.getTextureOverlay() != null) {
            mat.textureOverlay = cow.getTextureOverlay()
                .toString();
        }
        mat.spawnType = cow.getSpawnType() != null ? cow.getSpawnType()
            .name() : "NORMAL";

        if (cow.getFluid() != null) {
            mat.fluid = FluidJson.parseFluidStack(cow.getFluid());
        } else if (cow.getFluidString() != null) {
            mat.fluid = FluidJson.parseFluidString(cow.getFluidString());
        }

        mat.lang = cow.getLang();
        return mat;
    }

    public void createDefaultConfig(File file, List<CowsRegistryItem> allCows) {
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();

            List<CowMaterial> materials = new ArrayList<>();
            for (CowsRegistryItem cow : allCows) {
                CowMaterial mat = toCowMaterial(cow);
                if (mat != null) materials.add(mat);
            }

            new CowJsonWriter(file).write(materials);
            Logger.info("Created default {}", file.getPath());
        } catch (Exception e) {
            Logger.error("Failed to create default config: {} ({})", file.getPath(), e.getMessage());
        }
    }

    private void updateConfigWithMissing(File file, List<CowsRegistryItem> allCows) {
        List<CowMaterial> existing = new ArrayList<>();

        if (file.exists()) {
            try {
                existing.addAll(new CowJsonReader(file).read());
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
                CowMaterial mat = toCowMaterial(cow);
                if (mat != null) {
                    existing.add(mat);
                    addedCows.add(cow.getEntityName());
                    updated = true;
                }
            }
        }

        if (updated) {
            try {
                new CowJsonWriter(file).write(existing);
                Logger.info("Updated cow config with missing cows: {}", file.getName());
                Logger.info("Added {} cow(s): {}", addedCows.size(), String.join(", ", addedCows));
            } catch (IOException e) {
                Logger.error("Failed to update cow config: {}", e.getMessage());
            }
        } else {
            Logger.info("No new cows to add to config: {}", file.getName());
        }
    }

    /**
     * Synchronizes the current memory state back to the config file.
     * This is useful when colors are dynamically updated after loading.
     */
    public void syncConfig() {
        if (registeredCows.isEmpty()) return;

        File configDir = new File("config/" + LibMisc.MOD_ID + "/cow/");
        File configFile = new File(configDir, configFileName);

        try {
            List<CowMaterial> materials = new ArrayList<>();
            for (CowsRegistryItem cow : registeredCows) {
                CowMaterial mat = toCowMaterial(cow);
                if (mat != null) {
                    materials.add(mat);
                }
            }
            new CowJsonWriter(configFile).write(materials);
            Logger.info("Synchronized configuration for {} cows in {}", registeredCows.size(), configFileName);
        } catch (Exception e) {
            Logger.error("Failed to synchronize config {}: {}", configFileName, e.getMessage());
        }
    }
}
