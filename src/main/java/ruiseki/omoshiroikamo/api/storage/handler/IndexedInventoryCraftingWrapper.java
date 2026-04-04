package ruiseki.omoshiroikamo.api.storage.handler;

import net.minecraft.inventory.Container;

import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;

import ruiseki.omoshiroikamo.api.storage.wrapper.ICraftingUpgrade.CraftingDestination;

public class IndexedInventoryCraftingWrapper extends InventoryCraftingWrapper {

    private final int upgradeSlotIndex;
    private CraftingDestination craftingDestination = CraftingDestination.INVENTORY;

    public IndexedInventoryCraftingWrapper(int upgradeSlotIndex, Container cont, int width, int height,
        IItemHandlerModifiable delegate, int startIndex) {
        super(cont, width, height, delegate, startIndex);
        this.upgradeSlotIndex = upgradeSlotIndex;
    }

    public int getUpgradeSlotIndex() {
        return upgradeSlotIndex;
    }

    public CraftingDestination getCraftingDestination() {
        return craftingDestination;
    }

    public void setCraftingDestination(CraftingDestination craftingDestination) {
        this.craftingDestination = craftingDestination;
    }
}
