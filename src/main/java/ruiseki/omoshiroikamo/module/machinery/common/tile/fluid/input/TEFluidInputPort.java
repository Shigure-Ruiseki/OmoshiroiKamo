package ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.input;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.fluid.FluidTransfer;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.module.machinery.common.block.AbstractPortBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.AbstractFluidPortTE;

public abstract class TEFluidInputPort extends AbstractFluidPortTE {

    public TEFluidInputPort(int fluidCapacity) {
        super(fluidCapacity);
    }

    @Override
    public IO getIOLimit() {
        return IO.INPUT;
    }

    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {
        if (isRedstoneActive() && !tank.isFull()) {
            FluidTransfer transfer = new FluidTransfer();
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
        return super.processTasks(redstoneCheckPassed);
    }

    @Override
    public Direction getPortDirection() {
        return Direction.INPUT;
    }

    @Override
    public IIcon getTexture(ForgeDirection side, int renderPass) {
        if (renderPass == 0) {
            return AbstractPortBlock.baseIcon;
        }
        if (renderPass == 1 && getSideIO(side) != IO.NONE) {
            return IconRegistry.getIcon("overlay_fluidinput_" + getTier());
        }
        return AbstractPortBlock.baseIcon;
    }
}
