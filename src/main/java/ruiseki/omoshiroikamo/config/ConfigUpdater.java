package ruiseki.omoshiroikamo.config;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

import ruiseki.omoshiroikamo.common.util.Logger;

public class ConfigUpdater {

    public ConfigUpdater() {}

    public static void updateBoolean(Class<?> configClass, String fieldName, boolean value) {
        updateField(configClass, fieldName, value);
    }

    public static void updateInt(Class<?> configClass, String fieldName, int value) {
        updateField(configClass, fieldName, value);
    }

    public static void updateFloat(Class<?> configClass, String fieldName, float value) {
        updateField(configClass, fieldName, value);
    }

    public static void updateDouble(Class<?> configClass, String fieldName, double value) {
        updateField(configClass, fieldName, value);
    }

    public static void updateString(Class<?> configClass, String fieldName, String value) {
        updateField(configClass, fieldName, value);
    }

    public static void updateIntArray(Class<?> configClass, String fieldName, int[] value) {
        updateField(configClass, fieldName, value);
    }

    public static void updateStringArray(Class<?> configClass, String fieldName, String[] value) {
        updateField(configClass, fieldName, value);
    }

    private static void updateField(Class<?> configClass, String fieldName, Object value) {
        try {
            Field field = configClass.getDeclaredField(fieldName);
            field.setAccessible(true);

            if (field.getType()
                .isArray()
                && value.getClass()
                    .isArray()) {
                int length = Array.getLength(value);
                Object newArray = Array.newInstance(
                    field.getType()
                        .getComponentType(),
                    length);
                for (int i = 0; i < length; i++) {
                    Array.set(newArray, i, Array.get(value, i));
                }
                field.set(null, newArray);
            } else {
                field.set(null, value);
            }

            ConfigurationManager.save(configClass);

            Logger.info("Updated " + configClass.getSimpleName() + "." + fieldName + " = " + value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Logger.info("Failed to update config: " + configClass.getSimpleName() + "." + fieldName);
            e.printStackTrace();
        }
    }
}
