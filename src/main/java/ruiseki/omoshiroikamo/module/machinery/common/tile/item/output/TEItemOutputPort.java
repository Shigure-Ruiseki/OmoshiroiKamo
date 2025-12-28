package ruiseki.omoshiroikamo.module.machinery.common.tile.item.output;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.item.ItemTransfer;

import ruiseki.omoshiroikamo.module.machinery.common.tile.item.AbstractItemIOPortTE;

/**
 * Item Input Port TileEntity.
 * Holds slots for inputting items into machine processing.
 * Extends AbstractStorageTE to leverage existing inventory management system.
 */
public abstract class TEItemOutputPort extends AbstractItemIOPortTE {

    public TEItemOutputPort(int numOutput) {
        super(0, numOutput);
    }

    @Override
    public IO getIOLimit() {
        return IO.OUTPUT;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack itemstack, int side) {
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        if (!canOutput(dir)) {
            return false;
        }
        return super.canExtractItem(slot, itemstack, side);
    }

    @Override
    public boolean processTasks(boolean redstoneChecksPassed) {
        if (isRedstoneActive()) {
            ItemTransfer transfer = new ItemTransfer();
            for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
                if (!getSideIO(direction).canOutput()) {
                    continue;
                }
                TileEntity source = getPos().offset(direction)
                    .getTileEntity(worldObj);
                transfer.push(this, direction, source);
                transfer.transfer();
            }
        }

        return super.processTasks(redstoneChecksPassed);
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
