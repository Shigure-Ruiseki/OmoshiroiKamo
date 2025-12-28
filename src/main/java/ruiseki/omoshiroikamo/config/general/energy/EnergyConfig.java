package ruiseki.omoshiroikamo.config.general.energy;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;

@Config.Comment("Main Energy settings")
@Config.LangKey(LibResources.CONFIG + "energyConfig")
@Config(modid = LibMisc.MOD_ID, category = "general.energy", configSubDirectory = LibMisc.MOD_ID)
public class EnergyConfig {

    public static void registerConfig() throws ConfigException {
        ConfigurationManager.registerConfig(EnergyConfig.class);
    }

    @Config.DefaultBoolean(true)
    public static boolean ic2Capability;

    @Config.DefaultInt(4)
    public static int ic2SinkTier;

    @Config.DefaultInt(4)
    public static int ic2SourceTier;

    @Config.DefaultInt(4)
    public static int rftToEU;
}
