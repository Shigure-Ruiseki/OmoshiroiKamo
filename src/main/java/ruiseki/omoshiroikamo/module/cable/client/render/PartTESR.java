package ruiseki.omoshiroikamo.module.cable.client.render;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.client.renderer.TessellatorManager;

import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.core.common.util.RenderUtils;
import ruiseki.omoshiroikamo.module.cable.common.cable.TECable;

public class PartTESR extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        if (!(tile instanceof TECable cable)) return;
        if (tile.getWorldObj() == null) return;

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glDisable(GL11.GL_LIGHTING);

        Tessellator tess = TessellatorManager.get();

        int brightness = tile.getWorldObj()
            .getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord, tile.zCoord, 0);

        tess.setBrightness(brightness);

        for (ICablePart part : cable.getParts()) {
            AxisAlignedBB bb = part.getCollisionBox();
            if (bb == null) continue;

            renderCubeWithCollisionBB(bb, tess, part);
        }

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    private void bindFaceTexture(ICablePart part, ForgeDirection face) {
        ForgeDirection side = part.getSide();

        if (face == side) {
            RenderUtils.bindTexture(part.getIcon());
        } else if (face == side.getOpposite()) {
            RenderUtils.bindTexture(part.getBackIcon());
        } else {
            RenderUtils.bindTexture(part.getIcon());
        }
    }

    private void renderCubeWithCollisionBB(AxisAlignedBB bb, Tessellator t, ICablePart part) {

        double eps = 0.0001;

        double minX = bb.minX - eps, minY = bb.minY - eps, minZ = bb.minZ - eps;
        double maxX = bb.maxX + eps, maxY = bb.maxY + eps, maxZ = bb.maxZ + eps;

        double du = 1.0 / 16.0;
        double dv = 1.0 / 16.0;

        // DOWN
        bindFaceTexture(part, ForgeDirection.DOWN);
        t.startDrawingQuads();
        t.addVertexWithUV(minX, minY, minZ, minX * 16 * du, minZ * 16 * dv);
        t.addVertexWithUV(maxX, minY, minZ, maxX * 16 * du, minZ * 16 * dv);
        t.addVertexWithUV(maxX, minY, maxZ, maxX * 16 * du, maxZ * 16 * dv);
        t.addVertexWithUV(minX, minY, maxZ, minX * 16 * du, maxZ * 16 * dv);
        t.draw();

        // UP
        bindFaceTexture(part, ForgeDirection.UP);
        t.startDrawingQuads();
        t.addVertexWithUV(minX, maxY, maxZ, minX * 16 * du, maxZ * 16 * dv);
        t.addVertexWithUV(maxX, maxY, maxZ, maxX * 16 * du, maxZ * 16 * dv);
        t.addVertexWithUV(maxX, maxY, minZ, maxX * 16 * du, minZ * 16 * dv);
        t.addVertexWithUV(minX, maxY, minZ, minX * 16 * du, minZ * 16 * dv);
        t.draw();

        // NORTH
        bindFaceTexture(part, ForgeDirection.NORTH);
        t.startDrawingQuads();
        t.addVertexWithUV(maxX, minY, minZ, maxX * 16 * du, minY * 16 * dv);
        t.addVertexWithUV(minX, minY, minZ, minX * 16 * du, minY * 16 * dv);
        t.addVertexWithUV(minX, maxY, minZ, minX * 16 * du, maxY * 16 * dv);
        t.addVertexWithUV(maxX, maxY, minZ, maxX * 16 * du, maxY * 16 * dv);
        t.draw();

        // SOUTH
        bindFaceTexture(part, ForgeDirection.SOUTH);
        t.startDrawingQuads();
        t.addVertexWithUV(minX, minY, maxZ, minX * 16 * du, minY * 16 * dv);
        t.addVertexWithUV(maxX, minY, maxZ, maxX * 16 * du, minY * 16 * dv);
        t.addVertexWithUV(maxX, maxY, maxZ, maxX * 16 * du, maxY * 16 * dv);
        t.addVertexWithUV(minX, maxY, maxZ, minX * 16 * du, maxY * 16 * dv);
        t.draw();

        // WEST
        bindFaceTexture(part, ForgeDirection.WEST);
        t.startDrawingQuads();
        t.addVertexWithUV(minX, minY, minZ, minZ * 16 * du, minY * 16 * dv);
        t.addVertexWithUV(minX, minY, maxZ, maxZ * 16 * du, minY * 16 * dv);
        t.addVertexWithUV(minX, maxY, maxZ, maxZ * 16 * du, maxY * 16 * dv);
        t.addVertexWithUV(minX, maxY, minZ, minZ * 16 * du, maxY * 16 * dv);
        t.draw();

        // EAST
        bindFaceTexture(part, ForgeDirection.EAST);
        t.startDrawingQuads();
        t.addVertexWithUV(maxX, minY, maxZ, maxZ * 16 * du, minY * 16 * dv);
        t.addVertexWithUV(maxX, minY, minZ, minZ * 16 * du, minY * 16 * dv);
        t.addVertexWithUV(maxX, maxY, minZ, minZ * 16 * du, maxY * 16 * dv);
        t.addVertexWithUV(maxX, maxY, maxZ, maxZ * 16 * du, maxY * 16 * dv);
        t.draw();
    }
}
