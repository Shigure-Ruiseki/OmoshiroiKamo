package ruiseki.omoshiroikamo.config.backport.multiblock;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;

@Config.Comment("Quantum Beacon Effect Level Settings")
@Config.LangKey(LibResources.CONFIG + "quantumBeaconConfig")
@Config(
    modid = LibMisc.MOD_ID,
    category = "beacon",
    configSubDirectory = LibMisc.MOD_ID + "/environmentaltech",
    filename = "beacon")
public class QuantumBeaconConfig {

    @Config.Comment("If true, the beacon requires clear sky above to function and show beam")
    @Config.DefaultBoolean(true)
    public static boolean requireSky;

    @Config.Comment("If true, the beacon will render a beam when formed and active")
    @Config.DefaultBoolean(true)
    public static boolean enableBeam;

    @Config.Comment("Beacon Modifier Energy Cost Settings")
    public static final ModifierEnergyCost modifierEnergyCost = new ModifierEnergyCost();

    @Config.LangKey(LibResources.CONFIG + "modifierEnergyCost")
    public static class ModifierEnergyCost {

        @Config.Comment("Flight Modifier: Energy cost per tick (RF)")
        @Config.DefaultInt(2048)
        @Config.RangeInt(min = 0, max = 1000000)
        public int flightEnergyCost;

        @Config.Comment("Haste Modifier: Energy cost per tick (RF)")
        @Config.DefaultInt(512)
        @Config.RangeInt(min = 0, max = 1000000)
        public int hasteEnergyCost;

        @Config.Comment("Regeneration Modifier: Energy cost per tick (RF)")
        @Config.DefaultInt(1024)
        @Config.RangeInt(min = 0, max = 1000000)
        public int regenerationEnergyCost;

        @Config.Comment("Jump Boost Modifier: Energy cost per tick (RF)")
        @Config.DefaultInt(256)
        @Config.RangeInt(min = 0, max = 1000000)
        public int jumpBoostEnergyCost;

        @Config.Comment("Night Vision Modifier: Energy cost per tick (RF)")
        @Config.DefaultInt(256)
        @Config.RangeInt(min = 0, max = 1000000)
        public int nightVisionEnergyCost;

        @Config.Comment("Resistance Modifier: Energy cost per tick (RF)")
        @Config.DefaultInt(512)
        @Config.RangeInt(min = 0, max = 1000000)
        public int resistanceEnergyCost;

        @Config.Comment("Saturation Modifier: Energy cost per tick (RF)")
        @Config.DefaultInt(2048)
        @Config.RangeInt(min = 0, max = 1000000)
        public int saturationEnergyCost;

        @Config.Comment("Strength Modifier: Energy cost per tick (RF)")
        @Config.DefaultInt(256)
        @Config.RangeInt(min = 0, max = 1000000)
        public int strengthEnergyCost;

        @Config.Comment("Water Breathing Modifier: Energy cost per tick (RF)")
        @Config.DefaultInt(256)
        @Config.RangeInt(min = 0, max = 1000000)
        public int waterBreathingEnergyCost;

        @Config.Comment("Fire Resistance Modifier: Energy cost per tick (RF)")
        @Config.DefaultInt(256)
        @Config.RangeInt(min = 0, max = 1000000)
        public int fireResistanceEnergyCost;
    }

    @Config.Comment("Beacon Effect Range Settings for each Tier")
    public static final BeaconTierRangeConfig range1 = new BeaconTierRangeConfig(30, 512, 20);
    public static final BeaconTierRangeConfig range2 = new BeaconTierRangeConfig(50, 512, 30);
    public static final BeaconTierRangeConfig range3 = new BeaconTierRangeConfig(70, 512, 40);
    public static final BeaconTierRangeConfig range4 = new BeaconTierRangeConfig(90, 512, 50);
    public static final BeaconTierRangeConfig range5 = new BeaconTierRangeConfig(110, 512, 60);
    public static final BeaconTierRangeConfig range6 = new BeaconTierRangeConfig(130, 512, 70);

    @Config.LangKey(LibResources.CONFIG + "beaconTierRangeConfig")
    public static class BeaconTierRangeConfig {

        @Config.Comment("Horizontal range (blocks) - X and Z direction")
        @Config.RangeInt(min = 0, max = 1000)
        public int horizontalRange;

        @Config.Comment("Upward range (blocks) - positive Y direction")
        @Config.RangeInt(min = 0, max = 1000)
        public int upwardRange;

        @Config.Comment("Downward range (blocks) - negative Y direction")
        @Config.RangeInt(min = 0, max = 1000)
        public int downwardRange;

        public BeaconTierRangeConfig() {
            this(30, 512, 20);
        }

        public BeaconTierRangeConfig(int horizontalRange, int upwardRange, int downwardRange) {
            this.horizontalRange = horizontalRange;
            this.upwardRange = upwardRange;
            this.downwardRange = downwardRange;
        }
    }

    /**
     * Get the range config for a given tier
     */
    public static BeaconTierRangeConfig getRangeConfig(int tier) {
        switch (tier) {
            case 1:
                return range1;
            case 2:
                return range2;
            case 3:
                return range3;
            case 4:
                return range4;
            case 5:
                return range5;
            case 6:
                return range6;
            default:
                return range1;
        }
    }

    @Config.Comment("Tier 1 Beacon Effect Level Limits")
    public static final BeaconTierConfig tier1 = new BeaconTierConfig(
        0, // saturation
        1, // speed
        1, // haste
        1, // strength
        1, // regeneration
        1, // resistance
        1, // fireResistance
        1, // waterBreathing
        1, // nightVision
        1 // jumpBoost
    );

    @Config.Comment("Tier 2 Beacon Effect Level Limits")
    public static final BeaconTierConfig tier2 = new BeaconTierConfig(
        0, // saturation
        2, // speed
        2, // haste
        2, // strength
        1, // regeneration
        1, // resistance
        1, // fireResistance
        1, // waterBreathing
        1, // nightVision
        2 // jumpBoost
    );

    @Config.Comment("Tier 3 Beacon Effect Level Limits")
    public static final BeaconTierConfig tier3 = new BeaconTierConfig(
        1, // saturation
        3, // speed
        3, // haste
        3, // strength
        2, // regeneration
        2, // resistance
        1, // fireResistance
        1, // waterBreathing
        1, // nightVision
        3 // jumpBoost
    );

    @Config.Comment("Tier 4 Beacon Effect Level Limits")
    public static final BeaconTierConfig tier4 = new BeaconTierConfig(
        2, // saturation
        4, // speed
        4, // haste
        3, // strength
        2, // regeneration
        3, // resistance
        1, // fireResistance
        1, // waterBreathing
        1, // nightVision
        4 // jumpBoost
    );

    @Config.Comment("Tier 5 Beacon Effect Level Limits")
    public static final BeaconTierConfig tier5 = new BeaconTierConfig(
        3, // saturation
        5, // speed
        5, // haste
        4, // strength
        3, // regeneration
        4, // resistance
        1, // fireResistance
        1, // waterBreathing
        1, // nightVision
        5 // jumpBoost
    );

    @Config.Comment("Tier 6 Beacon Effect Level Limits")
    public static final BeaconTierConfig tier6 = new BeaconTierConfig(
        4, // saturation
        6, // speed
        6, // haste
        4, // strength
        4, // regeneration
        5, // resistance
        1, // fireResistance
        1, // waterBreathing
        1, // nightVision
        6 // jumpBoost
    );

    @Config.LangKey(LibResources.CONFIG + "beaconTierConfig")
    public static class BeaconTierConfig {

        @Config.Comment("Saturation effect max level (0 = disabled)")
        @Config.RangeInt(min = 0, max = 10)
        public int saturationLevel;

        @Config.Comment("Speed effect max level")
        @Config.RangeInt(min = 0, max = 10)
        public int speedLevel;

        @Config.Comment("Haste (Dig Speed) effect max level")
        @Config.RangeInt(min = 0, max = 10)
        public int hasteLevel;

        @Config.Comment("Strength (Damage Boost) effect max level")
        @Config.RangeInt(min = 0, max = 10)
        public int strengthLevel;

        @Config.Comment("Regeneration effect max level")
        @Config.RangeInt(min = 0, max = 10)
        public int regenerationLevel;

        @Config.Comment("Resistance effect max level")
        @Config.RangeInt(min = 0, max = 10)
        public int resistanceLevel;

        @Config.Comment("Fire Resistance effect max level (usually 1)")
        @Config.RangeInt(min = 0, max = 10)
        public int fireResistanceLevel;

        @Config.Comment("Water Breathing effect max level (usually 1)")
        @Config.RangeInt(min = 0, max = 10)
        public int waterBreathingLevel;

        @Config.Comment("Night Vision effect max level (usually 1)")
        @Config.RangeInt(min = 0, max = 10)
        public int nightVisionLevel;

        @Config.Comment("Jump Boost effect max level")
        @Config.RangeInt(min = 0, max = 10)
        public int jumpBoostLevel;

        public BeaconTierConfig() {
            this(0, 1, 1, 1, 1, 1, 1, 1, 1, 1);
        }

        public BeaconTierConfig(int saturation, int speed, int haste, int strength, int regeneration, int resistance,
            int fireResistance, int waterBreathing, int nightVision, int jumpBoost) {
            this.saturationLevel = saturation;
            this.speedLevel = speed;
            this.hasteLevel = haste;
            this.strengthLevel = strength;
            this.regenerationLevel = regeneration;
            this.resistanceLevel = resistance;
            this.fireResistanceLevel = fireResistance;
            this.waterBreathingLevel = waterBreathing;
            this.nightVisionLevel = nightVision;
            this.jumpBoostLevel = jumpBoost;
        }
    }
}
