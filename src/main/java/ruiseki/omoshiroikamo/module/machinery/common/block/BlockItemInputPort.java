package ruiseki.omoshiroikamo.module.machinery.common.block;

import ruiseki.omoshiroikamo.core.common.block.BlockOK;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEItemInputPort;

/**
 * Item Input Port - accepts items for machine processing.
 * Can be placed at IO slot positions in machine structures.
 * 
 * TODO: Texture required -
 * assets/omoshiroikamo/textures/blocks/machinery/item_input_port.png
 */
public class BlockItemInputPort extends BlockOK {

    protected BlockItemInputPort() {
        super("modularItemInput", TEItemInputPort.class);
        setHardness(5.0F);
        setResistance(10.0F);
    }

    public static BlockItemInputPort create() {
        return new BlockItemInputPort();
    }

    @Override
    public BlockOK setTextureName(String texture) {
        return super.setTextureName("machinery/item_input_port");
    }
}
