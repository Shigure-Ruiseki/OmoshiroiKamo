package ruiseki.omoshiroikamo.module.machinery.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

/**
 * Machine Casing - basic structural block for Modular Machinery.
 * Used as the main building block for machine structures.
 */
public class BlockMachineCasing extends Block {

    public BlockMachineCasing() {
        super(Material.iron);
        setBlockName("machineCasing");
        setBlockTextureName("omoshiroikamo:machinery/machine_casing");
        setHardness(5.0F);
        setResistance(10.0F);
        setCreativeTab(CreativeTabs.tabRedstone);
    }
}
