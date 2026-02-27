package ruiseki.omoshiroikamo.config.backport;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;

@Config.Comment("Main Integrated Dynamics Settings")
@Config.LangKey(LibResources.CONFIG + "idsConfig")
@Config(modid = LibMisc.MOD_ID, category = "ids", configSubDirectory = LibMisc.MOD_ID + "/ids", filename = "ids")
public class IDsConfig {

    @Config.DefaultIntList({ -1, 1 })
    public static int[] meneglinBiomeDimensionBlacklist;
}
