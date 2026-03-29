package ruiseki.omoshiroikamo.core.common.structure;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ruiseki.omoshiroikamo.core.lib.LibMisc;

/**
 * Utility for block ID compatibility following name refactoring (camelCase to snake_case).
 */
public class BlockCompat {

    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("([a-z])([A-Z])");
    private static final String MOD_PREFIX = LibMisc.MOD_ID + ":";

    /**
     * Converts a block ID from camelCase to snake_case if it belongs to this mod.
     * Example: "omoshiroikamo:blockCrystal" -> "omoshiroikamo:block_crystal"
     * Example: "omoshiroikamo:blockCrystal:0" -> "omoshiroikamo:block_crystal:0"
     * 
     * This method is idempotent: if it's already snake_case, it returns the same string.
     *
     * @param id The block ID string to remap
     * @return The remapped ID, or the original if no conversion is needed
     */
    public static String camelToSnake(String id) {
        if (id == null || !id.startsWith(MOD_PREFIX)) {
            return id;
        }

        // Handle meta values (e.g., "modid:name:0")
        String[] parts = id.split(":", 3);
        if (parts.length < 2) {
            return id;
        }

        String name = parts[1];

        // Skip if there are no uppercase letters
        if (!hasUppercase(name)) {
            return id;
        }

        // Perform snake_case conversion
        Matcher matcher = CAMEL_CASE_PATTERN.matcher(name);
        String newName = matcher.replaceAll("$1_$2")
            .toLowerCase();

        // Reconstruct ID
        StringBuilder sb = new StringBuilder();
        sb.append(MOD_PREFIX)
            .append(newName);
        if (parts.length > 2) {
            sb.append(":")
                .append(parts[2]);
        }

        return sb.toString();
    }

    private static boolean hasUppercase(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (Character.isUpperCase(s.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}
