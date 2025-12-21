package ruiseki.omoshiroikamo.config.backport;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

@Config.Comment("Main Deep Mob Learning Settings")
@Config.LangKey(LibResources.CONFIG + "deepMobLearningConfig")
@Config(modid = LibMisc.MOD_ID, category = "general.backports.deepMobLearning", configSubDirectory = LibMisc.MOD_ID)
public class DeepMobLearningConfig {

    @Config.DefaultBoolean(false)
    @Config.RequiresMcRestart
    public static boolean updateMissing;

    @Config.DefaultInt(256)
    public static int lootFabricatorRfCost;

    @Config.DefaultInt(51)
    public static int lootFabricatorPrecessingTime;
}
