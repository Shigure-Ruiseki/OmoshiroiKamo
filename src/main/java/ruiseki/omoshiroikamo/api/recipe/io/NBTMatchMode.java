package ruiseki.omoshiroikamo.api.recipe.io;

/**
 * Defines how NBT data should be matched when comparing items or blocks in recipes.
 *
 * <p>
 * This enum controls the strictness of NBT matching for recipe inputs and outputs:
 * <ul>
 * <li>{@link #IGNORE} - NBT is completely ignored (default, maintains backward compatibility)</li>
 * <li>{@link #EXACT} - NBT must match exactly</li>
 * <li>{@link #NONE} - Only items/blocks without NBT are accepted</li>
 * <li>{@link #PARTIAL} - Uses expression/listOp system (internal use)</li>
 * </ul>
 */
public enum NBTMatchMode {

    /**
     * Ignore NBT completely. Item/Block matches regardless of NBT presence or content.
     * This is the default mode and maintains compatibility with existing recipes.
     */
    IGNORE,

    /**
     * Require exact NBT match. Uses ItemStack.areItemStackTagsEqual() for items.
     * For blocks, compares TileEntity NBT data.
     * Items/Blocks without NBT only match other items/blocks without NBT.
     */
    EXACT,

    /**
     * Only accept items/blocks that have NO NBT data.
     * Items/Blocks with any NBT data will not match.
     */
    NONE,

    /**
     * Use existing expression/listOp system for partial matching.
     * This mode is implicit when nbtExpressions or nbtListOp are specified.
     * Not directly settable via JSON - it's automatically determined.
     */
    PARTIAL;

    /**
     * Get the default match mode used when no mode is specified.
     *
     * @return {@link #IGNORE} to maintain backward compatibility
     */
    public static NBTMatchMode getDefault() {
        return IGNORE;
    }

    /**
     * Parse NBTMatchMode from a JSON string value.
     *
     * @param str JSON value ("ignore", "exact", "none")
     * @return Corresponding NBTMatchMode, or {@link #IGNORE} if str is null or invalid
     */
    public static NBTMatchMode fromString(String str) {
        if (str == null) return IGNORE;

        switch (str.toLowerCase()) {
            case "exact":
                return EXACT;
            case "none":
                return NONE;
            case "ignore":
                return IGNORE;
            case "partial":
                return PARTIAL;
            default:
                return IGNORE;
        }
    }

    /**
     * Convert this mode to its JSON string representation (lowercase).
     *
     * @return lowercase name of this mode (e.g., "exact", "none", "ignore")
     */
    public String toJsonString() {
        return name().toLowerCase();
    }
}
