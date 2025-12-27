package ruiseki.omoshiroikamo.module.machinery.common.block;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractBlock;
import ruiseki.omoshiroikamo.module.machinery.client.util.MachineryTextureGenerator;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;

/**
 * Machine Controller - the brain of a Modular Machinery structure.
 * This block is mapped to the 'Q' symbol in structure definitions.
 * When right-clicked, it validates and forms the multiblock structure.
 * Uses combined texture generated via TextureStitchEvent (base + overlay).
 */
public class BlockMachineController extends AbstractBlock<TEMachineController> {

    public static final String TEXTURE_NAME = "machine_controller";
    public static final String OVERLAY_NAME = "overlay_machine_controller";

    protected BlockMachineController() {
        super("modularMachineController", TEMachineController.class);
        setHardness(5.0F);
        setResistance(10.0F);
    }

    public static BlockMachineController create() {
        return new BlockMachineController();
    }

    /**
     * Register texture generation request.
     * Called from MachineryClient.preInit() on client side.
     */
    @SideOnly(Side.CLIENT)
    public static void registerTexture() {
        MachineryTextureGenerator.requestTexture(TEXTURE_NAME, OVERLAY_NAME);
    }

    @Override
    public String getTextureName() {
        return "modular_machine_controller";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        // Get the combined texture generated via TextureStitchEvent
        IIcon generatedIcon = MachineryTextureGenerator.getGeneratedIcon(TEXTURE_NAME);
        if (generatedIcon != null) {
            blockIcon = generatedIcon;
        } else {
            // Fallback to base texture if generation failed
            blockIcon = reg.registerIcon("omoshiroikamo:" + MachineryTextureGenerator.getBaseTexture());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return blockIcon;
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
