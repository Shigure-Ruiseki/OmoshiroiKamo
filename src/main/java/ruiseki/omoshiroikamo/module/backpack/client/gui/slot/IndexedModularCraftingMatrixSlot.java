package ruiseki.omoshiroikamo.module.backpack.client.gui.slot;

import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

public class IndexedModularCraftingMatrixSlot extends ModularSlot {

    protected final int upgradeSlotIndex;

    public IndexedModularCraftingMatrixSlot(int upgradeSlotIndex, IItemHandler itemHandler, int index) {
        super(itemHandler, index);
        this.upgradeSlotIndex = upgradeSlotIndex;
    }

    public int getUpgradeSlotIndex() {
        return upgradeSlotIndex;
    }
}
