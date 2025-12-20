package ruiseki.omoshiroikamo.config;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.config.block.BlockConfigs;
import ruiseki.omoshiroikamo.config.general.energy.EnergyConfig;
import ruiseki.omoshiroikamo.config.item.ItemConfigs;

@Config(modid = LibMisc.MOD_ID, configSubDirectory = LibMisc.MOD_ID, category = "general")
public class GeneralConfig {

    @Config.DefaultBoolean(false)
    public static boolean enableDebug;

    public static void registerConfig() throws ConfigException {
        ConfigurationManager.registerConfig(GeneralConfig.class);
        ItemConfigs.registerConfig();
        BlockConfigs.registerConfig();
        BackportConfigs.registerConfig();
        EnergyConfig.registerConfig();
    }
}
