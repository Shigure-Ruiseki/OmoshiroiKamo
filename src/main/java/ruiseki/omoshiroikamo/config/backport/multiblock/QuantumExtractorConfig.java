package ruiseki.omoshiroikamo.config.backport.multiblock;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;

@Config.Comment("Main Quantum Extractor Settings")
@Config.LangKey(LibResources.CONFIG + "quantumExtractorConfig")
@Config(
    modid = LibMisc.MOD_ID,
    category = "extractor",
    configSubDirectory = LibMisc.MOD_ID + "/environmentaltech",
    filename = "extractor")
public class QuantumExtractorConfig {

    @Config.DefaultInt(400)
    @Config.RangeInt(min = 0)
    public static int tickOreTier1;

    @Config.DefaultInt(300)
    @Config.RangeInt(min = 0)
    public static int tickOreTier2;

    @Config.DefaultInt(160)
    @Config.RangeInt(min = 0)
    public static int tickOreTier3;

    @Config.DefaultInt(80)
    @Config.RangeInt(min = 0)
    public static int tickOreTier4;

    @Config.DefaultInt(40)
    @Config.RangeInt(min = 0)
    public static int tickOreTier5;

    @Config.DefaultInt(20)
    @Config.RangeInt(min = 0)
    public static int tickOreTier6;

    @Config.DefaultInt(400)
    @Config.RangeInt(min = 0)
    public static int tickResTier1;

    @Config.DefaultInt(300)
    @Config.RangeInt(min = 0)
    public static int tickResTier2;

    @Config.DefaultInt(160)
    @Config.RangeInt(min = 0)
    public static int tickResTier3;

    @Config.DefaultInt(80)
    @Config.RangeInt(min = 0)
    public static int tickResTier4;

    @Config.DefaultInt(40)
    @Config.RangeInt(min = 0)
    public static int tickResTier5;

    @Config.DefaultInt(20)
    @Config.RangeInt(min = 0)
    public static int tickResTier6;

    @Config.DefaultInt(320000)
    @Config.RangeInt(min = 0)
    public static int energyCostOreTier1;

    @Config.DefaultInt(300000)
    @Config.RangeInt(min = 0)
    public static int energyCostOreTier2;

    @Config.DefaultInt(192000)
    @Config.RangeInt(min = 0)
    public static int energyCostOreTier3;

    @Config.DefaultInt(128000)
    @Config.RangeInt(min = 0)
    public static int energyCostOreTier4;

    @Config.DefaultInt(80000)
    @Config.RangeInt(min = 0)
    public static int energyCostOreTier5;

    @Config.DefaultInt(60000)
    @Config.RangeInt(min = 0)
    public static int energyCostOreTier6;

    @Config.DefaultInt(320000)
    @Config.RangeInt(min = 0)
    public static int energyCostResTier1;

    @Config.DefaultInt(300000)
    @Config.RangeInt(min = 0)
    public static int energyCostResTier2;

    @Config.DefaultInt(192000)
    @Config.RangeInt(min = 0)
    public static int energyCostResTier3;

    @Config.DefaultInt(128000)
    @Config.RangeInt(min = 0)
    public static int energyCostResTier4;

    @Config.DefaultInt(80000)
    @Config.RangeInt(min = 0)
    public static int energyCostResTier5;

    @Config.DefaultInt(60000)
    @Config.RangeInt(min = 0)
    public static int energyCostResTier6;

    @Config.Comment("Minimum tick duration for Ore Miners (limits how fast Speed Modifiers can make them)")
    @Config.DefaultInt(200)
    @Config.RangeInt(min = 1)
    public static int minTickOreTier1;

    @Config.DefaultInt(100)
    @Config.RangeInt(min = 1)
    public static int minTickOreTier2;

    @Config.DefaultInt(50)
    @Config.RangeInt(min = 1)
    public static int minTickOreTier3;

    @Config.DefaultInt(30)
    @Config.RangeInt(min = 1)
    public static int minTickOreTier4;

    @Config.DefaultInt(10)
    @Config.RangeInt(min = 1)
    public static int minTickOreTier5;

    @Config.DefaultInt(1)
    @Config.RangeInt(min = 1)
    public static int minTickOreTier6;

    @Config.Comment("Minimum tick duration for Resource Miners (limits how fast Speed Modifiers can make them)")
    @Config.DefaultInt(200)
    @Config.RangeInt(min = 1)
    public static int minTickResTier1;

    @Config.DefaultInt(100)
    @Config.RangeInt(min = 1)
    public static int minTickResTier2;

    @Config.DefaultInt(50)
    @Config.RangeInt(min = 1)
    public static int minTickResTier3;

    @Config.DefaultInt(30)
    @Config.RangeInt(min = 1)
    public static int minTickResTier4;

    @Config.DefaultInt(10)
    @Config.RangeInt(min = 1)
    public static int minTickResTier5;

    @Config.DefaultInt(1)
    @Config.RangeInt(min = 1)
    public static int minTickResTier6;

    @Config.Comment("Additional blocks to allow in the miner's path to void (e.g., \"OmoshiroiKamo:LASER_CORE:0\")")
    @Config.DefaultStringList({})
    public static String[] pathToVoidWhitelist;

    @Config.Comment("Luck Modifier: Chance to get bonus output per modifier (0.1 = 10%)")
    @Config.DefaultFloat(0.1f)
    @Config.RangeFloat(min = 0.01f, max = 63.0f)
    public static float luckModifierBonusChance;

    @Config.Comment("Luck Modifier: Energy cost multiplier per modifier (1.25 = 25% more energy)")
    @Config.DefaultFloat(1.25f)
    @Config.RangeFloat(min = 1.0f, max = 1000.0f)
    public static float luckModifierEnergyCost;

    @Config.Comment("Speed Modifier: Speed multiplier base (0.7 = 30% faster per modifier)")
    @Config.DefaultFloat(0.7f)
    @Config.RangeFloat(min = 0.1f, max = 1.0f)
    public static float speedModifierMultiplier;

    @Config.Comment("Speed Modifier: Energy cost multiplier per modifier (1.5 = 50% more energy)")
    @Config.DefaultFloat(1.5f)
    @Config.RangeFloat(min = 1.0f, max = 1000.0f)
    public static float speedModifierEnergyCost;

    @Config.Comment("Accuracy Modifier: Focus multiplier per modifier (1.18 = 18% better focus)")
    @Config.DefaultFloat(1.18f)
    @Config.RangeFloat(min = 1.0f, max = 100.0f)
    public static float accuracyModifierMultiplier;

    @Config.Comment("Accuracy Modifier: Energy cost multiplier per modifier (1.05 = 5% more energy)")
    @Config.DefaultFloat(1.05f)
    @Config.RangeFloat(min = 1.0f, max = 1000.0f)
    public static float accuracyModifierEnergyCost;

    @Config.Comment("Accuracy Modifier: Speed penalty per modifier (0.9 = 10% slower)")
    @Config.DefaultFloat(0.9f)
    @Config.RangeFloat(min = 0.1f, max = 1.0f)
    public static float accuracyModifierSpeedPenalty;

    @Config.Comment("Speed Modifier: Minimum speed multiplier (0.1 = processing time can be reduced to 10% at most)")
    @Config.DefaultFloat(0.1f)
    @Config.RangeFloat(min = 0.01f, max = 1.0f)
    public static float speedModifierMinMultiplier;

    @Config.Comment("Accuracy Modifier: Maximum focus multiplier (5.0 = focus can be increased by 5x at most)")
    @Config.DefaultFloat(5.0f)
    @Config.RangeFloat(min = 1.0f, max = 100.0f)
    public static float accuracyModifierMaxMultiplier;

    @Config.Comment("Luck Modifier: Maximum bonus chance (3.0 = 300% = guaranteed 3 bonus items at most)")
    @Config.DefaultFloat(3.0f)
    @Config.RangeFloat(min = 0.1f, max = 63.0f)
    public static float luckModifierMaxBonus;
}
