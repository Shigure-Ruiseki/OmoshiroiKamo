package ruiseki.omoshiroikamo.module.machinery.common.tile.item.input;

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
 * Item Input Port TileEntity.
 * Holds slots for inputting items into machine processing.
 * TODO: Add auto-sort
 */
public abstract class TEItemInputPort extends AbstractItemIOPortTE {

    public TEItemInputPort(int numInputs) {
        super(numInputs, 0);
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return slotDefinition.isInputSlot(slot);
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack itemstack, int side) {
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        if (!canInput(dir)) {
            return false;
        }
        return super.canInsertItem(slot, itemstack, side);
    }

    @Override
    public EnumIO getIOLimit() {
        return EnumIO.INPUT;
    }

    @Override
    public boolean processTasks(boolean redstoneChecksPassed) {
        if (isRedstoneActive() && inv.hasEmptySlot()) {
            ItemTransfer transfer = new ItemTransfer();
            for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
                if (!getSideIO(direction).canInput()) {
                    continue;
                }
                TileEntity source = getPos().offset(direction)
                    .getTileEntity(worldObj);
                transfer.pull(this, direction, source);
                transfer.transfer();
            }
        }

        return super.processTasks(redstoneChecksPassed);
    }

    @Override
    public Direction getPortDirection() {
        return Direction.INPUT;
    }

    @Override
    public IIcon getTexture(ForgeDirection side, int renderPass) {
        if (renderPass == 0) {
            return ((AbstractPortBlock<?>) getBlockType()).baseIcon;
        }
        if (renderPass == 1) {
            if (getSideIO(side) != EnumIO.NONE) {
                return IconRegistry.getIcon("overlay_iteminput_" + getTier());
            }
            return null;
        }
        return ((AbstractPortBlock<?>) getBlockType()).baseIcon;
    }
}
