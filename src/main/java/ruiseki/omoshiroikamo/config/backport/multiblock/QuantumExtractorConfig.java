package ruiseki.omoshiroikamo.config.backport.multiblock;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;

@Config.Comment("Main Quantum Extractor Settings")
@Config.LangKey(LibResources.CONFIG + "quantumExtractorConfig")
@Config(
    modid = LibMisc.MOD_ID,
    category = "extractor",
    configSubDirectory = LibMisc.MOD_ID + "/multiblock",
    filename = "extractor")
public class QuantumExtractorConfig {

    @Config.Comment("Ore Miner settings per tier")
    @Config.LangKey(LibResources.CONFIG + "oreMinerConfig")
    public static final MinerTierSettings oreMiner = new MinerTierSettings();

    @Config.Comment("Resource Miner settings per tier")
    @Config.LangKey(LibResources.CONFIG + "resMinerConfig")
    public static final MinerTierSettings resMiner = new MinerTierSettings();

    @Config.Comment("Modifier settings")
    @Config.LangKey(LibResources.CONFIG + "extractorModifiers")
    public static final ModifierSettings modifiers = new ModifierSettings();

    @Config.Comment("Additional blocks to allow in the miner's path to void (e.g., \"OmoshiroiKamo:LASER_CORE:0\")")
    @Config.DefaultStringList({})
    public static String[] pathToVoidWhitelist;

    public static class MinerTierSettings {

        // Processing ticks per tier
        @Config.Comment("Tier 1: Processing ticks per operation")
        @Config.DefaultInt(400)
        @Config.RangeInt(min = 1)
        public int tickTier1;

        @Config.Comment("Tier 2: Processing ticks per operation")
        @Config.DefaultInt(300)
        @Config.RangeInt(min = 1)
        public int tickTier2;

        @Config.Comment("Tier 3: Processing ticks per operation")
        @Config.DefaultInt(160)
        @Config.RangeInt(min = 1)
        public int tickTier3;

        @Config.Comment("Tier 4: Processing ticks per operation")
        @Config.DefaultInt(80)
        @Config.RangeInt(min = 1)
        public int tickTier4;

        @Config.Comment("Tier 5: Processing ticks per operation")
        @Config.DefaultInt(40)
        @Config.RangeInt(min = 1)
        public int tickTier5;

        @Config.Comment("Tier 6: Processing ticks per operation")
        @Config.DefaultInt(20)
        @Config.RangeInt(min = 1)
        public int tickTier6;

        // Energy cost per tier
        @Config.Comment("Tier 1: Energy cost per operation (RF)")
        @Config.DefaultInt(320000)
        @Config.RangeInt(min = 0)
        public int energyTier1;

        @Config.Comment("Tier 2: Energy cost per operation (RF)")
        @Config.DefaultInt(300000)
        @Config.RangeInt(min = 0)
        public int energyTier2;

        @Config.Comment("Tier 3: Energy cost per operation (RF)")
        @Config.DefaultInt(192000)
        @Config.RangeInt(min = 0)
        public int energyTier3;

        @Config.Comment("Tier 4: Energy cost per operation (RF)")
        @Config.DefaultInt(128000)
        @Config.RangeInt(min = 0)
        public int energyTier4;

        @Config.Comment("Tier 5: Energy cost per operation (RF)")
        @Config.DefaultInt(80000)
        @Config.RangeInt(min = 0)
        public int energyTier5;

        @Config.Comment("Tier 6: Energy cost per operation (RF)")
        @Config.DefaultInt(60000)
        @Config.RangeInt(min = 0)
        public int energyTier6;

        // Minimum tick per tier
        @Config.Comment("Tier 1: Minimum ticks (Speed Modifier limit)")
        @Config.DefaultInt(200)
        @Config.RangeInt(min = 1)
        public int minTickTier1;

        @Config.Comment("Tier 2: Minimum ticks (Speed Modifier limit)")
        @Config.DefaultInt(100)
        @Config.RangeInt(min = 1)
        public int minTickTier2;

        @Config.Comment("Tier 3: Minimum ticks (Speed Modifier limit)")
        @Config.DefaultInt(50)
        @Config.RangeInt(min = 1)
        public int minTickTier3;

        @Config.Comment("Tier 4: Minimum ticks (Speed Modifier limit)")
        @Config.DefaultInt(30)
        @Config.RangeInt(min = 1)
        public int minTickTier4;

        @Config.Comment("Tier 5: Minimum ticks (Speed Modifier limit)")
        @Config.DefaultInt(10)
        @Config.RangeInt(min = 1)
        public int minTickTier5;

        @Config.Comment("Tier 6: Minimum ticks (Speed Modifier limit)")
        @Config.DefaultInt(1)
        @Config.RangeInt(min = 1)
        public int minTickTier6;

        public int getTick(int tier) {
            switch (tier) {
                case 1:
                    return tickTier1;
                case 2:
                    return tickTier2;
                case 3:
                    return tickTier3;
                case 4:
                    return tickTier4;
                case 5:
                    return tickTier5;
                case 6:
                    return tickTier6;
                default:
                    return tickTier1;
            }
        }

        public int getEnergyCost(int tier) {
            switch (tier) {
                case 1:
                    return energyTier1;
                case 2:
                    return energyTier2;
                case 3:
                    return energyTier3;
                case 4:
                    return energyTier4;
                case 5:
                    return energyTier5;
                case 6:
                    return energyTier6;
                default:
                    return energyTier1;
            }
        }

        public int getMinTick(int tier) {
            switch (tier) {
                case 1:
                    return minTickTier1;
                case 2:
                    return minTickTier2;
                case 3:
                    return minTickTier3;
                case 4:
                    return minTickTier4;
                case 5:
                    return minTickTier5;
                case 6:
                    return minTickTier6;
                default:
                    return minTickTier1;
            }
        }
    }

    public static class ModifierSettings {

        @Config.Comment("Speed Modifier: Speed multiplier base (0.7 = 30% faster per modifier)")
        @Config.DefaultFloat(0.7f)
        @Config.RangeFloat(min = 0.1f, max = 1.0f)
        public float speedMultiplier;

        @Config.Comment("Speed Modifier: Energy cost multiplier per modifier (1.5 = 50% more energy)")
        @Config.DefaultFloat(1.5f)
        @Config.RangeFloat(min = 1.0f, max = 1000.0f)
        public float speedEnergyCost;

        @Config.Comment("Speed Modifier: Minimum speed multiplier (0.1 = processing time can be reduced to 10% at most)")
        @Config.DefaultFloat(0.1f)
        @Config.RangeFloat(min = 0.01f, max = 1.0f)
        public float speedMinMultiplier;

        @Config.Comment("Accuracy Modifier: Focus multiplier per modifier (1.18 = 18% better focus)")
        @Config.DefaultFloat(1.18f)
        @Config.RangeFloat(min = 1.0f, max = 100.0f)
        public float accuracyMultiplier;

        @Config.Comment("Accuracy Modifier: Energy cost multiplier per modifier (1.05 = 5% more energy)")
        @Config.DefaultFloat(1.05f)
        @Config.RangeFloat(min = 1.0f, max = 1000.0f)
        public float accuracyEnergyCost;

        @Config.Comment("Accuracy Modifier: Speed penalty per modifier (0.9 = 10% slower)")
        @Config.DefaultFloat(0.9f)
        @Config.RangeFloat(min = 0.1f, max = 1.0f)
        public float accuracySpeedPenalty;

        @Config.Comment("Accuracy Modifier: Maximum focus multiplier (5.0 = focus can be increased by 5x at most)")
        @Config.DefaultFloat(5.0f)
        @Config.RangeFloat(min = 1.0f, max = 100.0f)
        public float accuracyMaxMultiplier;

        @Config.Comment("Luck Modifier: Chance to get bonus output per modifier (0.1 = 10%)")
        @Config.DefaultFloat(0.1f)
        @Config.RangeFloat(min = 0.01f, max = 63.0f)
        public float luckBonusChance;

        @Config.Comment("Luck Modifier: Energy cost multiplier per modifier (1.25 = 25% more energy)")
        @Config.DefaultFloat(1.25f)
        @Config.RangeFloat(min = 1.0f, max = 1000.0f)
        public float luckEnergyCost;

        @Config.Comment("Luck Modifier: Maximum bonus chance (3.0 = 300% = guaranteed 3 bonus items at most)")
        @Config.DefaultFloat(3.0f)
        @Config.RangeFloat(min = 0.1f, max = 63.0f)
        public float luckMaxBonus;
    }
}
