package ruiseki.omoshiroikamo.module.machinery.common.tier;

import ruiseki.omoshiroikamo.api.modular.ModularTier;

/**
 * Manages the number of enabled tiers for the modular machinery system.
 * Allows configuration of how many tiers (0-15) are actually available in-game.
 */
public class TierManager {

    // TODO: Add this to MachineryConfig
    private static final int DEFAULT_ENABLED_TIERS = 6;

    /**
     * Gets the number of tiers enabled in the configuration.
     * Defaults to 6 if not configured.
     * 
     * @return Number of enabled tiers (1-16)
     */
    public static int getEnabledTierCount() {
        // TODO: Read from MachineryConfig.enabledTierCount when available
        return DEFAULT_ENABLED_TIERS;
    }

    /**
     * Checks if a tier is enabled in the configuration.
     * 
     * @param tier Tier to check
     * @return true if the tier is enabled
     */
    public static boolean isTierEnabled(ModularTier tier) {
        return tier.getMeta() < getEnabledTierCount();
    }

    /**
     * Checks if a tier meta value is enabled in the configuration.
     * 
     * @param meta Metadata value (0-15)
     * @return true if the tier is enabled
     */
    public static boolean isTierEnabled(int meta) {
        return meta >= 0 && meta < getEnabledTierCount();
    }

    /**
     * Gets the maximum tier meta value that is enabled.
     * 
     * @return Maximum enabled tier meta (0-15)
     */
    public static int getMaxEnabledTierMeta() {
        return Math.min(getEnabledTierCount() - 1, ModularTier.getTierCount() - 1);
    }
}
