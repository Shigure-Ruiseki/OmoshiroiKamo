package ruiseki.omoshiroikamo.module.machinery.common.tile.gas.output;

import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.module.machinery.common.block.AbstractPortBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.AbstractGasPortTE;

public abstract class TEGasOutputPort extends AbstractGasPortTE {

    public TEGasOutputPort(int gasCapacity) {
        super(gasCapacity);
    }

    @Override
    public EnumIO getIOLimit() {
        return EnumIO.OUTPUT;
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
        if (renderPass == 1 && getSideIO(side) != EnumIO.NONE) {
            return IconRegistry.getIcon("overlay_gasoutput_" + getTier());
        }
        return AbstractPortBlock.baseIcon;
    }
}
