package ruiseki.omoshiroikamo.config.backport.multiblock;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;

@Config.Comment("Main MultiBlock WorldGen Settings")
@Config.LangKey(LibResources.CONFIG + "eTWorldGenConfig")
@Config(
    modid = LibMisc.MOD_ID,
    category = "general.backports.multiblock.woldGen",
    configSubDirectory = LibMisc.MOD_ID + "/environmentaltech",
    filename = "worldgen")
public class MultiblockWorldGenConfig {

    @Config.DefaultBoolean(true)
    public static boolean enableHardenedStoneGeneration;

    @Config.DefaultInt(30)
    @Config.RangeInt(min = 0)
    public static int hardenedStoneNodeSize;

    @Config.DefaultInt(12)
    @Config.RangeInt(min = 0)
    public static int hardenedStoneNodes;

    @Config.DefaultInt(0)
    @Config.RangeInt(min = 0)
    public static int hardenedStoneMinHeight;

    @Config.DefaultInt(12)
    @Config.RangeInt(min = 0)
    public static int hardenedStoneMaxHeight;

    @Config.DefaultBoolean(true)
    public static boolean enableAlabasterGeneration;

    @Config.DefaultInt(22)
    @Config.RangeInt(min = 0)
    public static int alabasterNodes;

    @Config.DefaultInt(30)
    @Config.RangeInt(min = 0)
    public static int alabasterNodeSize;

    @Config.DefaultInt(40)
    @Config.RangeInt(min = 0)
    public static int alabasterMinHeight;

    @Config.DefaultInt(200)
    @Config.RangeInt(min = 0)
    public static int alabasterMaxHeight;

    @Config.DefaultBoolean(true)
    public static boolean enableBasaltGeneration;

    @Config.DefaultInt(14)
    @Config.RangeInt(min = 0)
    public static int basaltNodes;

    @Config.DefaultInt(28)
    @Config.RangeInt(min = 0)
    public static int basaltNodeSize;

    @Config.DefaultInt(8)
    @Config.RangeInt(min = 0)
    public static int basaltMinHeight;

    @Config.DefaultInt(32)
    @Config.RangeInt(min = 0)
    public static int basaltMaxHeight;
}
