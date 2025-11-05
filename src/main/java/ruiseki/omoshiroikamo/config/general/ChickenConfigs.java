package ruiseki.omoshiroikamo.config.general;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

@Config.Comment("Main Chicken Settings")
@Config.LangKey(LibResources.CONFIG + "chickenConfig")
@Config(modid = LibMisc.MOD_ID, category = "general.chicken", configSubDirectory = LibMisc.MOD_ID)
public class ChickenConfigs {

    @Config.DefaultInt(30000)
    public static int chickenEntityId;

    @Config.DefaultInt(10)
    public static int spawnProbability;

    @Config.DefaultInt(3)
    @Config.RangeInt(min = 1)
    public static int minBroodSize;

    @Config.DefaultInt(5)
    @Config.RangeInt(min = 2)
    public static int maxBroodSize;

    @Config.DefaultFloat(1.0f)
    @Config.RangeFloat(min = 0f)
    public static int netherSpawnChanceMultiplier;

    @Config.DefaultBoolean(false)
    public static boolean alwaysShowStats;
}
