package ruiseki.omoshiroikamo.config.backport;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;

@Config.Comment("Main Backport settings")
@Config.LangKey(LibResources.CONFIG + "backportConfig")
@Config(modid = LibMisc.MOD_ID, category = "backports", configSubDirectory = LibMisc.MOD_ID, filename = "backports")
public class BackportConfigs {

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableChicken;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableMultiBlock;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableCow;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableIDs;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableBackpack;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableDML;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableMachinery;

    public static void registerConfig() throws ConfigException {
        ConfigurationManager.registerConfig(BackportConfigs.class);
        MultiBlockConfigs.registerConfig();
        ConfigurationManager.registerConfig(ChickenConfig.class);
        ConfigurationManager.registerConfig(CowConfig.class);
        ConfigurationManager.registerConfig(BackpackConfig.class);
        ConfigurationManager.registerConfig(DMLConfig.class);
        ConfigurationManager.registerConfig(MachineryConfig.class);
    }
}
