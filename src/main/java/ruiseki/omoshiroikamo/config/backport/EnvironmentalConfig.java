package ruiseki.omoshiroikamo.config.backport;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

@Config.Comment("Main Environmental Tech Settings")
@Config.LangKey(LibResources.CONFIG + "environmentalConfig")
@Config(modid = LibMisc.MOD_ID, category = "general.backports.environmental", configSubDirectory = LibMisc.MOD_ID)
public class EnvironmentalConfig {

    @Config.Comment("Main Quantum Extractor Settings")
    public static final QuantumExtractorConfig quantumExtractorConfig = new QuantumExtractorConfig();

    @Config.Comment("Main Quantum Extractor Settings")
    public static final SolarArrayConfig solarArrayConfig = new SolarArrayConfig();

    @Config.Comment("Main Quantum Extractor Settings")
    public static final WorldGenConfig worldGenConfig = new WorldGenConfig();

    @Config.LangKey(LibResources.CONFIG + "quantumExtractorConfig")
    public static class QuantumExtractorConfig {

        @Config.DefaultInt(400)
        @Config.RangeInt(min = 0)
        public int tickOreTier1;

        @Config.DefaultInt(320)
        @Config.RangeInt(min = 0)
        public int tickOreTier2;

        @Config.DefaultInt(160)
        @Config.RangeInt(min = 0)
        public int tickOreTier3;

        @Config.DefaultInt(80)
        @Config.RangeInt(min = 0)
        public int tickOreTier4;

        @Config.DefaultInt(40)
        @Config.RangeInt(min = 0)
        public int tickOreTier5;

        @Config.DefaultInt(20)
        @Config.RangeInt(min = 0)
        public int tickOreTier6;

        @Config.DefaultInt(400)
        @Config.RangeInt(min = 0)
        public int tickResTier1;

        @Config.DefaultInt(320)
        @Config.RangeInt(min = 0)
        public int tickResTier2;

        @Config.DefaultInt(160)
        @Config.RangeInt(min = 0)
        public int tickResTier3;

        @Config.DefaultInt(80)
        @Config.RangeInt(min = 0)
        public int tickResTier4;

        @Config.DefaultInt(40)
        @Config.RangeInt(min = 0)
        public int tickResTier5;

        @Config.DefaultInt(20)
        @Config.RangeInt(min = 0)
        public int tickResTier6;

        @Config.DefaultInt(264000)
        @Config.RangeInt(min = 0)
        public int energyCostOreTier1;

        @Config.DefaultInt(184000)
        @Config.RangeInt(min = 0)
        public int energyCostOreTier2;

        @Config.DefaultInt(120000)
        @Config.RangeInt(min = 0)
        public int energyCostOreTier3;

        @Config.DefaultInt(72000)
        @Config.RangeInt(min = 0)
        public int energyCostOreTier4;

        @Config.DefaultInt(40000)
        @Config.RangeInt(min = 0)
        public int energyCostOreTier5;

        @Config.DefaultInt(16000)
        @Config.RangeInt(min = 0)
        public int energyCostOreTier6;

        @Config.DefaultInt(264000)
        @Config.RangeInt(min = 0)
        public int energyCostResTier1;

        @Config.DefaultInt(184000)
        @Config.RangeInt(min = 0)
        public int energyCostResTier2;

        @Config.DefaultInt(120000)
        @Config.RangeInt(min = 0)
        public int energyCostResTier3;

        @Config.DefaultInt(72000)
        @Config.RangeInt(min = 0)
        public int energyCostResTier4;

        @Config.DefaultInt(40000)
        @Config.RangeInt(min = 0)
        public int energyCostResTier5;

        @Config.DefaultInt(16000)
        @Config.RangeInt(min = 0)
        public int energyCostResTier6;
    }

    @Config.LangKey(LibResources.CONFIG + "solarArrayConfig")
    public static class SolarArrayConfig {

        @Config.DefaultInt(2000)
        @Config.RangeInt(min = 0)
        public int peakEnergyTier1;

        @Config.DefaultInt(12800)
        @Config.RangeInt(min = 0)
        public int peakEnergyTier2;

        @Config.DefaultInt(64190)
        @Config.RangeInt(min = 0)
        public int peakEnergyTier3;

        @Config.DefaultInt(212301)
        @Config.RangeInt(min = 0)
        public int peakEnergyTier4;

        @Config.DefaultInt(634282)
        @Config.RangeInt(min = 0)
        public int peakEnergyTier5;

        @Config.DefaultInt(1771965)
        @Config.RangeInt(min = 0)
        public int peakEnergyTier6;

        @Config.DefaultInt(60)
        @Config.RangeInt(min = 0)
        public int cellGen;

        @Config.DefaultFloat(1.3f)
        @Config.RangeFloat(min = 1f)
        public float cellMul;
    }

    @Config.LangKey(LibResources.CONFIG + "eTWorldGenConfig")
    public static class WorldGenConfig {

        @Config.DefaultBoolean(true)
        public boolean enableHardenedStoneGeneration;

        @Config.DefaultInt(30)
        @Config.RangeInt(min = 0)
        public int hardenedStoneNodeSize;

        @Config.DefaultInt(12)
        @Config.RangeInt(min = 0)
        public int hardenedStoneNodes;

        @Config.DefaultInt(0)
        @Config.RangeInt(min = 0)
        public int hardenedStoneMinHeight;

        @Config.DefaultInt(12)
        @Config.RangeInt(min = 0)
        public int hardenedStoneMaxHeight;

        @Config.DefaultBoolean(true)
        public boolean enableAlabasterGeneration;

        @Config.DefaultInt(22)
        @Config.RangeInt(min = 0)
        public int alabasterNodes;

        @Config.DefaultInt(30)
        @Config.RangeInt(min = 0)
        public int alabasterNodeSize;

        @Config.DefaultInt(40)
        @Config.RangeInt(min = 0)
        public int alabasterMinHeight;

        @Config.DefaultInt(200)
        @Config.RangeInt(min = 0)
        public int alabasterMaxHeight;

        @Config.DefaultBoolean(true)
        public boolean enableBasaltGeneration;

        @Config.DefaultInt(14)
        @Config.RangeInt(min = 0)
        public int basaltNodes;

        @Config.DefaultInt(28)
        @Config.RangeInt(min = 0)
        public int basaltNodeSize;

        @Config.DefaultInt(8)
        @Config.RangeInt(min = 0)
        public int basaltMinHeight;

        @Config.DefaultInt(32)
        @Config.RangeInt(min = 0)
        public int basaltMaxHeight;
    }
}
