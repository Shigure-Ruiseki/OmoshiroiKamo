package ruiseki.omoshiroikamo.module.machinery.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.module.machinery.common.tile.TEEnergyInputPort;

/**
 * Energy Input Port - accepts RF energy for machine processing.
 * Can be placed at IO slot positions in machine structures.
 */
public class BlockEnergyInputPort extends Block implements ITileEntityProvider {

    public BlockEnergyInputPort() {
        super(Material.iron);
        setBlockName("energyInputPort");
        setBlockTextureName("omoshiroikamo:machinery/energy_input_port");
        setHardness(5.0F);
        setResistance(10.0F);
        setCreativeTab(CreativeTabs.tabRedstone);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TEEnergyInputPort();
    }
}
