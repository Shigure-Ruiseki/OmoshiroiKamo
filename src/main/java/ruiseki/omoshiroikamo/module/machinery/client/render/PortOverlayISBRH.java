package ruiseki.omoshiroikamo.module.machinery.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.structurelib.alignment.enumerable.Flip;
import com.gtnewhorizon.structurelib.alignment.enumerable.Rotation;
import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.modular.ISidedTexture;
import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
import ruiseki.omoshiroikamo.core.common.util.RenderUtils;
import ruiseki.omoshiroikamo.core.tileentity.ISidedIO;
import ruiseki.omoshiroikamo.module.machinery.common.block.AbstractPortBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.StructureTintCache;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;

/**
 * ISBRH for rendering port overlays.
 * Optimized for performance - only renders when chunk is rebuilt.
 * Note: BlockMachineController uses ItemPortRenderer for inventory rendering.
 */
@SideOnly(Side.CLIENT)
@ThreadSafeISBRH(perThread = false)
public class PortOverlayISBRH implements ISimpleBlockRenderingHandler {

    public static final PortOverlayISBRH INSTANCE = new PortOverlayISBRH();

    private static final float EPS = 0.003f;

    private PortOverlayISBRH() {}

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
        // Note: BlockMachineController is handled by ItemPortRenderer.renderMachineController()
        // This method is called by ItemPortRenderer via renderer.renderBlockAsItem() for Ports

        Tessellator tess = Tessellator.instance;
        IIcon baseIcon = block.getIcon(0, metadata);

        // Get tint color from config
        int tintColor = MachineryConfig.getDefaultTintColorInt();
        float r = ((tintColor >> 16) & 0xFF) / 255.0f;
        float g = ((tintColor >> 8) & 0xFF) / 255.0f;
        float b = (tintColor & 0xFF) / 255.0f;

        // Bind texture atlas
        RenderUtils.bindTexture(TextureMap.locationBlocksTexture);

        GL11.glPushMatrix();
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        // Render tinted base cube
        tess.startDrawingQuads();
        tess.setColorOpaque_F(r, g, b);

        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            tess.setNormal(dir.offsetX, dir.offsetY, dir.offsetZ);
            RenderUtils.renderFaceCorrected(tess, dir, 0, 0, 0, baseIcon, 0.0f, Rotation.NORMAL, Flip.NONE);
        }
        tess.draw();

        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {

        // Get tint color from cache or config
        Integer structureColor = StructureTintCache.get(world, x, y, z);
        int tintColor = structureColor != null ? structureColor : MachineryConfig.getDefaultTintColorInt();

        // Calculate tint components
        float r = ((tintColor >> 16) & 0xFF) / 255.0f;
        float g = ((tintColor >> 8) & 0xFF) / 255.0f;
        float b = (tintColor & 0xFF) / 255.0f;

        // Ensure not black (fallback if calculation failed)
        if (r == 0 && g == 0 && b == 0) {
            r = g = b = 1.0f;
        }

        // Get TileEntity for textures and IO state
        TileEntity te = world.getTileEntity(x, y, z);
        ISidedIO ioConfig = te instanceof ISidedIO ? (ISidedIO) te : null;
        ISidedTexture sidedTexture = te instanceof ISidedTexture ? (ISidedTexture) te : null;

        renderer.setRenderBounds(0, 0, 0, 1, 1, 1);

        boolean prevRenderAllFaces = renderer.renderAllFaces;
        renderer.renderAllFaces = true;
        renderer.renderStandardBlockWithColorMultiplier(block, x, y, z, r, g, b);

        Tessellator t = Tessellator.instance;
        for (int i = 0; i < 6; i++) {
            ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];

            if (ioConfig != null && ioConfig.getSideIO(dir) == EnumIO.NONE) {
                continue;
            }

            int ax = x + dir.offsetX;
            int ay = y + dir.offsetY;
            int az = z + dir.offsetZ;
            if (world.getBlock(ax, ay, az)
                .isOpaqueCube()) {
                continue;
            }

            if (sidedTexture != null) {
                IIcon overlayIcon = sidedTexture.getTexture(dir, 1);
                if (overlayIcon != null) {
                    // Use neighbor brightness for overlay
                    int neighborBrightness = world.getLightBrightnessForSkyBlocks(ax, ay, az, 0);
                    t.setBrightness(neighborBrightness);

                    // Vanilla-style face shading
                    float shade = switch (dir) {
                        case DOWN -> 0.5f;
                        case UP -> 1.0f;
                        case NORTH, SOUTH -> 0.8f;
                        default -> 0.6f;
                    };
                    // Use RGBA to preserve alpha channel for translucent overlays
                    t.setColorRGBA_F(shade, shade, shade, 1.0f);
                    t.setNormal(dir.offsetX, dir.offsetY, dir.offsetZ);

                    // Determine rotation and flip for overlay texture
                    Rotation rotation = Rotation.NORMAL;
                    Flip flip = Flip.NONE;
                    if (te instanceof TEMachineController) {
                        var extFacing = ((TEMachineController) te).getExtendedFacing();
                        rotation = extFacing.getRotation();
                        flip = extFacing.getFlip();
                    }

                    RenderUtils.renderFaceCorrected(t, dir, x, y, z, overlayIcon, EPS, rotation, flip);
                }
            }
        }

        // Restore flags
        renderer.renderAllFaces = prevRenderAllFaces;

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return AbstractPortBlock.portRendererId;
    }
}
