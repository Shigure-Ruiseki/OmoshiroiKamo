package ruiseki.omoshiroikamo.api.structure.core;

import java.util.Collections;
import java.util.List;

/**
 * References a structure that contributes to a machine's tier.
 * Supports multiple offset slots and count-based or max-tier-based recognition.
 */
public class TierStructureRef {

    private final String structureName;
    private final int tier;
    private final String componentName;
    private final List<OffsetPair> offsetPairs;
    private final boolean isCountMode;

    public TierStructureRef(String structureName, int tier, String componentName, List<OffsetPair> offsetPairs,
        boolean isCountMode) {
        this.structureName = structureName;
        this.tier = tier;
        this.componentName = componentName != null ? componentName : "structure";
        this.offsetPairs = offsetPairs != null ? Collections.unmodifiableList(offsetPairs) : Collections.emptyList();
        this.isCountMode = isCountMode;
    }

    public String getStructureName() {
        return structureName;
    }

    public int getTier() {
        return tier;
    }

    public String getComponentName() {
        return componentName;
    }

    public List<OffsetPair> getOffsetPairs() {
        return offsetPairs;
    }

    public boolean isCountMode() {
        return isCountMode;
    }

    /**
     * A pair of target (base machine relative) and anchor (sub-structure relative)
     * coordinates.
     */
    public static class OffsetPair {

        public final int[] target;
        public final int[] anchor;

        public OffsetPair(int[] target, int[] anchor) {
            this.target = target != null ? target.clone() : new int[] { 0, 0, 0 };
            this.anchor = anchor != null ? anchor.clone() : new int[] { 0, 0, 0 };
        }

        public int[] getTarget() {
            return target.clone();
        }

        public int[] getAnchor() {
            return anchor.clone();
        }
    }
}
