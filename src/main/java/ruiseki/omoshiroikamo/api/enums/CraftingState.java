package ruiseki.omoshiroikamo.api.enums;

import java.util.HashMap;
import java.util.Map;

public enum CraftingState {

    IDLE(0, "idle"),
    RUNNING(1, "running"),
    ERROR(2, "error");

    private final int index;
    private final String name;
    private static final Map<Integer, CraftingState> indexMap = new HashMap<>();

    CraftingState(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public static CraftingState byIndex(int index) {
        return indexMap.getOrDefault(index, CraftingState.IDLE);
    }

    static {
        for (CraftingState state : CraftingState.values()) {
            indexMap.put(state.index, state);
        }
    }
}
