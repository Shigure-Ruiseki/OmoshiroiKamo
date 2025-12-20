package ruiseki.omoshiroikamo.config.backport;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

@Config.Comment("Main Deep Mob Learning Settings")
@Config.LangKey(LibResources.CONFIG + "deepMobLearningConfig")
@Config(modid = LibMisc.MOD_ID, category = "general.backports.deepMobLearning", configSubDirectory = LibMisc.MOD_ID)
public class DeepMobLearningConfig {

    @Config.Comment("Main Data Model Settings")
    public static final DataModelConfig dataModelConfig = new DataModelConfig();

    @Config.DefaultBoolean(false)
    @Config.RequiresMcRestart
    public static boolean updateMissing;

    @Config.LangKey(LibResources.CONFIG + "dataModelConfig")
    public static class DataModelConfig {

        @Config.DefaultIntList({ 1, 4, 10, 18 })
        @Config.RangeInt(min = 1)
        public int[] killMultiplier;

        @Config.DefaultIntList({ 6, 12, 30, 50 })
        @Config.RangeInt(min = 1)
        public int[] killsToTier;
    }
}
