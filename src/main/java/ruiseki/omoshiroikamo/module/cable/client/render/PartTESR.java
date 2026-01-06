package ruiseki.omoshiroikamo.module.cable.client.render;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.client.renderer.TessellatorManager;

import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.core.common.util.RenderUtils;
import ruiseki.omoshiroikamo.module.cable.common.cable.TECable;

public class PartTESR extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float tick) {
        if (!(tile instanceof TECable cable)) return;

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        Tessellator tess = TessellatorManager.get();

        for (ICablePart part : cable.getParts()) {
            AxisAlignedBB bb = part.getCollisionBox();
            if (bb == null) continue;

            RenderUtils.bindTexture(part.getIcon());
            renderCubeWithCollisionBB(bb, tess);
        }

        GL11.glPopMatrix();
    }

    private void renderCubeWithCollisionBB(AxisAlignedBB bb, Tessellator t) {

        double epsilon = 0.0001;

        double minX = bb.minX - epsilon, minY = bb.minY - epsilon, minZ = bb.minZ - epsilon;
        double maxX = bb.maxX + epsilon, maxY = bb.maxY + epsilon, maxZ = bb.maxZ + epsilon;

        double u0 = 0.0, v0 = 0.0;
        double du = 1.0 / 16.0;
        double dv = 1.0 / 16.0;

        t.startDrawingQuads();

        // DOWN (Y-)
        t.addVertexWithUV(minX, minY, maxZ, u0 + minX * 16 * du, v0 + maxZ * 16 * dv);
        t.addVertexWithUV(maxX, minY, maxZ, u0 + maxX * 16 * du, v0 + maxZ * 16 * dv);
        t.addVertexWithUV(maxX, minY, minZ, u0 + maxX * 16 * du, v0 + minZ * 16 * dv);
        t.addVertexWithUV(minX, minY, minZ, u0 + minX * 16 * du, v0 + minZ * 16 * dv);

        // UP (Y+)
        t.addVertexWithUV(minX, maxY, maxZ, u0 + minX * 16 * du, v0 + maxZ * 16 * dv);
        t.addVertexWithUV(maxX, maxY, maxZ, u0 + maxX * 16 * du, v0 + maxZ * 16 * dv);
        t.addVertexWithUV(maxX, maxY, minZ, u0 + maxX * 16 * du, v0 + minZ * 16 * dv);
        t.addVertexWithUV(minX, maxY, minZ, u0 + minX * 16 * du, v0 + minZ * 16 * dv);

        // NORTH (-Z)
        t.addVertexWithUV(maxX, minY, minZ, u0 + maxX * 16 * du, v0 + minY * 16 * dv);
        t.addVertexWithUV(minX, minY, minZ, u0 + minX * 16 * du, v0 + minY * 16 * dv);
        t.addVertexWithUV(minX, maxY, minZ, u0 + minX * 16 * du, v0 + maxY * 16 * dv);
        t.addVertexWithUV(maxX, maxY, minZ, u0 + maxX * 16 * du, v0 + maxY * 16 * dv);

        // SOUTH (+Z)
        t.addVertexWithUV(minX, minY, maxZ, u0 + minX * 16 * du, v0 + minY * 16 * dv);
        t.addVertexWithUV(maxX, minY, maxZ, u0 + maxX * 16 * du, v0 + minY * 16 * dv);
        t.addVertexWithUV(maxX, maxY, maxZ, u0 + maxX * 16 * du, v0 + maxY * 16 * dv);
        t.addVertexWithUV(minX, maxY, maxZ, u0 + minX * 16 * du, v0 + maxY * 16 * dv);

        // WEST (-X)
        t.addVertexWithUV(minX, minY, minZ, u0 + minZ * 16 * du, v0 + minY * 16 * dv);
        t.addVertexWithUV(minX, minY, maxZ, u0 + maxZ * 16 * du, v0 + minY * 16 * dv);
        t.addVertexWithUV(minX, maxY, maxZ, u0 + maxZ * 16 * du, v0 + maxY * 16 * dv);
        t.addVertexWithUV(minX, maxY, minZ, u0 + minZ * 16 * du, v0 + maxY * 16 * dv);

        // EAST (+X)
        t.addVertexWithUV(maxX, minY, maxZ, u0 + maxZ * 16 * du, v0 + minY * 16 * dv);
        t.addVertexWithUV(maxX, minY, minZ, u0 + minZ * 16 * du, v0 + minY * 16 * dv);
        t.addVertexWithUV(maxX, maxY, minZ, u0 + minZ * 16 * du, v0 + maxY * 16 * dv);
        t.addVertexWithUV(maxX, maxY, maxZ, u0 + maxZ * 16 * du, v0 + maxY * 16 * dv);

        t.draw();
    }

}
