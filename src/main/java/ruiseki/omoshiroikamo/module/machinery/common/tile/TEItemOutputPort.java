package ruiseki.omoshiroikamo.module.machinery.common.tile;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.io.SlotDefinition;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractStorageTE;

/**
 * Item Output Port TileEntity.
 * Holds a single slot for outputting processed items from machines.
 * Extends AbstractStorageTE to leverage existing inventory management system.
 * 
 * TODO: Texture required -
 * assets/omoshiroikamo/textures/blocks/machinery/item_output_port.png
 */
public class TEItemOutputPort extends AbstractStorageTE {

    public TEItemOutputPort() {
        super(new SlotDefinition().setItemSlots(0, 1)); // 0 input, 1 output
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return false; // Output only, no external insertion
    }

    /**
     * Try to insert an item into the output slot.
     * Used internally by the machine controller.
     * 
     * @return true if successful, false if slot is full
     */
    public boolean insertItem(ItemStack stack) {
        int outputSlot = slotDefinition.getMinItemOutput();
        if (outputSlot < 0) return false;

        ItemStack existing = inv.getStackInSlot(outputSlot);
        if (existing == null) {
            inv.setStackInSlot(outputSlot, stack.copy());
            return true;
        }

        if (existing.isItemEqual(stack) && existing.stackSize + stack.stackSize <= getInventoryStackLimit()) {
            existing.stackSize += stack.stackSize;
            inv.setStackInSlot(outputSlot, existing);
            return true;
        }

        return false;
    }
}
