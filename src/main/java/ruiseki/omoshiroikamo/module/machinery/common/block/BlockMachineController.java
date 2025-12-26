package ruiseki.omoshiroikamo.module.machinery.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;

/**
 * Machine Controller - the brain of a Modular Machinery structure.
 * This block is mapped to the 'Q' symbol in structure definitions.
 * When right-clicked, it validates and forms the multiblock structure.
 */
public class BlockMachineController extends Block implements ITileEntityProvider {

    public BlockMachineController() {
        super(Material.iron);
        setBlockName("machineController");
        setBlockTextureName("omoshiroikamo:machinery/machine_controller");
        setHardness(5.0F);
        setResistance(10.0F);
        setCreativeTab(CreativeTabs.tabRedstone);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TEMachineController();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TEMachineController) {
            TEMachineController controller = (TEMachineController) te;
            controller.onRightClick(player);
            return true;
        }
        return false;
    }
}
