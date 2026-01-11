package ruiseki.omoshiroikamo.module.machinery.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import ruiseki.omoshiroikamo.api.client.RenderUtils;
import ruiseki.omoshiroikamo.api.modular.ISidedTexture;

/**
 * Reuses render pass 1 textures from ISidedTexture implementors.
 * Uses world lighting from adjacent blocks to match surrounding environment.
 */
public class PortOverlayTESR extends TileEntitySpecialRenderer {

    private static final float EPS = 0.001f;
    private static final double MAX_RENDER_DISTANCE_SQ = 64 * 64; // 64 blocks

    // Vanilla side shading multipliers (applied by block renderer)
    private static final float[] SIDE_SHADING = { 0.5f, // DOWN (Y-)
        1.0f, // UP (Y+)
        0.8f, // NORTH (Z-)
        0.8f, // SOUTH (Z+)
        0.6f, // WEST (X-)
        0.6f // EAST (X+)
    };

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        if (!(tile instanceof ISidedTexture sided)) {
            return;
        }

        double distSq = x * x + y * y + z * z;
        if (distSq > MAX_RENDER_DISTANCE_SQ) {
            return;
        }

        IIcon[] icons = new IIcon[6];
        boolean hasAny = false;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            IIcon icon = sided.getTexture(dir, 1);
            icons[dir.ordinal()] = icon;
            if (icon != null) {
                hasAny = true;
            }
        }
        if (!hasAny) {
            return;
        }

        World world = tile.getWorldObj();
        int bx = tile.xCoord;
        int by = tile.yCoord;
        int bz = tile.zCoord;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glTranslated(x, y, z);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glPolygonOffset(-1.0f, -1.0f);
        GL11.glDisable(GL11.GL_CULL_FACE);

        bindTexture(TextureMap.locationBlocksTexture);

        Tessellator t = Tessellator.instance;

        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            IIcon icon = icons[dir.ordinal()];
            if (icon == null) continue;

            // Get adjacent block position
            int adjacentX = bx + dir.offsetX;
            int adjacentY = by + dir.offsetY;
            int adjacentZ = bz + dir.offsetZ;
            Block adjacentBlock = world.getBlock(adjacentX, adjacentY, adjacentZ);

            // Skip rendering if adjacent block is opaque (face wouldn't be visible anyway)
            if (adjacentBlock.isOpaqueCube()) {
                continue;
            }

            // Get brightness from adjacent block position
            int brightness = world.getLightBrightnessForSkyBlocks(adjacentX, adjacentY, adjacentZ, 0);

            // Render each face separately with correct brightness and side shading
            t.startDrawingQuads();
            t.setBrightness(brightness);

            // Apply side shading to match vanilla block rendering
            float shade = SIDE_SHADING[dir.ordinal()];
            t.setColorOpaque_F(shade, shade, shade);

            switch (dir) {
                case DOWN:
                    RenderUtils.renderSingleFace(t, 0, 0.0f, -EPS, 0.0f, 1.0f, 0.0f, 1.0f, icon);
                    break;
                case UP:
                    RenderUtils.renderSingleFace(t, 1, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f + EPS, 1.0f, icon);
                    break;
                case NORTH:
                    RenderUtils.renderSingleFace(t, 2, 0.0f, 0.0f, -EPS, 1.0f, 1.0f, 0.0f, icon);
                    break;
                case SOUTH:
                    RenderUtils.renderSingleFace(t, 3, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f + EPS, icon);
                    break;
                case WEST:
                    RenderUtils.renderSingleFace(t, 4, -EPS, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, icon);
                    break;
                case EAST:
                    RenderUtils.renderSingleFace(t, 5, 1.0f, 0.0f, 0.0f, 1.0f + EPS, 1.0f, 1.0f, icon);
                    break;
                default:
                    break;
            }

            t.draw();
        }

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
