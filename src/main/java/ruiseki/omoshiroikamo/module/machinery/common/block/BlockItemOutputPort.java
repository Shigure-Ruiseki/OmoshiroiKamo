package ruiseki.omoshiroikamo.module.machinery.common.block;

import ruiseki.omoshiroikamo.core.common.block.BlockOK;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEItemOutputPort;

/**
 * Item Output Port - outputs processed items from machines.
 * Can be placed at IO slot positions in machine structures.
 * 
 * TODO: Texture required -
 * assets/omoshiroikamo/textures/blocks/machinery/item_output_port.png
 */
public class BlockItemOutputPort extends BlockOK {

    protected BlockItemOutputPort() {
        super("modularItemOutput", TEItemOutputPort.class);
        setHardness(5.0F);
        setResistance(10.0F);
    }

    public static BlockItemOutputPort create() {
        return new BlockItemOutputPort();
    }

    @Override
    public BlockOK setTextureName(String texture) {
        return super.setTextureName("machinery/item_output_port");
    }
}
