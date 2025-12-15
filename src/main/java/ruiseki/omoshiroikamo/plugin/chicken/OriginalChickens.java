package ruiseki.omoshiroikamo.plugin.chicken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.LanguageRegistry;
import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;
import ruiseki.omoshiroikamo.common.util.Logger;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.plugin.ModCompatInformation;

public class OriginalChickens extends BaseChickenHandler {

    private static final String CONFIG_FILE_NAME = "custom_chickens.json";
    private static final int DEFAULT_TINT_COLOR = 0xFFFFFF;

    public OriginalChickens() {
        super("Original", "Original Chickens", "textures/entity/chicken/custom/");
        this.setStartID(5000);
        this.setNeedsModPresent(false); // Does not depend on external mods
    }

    private static class CustomChickenData {

        String name;
        String texture;
        String textureOverlay;
        String tintColor; // Hex string e.g. "0xFF0000"
        CustomItemData layItem;
        CustomItemData dropItem;
        String colorBorder; // Hex string
        String colorCenter; // Hex string
        String spawnType;
        String parent1;
        String parent2;
        boolean enabled = true;
        float coefficient = 1.0f;
        java.util.Map<String, String> lang;
    }

    private static class CustomItemData {

        String name;
        int amount;
        int meta;
    }

    // Temporary storage for parent resolving
    private List<CustomChickenData> loadedCustomChickens = new ArrayList<>();

    @Override
    public List<ChickensRegistryItem> registerChickens(List<ChickensRegistryItem> allChickens) {
        File configFile = new File("config/OmoshiroiKamo/chicken", CONFIG_FILE_NAME);

        if (!configFile.exists()) {
            createDefaultConfig(configFile);
        }

        try (FileReader fileReader = new FileReader(configFile)) {
            JsonReader reader = new JsonReader(fileReader);
            reader.setLenient(true); // Allow comments

            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<CustomChickenData>>() {}.getType();
            List<CustomChickenData> customChickens = gson.fromJson(reader, listType);

            if (customChickens == null) {
                Logger.info("custom_chickens.json is empty or invalid.");
                return allChickens;
            }

            this.loadedCustomChickens = customChickens;

            for (CustomChickenData data : customChickens) {
                try {
                    ItemStack layItem = resolveItemStack(data.layItem);
                    if (layItem == null) {
                        Logger.error("Failed to resolve lay item for custom chicken: " + data.name);
                        continue;
                    }

                    ItemStack dropItem = resolveItemStack(data.dropItem);

                    int bgColor = parseColor(data.colorCenter, 0xFFFFFF);
                    int fgColor = parseColor(data.colorBorder, 0xFF0000);
                    int tint = parseColor(data.tintColor, DEFAULT_TINT_COLOR);

                    SpawnType type = SpawnType.NORMAL;
                    try {
                        if (data.spawnType != null && !data.spawnType.isEmpty()) {
                            type = SpawnType.valueOf(data.spawnType.toUpperCase());
                        }
                    } catch (IllegalArgumentException e) {
                        Logger.error("Invalid spawn type for " + data.name + ": " + data.spawnType);
                    }

                    ChickensRegistryItem chicken = addChicken(
                        allChickens,
                        data.name,
                        this.nextID(),
                        data.texture,
                        layItem,
                        bgColor,
                        fgColor,
                        type);

                    if (chicken != null) {
                        chicken.setEnabled(data.enabled);
                        chicken.setCoefficient(data.coefficient);
                        if (dropItem != null) {
                            chicken.setDropItem(dropItem);
                        }

                        // Register Localization
                        String locKey = "entity." + data.name + ".name";
                        // Register 'name' as en_US default
                        LanguageRegistry.instance()
                            .addStringLocalization(locKey, "en_US", data.name);

                        // Register other languages if present
                        if (data.lang != null) {
                            for (Entry<String, String> entry : data.lang.entrySet()) {
                                LanguageRegistry.instance()
                                    .addStringLocalization(locKey, entry.getKey(), entry.getValue());
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
                    }

                } catch (Exception e) {
                    Logger.error("Error registering custom chicken " + data.name + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            Logger.error("Failed to read " + CONFIG_FILE_NAME + ": " + e.getMessage());
        }

        return allChickens;
    }

    @Override
    public void registerAllParents(List<ChickensRegistryItem> allChickens) {
        if (loadedCustomChickens == null) return;

        for (CustomChickenData data : loadedCustomChickens) {
            if (data.parent1 == null || data.parent2 == null) continue;

            ChickensRegistryItem child = findChicken(allChickens, data.name);
            if (child == null) continue;

            ChickensRegistryItem p1 = findChicken(allChickens, data.parent1);
            ChickensRegistryItem p2 = findChicken(allChickens, data.parent2);

            if (p1 != null && p2 != null) {
                child.setParents(p1, p2);
            } else {
                Logger.error(
                    "Could not find parents for custom chicken " + data.name
                        + ": "
                        + data.parent1
                        + ", "
                        + data.parent2);
            }
        }

        // Clear memory
        loadedCustomChickens.clear();
    }

    private void createDefaultConfig(File file) {
        try (Writer writer = new FileWriter(file)) {
            String defaultConfig = "// This file is for custom chicken settings.\n"
                + "// You can add original chicken by writing format below.\n"
                + "// name : Chicken's name\n"
                + "// texture : Texture path. Same name file for model and item\n"
                + "// overlay Texture : If you use auto generated texture, write default. Same name file for model and item\n"
                + "// tintcolor : Color that base texture is tinted. Same color for model and item\n"
                + "// color border : Spawn egg color\n"
                + "// color center : Spawn egg color\n"
                + "// coefficient : Scale time to lay an egg. 0.01 ~ 100\n"
                + "// lay item : Item that chicken produce. Amount is 1 ~ 64\n"
                + "// drop item : Item that chicken drops when killed. Amount is 1 ~ 64\n"
                + "// enabled : Enable or disable\n"
                + "// parent : Parent chickens name. Empty if it's base chicken.\n"
                + "// spawn type : Choose NONE, NORMAL, SNOW, HELL\n"
                + "// lang : If you use language other than english, you can add\n"
                + "// ===============================================================================\n"
                + "/*\n"
                + "[\n"
                + "  {\n"
                + "    \"name\": \"ExampleRedChicken\",\n"
                + "    \"texture\": \"base_chicken.png\",\n"
                + "    \"textureOverlay\": \"base_chicken_overlay.png\",\n"
                + "    \"tintColor\": \"0xFF0000\",\n"
                + "    \"colorBorder\": \"0xFF0000\",\n"
                + "    \"colorCenter\": \"0x800000\",\n"
                + "    \"coefficient\": 1.0,\n"
                + "    \"layItem\": {\n"
                + "      \"name\": \"minecraft:redstone\",\n"
                + "      \"amount\": 1,\n"
                + "      \"meta\": 0\n"
                + "    },\n"
                + "    \"dropItem\": {\n"
                + "      \"name\": \"minecraft:redstone\",\n"
                + "      \"amount\": 1,\n"
                + "      \"meta\": 0\n"
                + "    },\n"
                + "    \"enabled\": true,\n"
                + "    \"parent1\": \"WhiteChicken\",\n"
                + "    \"parent2\": \"RedChicken\",\n"
                + "    \"spawnType\": \"NORMAL\",\n"
                + "    \"lang\": {\n"
                + "      \"en_US\": \"Red Chicken\"\n"
                + "    }\n"
                + "  }\n"
                + "]\n"
                + "*/\n"
                + "// ===============================================================================\n"
                + "[\n"
                + "]";
            writer.write(defaultConfig);
            Logger.info("Created default " + CONFIG_FILE_NAME);
        } catch (IOException e) {
            Logger.error("Failed to create default config: " + e.getMessage());
        }
    }

    private ItemStack resolveItemStack(CustomItemData data) {
        if (data == null || data.name == null) return null;
        Item item = GameData.getItemRegistry()
            .getObject(data.name);
        if (item == null) return null;
        return new ItemStack(item, data.amount > 0 ? data.amount : 1, data.meta);
    }

    private int parseColor(String hex, int def) {
        if (hex == null || hex.isEmpty()) return def;
        try {
            return Integer.decode(hex);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static class OriginalChickensRegistryItem extends ChickensRegistryItem {

        public OriginalChickensRegistryItem(int id, String entityName, ResourceLocation texture, ItemStack layItem,
            int bgColor, int fgColor) {
            super(id, entityName, texture, layItem, bgColor, fgColor);
        }
    }

    @Override
    protected ChickensRegistryItem addChicken(List<ChickensRegistryItem> chickenList, String chickenName, int chickenID,
        String texture, ItemStack layItem, int bgColor, int fgColor, SpawnType spawntype) {
        if (layItem == null || layItem.getItem() == null) {
            Logger.error("Error Registering (" + this.modID + ") Chicken: '" + chickenName + "' It's LayItem was null");
            return null;
        }

        Logger.debug(
            "Registering (" + this.modID
                + ") Chicken: '"
                + chickenName
                + "':"
                + chickenID
                + ":"
                + layItem.getDisplayName());

        ChickensRegistryItem chicken = new OriginalChickensRegistryItem(
            chickenID,
            chickenName,
            new ResourceLocation(LibMisc.MOD_ID, this.texturesLocation + texture),
            layItem.copy(),
            bgColor,
            fgColor).setSpawnType(spawntype);

        chickenList.add(chicken);

        ModCompatInformation
            .addInformation(chickenID, new ModCompatInformation(this.getModID(), "", this.getModName()));

        return chicken;
    }
}
