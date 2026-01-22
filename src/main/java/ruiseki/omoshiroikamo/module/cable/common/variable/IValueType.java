package ruiseki.omoshiroikamo.module.cable.common.variable;

public enum IValueType {

    BOOLEAN,
    INT,
    LONG,
    FLOAT,
    STRING;

    public boolean isNumeric() {
        return this == INT || this == LONG || this == FLOAT;
    }
}
