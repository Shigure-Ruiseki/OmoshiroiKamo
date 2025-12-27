package ruiseki.omoshiroikamo.module.machinery.common.block;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;

/**
 * Machine Controller - the brain of a Modular Machinery structure.
 * This block is mapped to the 'Q' symbol in structure definitions.
 * When right-clicked, it validates and forms the multiblock structure.
 * 
 * TODO: Texture required -
 * assets/omoshiroikamo/textures/blocks/machinery/machine_controller.png
 */
public class BlockMachineController extends AbstractBlock<TEMachineController> {

    protected BlockMachineController() {
        super("modularMachineController", TEMachineController.class);
        setHardness(5.0F);
        setResistance(10.0F);
    }

    public static BlockMachineController create() {
        return new BlockMachineController();
    }

    @Override
    public String getTextureName() {
        return "machinery/machine_controller";
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        TEMachineController te = (TEMachineController) world.getTileEntity(x, y, z);
        if (te != null) {
            te.onRightClick(player);
            return true;
        }
        return false;
    }

    @Override
    public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
        // TODO: Add WAILA info for machine status
    }
}
