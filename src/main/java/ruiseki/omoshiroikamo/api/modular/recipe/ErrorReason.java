package ruiseki.omoshiroikamo.api.modular.recipe;

public enum ErrorReason {

    NONE("none"),
    NO_ENERGY("no_energy"),
    OUTPUT_FULL("output_full"),
    INPUT_MISSING("input_missing"),
    STRUCTURE_BROKEN("structure_broken");

    private final String name;

    ErrorReason(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
