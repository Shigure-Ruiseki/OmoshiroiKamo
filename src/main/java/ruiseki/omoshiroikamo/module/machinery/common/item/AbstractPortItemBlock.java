package ruiseki.omoshiroikamo.module.machinery.common.item;

import net.minecraft.block.Block;
import net.minecraft.util.IIcon;

import ruiseki.omoshiroikamo.core.common.block.ItemBlockOK;

public abstract class AbstractPortItemBlock extends ItemBlockOK {

    public AbstractPortItemBlock(Block blockA, Block blockB) {
        super(blockA, blockB);
    }

    public abstract IIcon getOverlayIcon(int tier);
}
