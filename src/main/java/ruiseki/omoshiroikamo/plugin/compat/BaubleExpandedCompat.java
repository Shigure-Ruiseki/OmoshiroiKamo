package ruiseki.omoshiroikamo.plugin.compat;

import baubles.api.expanded.BaubleExpandedSlots;
import ruiseki.omoshiroikamo.common.util.lib.LibMods;

public class BaubleExpandedCompat {

    public static int[] backpackSlotIDs;

    public static void preInit() {
        if (!LibMods.BaublesExpanded.isLoaded()) {
            return;
        }
        BaubleExpandedSlots.tryAssignSlotsUpToMinimum(BaubleExpandedSlots.bodyType, 1);
    }

    public static void postInit() {
        if (!LibMods.BaublesExpanded.isLoaded()) {
            return;
        }
        backpackSlotIDs = BaubleExpandedSlots.getIndexesOfAssignedSlotsOfType(BaubleExpandedSlots.bodyType);
    }
}
