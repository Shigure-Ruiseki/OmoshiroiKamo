package ruiseki.omoshiroikamo.config.backport;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;

@Config.Comment("Main Integrated Dynamics Settings")
@Config.LangKey(LibResources.CONFIG + "idsConfig")
@Config(modid = LibMisc.MOD_ID, category = "ids", configSubDirectory = LibMisc.MOD_ID + "/ids", filename = "ids")
public class IDsConfig {

    @Config.DefaultIntList({ -1, 1 })
    @Config.Comment("List of dimension IDs in which the meneglin biome should not generate.")
    public static int[] meneglinBiomeDimensionBlacklist;

    @Config.DefaultInt(5)
    @Config.Comment("The weight of spawning.")
    @Config.RangeInt(min = 0)
    public static int spawnWeight;

    @Config.DefaultInt(4)
    @Config.Comment("A 1/x chance menril berries will be dropped when breaking a leaves block.")
    @Config.RangeInt(min = 0)
    public static int berriesDropChance;

    @Config.DefaultBoolean(true)
    @Config.Comment("If the berries should give the night vision effect when eaten.")
    @Config.RequiresMcRestart
    public static boolean nightVision;
}
