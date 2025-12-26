package ruiseki.omoshiroikamo.config.backport;

import net.minecraft.util.MathHelper;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;

@Config.Comment("Main Chicken Settings")
@Config.LangKey(LibResources.CONFIG + "chickenConfig")
@Config(
    modid = LibMisc.MOD_ID,
    category = "chicken",
    configSubDirectory = LibMisc.MOD_ID + "/chicken",
    filename = "chicken")
public class ChickenConfig {

    @Config.DefaultBoolean(true)
    public static boolean useTrait;

    @Config.DefaultInt(30000)
    public static int chickenEntityId;

    @Config.DefaultInt(10)
    @Config.RangeInt(min = 1)
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

    @Config.DefaultInt(16)
    @Config.RangeInt(min = 1, max = 64)
    public static int chickenStackLimit;

    @Config.DefaultInt(10)
    @Config.RangeInt(min = 1)
    public static int maxGrowthStat;

    @Config.DefaultInt(10)
    @Config.RangeInt(min = 1)
    public static int maxGainStat;

    @Config.DefaultInt(10)
    @Config.RangeInt(min = 1)
    public static int maxStrengthStat;

    @Config.DefaultFloat(1.0f)
    @Config.RangeFloat(min = 1)
    public static float roostSpeed;

    @Config.DefaultBoolean(false)
    @Config.RequiresMcRestart
    public static boolean updateMissing;

    public static int getChickenStackLimit() {
        return MathHelper.clamp_int(chickenStackLimit, 1, 64);
    }

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
