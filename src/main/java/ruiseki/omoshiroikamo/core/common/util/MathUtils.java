package ruiseki.omoshiroikamo.core.common.util;

public class MathUtils {

    public static int divideAndRoundUp(int a, int b) {
        return a / b + ((a % b == 0) ? 0 : 1);
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }

    public static int ceilDiv(int a, int b) {
        return (a + b - 1) / b;
    }
}
