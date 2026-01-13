package ruiseki.omoshiroikamo.module.machinery.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.client.renderer.TessellatorManager;
import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.modular.ISidedTexture;
import ruiseki.omoshiroikamo.module.machinery.common.block.AbstractPortBlock;

/**
 * ISBRH for rendering port overlays.
 * Optimized for performance - only renders when chunk is rebuilt.
 */
@SideOnly(Side.CLIENT)
@ThreadSafeISBRH(perThread = false)
public class PortOverlayISBRH implements ISimpleBlockRenderingHandler {

    public static final PortOverlayISBRH INSTANCE = new PortOverlayISBRH();

    private static final float EPS = 0.003f;

    private PortOverlayISBRH() {}

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
        Tessellator tess = TessellatorManager.get();
        IIcon baseIcon = AbstractPortBlock.baseIcon;
        if (baseIcon == null) {
            baseIcon = block.getIcon(0, metadata);
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        // Enable proper lighting for inventory
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHT0);
        GL11.glEnable(GL11.GL_LIGHT1);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);

        tess.startDrawingQuads();
        tess.setColorOpaque_F(1.0f, 1.0f, 1.0f);
        tess.setNormal(0.0F, 1.0F, 0.0F);
        ruiseki.omoshiroikamo.core.common.util.RenderUtils.renderCube(tess, 0, 0, 0, 1, 1, 1, baseIcon);
        tess.draw();

        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {

        // Render base block
        renderer.renderStandardBlock(block, x, y, z);

        // Get tile entity for overlay textures
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof ISidedTexture sided)) {
            return true;
        }

        // Pre-calculate brightness once from block position (optimization)
        int baseBrightness = block.getMixedBrightnessForBlock(world, x, y, z);

        Tessellator t = Tessellator.instance;

        // Render overlays for each direction
        for (int i = 0; i < 6; i++) {
            ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
            IIcon icon = sided.getTexture(dir, 1);
            if (icon == null) continue;

            // Check if adjacent block is opaque
            int ax = x + dir.offsetX;
            int ay = y + dir.offsetY;
            int az = z + dir.offsetZ;
            if (world.getBlock(ax, ay, az)
                .isOpaqueCube()) continue;

            // Set brightness and color
            t.setBrightness(baseBrightness);

            // Inline side shading for performance
            float shade;
            switch (i) {
                case 0:
                    shade = 0.5f;
                    break; // DOWN
                case 1:
                    shade = 1.0f;
                    break; // UP
                case 2:
                case 3:
                    shade = 0.8f;
                    break; // NORTH/SOUTH
                default:
                    shade = 0.6f;
                    break; // WEST/EAST
            }
            t.setColorOpaque_F(shade, shade, shade);

            // Inline face rendering for performance
            float u0 = icon.getMinU();
            float u1 = icon.getMaxU();
            float v0 = icon.getMinV();
            float v1 = icon.getMaxV();

            switch (dir) {
                case DOWN:
                    t.addVertexWithUV(x, y - EPS, z, u0, v0);
                    t.addVertexWithUV(x + 1, y - EPS, z, u1, v0);
                    t.addVertexWithUV(x + 1, y - EPS, z + 1, u1, v1);
                    t.addVertexWithUV(x, y - EPS, z + 1, u0, v1);
                    break;
                case UP:
                    t.addVertexWithUV(x, y + 1 + EPS, z + 1, u0, v1);
                    t.addVertexWithUV(x + 1, y + 1 + EPS, z + 1, u1, v1);
                    t.addVertexWithUV(x + 1, y + 1 + EPS, z, u1, v0);
                    t.addVertexWithUV(x, y + 1 + EPS, z, u0, v0);
                    break;
                case NORTH:
                    t.addVertexWithUV(x + 1, y, z - EPS, u1, v1);
                    t.addVertexWithUV(x, y, z - EPS, u0, v1);
                    t.addVertexWithUV(x, y + 1, z - EPS, u0, v0);
                    t.addVertexWithUV(x + 1, y + 1, z - EPS, u1, v0);
                    break;
                case SOUTH:
                    t.addVertexWithUV(x, y, z + 1 + EPS, u0, v1);
                    t.addVertexWithUV(x + 1, y, z + 1 + EPS, u1, v1);
                    t.addVertexWithUV(x + 1, y + 1, z + 1 + EPS, u1, v0);
                    t.addVertexWithUV(x, y + 1, z + 1 + EPS, u0, v0);
                    break;
                case WEST:
                    t.addVertexWithUV(x - EPS, y, z, u0, v1);
                    t.addVertexWithUV(x - EPS, y, z + 1, u1, v1);
                    t.addVertexWithUV(x - EPS, y + 1, z + 1, u1, v0);
                    t.addVertexWithUV(x - EPS, y + 1, z, u0, v0);
                    break;
                case EAST:
                    t.addVertexWithUV(x + 1 + EPS, y, z + 1, u1, v1);
                    t.addVertexWithUV(x + 1 + EPS, y, z, u0, v1);
                    t.addVertexWithUV(x + 1 + EPS, y + 1, z, u0, v0);
                    t.addVertexWithUV(x + 1 + EPS, y + 1, z + 1, u1, v0);
                    break;
                default:
                    break;
            }
        }

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
