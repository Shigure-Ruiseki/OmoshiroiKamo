package ruiseki.omoshiroikamo.api.json;

import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.common.registry.LanguageRegistry;

public class JsonUtils {

    private JsonUtils() {}

    /**
     * Registers multiple translations for a given localization key.
     *
     * @param key     the localization key (e.g., "item.model.itemName.name")
     * @param langMap an array of strings in the format "langCode": "Translation",
     *                e.g., "en_US": "My Item", "ja_JP": "アイテム"
     */
    public static void registerLang(String key, Map<String, String> langMap) {
        if (key == null || key.trim()
            .isEmpty() || langMap == null || langMap.isEmpty()) return;

        for (Map.Entry<String, String> entry : langMap.entrySet()) {
            String lang = entry.getKey();
            String value = entry.getValue();

            if (lang == null || lang.trim()
                .isEmpty()
                || value == null
                || value.trim()
                    .isEmpty())
                continue;

            LanguageRegistry.instance()
                .injectLanguage(lang, new HashMap<>() {

                    {
                        put(key, value);
                    }
                });
        }
    }

    public static int resolveColor(String hex, int def) {
        if (hex == null) return def;

        String value = hex.trim();
        if (value.isEmpty()) return def;

        try {
            if (value.startsWith("#")) {
                value = "0x" + value.substring(1);
            }
            return Integer.decode(value);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static String parseColor(int color) {
        return String.format("0x%06X", color & 0xFFFFFF);
    }
}
