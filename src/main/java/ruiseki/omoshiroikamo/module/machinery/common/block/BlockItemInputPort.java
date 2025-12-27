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
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEItemInputPort;

/**
 * Item Input Port - accepts items for machine processing.
 * Can be placed at IO slot positions in machine structures.
 * Uses combined texture generated via TextureStitchEvent (base + overlay).
 */
public class BlockItemInputPort extends AbstractBlock<TEItemInputPort> {

    public static final String TEXTURE_NAME = "item_input";
    public static final String OVERLAY_NAME = "overlay_iteminput_1";

    protected BlockItemInputPort() {
        super("modularItemInput", TEItemInputPort.class);
        setHardness(5.0F);
        setResistance(10.0F);
    }

    public static BlockItemInputPort create() {
        return new BlockItemInputPort();
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
        return "modulariteminput";
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
    public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
        // TODO: Add WAILA info for slot contents
    }
}
