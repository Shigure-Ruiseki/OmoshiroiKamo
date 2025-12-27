package ruiseki.omoshiroikamo.module.machinery.common.block;

import ruiseki.omoshiroikamo.core.common.block.BlockOK;

/**
 * Machine Casing - basic structural block for Modular Machinery.
 * Used as the main building block for machine structures.
 */
public class BlockMachineCasing extends BlockOK {

    protected BlockMachineCasing() {
        super("modularMachineCasing");
        setHardness(5.0F);
        setResistance(10.0F);
    }

    public static BlockMachineCasing create() {
        return new BlockMachineCasing();
    }

    @Override
    public String getTextureName() {
        return "modular_machine_casing";
    }
}
