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
                + "// numberOfHearts  : Number of hearts the mob has\n"
                + "// interfaceScale  : UI scale (float)\n"
                + "// interfaceOffsetX: UI X offset (int)\n"
                + "// interfaceOffsetY: UI Y offset (int)\n"
                + "// mobTrivia       : Array of strings, info about the mob\n"
                + "// lang            : Array of strings 'lang:value' for localization\n"
                + "// ===============================================================================\n\n"
                + "/*\n"
                + "[\n"
                + "  {\n"
                + "    \"name\": \"Creeper\",\n"
                + "    \"texture\": \"creeper\",\n"
                + "    \"numberOfHearts\": 10.0,\n"
                + "    \"interfaceScale\": 1.0,\n"
                + "    \"interfaceOffsetX\": 0,\n"
                + "    \"interfaceOffsetY\": 0,\n"
                + "    \"mobTrivia\": [\n"
                + "      \"Will blow up your base if left unattended.\"\n"
                + "    ],\n"
                + "    \"lang\": [\n"
                + "      \"en_US:§bCreeper Data Model§r\",\n"
                + "      \"ja_JP:§bクリーパーデータモデル§r\"\n"
                + "    ]\n"
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
