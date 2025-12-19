package ruiseki.omoshiroikamo.api.json;

public class JsonUtils {

    private JsonUtils() {}

    public static int resolveColor(String hex, int def) {
        if (hex == null) return def;

        String value = hex.trim();
        if (value.isEmpty()) return def;

        try {
            if (value.startsWith("#")) {
                value = "0x" + value.substring(1);
            }
            return Integer.decode(value);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static String parseColor(int color) {
        return String.format("0x%06X", color & 0xFFFFFF);
    }
}
