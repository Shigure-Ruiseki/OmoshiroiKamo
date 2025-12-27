package ruiseki.omoshiroikamo.module.machinery.common.block;

import ruiseki.omoshiroikamo.core.common.block.BlockOK;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEEnergyInputPort;

/**
 * Energy Input Port - accepts RF energy for machine processing.
 * Can be placed at IO slot positions in machine structures.
 * 
 * TODO: Texture required -
 * assets/omoshiroikamo/textures/blocks/machinery/energy_input_port.png
 */
public class BlockEnergyInputPort extends BlockOK {

    protected BlockEnergyInputPort() {
        super("modularEnergyInput", TEEnergyInputPort.class);
        setHardness(5.0F);
        setResistance(10.0F);
    }

    public static BlockEnergyInputPort create() {
        return new BlockEnergyInputPort();
    }

    @Override
    public BlockOK setTextureName(String texture) {
        return super.setTextureName("machinery/energy_input_port");
    }
}
