package ruiseki.omoshiroikamo.api.enums;

public enum EnumIO {

    NONE,
    INPUT,
    OUTPUT,
    BOTH;

    public boolean canInput() {
        return this == INPUT || this == BOTH;
    }

    public boolean canOutput() {
        return this == OUTPUT || this == BOTH;
    }

    public String getName() {
        return "gui.io." + this.name()
            .toLowerCase();
    }
}
