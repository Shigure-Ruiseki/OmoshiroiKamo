package ruiseki.omoshiroikamo.plugin.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.entity.model.ModelRegistryItem;
import ruiseki.omoshiroikamo.common.util.Logger;

public class OriginalModels extends BaseModelHandler {

    public OriginalModels() {
        super("Original", "Original Models", "model/custom/");
        this.setStartID(5000);
        this.setNeedsModPresent(false);
    }

    @Override
    public List<ModelRegistryItem> registerModels() {
        return new ArrayList<>();
    }

    @Override
    public void createDefaultConfig(File file, List<ModelRegistryItem> allModels) {
        try (Writer writer = new FileWriter(file)) {
            String defaultConfig = "// ===============================================================================\n"
                + "// This file is for custom model settings.\n"
                + "// You can add your own models by following the format below.\n"
                + "// Fields:\n"
                + "// name            : Model's internal name (used as key)\n"
                + "// texture         : Texture path (file name only, same for model and item)\n"
                + "// enable          : true/false\n"
                + "// deepLearnerDisplay: Object containing UI and mob info\n"
                + "// lootItems       : Array of items dropped by the mob\n"
                + "// lang            : Object of localization strings {lang:value}\n"
                + "// ===============================================================================\n\n"
                + "/*\n"
                + "[\n"
                + "  {\n"
                + "    \"name\": \"Creeper\",\n"
                + "    \"texture\": \"creeper\",\n"
                + "    \"enabled\": true,\n"
                + "    \"deepLearnerDisplay\": {\n"
                + "         \"numberOfHearts\": 10.0,\n"
                + "         \"interfaceScale\": 1.0,\n"
                + "         \"interfaceOffsetX\": 0,\n"
                + "         \"interfaceOffsetY\": 0,\n"
                + "         \"mobTrivia\": [\n"
                + "             \"Will blow up your base if left unattended.\"\n"
                + "         ]\n"
                + "     },\n"
                + "    \"lootItems\": [\n"
                + "      {\n"
                + "        \"name\": \"minecraft:gunpowder\",\n"
                + "        \"amount\": 64,\n"
                + "        \"meta\": 0\n"
                + "      },\n"
                + "      {\n"
                + "        \"name\": \"minecraft:skull\",\n"
                + "        \"amount\": 6,\n"
                + "        \"meta\": 4\n"
                + "      }\n"
                + "    ],\n"
                + "    \"lang\": {\n"
                + "      \"en_US\": \"§bCreeper Data Model§r\",\n"
                + "      \"ja_JP\": \"§bクリーパーデータモデル§r\"\n"
                + "    }\n"
                + "  }\n"
                + "]\n"
                + "*/\n\n"
                + "// ===============================================================================\n"
                + "[]";

            writer.write(defaultConfig);
            Logger.info("Created default {}", configFileName);
        } catch (IOException e) {
            Logger.error("Failed to create default config: {}", e.getMessage());
        }

    }
}
