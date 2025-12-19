package ruiseki.omoshiroikamo.plugin.chicken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.LanguageRegistry;
import lombok.Getter;
import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistry;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;
import ruiseki.omoshiroikamo.api.json.ItemJson;
import ruiseki.omoshiroikamo.api.json.JsonUtils;
import ruiseki.omoshiroikamo.common.util.Logger;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.config.ConfigUpdater;
import ruiseki.omoshiroikamo.config.backport.ChickenConfig;
import ruiseki.omoshiroikamo.plugin.ModCompatInformation;

// Refactor base on OriginalChicken by Chlorine0808
public abstract class BaseChickenHandler {

    @Getter
    protected String modID;
    @Getter
    protected String modName;
    protected String texturesLocation;

    private int startID = 0;
    private int id = 0;
    protected String configFileName;

    private boolean needsMod = true;

    public BaseChickenHandler(String modID, String modName, String texturesLocation) {
        this.modID = modID;
        this.modName = modName;
        this.texturesLocation = texturesLocation;
        this.configFileName = modID.toLowerCase() + "_chickens.json";
    }

    public void setStartID(int startID) {
        this.startID = startID;
        this.id = startID;
    }

    public void setNeedsModPresent(boolean bool) {
        this.needsMod = bool;
    }

    public static class ChickenJson {

        Integer id;
        String name;
        boolean enabled;
        String texture;
        String textureOverlay;
        String tintColor; // Hex string e.g. "0xFF0000"
        ItemJson layItem;
        ItemJson dropItem;
        String bgColor; // Hex string
        String fgColor; // Hex string
        String spawnType;
        String parent1;
        String parent2;
        float coefficient = 1.0f;
        String[] lang;
    }

    private List<ChickenJson> loadedCustomChickens;

    public List<ChickensRegistryItem> tryRegisterChickens(List<ChickensRegistryItem> allChickens) {
        Logger.info("Looking for {} chickens...", modName);

        if (needsMod && !Loader.isModLoaded(modID)) {
            Logger.info("Skipped {} chickens → required mod \"{}\" is not loaded.", modName, modID);
            return allChickens;
        }

        Logger.info("Loading {} chickens...", modName);

        File configFile = new File("config/" + LibMisc.MOD_ID + "/chicken/" + configFileName);
        if (!configFile.exists()) {
            List<ChickensRegistryItem> defaultChickens = registerChickens();
            createDefaultConfig(configFile, defaultChickens);
        }

        if (ChickenConfig.updateMissing) {
            updateConfigWithMissing(configFile, registerChickens());
            ConfigUpdater.updateBoolean(ChickenConfig.class, "updateMissing", false);
        }

        this.id = startID;

        try (FileReader fileReader = new FileReader(configFile)) {
            JsonReader reader = new JsonReader(fileReader);
            reader.setLenient(true); // Allow comments

            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<ChickenJson>>() {}.getType();
            List<ChickenJson> customChickens = gson.fromJson(reader, listType);

            if (customChickens == null) {
                Logger.info( "{} is empty or invalid.",configFileName);
                return allChickens;
            }

            this.loadedCustomChickens = customChickens;

            for (ChickenJson data : customChickens) {
                try {
                    ItemStack layItem = ItemJson.resolveItemStack(data.layItem);
                    if (layItem == null) {
                        Logger.error("Failed to resolve lay item for custom chicken: {}", data.name);
                        continue;
                    }

                    ItemStack dropItem = ItemJson.resolveItemStack(data.dropItem);

                    int bgColor = JsonUtils.resolveColor(data.bgColor, 0xFFFFFF);
                    int fgColor = JsonUtils.resolveColor(data.fgColor, 0xFF0000);
                    int tint = JsonUtils.resolveColor(data.tintColor, 0xFFFFFF);

                    SpawnType type = SpawnType.NORMAL;
                    try {
                        if (data.spawnType != null && !data.spawnType.isEmpty()) {
                            type = SpawnType.valueOf(data.spawnType.toUpperCase());
                        }
                    } catch (IllegalArgumentException e) {
                        Logger.error("Invalid spawn type for {}: {}", data.name, data.spawnType);
                    }

                    // Migrate
                    int chickenID = (data.id != null && data.id >= 0) ? data.id : fixedID(data.name);
                    if (data.id == null || data.id < 0) {
                        data.id = chickenID;
                        saveJsonMigration(configFile, loadedCustomChickens);
                    }

                    ChickensRegistryItem chicken = addChicken(
                        data.name,
                        chickenID,
                        data.texture,
                        layItem,
                        bgColor,
                        fgColor,
                        type,
                        data.lang);

                    if (chicken != null) {
                        Logger.debug("Registering ({}) Chicken: '{}':{}:{}", this.modID, chicken.getEntityName(), chicken.getId(), layItem.getDisplayName());

                        chicken.setEnabled(data.enabled);
                        chicken.setCoefficient(data.coefficient);
                        if (dropItem != null) {
                            chicken.setDropItem(dropItem);
                        }

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

                        if (data.textureOverlay != null && !data.textureOverlay.isEmpty()) {
                            chicken.setTintColor(tint);
                            chicken.setTextureOverlay(
                                new ResourceLocation(
                                    LibResources.PREFIX_MOD + "textures/entity/chicken/custom/" + data.textureOverlay));
                        }

                        // Set Item Icons explicitly
                        // User path: assets/omoshiroikamo/textures/items/chicken/filename.png
                        // ResourceLocation expected: omoshiroikamo:chicken/filename
                        if (data.texture != null && !data.texture.isEmpty()) {
                            String iconName = data.texture;
                            if (iconName.endsWith(".png")) iconName = iconName.substring(0, iconName.length() - 4);
                            chicken.setIconName(LibResources.PREFIX_MOD + "chicken/" + iconName);
                        }

                        if (data.textureOverlay != null && !data.textureOverlay.isEmpty()) {
                            String iconOverlayName = data.textureOverlay;
                            if (iconOverlayName.endsWith(".png"))
                                iconOverlayName = iconOverlayName.substring(0, iconOverlayName.length() - 4);
                            chicken.setIconOverlayName(LibResources.PREFIX_MOD + "chicken/" + iconOverlayName);
                        }

                        ModCompatInformation.addInformation(
                            chicken.getId(),
                            new ModCompatInformation(this.getModID(), "", this.getModName()));

                        allChickens.add(chicken);
                    }

                } catch (Exception e) {
                    Logger.error("Error registering custom chicken {}", data.name, e);
                }
            }

        } catch (IOException e) {
            Logger.error("Failed to read {}: {}", configFileName, e.getMessage());
        }

        return allChickens;
    }

    public void loadParents(List<ChickensRegistryItem> allChickens) {
        if (loadedCustomChickens == null) return;

        for (ChickenJson data : loadedCustomChickens) {
            if (data.parent1 == null || data.parent1.isEmpty() || data.parent2 == null || data.parent2.isEmpty())
                continue;

            ChickensRegistryItem child = findChicken(allChickens, data.name);
            if (child == null) continue;

            ChickensRegistryItem p1 = findChicken(allChickens, data.parent1);
            ChickensRegistryItem p2 = findChicken(allChickens, data.parent2);

            if (p1 != null && p2 != null) {
                child.setParents(p1, p2);
            } else {
                Logger.error("Could not find parents for custom chicken {}: {}, {}",data.name, data.parent1, data.parent2);
            }
        }

        // Clear memory
        loadedCustomChickens = null;
    }

    public abstract List<ChickensRegistryItem> registerChickens();

    public abstract void registerAllParents(List<ChickensRegistryItem> allChickens);

    protected int nextID() {
        return this.id++;
    }

    protected int fixedID(String name) {
        int hash = (modID + ":" + name).toLowerCase()
            .hashCode();
        return startID + Math.abs(hash % (30000 - startID));
    }

    private void saveJsonMigration(File file, List<ChickenJson> chickens) {
        try (Writer writer = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting()
                .create();
            writer.write(gson.toJson(chickens));
            Logger.info("Migrated config with new IDs: {}", file.getName());
        } catch (IOException e) {
            Logger.error("Failed to migrate config with IDs: {}", e);
        }
    }

    protected ChickensRegistryItem addChicken(String chickenName, int chickenID, String texture, ItemStack layItem,
        int bgColor, int fgColor, SpawnType spawntype, String[] lang) {
        if (layItem == null || layItem.getItem() == null) {
            Logger.error("Error Registering ({}) Chicken: '{}' It's LayItem was null",this.modID , chickenName);
            return null;
        }

        return new ChickensRegistryItem(
            chickenID,
            chickenName,
            new ResourceLocation(LibMisc.MOD_ID, this.texturesLocation + texture),
            layItem,
            bgColor,
            fgColor,
            lang).setSpawnType(spawntype);
    }

    protected void setParents(ChickensRegistryItem child, Object parent1, Object parent2) {
        ChickensRegistryItem parentChicken1 = null;
        ChickensRegistryItem parentChicken2 = null;

        if (child == null || parent1 == null || parent2 == null) {
            String msg = "Setting Parents ";
            if (child == null) {
                msg += ": Child Missing";
            } else {
                msg += ": " + child.getEntityName();
            }
            if (parent1 == null) {
                msg += ": Parent 1 Missing ";
            }
            if (parent2 == null) {
                msg += ": Parent 2 Missing";
            }

            Logger.debug(msg);
            return;
        }

        if (parent1 instanceof String) {
            parentChicken1 = findChickenChickensMod((String) parent1);
        } else if (parent1 instanceof ChickensRegistryItem) {
            parentChicken1 = (ChickensRegistryItem) parent1;
        }

        if (parent2 instanceof String) {
            parentChicken2 = findChickenChickensMod((String) parent2);
        } else if (parent2 instanceof ChickensRegistryItem) {
            parentChicken2 = (ChickensRegistryItem) parent2;
        }

        if (parentChicken1 == null) {
            Logger.error("Could not find Parent 1 for {}", child.getEntityName());
            return;
        }

        if (parentChicken2 == null) {
            Logger.error("Could not find Parent 2 for {}", child.getEntityName());
            return;
        }

        child.setParents(parentChicken1, parentChicken2);

    }

    public static ChickensRegistryItem findChicken(Collection<ChickensRegistryItem> chickens, String name) {

        for (ChickensRegistryItem chicken : chickens) {
            if (chicken.getEntityName()
                .compareToIgnoreCase(name) == 0) {
                return chicken;
            }
        }

        return findChickenChickensMod(name);
    }

    // Looks for a chicken inside Chickens mod
    public static ChickensRegistryItem findChickenChickensMod(String name) {
        for (ChickensRegistryItem chicken : ChickensRegistry.INSTANCE.getItems()) {
            if (chicken.getEntityName()
                .compareToIgnoreCase(name) == 0) {
                return chicken;
            }
        }

        return null;
    }

    @Nullable
    public ItemStack getFirstOreDictionary(String oreID) {
        List<ItemStack> itemstacks = OreDictionary.getOres(oreID);
        return !itemstacks.isEmpty() ? itemstacks.get(0) : null;
    }

    private ChickenJson toChickenJson(ChickensRegistryItem chicken) {
        if (chicken == null) return null;

        ChickenJson json = new ChickenJson();
        json.id = chicken.getId();
        json.name = chicken.getEntityName();
        json.enabled = true;
        ResourceLocation tex = chicken.getTexture();
        json.texture = tex.getResourcePath()
            .substring(
                tex.getResourcePath()
                    .lastIndexOf("/") + 1);
        json.tintColor = JsonUtils.parseColor(chicken.getTintColor());
        json.bgColor = JsonUtils.parseColor(chicken.getBgColor());
        json.fgColor = JsonUtils.parseColor(chicken.getFgColor());
        json.parent1 = chicken.getParent1() != null ? chicken.getParent1()
            .getEntityName() : null;
        json.parent2 = chicken.getParent2() != null ? chicken.getParent2()
            .getEntityName() : null;
        json.spawnType = chicken.getSpawnType()
            .name();
        json.coefficient = chicken.getCoefficient();
        json.layItem = ItemJson.parseItemStack(chicken.getLayItem());
        if (chicken.getDropItem() != null) {
            json.dropItem = ItemJson.parseItemStack((chicken.getDropItem()));
        }
        json.lang = chicken.getLang();

        return json;
    }

    public void createDefaultConfig(File file, List<ChickensRegistryItem> chickens) {
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();

            List<ChickenJson> jsonList = new ArrayList<>();
            for (ChickensRegistryItem chicken : chickens) {
                ChickenJson json = toChickenJson(chicken);
                if (json != null) jsonList.add(json);
            }

            try (Writer writer = new FileWriter(file)) {
                new GsonBuilder().setPrettyPrinting()
                    .create()
                    .toJson(jsonList, writer);
                Logger.info("Created default {}", configFileName);
            }
        } catch (Exception e) {
            Logger.error("Failed to create default config: {} ({})", file.getPath(), e);
        }
    }

    private void updateConfigWithMissing(File file, List<ChickensRegistryItem> allChickens) {
        List<ChickenJson> existing = new ArrayList<>();
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                JsonReader jsonReader = new JsonReader(reader);
                jsonReader.setLenient(true);
                Type listType = new TypeToken<ArrayList<ChickenJson>>() {}.getType();
                List<ChickenJson> loaded = new Gson().fromJson(jsonReader, listType);
                if (loaded != null) existing.addAll(loaded);
            } catch (Exception e) {
                Logger.error("Failed to read existing chicken config: {}", e);
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
        }

        boolean updated = false;
        List<String> addedChickens = new ArrayList<>();
        for (ChickensRegistryItem chicken : allChickens) {
            if (chicken == null) continue;

            boolean exists = existing.stream()
                .anyMatch(c -> c.name.equalsIgnoreCase(chicken.getEntityName()));
            if (!exists) {
                ChickenJson json = toChickenJson(chicken);
                if (json != null) {
                    existing.add(json);
                    addedChickens.add(chicken.getEntityName());
                    updated = true;
                }
            }
        }

        if (updated) {
            try (Writer writer = new FileWriter(file)) {
                new GsonBuilder().setPrettyPrinting()
                    .create()
                    .toJson(existing, writer);
                Logger.info("Updated chicken config with missing chickens: {}", file.getName());
                Logger.info("Added {} chicken(s): {}", addedChickens.size(), String.join(", ", addedChickens));
            } catch (IOException e) {
                Logger.error("Failed to update chicken config: {}", e);
            }
        } else {
            Logger.info("No new chickens to add to config: {}", file.getName());
        }
    }
}
