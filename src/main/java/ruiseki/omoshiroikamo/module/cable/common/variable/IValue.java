package ruiseki.omoshiroikamo.module.cable.common.variable;

public interface IValue {

    IValueType getType();

    Object raw();

    default boolean asBoolean() {
        return switch (getType()) {
            case BOOLEAN -> (boolean) raw();
            case INT -> (int) raw() != 0;
            case LONG -> (long) raw() != 0L;
            case FLOAT -> (float) raw() != 0f;
            case STRING -> !((String) raw()).isEmpty();
        };
    }

    default int asInt() {
        return switch (getType()) {
            case BOOLEAN -> ((boolean) raw()) ? 1 : 0;
            case INT -> (int) raw();
            case LONG -> (int) ((long) raw());
            case FLOAT -> (int) ((float) raw());
            case STRING -> Integer.parseInt((String) raw());
        };
    }
}
