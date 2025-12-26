package ruiseki.omoshiroikamo.config.backport.multiblock;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;

@Config.Comment("Main Solar Array Settings")
@Config.LangKey(LibResources.CONFIG + "solarArrayConfig")
@Config(
    modid = LibMisc.MOD_ID,
    category = "solar",
    configSubDirectory = LibMisc.MOD_ID + "/multiblock",
    filename = "solararray")
public class SolarArrayConfig {

    @Config.Comment("Peak energy output per tier (RF/t)")
    @Config.LangKey(LibResources.CONFIG + "solarTierEnergy")
    public static final TierEnergy tierEnergy = new TierEnergy();

    @Config.Comment("Solar cell generation settings")
    @Config.LangKey(LibResources.CONFIG + "solarCellSettings")
    public static final CellSettings cellSettings = new CellSettings();

    public static class TierEnergy {

        @Config.Comment("Tier 1 peak energy output (RF/t)")
        @Config.DefaultInt(2000)
        @Config.RangeInt(min = 0)
        public int tier1;

        @Config.Comment("Tier 2 peak energy output (RF/t)")
        @Config.DefaultInt(12800)
        @Config.RangeInt(min = 0)
        public int tier2;

        @Config.Comment("Tier 3 peak energy output (RF/t)")
        @Config.DefaultInt(64190)
        @Config.RangeInt(min = 0)
        public int tier3;

        @Config.Comment("Tier 4 peak energy output (RF/t)")
        @Config.DefaultInt(212301)
        @Config.RangeInt(min = 0)
        public int tier4;

        @Config.Comment("Tier 5 peak energy output (RF/t)")
        @Config.DefaultInt(634282)
        @Config.RangeInt(min = 0)
        public int tier5;

        @Config.Comment("Tier 6 peak energy output (RF/t)")
        @Config.DefaultInt(1771965)
        @Config.RangeInt(min = 0)
        public int tier6;

        public int get(int tier) {
            switch (tier) {
                case 1:
                    return tier1;
                case 2:
                    return tier2;
                case 3:
                    return tier3;
                case 4:
                    return tier4;
                case 5:
                    return tier5;
                case 6:
                    return tier6;
                default:
                    return tier1;
            }
        }
    }

    public static class CellSettings {

        @Config.Comment("Base energy generation per cell (RF/t)")
        @Config.DefaultInt(60)
        @Config.RangeInt(min = 0)
        public int baseGeneration;

        @Config.Comment("Multiplier per cell tier")
        @Config.DefaultFloat(1.3f)
        @Config.RangeFloat(min = 1f)
        public float tierMultiplier;
    }
}
