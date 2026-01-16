package ruiseki.omoshiroikamo.api.modular.recipe;

/**
 * Represents the reason why a recipe cannot run or is blocked.
 */
public enum ErrorReason {

    NONE("none", ""),
    IDLE("idle", "Idle"),
    RUNNING("running", "Processing"),
    NO_RECIPES("no_recipes", "No recipes registered"),
    NO_INPUT_PORTS("no_input_ports", "No input ports connected"),
    NO_OUTPUT_PORTS("no_output_ports", "No output ports connected"),
    NO_ENERGY("no_energy", "Insufficient energy"),
    OUTPUT_FULL("output_full", "Output full"),
    INPUT_MISSING("input_missing", "Input missing"),
    NO_MATCHING_RECIPE("no_matching_recipe", "No matching recipe"),
    WAITING_OUTPUT("waiting_output", "Waiting for output space");

    private final String id;
    private final String defaultMessage;
    private String detail = "";

    ErrorReason(String id, String defaultMessage) {
        this.id = id;
        this.defaultMessage = defaultMessage;
    }

    public String getId() {
        return id;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public ErrorReason withDetail(String detail) {
        this.detail = detail;
        return this;
    }

    public String getMessage() {
        if (detail != null && !detail.isEmpty()) {
            return defaultMessage + ": " + detail;
        }
        return defaultMessage;
    }
}
