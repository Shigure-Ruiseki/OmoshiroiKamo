package ruiseki.omoshiroikamo.core.common.util;

/**
 * Utility for comparing semantic version strings.
 */
public class VersionComparator {

    /**
     * Strips build metadata and pre-release identifiers from a version string,
     * returning only the numeric base version.
     * Examples:
     * "1.5.4-JSONrefact.15+c4a815807b-dirty" -> "1.5.4"
     * "1.5.4" -> "1.5.4"
     * "v1.5.4" -> "1.5.4"
     *
     * @param version The version string to normalize
     * @return The base numeric version string (e.g., "1.5.4")
     */
    public static String toBaseVersion(String version) {
        if (version == null || version.isEmpty()) return "0.0.0";
        // Strip build metadata first ("+...")
        int plus = version.indexOf('+');
        if (plus != -1) version = version.substring(0, plus);
        // Strip pre-release identifier ("-...")
        int hyphen = version.indexOf('-');
        if (hyphen != -1) version = version.substring(0, hyphen);
        return normalize(version);
    }

    /**
     * Compares two version strings.
     * Handles build metadata and pre-release identifiers (e.g., "1.5.4-foo+bar").
     *
     * @param v1 The first version string
     * @param v2 The second version string
     * @return 0 if equal, negative if v1 < v2, positive if v1 > v2
     */
    public static int compare(String v1, String v2) {
        v1 = toBaseVersion(v1);
        v2 = toBaseVersion(v2);

        if (v1.isEmpty()) v1 = "0.0.0";
        if (v2.isEmpty()) v2 = "0.0.0";

        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");
        int length = Math.max(parts1.length, parts2.length);

        for (int i = 0; i < length; i++) {
            int p1 = i < parts1.length ? parseSafe(parts1[i]) : 0;
            int p2 = i < parts2.length ? parseSafe(parts2[i]) : 0;

            if (p1 < p2) return -1;
            if (p1 > p2) return 1;
        }

        return 0;
    }

    private static String normalize(String v) {
        int firstDigit = -1;
        for (int i = 0; i < v.length(); i++) {
            if (Character.isDigit(v.charAt(i))) {
                firstDigit = i;
                break;
            }
        }
        return firstDigit != -1 ? v.substring(firstDigit) : v;
    }

    private static int parseSafe(String s) {
        try {
            // Handle versions like "1.2.3-beta" by taking the prefix
            int hyphen = s.indexOf('-');
            if (hyphen != -1) {
                s = s.substring(0, hyphen);
            }
            return Integer.parseInt(s.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
