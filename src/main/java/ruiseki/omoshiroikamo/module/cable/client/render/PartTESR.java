package ruiseki.omoshiroikamo.module.cable.client.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.client.renderer.TessellatorManager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.core.common.util.RenderUtils;
import ruiseki.omoshiroikamo.module.cable.common.cable.TECable;

@SideOnly(Side.CLIENT)
public class PartTESR extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        if (!(tile instanceof TECable cable)) return;

        var parts = cable.getParts();
        if (parts.isEmpty()) return;

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        Tessellator tess = TessellatorManager.get();

        // Group parts by texture to minimize texture bind calls
        Map<ResourceLocation, List<AxisAlignedBB>> partsByTexture = new HashMap<>();
        for (ICablePart part : parts) {
            AxisAlignedBB bb = part.getCollisionBox();
            if (bb == null) continue;
            ResourceLocation icon = part.getIcon();
            if (icon == null) continue;

            partsByTexture.computeIfAbsent(icon, k -> new ArrayList<>())
                .add(bb);
        }

        // Render each texture group with a single bind
        for (Map.Entry<ResourceLocation, List<AxisAlignedBB>> entry : partsByTexture.entrySet()) {
            RenderUtils.bindTexture(entry.getKey());
            for (AxisAlignedBB bb : entry.getValue()) {
                renderCubeWithCollisionBB(bb, tess);
            }
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
        t.addVertexWithUV(minX, minY, minZ, u0 + minX * 16 * du, v0 + minZ * 16 * dv);
        t.addVertexWithUV(maxX, minY, minZ, u0 + maxX * 16 * du, v0 + minZ * 16 * dv);
        t.addVertexWithUV(maxX, minY, maxZ, u0 + maxX * 16 * du, v0 + maxZ * 16 * dv);
        t.addVertexWithUV(minX, minY, maxZ, u0 + minX * 16 * du, v0 + maxZ * 16 * dv);

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
