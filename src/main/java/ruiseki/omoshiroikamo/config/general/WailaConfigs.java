package ruiseki.omoshiroikamo.config.general;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

@Config.Comment("Main Waila Settings")
@Config.LangKey(LibResources.CONFIG + "wailaConfig")
@Config(modid = LibMisc.MOD_ID, category = "general.waila", configSubDirectory = LibMisc.MOD_ID)
public class WailaConfigs {

    @Config.DefaultBoolean(true)
    public static boolean showGrowTime;

    @Config.DefaultBoolean(true)
    public static boolean showBreedTime;

}
