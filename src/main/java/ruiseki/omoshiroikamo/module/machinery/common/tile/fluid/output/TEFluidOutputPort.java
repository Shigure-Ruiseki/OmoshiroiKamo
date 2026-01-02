package ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.output;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.fluid.FluidTransfer;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.module.machinery.common.block.AbstractPortBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.AbstractFluidPortTE;

public abstract class TEFluidOutputPort extends AbstractFluidPortTE {

    public TEFluidOutputPort(int fluidCapacity) {
        super(fluidCapacity);
    }

    @Override
    public IO getIOLimit() {
        return IO.OUTPUT;
    }

    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {
        if (isRedstoneActive()) {
            FluidTransfer transfer = new FluidTransfer();
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
        return super.processTasks(redstoneCheckPassed);
    }

    @Override
    public Direction getPortDirection() {
        return Direction.OUTPUT;
    }

    @Override
    public IIcon getTexture(ForgeDirection side, int renderPass) {
        if (renderPass == 0) {
            return AbstractPortBlock.baseIcon;
        }
        if (renderPass == 1 && getSideIO(side) == IO.INPUT) {
            return IconRegistry.getIcon("overlay_fluidoutput_" + getTier());
        }
        return AbstractPortBlock.baseIcon;
    }
}
