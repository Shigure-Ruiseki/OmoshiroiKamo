package ruiseki.omoshiroikamo.config.backport;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;

@Config.Comment("Quantum Beacon Effect Level Settings")
@Config.LangKey(LibResources.CONFIG + "quantumBeaconConfig")
@Config(modid = LibMisc.MOD_ID, category = "beacon", configSubDirectory = LibMisc.MOD_ID + "/QuantumBeacon")
public class QuantumBeaconConfig {

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

        public BeaconTierConfig(int saturation, int speed, int haste, int strength, int regeneration,
                int resistance, int fireResistance, int waterBreathing, int nightVision, int jumpBoost) {
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
