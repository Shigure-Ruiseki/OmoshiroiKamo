package ruiseki.omoshiroikamo.config.backport.multiblock;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;

@Config.Comment("Quantum Beacon Settings")
@Config.LangKey(LibResources.CONFIG + "quantumBeaconConfig")
@Config(
    modid = LibMisc.MOD_ID,
    category = "beacon",
    configSubDirectory = LibMisc.MOD_ID + "/multiblock",
    filename = "beacon")
public class QuantumBeaconConfig {

    @Config.Comment("General beacon settings")
    @Config.LangKey(LibResources.CONFIG + "beaconGeneral")
    public static final GeneralSettings general = new GeneralSettings();

    @Config.Comment("Energy cost per modifier effect (RF/t)")
    @Config.LangKey(LibResources.CONFIG + "modifierEnergyCost")
    public static final ModifierEnergyCost modifierEnergyCost = new ModifierEnergyCost();

    @Config.Comment("Effect levels per tier")
    @Config.LangKey(LibResources.CONFIG + "beaconEffectLevels")
    public static final EffectLevels effectLevels = new EffectLevels();

    @Config.Comment("Effect range per tier")
    @Config.LangKey(LibResources.CONFIG + "beaconRange")
    public static final RangeSettings rangeSettings = new RangeSettings();

    public static class GeneralSettings {

        @Config.Comment("If true, the beacon requires clear sky above to function and show beam")
        @Config.DefaultBoolean(true)
        public boolean requireSky;

        @Config.Comment("If true, the beacon will render a beam when formed and active")
        @Config.DefaultBoolean(true)
        public boolean enableBeam;
    }

    public static class ModifierEnergyCost {

        @Config.Comment("Flight Modifier: Energy cost per tick (RF)")
        @Config.DefaultInt(2048)
        @Config.RangeInt(min = 0, max = 1000000)
        public int flight;

        @Config.Comment("Haste Modifier: Energy cost per tick (RF)")
        @Config.DefaultInt(512)
        @Config.RangeInt(min = 0, max = 1000000)
        public int haste;

        @Config.Comment("Regeneration Modifier: Energy cost per tick (RF)")
        @Config.DefaultInt(1024)
        @Config.RangeInt(min = 0, max = 1000000)
        public int regeneration;

        @Config.Comment("Jump Boost Modifier: Energy cost per tick (RF)")
        @Config.DefaultInt(256)
        @Config.RangeInt(min = 0, max = 1000000)
        public int jumpBoost;

        @Config.Comment("Night Vision Modifier: Energy cost per tick (RF)")
        @Config.DefaultInt(256)
        @Config.RangeInt(min = 0, max = 1000000)
        public int nightVision;

        @Config.Comment("Resistance Modifier: Energy cost per tick (RF)")
        @Config.DefaultInt(512)
        @Config.RangeInt(min = 0, max = 1000000)
        public int resistance;

        @Config.Comment("Saturation Modifier: Energy cost per tick (RF)")
        @Config.DefaultInt(2048)
        @Config.RangeInt(min = 0, max = 1000000)
        public int saturation;

        @Config.Comment("Strength Modifier: Energy cost per tick (RF)")
        @Config.DefaultInt(256)
        @Config.RangeInt(min = 0, max = 1000000)
        public int strength;

        @Config.Comment("Water Breathing Modifier: Energy cost per tick (RF)")
        @Config.DefaultInt(256)
        @Config.RangeInt(min = 0, max = 1000000)
        public int waterBreathing;

        @Config.Comment("Fire Resistance Modifier: Energy cost per tick (RF)")
        @Config.DefaultInt(256)
        @Config.RangeInt(min = 0, max = 1000000)
        public int fireResistance;
    }

    public static class EffectLevels {

        // Speed
        @Config.Comment("Tier 1: Speed effect level")
        @Config.DefaultInt(1)
        @Config.RangeInt(min = 0, max = 10)
        public int speedTier1;

        @Config.Comment("Tier 2: Speed effect level")
        @Config.DefaultInt(2)
        @Config.RangeInt(min = 0, max = 10)
        public int speedTier2;

        @Config.Comment("Tier 3: Speed effect level")
        @Config.DefaultInt(3)
        @Config.RangeInt(min = 0, max = 10)
        public int speedTier3;

        @Config.Comment("Tier 4: Speed effect level")
        @Config.DefaultInt(4)
        @Config.RangeInt(min = 0, max = 10)
        public int speedTier4;

        @Config.Comment("Tier 5: Speed effect level")
        @Config.DefaultInt(5)
        @Config.RangeInt(min = 0, max = 10)
        public int speedTier5;

        @Config.Comment("Tier 6: Speed effect level")
        @Config.DefaultInt(6)
        @Config.RangeInt(min = 0, max = 10)
        public int speedTier6;

        // Haste
        @Config.Comment("Tier 1: Haste effect level")
        @Config.DefaultInt(1)
        @Config.RangeInt(min = 0, max = 10)
        public int hasteTier1;

        @Config.Comment("Tier 2: Haste effect level")
        @Config.DefaultInt(2)
        @Config.RangeInt(min = 0, max = 10)
        public int hasteTier2;

        @Config.Comment("Tier 3: Haste effect level")
        @Config.DefaultInt(3)
        @Config.RangeInt(min = 0, max = 10)
        public int hasteTier3;

        @Config.Comment("Tier 4: Haste effect level")
        @Config.DefaultInt(4)
        @Config.RangeInt(min = 0, max = 10)
        public int hasteTier4;

        @Config.Comment("Tier 5: Haste effect level")
        @Config.DefaultInt(5)
        @Config.RangeInt(min = 0, max = 10)
        public int hasteTier5;

        @Config.Comment("Tier 6: Haste effect level")
        @Config.DefaultInt(6)
        @Config.RangeInt(min = 0, max = 10)
        public int hasteTier6;

        // Strength
        @Config.Comment("Tier 1: Strength effect level")
        @Config.DefaultInt(1)
        @Config.RangeInt(min = 0, max = 10)
        public int strengthTier1;

        @Config.Comment("Tier 2: Strength effect level")
        @Config.DefaultInt(2)
        @Config.RangeInt(min = 0, max = 10)
        public int strengthTier2;

        @Config.Comment("Tier 3: Strength effect level")
        @Config.DefaultInt(3)
        @Config.RangeInt(min = 0, max = 10)
        public int strengthTier3;

        @Config.Comment("Tier 4: Strength effect level")
        @Config.DefaultInt(3)
        @Config.RangeInt(min = 0, max = 10)
        public int strengthTier4;

        @Config.Comment("Tier 5: Strength effect level")
        @Config.DefaultInt(4)
        @Config.RangeInt(min = 0, max = 10)
        public int strengthTier5;

        @Config.Comment("Tier 6: Strength effect level")
        @Config.DefaultInt(4)
        @Config.RangeInt(min = 0, max = 10)
        public int strengthTier6;

        // Regeneration
        @Config.Comment("Tier 1: Regeneration effect level")
        @Config.DefaultInt(1)
        @Config.RangeInt(min = 0, max = 10)
        public int regenerationTier1;

        @Config.Comment("Tier 2: Regeneration effect level")
        @Config.DefaultInt(1)
        @Config.RangeInt(min = 0, max = 10)
        public int regenerationTier2;

        @Config.Comment("Tier 3: Regeneration effect level")
        @Config.DefaultInt(2)
        @Config.RangeInt(min = 0, max = 10)
        public int regenerationTier3;

        @Config.Comment("Tier 4: Regeneration effect level")
        @Config.DefaultInt(2)
        @Config.RangeInt(min = 0, max = 10)
        public int regenerationTier4;

        @Config.Comment("Tier 5: Regeneration effect level")
        @Config.DefaultInt(3)
        @Config.RangeInt(min = 0, max = 10)
        public int regenerationTier5;

        @Config.Comment("Tier 6: Regeneration effect level")
        @Config.DefaultInt(4)
        @Config.RangeInt(min = 0, max = 10)
        public int regenerationTier6;

        // Resistance
        @Config.Comment("Tier 1: Resistance effect level")
        @Config.DefaultInt(1)
        @Config.RangeInt(min = 0, max = 10)
        public int resistanceTier1;

        @Config.Comment("Tier 2: Resistance effect level")
        @Config.DefaultInt(1)
        @Config.RangeInt(min = 0, max = 10)
        public int resistanceTier2;

        @Config.Comment("Tier 3: Resistance effect level")
        @Config.DefaultInt(2)
        @Config.RangeInt(min = 0, max = 10)
        public int resistanceTier3;

        @Config.Comment("Tier 4: Resistance effect level")
        @Config.DefaultInt(3)
        @Config.RangeInt(min = 0, max = 10)
        public int resistanceTier4;

        @Config.Comment("Tier 5: Resistance effect level")
        @Config.DefaultInt(4)
        @Config.RangeInt(min = 0, max = 10)
        public int resistanceTier5;

        @Config.Comment("Tier 6: Resistance effect level")
        @Config.DefaultInt(5)
        @Config.RangeInt(min = 0, max = 10)
        public int resistanceTier6;

        // Saturation
        @Config.Comment("Tier 1: Saturation effect level (0 = disabled)")
        @Config.DefaultInt(0)
        @Config.RangeInt(min = 0, max = 10)
        public int saturationTier1;

        @Config.Comment("Tier 2: Saturation effect level (0 = disabled)")
        @Config.DefaultInt(0)
        @Config.RangeInt(min = 0, max = 10)
        public int saturationTier2;

        @Config.Comment("Tier 3: Saturation effect level")
        @Config.DefaultInt(1)
        @Config.RangeInt(min = 0, max = 10)
        public int saturationTier3;

        @Config.Comment("Tier 4: Saturation effect level")
        @Config.DefaultInt(2)
        @Config.RangeInt(min = 0, max = 10)
        public int saturationTier4;

        @Config.Comment("Tier 5: Saturation effect level")
        @Config.DefaultInt(3)
        @Config.RangeInt(min = 0, max = 10)
        public int saturationTier5;

        @Config.Comment("Tier 6: Saturation effect level")
        @Config.DefaultInt(4)
        @Config.RangeInt(min = 0, max = 10)
        public int saturationTier6;

        // Jump Boost
        @Config.Comment("Tier 1: Jump Boost effect level")
        @Config.DefaultInt(1)
        @Config.RangeInt(min = 0, max = 10)
        public int jumpBoostTier1;

        @Config.Comment("Tier 2: Jump Boost effect level")
        @Config.DefaultInt(2)
        @Config.RangeInt(min = 0, max = 10)
        public int jumpBoostTier2;

        @Config.Comment("Tier 3: Jump Boost effect level")
        @Config.DefaultInt(3)
        @Config.RangeInt(min = 0, max = 10)
        public int jumpBoostTier3;

        @Config.Comment("Tier 4: Jump Boost effect level")
        @Config.DefaultInt(4)
        @Config.RangeInt(min = 0, max = 10)
        public int jumpBoostTier4;

        @Config.Comment("Tier 5: Jump Boost effect level")
        @Config.DefaultInt(5)
        @Config.RangeInt(min = 0, max = 10)
        public int jumpBoostTier5;

        @Config.Comment("Tier 6: Jump Boost effect level")
        @Config.DefaultInt(6)
        @Config.RangeInt(min = 0, max = 10)
        public int jumpBoostTier6;

        // Night Vision (usually 1)
        @Config.Comment("Tier 1: Night Vision effect level")
        @Config.DefaultInt(1)
        @Config.RangeInt(min = 0, max = 10)
        public int nightVisionTier1;

        @Config.Comment("Tier 2: Night Vision effect level")
        @Config.DefaultInt(1)
        @Config.RangeInt(min = 0, max = 10)
        public int nightVisionTier2;

        @Config.Comment("Tier 3: Night Vision effect level")
        @Config.DefaultInt(1)
        @Config.RangeInt(min = 0, max = 10)
        public int nightVisionTier3;

        @Config.Comment("Tier 4: Night Vision effect level")
        @Config.DefaultInt(1)
        @Config.RangeInt(min = 0, max = 10)
        public int nightVisionTier4;

        @Config.Comment("Tier 5: Night Vision effect level")
        @Config.DefaultInt(1)
        @Config.RangeInt(min = 0, max = 10)
        public int nightVisionTier5;

        @Config.Comment("Tier 6: Night Vision effect level")
        @Config.DefaultInt(1)
        @Config.RangeInt(min = 0, max = 10)
        public int nightVisionTier6;

        // Water Breathing (usually 1)
        @Config.Comment("Tier 1: Water Breathing effect level")
        @Config.DefaultInt(1)
        @Config.RangeInt(min = 0, max = 10)
        public int waterBreathingTier1;

        @Config.Comment("Tier 2: Water Breathing effect level")
        @Config.DefaultInt(1)
        @Config.RangeInt(min = 0, max = 10)
        public int waterBreathingTier2;

        @Config.Comment("Tier 3: Water Breathing effect level")
        @Config.DefaultInt(1)
        @Config.RangeInt(min = 0, max = 10)
        public int waterBreathingTier3;

        @Config.Comment("Tier 4: Water Breathing effect level")
        @Config.DefaultInt(1)
        @Config.RangeInt(min = 0, max = 10)
        public int waterBreathingTier4;

        @Config.Comment("Tier 5: Water Breathing effect level")
        @Config.DefaultInt(1)
        @Config.RangeInt(min = 0, max = 10)
        public int waterBreathingTier5;

        @Config.Comment("Tier 6: Water Breathing effect level")
        @Config.DefaultInt(1)
        @Config.RangeInt(min = 0, max = 10)
        public int waterBreathingTier6;

        // Fire Resistance (usually 1)
        @Config.Comment("Tier 1: Fire Resistance effect level")
        @Config.DefaultInt(1)
        @Config.RangeInt(min = 0, max = 10)
        public int fireResistanceTier1;

        @Config.Comment("Tier 2: Fire Resistance effect level")
        @Config.DefaultInt(1)
        @Config.RangeInt(min = 0, max = 10)
        public int fireResistanceTier2;

        @Config.Comment("Tier 3: Fire Resistance effect level")
        @Config.DefaultInt(1)
        @Config.RangeInt(min = 0, max = 10)
        public int fireResistanceTier3;

        @Config.Comment("Tier 4: Fire Resistance effect level")
        @Config.DefaultInt(1)
        @Config.RangeInt(min = 0, max = 10)
        public int fireResistanceTier4;

        @Config.Comment("Tier 5: Fire Resistance effect level")
        @Config.DefaultInt(1)
        @Config.RangeInt(min = 0, max = 10)
        public int fireResistanceTier5;

        @Config.Comment("Tier 6: Fire Resistance effect level")
        @Config.DefaultInt(1)
        @Config.RangeInt(min = 0, max = 10)
        public int fireResistanceTier6;
    }

    public static class RangeSettings {

        @Config.Comment("Tier 1: Horizontal range (blocks)")
        @Config.DefaultInt(30)
        @Config.RangeInt(min = 0, max = 1000)
        public int horizontalTier1;

        @Config.Comment("Tier 2: Horizontal range (blocks)")
        @Config.DefaultInt(50)
        @Config.RangeInt(min = 0, max = 1000)
        public int horizontalTier2;

        @Config.Comment("Tier 3: Horizontal range (blocks)")
        @Config.DefaultInt(70)
        @Config.RangeInt(min = 0, max = 1000)
        public int horizontalTier3;

        @Config.Comment("Tier 4: Horizontal range (blocks)")
        @Config.DefaultInt(90)
        @Config.RangeInt(min = 0, max = 1000)
        public int horizontalTier4;

        @Config.Comment("Tier 5: Horizontal range (blocks)")
        @Config.DefaultInt(110)
        @Config.RangeInt(min = 0, max = 1000)
        public int horizontalTier5;

        @Config.Comment("Tier 6: Horizontal range (blocks)")
        @Config.DefaultInt(130)
        @Config.RangeInt(min = 0, max = 1000)
        public int horizontalTier6;

        @Config.Comment("Tier 1: Upward range (blocks)")
        @Config.DefaultInt(512)
        @Config.RangeInt(min = 0, max = 1000)
        public int upwardTier1;

        @Config.Comment("Tier 2: Upward range (blocks)")
        @Config.DefaultInt(512)
        @Config.RangeInt(min = 0, max = 1000)
        public int upwardTier2;

        @Config.Comment("Tier 3: Upward range (blocks)")
        @Config.DefaultInt(512)
        @Config.RangeInt(min = 0, max = 1000)
        public int upwardTier3;

        @Config.Comment("Tier 4: Upward range (blocks)")
        @Config.DefaultInt(512)
        @Config.RangeInt(min = 0, max = 1000)
        public int upwardTier4;

        @Config.Comment("Tier 5: Upward range (blocks)")
        @Config.DefaultInt(512)
        @Config.RangeInt(min = 0, max = 1000)
        public int upwardTier5;

        @Config.Comment("Tier 6: Upward range (blocks)")
        @Config.DefaultInt(512)
        @Config.RangeInt(min = 0, max = 1000)
        public int upwardTier6;

        @Config.Comment("Tier 1: Downward range (blocks)")
        @Config.DefaultInt(20)
        @Config.RangeInt(min = 0, max = 1000)
        public int downwardTier1;

        @Config.Comment("Tier 2: Downward range (blocks)")
        @Config.DefaultInt(30)
        @Config.RangeInt(min = 0, max = 1000)
        public int downwardTier2;

        @Config.Comment("Tier 3: Downward range (blocks)")
        @Config.DefaultInt(40)
        @Config.RangeInt(min = 0, max = 1000)
        public int downwardTier3;

        @Config.Comment("Tier 4: Downward range (blocks)")
        @Config.DefaultInt(50)
        @Config.RangeInt(min = 0, max = 1000)
        public int downwardTier4;

        @Config.Comment("Tier 5: Downward range (blocks)")
        @Config.DefaultInt(60)
        @Config.RangeInt(min = 0, max = 1000)
        public int downwardTier5;

        @Config.Comment("Tier 6: Downward range (blocks)")
        @Config.DefaultInt(70)
        @Config.RangeInt(min = 0, max = 1000)
        public int downwardTier6;

        public int getHorizontal(int tier) {
            switch (tier) {
                case 1:
                    return horizontalTier1;
                case 2:
                    return horizontalTier2;
                case 3:
                    return horizontalTier3;
                case 4:
                    return horizontalTier4;
                case 5:
                    return horizontalTier5;
                case 6:
                    return horizontalTier6;
                default:
                    return horizontalTier1;
            }
        }

        public int getUpward(int tier) {
            switch (tier) {
                case 1:
                    return upwardTier1;
                case 2:
                    return upwardTier2;
                case 3:
                    return upwardTier3;
                case 4:
                    return upwardTier4;
                case 5:
                    return upwardTier5;
                case 6:
                    return upwardTier6;
                default:
                    return upwardTier1;
            }
        }

        public int getDownward(int tier) {
            switch (tier) {
                case 1:
                    return downwardTier1;
                case 2:
                    return downwardTier2;
                case 3:
                    return downwardTier3;
                case 4:
                    return downwardTier4;
                case 5:
                    return downwardTier5;
                case 6:
                    return downwardTier6;
                default:
                    return downwardTier1;
            }
        }
    }

    // =============================================
    // Helper classes for backward compatibility
    // =============================================

    /**
     * Legacy BeaconTierConfig class for backward compatibility.
     */
    public static class BeaconTierConfig {

        private final int tier;

        public BeaconTierConfig(int tier) {
            this.tier = tier;
        }

        public int getSaturationLevel() {
            switch (tier) {
                case 1:
                    return effectLevels.saturationTier1;
                case 2:
                    return effectLevels.saturationTier2;
                case 3:
                    return effectLevels.saturationTier3;
                case 4:
                    return effectLevels.saturationTier4;
                case 5:
                    return effectLevels.saturationTier5;
                case 6:
                    return effectLevels.saturationTier6;
                default:
                    return 0;
            }
        }

        public int getSpeedLevel() {
            switch (tier) {
                case 1:
                    return effectLevels.speedTier1;
                case 2:
                    return effectLevels.speedTier2;
                case 3:
                    return effectLevels.speedTier3;
                case 4:
                    return effectLevels.speedTier4;
                case 5:
                    return effectLevels.speedTier5;
                case 6:
                    return effectLevels.speedTier6;
                default:
                    return 1;
            }
        }

        public int getHasteLevel() {
            switch (tier) {
                case 1:
                    return effectLevels.hasteTier1;
                case 2:
                    return effectLevels.hasteTier2;
                case 3:
                    return effectLevels.hasteTier3;
                case 4:
                    return effectLevels.hasteTier4;
                case 5:
                    return effectLevels.hasteTier5;
                case 6:
                    return effectLevels.hasteTier6;
                default:
                    return 1;
            }
        }

        public int getStrengthLevel() {
            switch (tier) {
                case 1:
                    return effectLevels.strengthTier1;
                case 2:
                    return effectLevels.strengthTier2;
                case 3:
                    return effectLevels.strengthTier3;
                case 4:
                    return effectLevels.strengthTier4;
                case 5:
                    return effectLevels.strengthTier5;
                case 6:
                    return effectLevels.strengthTier6;
                default:
                    return 1;
            }
        }

        public int getRegenerationLevel() {
            switch (tier) {
                case 1:
                    return effectLevels.regenerationTier1;
                case 2:
                    return effectLevels.regenerationTier2;
                case 3:
                    return effectLevels.regenerationTier3;
                case 4:
                    return effectLevels.regenerationTier4;
                case 5:
                    return effectLevels.regenerationTier5;
                case 6:
                    return effectLevels.regenerationTier6;
                default:
                    return 1;
            }
        }

        public int getResistanceLevel() {
            switch (tier) {
                case 1:
                    return effectLevels.resistanceTier1;
                case 2:
                    return effectLevels.resistanceTier2;
                case 3:
                    return effectLevels.resistanceTier3;
                case 4:
                    return effectLevels.resistanceTier4;
                case 5:
                    return effectLevels.resistanceTier5;
                case 6:
                    return effectLevels.resistanceTier6;
                default:
                    return 1;
            }
        }

        public int getFireResistanceLevel() {
            switch (tier) {
                case 1:
                    return effectLevels.fireResistanceTier1;
                case 2:
                    return effectLevels.fireResistanceTier2;
                case 3:
                    return effectLevels.fireResistanceTier3;
                case 4:
                    return effectLevels.fireResistanceTier4;
                case 5:
                    return effectLevels.fireResistanceTier5;
                case 6:
                    return effectLevels.fireResistanceTier6;
                default:
                    return 1;
            }
        }

        public int getWaterBreathingLevel() {
            switch (tier) {
                case 1:
                    return effectLevels.waterBreathingTier1;
                case 2:
                    return effectLevels.waterBreathingTier2;
                case 3:
                    return effectLevels.waterBreathingTier3;
                case 4:
                    return effectLevels.waterBreathingTier4;
                case 5:
                    return effectLevels.waterBreathingTier5;
                case 6:
                    return effectLevels.waterBreathingTier6;
                default:
                    return 1;
            }
        }

        public int getNightVisionLevel() {
            switch (tier) {
                case 1:
                    return effectLevels.nightVisionTier1;
                case 2:
                    return effectLevels.nightVisionTier2;
                case 3:
                    return effectLevels.nightVisionTier3;
                case 4:
                    return effectLevels.nightVisionTier4;
                case 5:
                    return effectLevels.nightVisionTier5;
                case 6:
                    return effectLevels.nightVisionTier6;
                default:
                    return 1;
            }
        }

        public int getJumpBoostLevel() {
            switch (tier) {
                case 1:
                    return effectLevels.jumpBoostTier1;
                case 2:
                    return effectLevels.jumpBoostTier2;
                case 3:
                    return effectLevels.jumpBoostTier3;
                case 4:
                    return effectLevels.jumpBoostTier4;
                case 5:
                    return effectLevels.jumpBoostTier5;
                case 6:
                    return effectLevels.jumpBoostTier6;
                default:
                    return 1;
            }
        }
    }

    /**
     * Legacy BeaconTierRangeConfig class for backward compatibility.
     */
    public static class BeaconTierRangeConfig {

        public final int horizontalRange;
        public final int upwardRange;
        public final int downwardRange;

        public BeaconTierRangeConfig(int tier) {
            this.horizontalRange = rangeSettings.getHorizontal(tier);
            this.upwardRange = rangeSettings.getUpward(tier);
            this.downwardRange = rangeSettings.getDownward(tier);
        }
    }

    // Legacy tier instances for backward compatibility (ignored in config GUI)
    @Config.Ignore
    public static final BeaconTierConfig tier1 = new BeaconTierConfig(1);
    @Config.Ignore
    public static final BeaconTierConfig tier2 = new BeaconTierConfig(2);
    @Config.Ignore
    public static final BeaconTierConfig tier3 = new BeaconTierConfig(3);
    @Config.Ignore
    public static final BeaconTierConfig tier4 = new BeaconTierConfig(4);
    @Config.Ignore
    public static final BeaconTierConfig tier5 = new BeaconTierConfig(5);
    @Config.Ignore
    public static final BeaconTierConfig tier6 = new BeaconTierConfig(6);

    /**
     * Legacy method for getting range config by tier
     */
    public static BeaconTierRangeConfig getRangeConfig(int tier) {
        return new BeaconTierRangeConfig(tier);
    }
}
