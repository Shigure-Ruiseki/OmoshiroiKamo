package ruiseki.omoshiroikamo.module.machinery.common.tile.item.output;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.item.ItemTransfer;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.module.machinery.common.block.AbstractPortBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.AbstractItemIOPortTE;

/**
 * Item Output Port TileEntity.
 * Holds slots for outputting items from machine processing.
 * Extends AbstractStorageTE to leverage existing inventory management system.
 */
public abstract class TEItemOutputPort extends AbstractItemIOPortTE {

    public TEItemOutputPort(int numOutput) {
        super(0, numOutput);
    }

    @Override
    public EnumIO getIOLimit() {
        return EnumIO.OUTPUT;
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
                TileEntity sink = getPos().offset(direction)
                    .getTileEntity(worldObj);
                transfer.push(this, direction, sink);
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

    @Override
    public Direction getPortDirection() {
        return Direction.OUTPUT;
    }

    @Override
    public IIcon getTexture(ForgeDirection side, int renderPass) {
        if (renderPass == 0) {
            return ((AbstractPortBlock<?>) getBlockType()).baseIcon;
        }
        if (renderPass == 1) {
            if (getSideIO(side) != EnumIO.NONE) {
                return IconRegistry.getIcon("overlay_itemoutput_" + getTier());
            }
            return null;
        }
        return ((AbstractPortBlock<?>) getBlockType()).baseIcon;
    }
}
