package ruiseki.omoshiroikamo.module.machinery.client.render;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import ruiseki.omoshiroikamo.api.client.RenderUtils;
import ruiseki.omoshiroikamo.api.modular.ISidedTexture;

/**
 * Generic port overlay TESR: depth-free, cutout overlays so other effects (e.g.
 * Thaumcraft orbs)
 * stay visible. Reuses render pass 1 textures from ISidedTexture implementors.
 */
public class PortOverlayTESR extends TileEntitySpecialRenderer {

    private static final float EPS = 0.0025f;
    private static final float OFFSET_FACTOR = -1.0f;
    private static final float OFFSET_UNITS = -200.0f;

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        if (!(tile instanceof ISidedTexture sided)) {
            return;
        }

        // Collect overlays first; early-return if nothing to draw.
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

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        // Render overlay as cutout: no lighting, no blending; alpha test keeps edges,
        // avoids face-dependent tint.
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glPolygonOffset(OFFSET_FACTOR, OFFSET_UNITS);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_CULL_FACE); // show both sides so top/bottom overlays don't vanish

        bindTexture(TextureMap.locationBlocksTexture);

        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        // Fullbright but slightly dimmed color to avoid overexposure; ensures no dark
        // overlays.
        t.setBrightness(0x00F000F0);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        t.setColorOpaque_F(0.8F, 0.8F, 0.8F);

        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            IIcon icon = icons[dir.ordinal()];
            if (icon == null) continue;

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
        }

        t.draw();

        GL11.glPolygonOffset(0.0f, 0.0f);
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }
}
