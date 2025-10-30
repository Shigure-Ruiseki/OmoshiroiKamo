package ruiseki.omoshiroikamo.plugin.baubles;

import baubles.api.expanded.BaubleExpandedSlots;

public class BaubleExpandedCompat {

    public static int[] backpackSlotIDs;

    public static void preInit() {
        BaubleExpandedSlots.tryAssignSlotsUpToMinimum(BaubleExpandedSlots.bodyType, 1);
    }

    public static void postInit() {
        backpackSlotIDs = BaubleExpandedSlots.getIndexesOfAssignedSlotsOfType(BaubleExpandedSlots.bodyType);
    }
}
