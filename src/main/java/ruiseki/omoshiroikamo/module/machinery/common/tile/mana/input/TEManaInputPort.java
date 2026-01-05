package ruiseki.omoshiroikamo.module.machinery.common.tile.mana.input;

import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.module.machinery.common.block.AbstractPortBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.mana.AbstractManaPortTE;

public abstract class TEManaInputPort extends AbstractManaPortTE {

    protected TEManaInputPort(int manaCapacity, int manaMaxReceive) {
        super(manaCapacity, manaMaxReceive);
    }

    @Override
    public boolean canRecieveManaFromBursts() {
        return true;
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
        if (renderPass == 1) {
            return IconRegistry.getIcon("overlay_manainput_" + getTier());
        }
        return AbstractPortBlock.baseIcon;
    }
}
