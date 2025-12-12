package ruiseki.omoshiroikamo.config.backport;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

@Config.Comment("Main Cows Settings")
@Config.LangKey(LibResources.CONFIG + "cowConfig")
@Config(modid = LibMisc.MOD_ID, category = "general.backports.cow", configSubDirectory = LibMisc.MOD_ID)
public class CowConfig {

    @Config.DefaultBoolean(true)
    public static boolean useTrait;

    @Config.DefaultInt(40000)
    public static int cowEntityId;

    @Config.DefaultInt(8)
    @Config.RangeInt(min = 1)
    public static int spawnProbability;

    @Config.DefaultInt(4)
    @Config.RangeInt(min = 1)
    public static int minBroodSize;

    @Config.DefaultInt(4)
    @Config.RangeInt(min = 2)
    public static int maxBroodSize;

    @Config.DefaultFloat(1.0f)
    @Config.RangeFloat(min = 0f)
    public static int netherSpawnChanceMultiplier;

    @Config.DefaultBoolean(false)
    public static boolean alwaysShowStats;

    @Config.DefaultInt(10)
    @Config.RangeInt(min = 1)
    public static int maxGrowthStat;

    @Config.DefaultInt(10)
    @Config.RangeInt(min = 1)
    public static int maxGainStat;

    @Config.DefaultInt(10)
    @Config.RangeInt(min = 1)
    public static int maxStrengthStat;

    public static int getMaxGrowthStat() {
        return Math.max(1, maxGrowthStat);
    }

    public static int getMaxGainStat() {
        return Math.max(1, maxGainStat);
    }

    public static int getMaxStrengthStat() {
        return Math.max(1, maxStrengthStat);
    }

}
