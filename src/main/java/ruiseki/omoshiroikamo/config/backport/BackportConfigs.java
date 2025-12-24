package ruiseki.omoshiroikamo.config.backport;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

@Config.Comment("Main Backport settings")
@Config.LangKey(LibResources.CONFIG + "backportConfig")
@Config(modid = LibMisc.MOD_ID, category = "general.backports", configSubDirectory = LibMisc.MOD_ID)
public class BackportConfigs {

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean useChicken;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean useEnvironmentalTech;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean useCow;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean useBackpack;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean useDML;

    public static void registerConfig() throws ConfigException {
        ConfigurationManager.registerConfig(BackportConfigs.class);
        ConfigurationManager.registerConfig(EnvironmentalConfig.class);
        ConfigurationManager.registerConfig(ChickenConfig.class);
        ConfigurationManager.registerConfig(CowConfig.class);
        ConfigurationManager.registerConfig(BackpackConfig.class);
        ConfigurationManager.registerConfig(DMLConfig.class);
    }
}
