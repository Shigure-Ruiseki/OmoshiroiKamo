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
        public int tickOreTier1;

        @Config.DefaultInt(64)
        public int tickOreTier2;

        @Config.DefaultInt(16)
        public int tickOreTier3;

        @Config.DefaultInt(4)
        public int tickOreTier4;

        @Config.DefaultInt(400)
        public int tickResTier1;

        @Config.DefaultInt(64)
        public int tickResTier2;

        @Config.DefaultInt(16)
        public int tickResTier3;

        @Config.DefaultInt(4)
        public int tickResTier4;

        @Config.DefaultInt(320000)
        public int energyCostOreTier1;

        @Config.DefaultInt(160000)
        public int energyCostOreTier2;

        @Config.DefaultInt(80000)
        public int energyCostOreTier3;

        @Config.DefaultInt(40000)
        public int energyCostOreTier4;

        @Config.DefaultInt(320000)
        public int energyCostResTier1;

        @Config.DefaultInt(160000)
        public int energyCostResTier2;

        @Config.DefaultInt(80000)
        public int energyCostResTier3;

        @Config.DefaultInt(40000)
        public int energyCostResTier4;
    }

    @Config.LangKey(LibResources.CONFIG + "solarArrayConfig")
    public static class SolarArrayConfig {

        @Config.DefaultInt(720)
        public int peakEnergyTier1;

        @Config.DefaultInt(4000)
        public int peakEnergyTier2;

        @Config.DefaultInt(15680)
        public int peakEnergyTier3;

        @Config.DefaultInt(51840)
        public int peakEnergyTier4;
    }

    @Config.LangKey(LibResources.CONFIG + "eTWorldGenConfig")
    public static class WorldGenConfig {

        @Config.DefaultBoolean(true)
        public boolean enableHardenedStoneGeneration;

        @Config.DefaultInt(30)
        public int hardenedStoneNodeSize;

        @Config.DefaultInt(12)
        public int hardenedStoneNodes;

        @Config.DefaultInt(0)
        public int hardenedStoneMinHeight;

        @Config.DefaultInt(12)
        public int hardenedStoneMaxHeight;

        @Config.DefaultBoolean(true)
        public boolean enableAlabasterGeneration;

        @Config.DefaultInt(22)
        public int alabasterNodes;

        @Config.DefaultInt(30)
        public int alabasterNodeSize;

        @Config.DefaultInt(40)
        public int alabasterMinHeight;

        @Config.DefaultInt(200)
        public int alabasterMaxHeight;

        @Config.DefaultBoolean(true)
        public boolean enableBasaltGeneration;

        @Config.DefaultInt(14)
        public int basaltNodes;

        @Config.DefaultInt(28)
        public int basaltNodeSize;

        @Config.DefaultInt(8)
        public int basaltMinHeight;

        @Config.DefaultInt(32)
        public int basaltMaxHeight;
    }
}
