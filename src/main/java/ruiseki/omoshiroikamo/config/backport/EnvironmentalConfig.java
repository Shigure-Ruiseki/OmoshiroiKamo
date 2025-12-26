package ruiseki.omoshiroikamo.config.backport;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;

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

        @Config.DefaultInt(300)
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

        @Config.DefaultInt(300)
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

        @Config.DefaultInt(320000)
        @Config.RangeInt(min = 0)
        public int energyCostOreTier1;

        @Config.DefaultInt(300000)
        @Config.RangeInt(min = 0)
        public int energyCostOreTier2;

        @Config.DefaultInt(192000)
        @Config.RangeInt(min = 0)
        public int energyCostOreTier3;

        @Config.DefaultInt(128000)
        @Config.RangeInt(min = 0)
        public int energyCostOreTier4;

        @Config.DefaultInt(80000)
        @Config.RangeInt(min = 0)
        public int energyCostOreTier5;

        @Config.DefaultInt(60000)
        @Config.RangeInt(min = 0)
        public int energyCostOreTier6;

        @Config.DefaultInt(320000)
        @Config.RangeInt(min = 0)
        public int energyCostResTier1;

        @Config.DefaultInt(300000)
        @Config.RangeInt(min = 0)
        public int energyCostResTier2;

        @Config.DefaultInt(192000)
        @Config.RangeInt(min = 0)
        public int energyCostResTier3;

        @Config.DefaultInt(128000)
        @Config.RangeInt(min = 0)
        public int energyCostResTier4;

        @Config.DefaultInt(80000)
        @Config.RangeInt(min = 0)
        public int energyCostResTier5;

        @Config.DefaultInt(60000)
        @Config.RangeInt(min = 0)
        public int energyCostResTier6;

        @Config.Comment("Minimum tick duration for Ore Miners (limits how fast Speed Modifiers can make them)")
        @Config.DefaultInt(200)
        @Config.RangeInt(min = 1)
        public int minTickOreTier1;

        @Config.DefaultInt(100)
        @Config.RangeInt(min = 1)
        public int minTickOreTier2;

        @Config.DefaultInt(50)
        @Config.RangeInt(min = 1)
        public int minTickOreTier3;

        @Config.DefaultInt(30)
        @Config.RangeInt(min = 1)
        public int minTickOreTier4;

        @Config.DefaultInt(10)
        @Config.RangeInt(min = 1)
        public int minTickOreTier5;

        @Config.DefaultInt(1)
        @Config.RangeInt(min = 1)
        public int minTickOreTier6;

        @Config.Comment("Minimum tick duration for Resource Miners (limits how fast Speed Modifiers can make them)")
        @Config.DefaultInt(200)
        @Config.RangeInt(min = 1)
        public int minTickResTier1;

        @Config.DefaultInt(100)
        @Config.RangeInt(min = 1)
        public int minTickResTier2;

        @Config.DefaultInt(50)
        @Config.RangeInt(min = 1)
        public int minTickResTier3;

        @Config.DefaultInt(30)
        @Config.RangeInt(min = 1)
        public int minTickResTier4;

        @Config.DefaultInt(10)
        @Config.RangeInt(min = 1)
        public int minTickResTier5;

        @Config.DefaultInt(1)
        @Config.RangeInt(min = 1)
        public int minTickResTier6;

        @Config.Comment("Additional blocks to allow in the miner's path to void (e.g., \"OmoshiroiKamo:LASER_CORE:0\")")
        @Config.DefaultStringList({})
        public String[] pathToVoidWhitelist;

        @Config.Comment("Luck Modifier: Chance to get bonus output per modifier (0.1 = 10%)")
        @Config.DefaultFloat(0.1f)
        @Config.RangeFloat(min = 0.01f, max = 63.0f)
        public float luckModifierBonusChance;

        @Config.Comment("Luck Modifier: Energy cost multiplier per modifier (1.25 = 25% more energy)")
        @Config.DefaultFloat(1.25f)
        @Config.RangeFloat(min = 1.0f, max = 1000.0f)
        public float luckModifierEnergyCost;

        @Config.Comment("Speed Modifier: Speed multiplier base (0.7 = 30% faster per modifier)")
        @Config.DefaultFloat(0.7f)
        @Config.RangeFloat(min = 0.1f, max = 1.0f)
        public float speedModifierMultiplier;

        @Config.Comment("Speed Modifier: Energy cost multiplier per modifier (1.5 = 50% more energy)")
        @Config.DefaultFloat(1.5f)
        @Config.RangeFloat(min = 1.0f, max = 1000.0f)
        public float speedModifierEnergyCost;

        @Config.Comment("Accuracy Modifier: Focus multiplier per modifier (1.18 = 18% better focus)")
        @Config.DefaultFloat(1.18f)
        @Config.RangeFloat(min = 1.0f, max = 100.0f)
        public float accuracyModifierMultiplier;

        @Config.Comment("Accuracy Modifier: Energy cost multiplier per modifier (1.05 = 5% more energy)")
        @Config.DefaultFloat(1.05f)
        @Config.RangeFloat(min = 1.0f, max = 1000.0f)
        public float accuracyModifierEnergyCost;

        @Config.Comment("Accuracy Modifier: Speed penalty per modifier (0.9 = 10% slower)")
        @Config.DefaultFloat(0.9f)
        @Config.RangeFloat(min = 0.1f, max = 1.0f)
        public float accuracyModifierSpeedPenalty;

        @Config.Comment("Speed Modifier: Minimum speed multiplier (0.1 = processing time can be reduced to 10% at most)")
        @Config.DefaultFloat(0.1f)
        @Config.RangeFloat(min = 0.01f, max = 1.0f)
        public float speedModifierMinMultiplier;

        @Config.Comment("Accuracy Modifier: Maximum focus multiplier (5.0 = focus can be increased by 5x at most)")
        @Config.DefaultFloat(5.0f)
        @Config.RangeFloat(min = 1.0f, max = 100.0f)
        public float accuracyModifierMaxMultiplier;

        @Config.Comment("Luck Modifier: Maximum bonus chance (3.0 = 300% = guaranteed 3 bonus items at most)")
        @Config.DefaultFloat(3.0f)
        @Config.RangeFloat(min = 0.1f, max = 63.0f)
        public float luckModifierMaxBonus;
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
