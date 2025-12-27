package ruiseki.omoshiroikamo.module.machinery.common.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.core.common.block.BlockOK;
import ruiseki.omoshiroikamo.core.common.block.TileEntityOK;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;

/**
 * Machine Controller - the brain of a Modular Machinery structure.
 * This block is mapped to the 'Q' symbol in structure definitions.
 * When right-clicked, it validates and forms the multiblock structure.
 * 
 * TODO: Texture required -
 * assets/omoshiroikamo/textures/blocks/machinery/machine_controller.png
 */
public class BlockMachineController extends BlockOK {

    protected BlockMachineController() {
        super("modularMachineController", TEMachineController.class);
        setHardness(5.0F);
        setResistance(10.0F);
    }

    public static BlockMachineController create() {
        return new BlockMachineController();
    }

    @Override
    public BlockOK setTextureName(String texture) {
        return super.setTextureName("machinery/machine_controller");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        TileEntityOK te = getTileEntityEio(world, x, y, z);
        if (te instanceof TEMachineController controller) {
            controller.onRightClick(player);
            return true;
        }
        return false;
    }
}
