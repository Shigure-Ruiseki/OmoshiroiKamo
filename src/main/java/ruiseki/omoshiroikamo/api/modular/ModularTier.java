package ruiseki.omoshiroikamo.api.modular;

/**
 * Defines 16 tiers (0-15) for the modular machinery system.
 * Tier is stored directly in block metadata (4 bits).
 *
 * This enum provides tier identification and naming only.
 * Color information is managed separately by StructureTintCache and MachineryConfig.
 */
public enum ModularTier {
    // spotless: off
    TIER_0(0, "0"),
    TIER_1(1, "1"),
    TIER_2(2, "2"),
    TIER_3(3, "3"),
    TIER_4(4, "4"),
    TIER_5(5, "5"),
    TIER_6(6, "6"),
    TIER_7(7, "7"),
    TIER_8(8, "8"),
    TIER_9(9, "9"),
    TIER_10(10, "10"),
    TIER_11(11, "11"),
    TIER_12(12, "12"),
    TIER_13(13, "13"),
    TIER_14(14, "14"),
    TIER_15(15, "15");
    // spotless: on

    private static final ModularTier[] VALUES = values();

    private final int meta;
    private final String unlocalizedName;

    ModularTier(int meta, String unlocalizedName) {
        this.meta = meta;
        this.unlocalizedName = unlocalizedName;
    }

    /**
     * Gets the metadata value (0-15) for this tier.
     */
    public int getMeta() {
        return meta;
    }

    /**
     * Gets the unlocalized name for this tier.
     * Used for localization keys like "machinery.tier.{unlocalizedName}".
     */
    public String getUnlocalizedName() {
        return unlocalizedName;
    }

    /**
     * Gets the tier from metadata value.
     * @param meta Metadata value (0-15)
     * @return Corresponding ModularTier, or TIER_0 if out of range
     */
    public static ModularTier fromMeta(int meta) {
        if (meta < 0 || meta >= VALUES.length) {
            return TIER_0;
        }
        return VALUES[meta];
    }

    /**
     * Gets the total number of tiers defined.
     */
    public static int getTierCount() {
        return VALUES.length;
    }
}
