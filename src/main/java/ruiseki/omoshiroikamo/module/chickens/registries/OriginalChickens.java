package ruiseki.omoshiroikamo.module.chickens.registries;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;
import ruiseki.omoshiroikamo.common.util.Logger;

public class OriginalChickens extends BaseChickenHandler {

    protected String defaultConfig = "// This file is for custom chicken settings.\n"
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
        + "    \"texture\": \"base_chicken\",\n"
        + "    \"textureOverlay\": \"base_chicken_overlay\",\n"
        + "    \"tintColor\": \"0xFF0000\",\n"
        + "    \"colorBorder\": \"0xFF0000\",\n"
        + "    \"colorCenter\": \"0x800000\",\n"
        + "    \"coefficient\": 1.0,\n"
        + "    \"layItem\": {\n"
        + "      \"ore\": \"dustRedstone\",\n"
        + "      \"amount\": 1\n"
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
        + "// ===============================================================================\n";

    public OriginalChickens() {
        super("Original", "Original Chickens", "textures/entity/chicken/original/");
        this.setStartID(5000);
        this.setNeedsModPresent(false); // Does not depend on external mods
    }

    @Override
    public List<ChickensRegistryItem> registerChickens() {
        return new ArrayList<>();
    }

    @Override
    public void registerAllParents(List<ChickensRegistryItem> allChickens) {

    }

    @Override
    public void createDefaultConfig(File file, List<ChickensRegistryItem> chickens) {
        try (Writer writer = new FileWriter(file)) {
            writer.write(defaultConfig);
            Logger.info("Created default {}", configFileName);
        } catch (IOException e) {
            Logger.error("Failed to create default config: {}", e);
        }
    }

    @Override
    public void saveJsonMigration(File file, List<ChickenJson> models) {
        try (Writer writer = new FileWriter(file)) {
            writer.write(defaultConfig);
            Gson gson = new GsonBuilder().setPrettyPrinting()
                .create();
            writer.write("\n");
            writer.write(gson.toJson(models));
            Logger.info("Migrated config with new IDs: {}", file.getName());
        } catch (IOException e) {
            Logger.error("Failed to migrate config with IDs: {}", e.getMessage());
        }
    }
}
