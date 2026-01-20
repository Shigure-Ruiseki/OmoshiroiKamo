package ruiseki.omoshiroikamo.config.backport;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;

@Config.Comment("Modular Machinery settings")
@Config.LangKey(LibResources.CONFIG + "machineryConfig")
@Config(modid = LibMisc.MOD_ID, category = "machinery", configSubDirectory = LibMisc.MOD_ID, filename = "machinery")
public class MachineryConfig {

    @Config.Comment("Default tint color for machine blocks (hex color code)")
    @Config.DefaultString("#FFFFFF")
    public static String defaultTintColor;

    /**
     * Parse the configured tint color string to an ARGB integer.
     * 
     * @return ARGB color value with full alpha, or white (0xFFFFFFFF) on parse
     *         error
     */
    public static int getDefaultTintColorInt() {
        try {
            String hex = defaultTintColor.replace("#", "");
            return (int) Long.parseLong(hex, 16) | 0xFF000000; // Add alpha channel
        } catch (Exception e) {
            return 0xFFFFFFFF; // White fallback
        }
    }
}
