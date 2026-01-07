package ruiseki.omoshiroikamo.module.machinery.common.tile.gas.output;

import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.module.machinery.common.block.AbstractPortBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.AbstractGasPortTE;

public abstract class TEGasOutputPort extends AbstractGasPortTE {

    public TEGasOutputPort(int gasCapacity) {
        super(gasCapacity);
    }

    @Override
    public IO getIOLimit() {
        return IO.OUTPUT;
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
        if (renderPass == 1) {
            if (getSideIO(side) != IO.NONE) {
                return IconRegistry.getIcon("overlay_gasoutput_" + getTier());
            }
            return IconRegistry.getIcon("overlay_gasoutput_disabled");
        }
        return AbstractPortBlock.baseIcon;
    }
}
