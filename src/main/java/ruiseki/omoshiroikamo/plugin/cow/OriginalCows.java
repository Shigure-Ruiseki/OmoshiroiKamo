package ruiseki.omoshiroikamo.plugin.cow;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;
import ruiseki.omoshiroikamo.common.util.Logger;

public class OriginalCows extends BaseCowHandler {

    public OriginalCows() {
        super("Original", "Original Models", "cow/custom/");
        this.setStartID(5000);
        this.setNeedsModPresent(false);
    }

    @Override
    public List<CowsRegistryItem> registerCows() {
        return new ArrayList<>();
    }

    @Override
    public void createDefaultConfig(File file, List<CowsRegistryItem> allModels) {
        try (Writer writer = new FileWriter(file)) {
            String defaultConfig = "// ===============================================================================\n"
                + "// Custom Cow configuration file\n"
                + "// Each entry defines a custom fluid cow\n"
                + "// ===============================================================================\n"
                + "// Fields:\n"
                + "// name       : Cow internal name\n"
                + "// enabled    : true / false\n"
                + "// fluid      : { name, amount }\n"
                + "// bgColor    : Background color (hex)\n"
                + "// fgColor    : Foreground color (hex)\n"
                + "// spawnType  : NORMAL, HELL, etc\n"
                + "// lang       : Array of 'lang:value'\n"
                + "// ===============================================================================\n\n"
                + "/*\n"
                + "[\n"
                + "  {\n"
                + "    \"name\": \"CustomWaterCow\",\n"
                + "    \"enabled\": true,\n"
                + "    \"fluid\": {\n"
                + "      \"name\": \"water\",\n"
                + "      \"amount\": 1000\n"
                + "    },\n"
                + "    \"bgColor\": \"0xFFFFFF\",\n"
                + "    \"fgColor\": \"0xAAAAAA\",\n"
                + "    \"spawnType\": \"NORMAL\",\n"
                + "    \"lang\": [\n"
                + "      \"en_US:Water Cow\"\n"
                + "    ]\n"
                + "  }\n"
                + "]\n"
                + "*/\n\n"
                + "// ===============================================================================\n"
                + "[]";

            writer.write(defaultConfig);
            Logger.info("Created default {}", configFileName);
        } catch (IOException e) {
            Logger.error("Failed to create default config: {}" + e.getMessage());
        }

    }
}
