package ruiseki.omoshiroikamo.module.backpack.integration.bauble;

import baubles.api.expanded.BaubleExpandedSlots;
import ruiseki.omoshiroikamo.core.lib.LibMods;

public class BackpackBaubleCompat {

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
