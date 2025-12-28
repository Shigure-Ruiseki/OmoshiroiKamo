package ruiseki.omoshiroikamo.module.machinery.common.tile;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.io.SlotDefinition;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractStorageTE;

/**
 * Item Input Port TileEntity.
 * Holds a single slot for inputting items into machine processing.
 * Extends AbstractStorageTE to leverage existing inventory management system.
 * 
 * TODO: Texture required -
 * assets/omoshiroikamo/textures/blocks/machinery/item_input_port.png
 */
public class TEItemInputPort extends AbstractStorageTE {

    public TEItemInputPort() {
        super(new SlotDefinition().setItemSlots(1, 0)); // 1 input, 0 output
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return slotDefinition.isInputSlot(slot);
    }
}
