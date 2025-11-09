package ruiseki.omoshiroikamo.api.entity;

/**
 * Represents different biome-based spawn categories for entities.
 * NORMAL Regular overworld biomes.
 * SNOW Cold or snowy biomes.
 * NONE Cannot spawn naturally.
 * HELL Nether biomes.
 */
public enum SpawnType {

    /**
     * Spawn in normal overworld biomes.
     */
    NORMAL,

    /**
     * Spawn in snowy or cold biomes.
     */
    SNOW,

    /**
     * Cannot spawn naturally.
     */
    NONE,

    /**
     * Spawn in Nether-like biomes.
     */
    HELL;

    /**
     * @return an array of enum constant names as Strings.
     */
    public static String[] names() {
        SpawnType[] values = values();
        String[] names = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            names[i] = values[i].name();
        }
        return names;
    }

    /**
     * Gets a SpawnType from a string name.
     * Case-insensitive. Returns NONE for invalid or null names.
     *
     * @param name Name of the enum value.
     * @return corresponding SpawnType or NONE if invalid.
     */
    public static SpawnType fromName(String name) {
        if (name == null || name.isEmpty()) {
            return NONE;
        }

        try {
            return valueOf(
                name.trim()
                    .toUpperCase());
        } catch (Exception e) {
            return NONE;
        }
    }
}
