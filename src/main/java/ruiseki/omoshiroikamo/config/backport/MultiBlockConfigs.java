package ruiseki.omoshiroikamo.config.backport;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

import ruiseki.omoshiroikamo.config.backport.multiblock.MultiblockWorldGenConfig;
import ruiseki.omoshiroikamo.config.backport.multiblock.QuantumBeaconConfig;
import ruiseki.omoshiroikamo.config.backport.multiblock.QuantumExtractorConfig;
import ruiseki.omoshiroikamo.config.backport.multiblock.SolarArrayConfig;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;

@Config.Comment("Main Multiblock Settings")
@Config.LangKey(LibResources.CONFIG + "multiblockConfig")
@Config(modid = LibMisc.MOD_ID, configSubDirectory = LibMisc.MOD_ID, category = "multiblock")
public class MultiBlockConfigs {

    public static void registerConfig() throws ConfigException {
        ConfigurationManager.registerConfig(QuantumBeaconConfig.class);
        ConfigurationManager.registerConfig(QuantumExtractorConfig.class);
        ConfigurationManager.registerConfig(SolarArrayConfig.class);
        ConfigurationManager.registerConfig(MultiblockWorldGenConfig.class);
    }
}
