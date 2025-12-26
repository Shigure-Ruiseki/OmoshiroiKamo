package ruiseki.omoshiroikamo.module.machinery.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.module.machinery.common.tile.TEItemInputPort;

/**
 * Item Input Port - accepts items for machine processing.
 * Can be placed at IO slot positions in machine structures.
 */
public class BlockItemInputPort extends Block implements ITileEntityProvider {

    public BlockItemInputPort() {
        super(Material.iron);
        setBlockName("itemInputPort");
        setBlockTextureName("omoshiroikamo:machinery/item_input_port");
        setHardness(5.0F);
        setResistance(10.0F);
        setCreativeTab(CreativeTabs.tabRedstone);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TEItemInputPort();
    }
}
