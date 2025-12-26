package ruiseki.omoshiroikamo.config.backport;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

import ruiseki.omoshiroikamo.config.backport.muliblock.MultiblockWorldGenConfig;
import ruiseki.omoshiroikamo.config.backport.muliblock.QuantumBeaconConfig;
import ruiseki.omoshiroikamo.config.backport.muliblock.QuantumExtractorConfig;
import ruiseki.omoshiroikamo.config.backport.muliblock.SolarArrayConfig;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;

@Config.Comment("Main Multiblock Settings")
@Config.LangKey(LibResources.CONFIG + "multiblockConfig")
@Config(modid = LibMisc.MOD_ID, category = "general.backports.multiblock", configSubDirectory = LibMisc.MOD_ID)
public class MultiBlockConfigs {

    public static void registerConfig() throws ConfigException {
        ConfigurationManager.registerConfig(MultiBlockConfigs.class);
        ConfigurationManager.registerConfig(QuantumBeaconConfig.class);
        ConfigurationManager.registerConfig(QuantumExtractorConfig.class);
        ConfigurationManager.registerConfig(SolarArrayConfig.class);
        ConfigurationManager.registerConfig(MultiblockWorldGenConfig.class);
    }
}
