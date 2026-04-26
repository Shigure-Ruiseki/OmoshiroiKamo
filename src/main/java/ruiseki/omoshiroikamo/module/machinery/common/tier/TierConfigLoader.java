package ruiseki.omoshiroikamo.module.machinery.common.tier;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

/**
 * Loads and manages tier display names from JSON config.
 * JSON is auto-generated on first run; users can freely edit it.
 * Falls back to "Tier {n}" if no entry is found.
 */
public class TierConfigLoader {

    public static final TierConfigLoader INSTANCE = new TierConfigLoader();

    private static final String DEFAULT_LOCALE = "en_US";
    private static final String CONFIG_RELATIVE_PATH = LibMisc.MOD_ID + "/modular/tiers.json";

    // tierID -> locale -> displayName
    private final Map<Integer, Map<String, String>> tierNames = new HashMap<>();
    private File configDir;

    // @formatter:off
    private static final String[][] DEFAULT_NAMES = {
        { "Standard",                            "標準的な"                  },
        { "Improved",                            "改良型の"                  },
        { "Revolutionary",                       "革命的な"                  },
        { "Exotic",                              "異形の"                    },
        { "Excellent",                           "優れた"                    },
        { "Ultimate",                            "究極の"                    },
        { "Paradox-Contained",                   "矛盾を孕んだ"              },
        { "Void Manipulating",                   "虚空を操る"                },
        { "Beyond Imaginally",                   "想像を超えた"              },
        { "Deep-Space Technical",                "深宇宙技術の"              },
        { "Optically Perfect",                   "光学的に完璧な"            },
        { "Antigravity",                         "反重力の"                  },
        { "Cosmically Best",                     "宇宙で最も優れた"          },
        { "Everlasting Guilty",                  "永遠のギルティ"            },
        { "Dimensionally Transcendent",          "次元を超越した"            },
        { "Magnetohydrodynamically Constrained", "磁気流体力学的に制御された" },
    };
    // @formatter:on

    private TierConfigLoader() {}

    public void load(File configDir) {
        this.configDir = configDir;
        File file = new File(configDir, CONFIG_RELATIVE_PATH);
        if (!file.exists()) {
            writeDefaultConfig(file);
        }
        readConfig(file);
    }

    public void reload() {
        if (configDir == null) return;
        tierNames.clear();
        load(configDir);
    }

    /**
     * Returns the display name for the given tier id and locale.
     * Falls back to en_US, then to "Tier {n}" if nothing is found.
     */
    public String getTierName(int tierId, String locale) {
        Map<String, String> locales = tierNames.get(tierId);
        if (locales == null) {
            return "Tier " + tierId;
        }
        String name = locales.get(locale);
        if (name == null) {
            name = locales.get(DEFAULT_LOCALE);
        }
        if (name == null && !locales.isEmpty()) {
            name = locales.values()
                .iterator()
                .next();
        }
        return name != null ? name : "Tier " + tierId;
    }

    private void writeDefaultConfig(File file) {
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();

            Gson gson = new GsonBuilder().setPrettyPrinting()
                .create();
            JsonArray tiersArray = new JsonArray();

            for (int i = 0; i < DEFAULT_NAMES.length; i++) {
                JsonObject tierObj = new JsonObject();
                tierObj.addProperty("id", i);

                JsonObject displayName = new JsonObject();
                displayName.addProperty("en_US", DEFAULT_NAMES[i][0]);
                displayName.addProperty("ja_JP", DEFAULT_NAMES[i][1]);
                tierObj.add("displayName", displayName);

                tiersArray.add(tierObj);
            }

            JsonObject root = new JsonObject();
            root.add("tiers", tiersArray);

            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(root, writer);
            }
            Logger.info("[{}] Generated default tier config: {}", LibMisc.MOD_ID, file.getPath());
        } catch (IOException e) {
            Logger.error("[{}] Failed to write default tier config: {}", LibMisc.MOD_ID, e.getMessage());
        }
    }

    private void readConfig(File file) {
        try (FileReader reader = new FileReader(file)) {
            JsonElement root = new JsonParser().parse(reader);
            if (!root.isJsonObject()) return;

            JsonObject rootObj = root.getAsJsonObject();
            if (!rootObj.has("tiers") || !rootObj.get("tiers")
                .isJsonArray()) return;

            for (JsonElement element : rootObj.getAsJsonArray("tiers")) {
                if (!element.isJsonObject()) continue;
                JsonObject tierObj = element.getAsJsonObject();
                if (!tierObj.has("id") || !tierObj.has("displayName")) continue;

                int id = tierObj.get("id")
                    .getAsInt();
                JsonElement displayNameEl = tierObj.get("displayName");
                if (!displayNameEl.isJsonObject()) continue;

                Map<String, String> locales = new HashMap<>();
                for (Map.Entry<String, JsonElement> entry : displayNameEl.getAsJsonObject()
                    .entrySet()) {
                    locales.put(
                        entry.getKey(),
                        entry.getValue()
                            .getAsString());
                }
                tierNames.put(id, locales);
            }
            Logger.info("[{}] Loaded tier config: {} tiers", LibMisc.MOD_ID, tierNames.size());
        } catch (Exception e) {
            Logger.error("[{}] Failed to read tier config: {}", LibMisc.MOD_ID, e.getMessage());
        }
    }
}
