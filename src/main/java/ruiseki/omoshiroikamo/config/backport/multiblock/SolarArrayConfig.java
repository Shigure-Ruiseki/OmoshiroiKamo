package ruiseki.omoshiroikamo.config.backport.multiblock;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;

@Config.Comment("Main Solar Array Settings")
@Config.LangKey(LibResources.CONFIG + "solarArrayConfig")
@Config(
    modid = LibMisc.MOD_ID,
    category = "solar",
    configSubDirectory = LibMisc.MOD_ID + "/environmentaltech",
    filename = "solararray")
public class SolarArrayConfig {

    @Config.DefaultInt(2000)
    @Config.RangeInt(min = 0)
    public static int peakEnergyTier1;

    @Config.DefaultInt(12800)
    @Config.RangeInt(min = 0)
    public static int peakEnergyTier2;

    @Config.DefaultInt(64190)
    @Config.RangeInt(min = 0)
    public static int peakEnergyTier3;

    @Config.DefaultInt(212301)
    @Config.RangeInt(min = 0)
    public static int peakEnergyTier4;

    @Config.DefaultInt(634282)
    @Config.RangeInt(min = 0)
    public static int peakEnergyTier5;

    @Config.DefaultInt(1771965)
    @Config.RangeInt(min = 0)
    public static int peakEnergyTier6;

    @Config.DefaultInt(60)
    @Config.RangeInt(min = 0)
    public static int cellGen;

    @Config.DefaultFloat(1.3f)
    @Config.RangeFloat(min = 1f)
    public static float cellMul;
}
