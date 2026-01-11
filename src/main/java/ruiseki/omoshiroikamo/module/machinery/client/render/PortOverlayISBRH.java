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
 * Much more efficient than TESR as it only renders when chunk is rebuilt.
 */
@SideOnly(Side.CLIENT)
@ThreadSafeISBRH(perThread = false)
public class PortOverlayISBRH implements ISimpleBlockRenderingHandler {

    public static final PortOverlayISBRH INSTANCE = new PortOverlayISBRH();

    private static final float EPS = 0.003f;

    // Vanilla side shading multipliers
    private static final float[] SIDE_SHADING = { 0.5f, // DOWN (Y-)
        1.0f, // UP (Y+)
        0.8f, // NORTH (Z-)
        0.8f, // SOUTH (Z+)
        0.6f, // WEST (X-)
        0.6f // EAST (X+)
    };

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

        tess.startDrawingQuads();
        // Render base cube
        ruiseki.omoshiroikamo.core.common.util.RenderUtils.renderCube(tess, 0, 0, 0, 1, 1, 1, baseIcon);
        tess.draw();

        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {

        // 1. Render base block using standard rendering
        renderer.renderStandardBlock(block, x, y, z);

        // 2. Render overlay from tile entity
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof ISidedTexture sided)) {
            return true;
        }

        Tessellator t = Tessellator.instance;

        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            IIcon icon = sided.getTexture(dir, 1);
            if (icon == null) continue;

            // Get adjacent position
            int ax = x + dir.offsetX;
            int ay = y + dir.offsetY;
            int az = z + dir.offsetZ;

            // Skip if adjacent block is opaque
            Block adjacentBlock = world.getBlock(ax, ay, az);
            if (adjacentBlock.isOpaqueCube()) continue;

            // Get brightness from adjacent block position
            int brightness = block.getMixedBrightnessForBlock(world, ax, ay, az);
            t.setBrightness(brightness);

            // Apply side shading
            float shade = SIDE_SHADING[dir.ordinal()];
            t.setColorOpaque_F(shade, shade, shade);

            // Render overlay face with slight offset to prevent z-fighting
            renderOverlayFace(t, x, y, z, dir, icon);
        }

        return true;
    }

    private void renderOverlayFace(Tessellator t, int x, int y, int z, ForgeDirection dir, IIcon icon) {
        float u0 = icon.getMinU();
        float u1 = icon.getMaxU();
        float v0 = icon.getMinV();
        float v1 = icon.getMaxV();

        float minX = x;
        float minY = y;
        float minZ = z;
        float maxX = x + 1;
        float maxY = y + 1;
        float maxZ = z + 1;

        switch (dir) {
            case DOWN:
                t.addVertexWithUV(minX, minY - EPS, minZ, u0, v0);
                t.addVertexWithUV(maxX, minY - EPS, minZ, u1, v0);
                t.addVertexWithUV(maxX, minY - EPS, maxZ, u1, v1);
                t.addVertexWithUV(minX, minY - EPS, maxZ, u0, v1);
                break;
            case UP:
                t.addVertexWithUV(minX, maxY + EPS, maxZ, u0, v1);
                t.addVertexWithUV(maxX, maxY + EPS, maxZ, u1, v1);
                t.addVertexWithUV(maxX, maxY + EPS, minZ, u1, v0);
                t.addVertexWithUV(minX, maxY + EPS, minZ, u0, v0);
                break;
            case NORTH:
                t.addVertexWithUV(maxX, minY, minZ - EPS, u1, v1);
                t.addVertexWithUV(minX, minY, minZ - EPS, u0, v1);
                t.addVertexWithUV(minX, maxY, minZ - EPS, u0, v0);
                t.addVertexWithUV(maxX, maxY, minZ - EPS, u1, v0);
                break;
            case SOUTH:
                t.addVertexWithUV(minX, minY, maxZ + EPS, u0, v1);
                t.addVertexWithUV(maxX, minY, maxZ + EPS, u1, v1);
                t.addVertexWithUV(maxX, maxY, maxZ + EPS, u1, v0);
                t.addVertexWithUV(minX, maxY, maxZ + EPS, u0, v0);
                break;
            case WEST:
                t.addVertexWithUV(minX - EPS, minY, minZ, u0, v1);
                t.addVertexWithUV(minX - EPS, minY, maxZ, u1, v1);
                t.addVertexWithUV(minX - EPS, maxY, maxZ, u1, v0);
                t.addVertexWithUV(minX - EPS, maxY, minZ, u0, v0);
                break;
            case EAST:
                t.addVertexWithUV(maxX + EPS, minY, maxZ, u1, v1);
                t.addVertexWithUV(maxX + EPS, minY, minZ, u0, v1);
                t.addVertexWithUV(maxX + EPS, maxY, minZ, u0, v0);
                t.addVertexWithUV(maxX + EPS, maxY, maxZ, u1, v0);
                break;
            default:
                break;
        }
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
