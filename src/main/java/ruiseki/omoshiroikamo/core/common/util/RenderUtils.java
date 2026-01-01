package ruiseki.omoshiroikamo.core.common.util;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;

public class RenderUtils {

    public static void renderCube(Tessellator t, float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
        IIcon icon) {

        float u0 = icon.getMinU();
        float u1 = icon.getMaxU();
        float v0 = icon.getMinV();
        float v1 = icon.getMaxV();

        float du = u1 - u0;
        float dv = v1 - v0;

        t.startDrawingQuads();

        // ==== DOWN (Y-) ====
        t.addVertexWithUV(minX, minY, maxZ, u0 + minX * du, v0 + maxZ * dv);
        t.addVertexWithUV(maxX, minY, maxZ, u0 + maxX * du, v0 + maxZ * dv);
        t.addVertexWithUV(maxX, minY, minZ, u0 + maxX * du, v0 + minZ * dv);
        t.addVertexWithUV(minX, minY, minZ, u0 + minX * du, v0 + minZ * dv);

        // ==== UP (Y+) ====
        t.addVertexWithUV(minX, maxY, maxZ, u0 + minX * du, v0 + maxZ * dv);
        t.addVertexWithUV(maxX, maxY, maxZ, u0 + maxX * du, v0 + maxZ * dv);
        t.addVertexWithUV(maxX, maxY, minZ, u0 + maxX * du, v0 + minZ * dv);
        t.addVertexWithUV(minX, maxY, minZ, u0 + minX * du, v0 + minZ * dv);

        // ==== NORTH (-Z) ====
        t.addVertexWithUV(maxX, minY, minZ, u0 + maxX * du, v0 + minY * dv);
        t.addVertexWithUV(minX, minY, minZ, u0 + minX * du, v0 + minY * dv);
        t.addVertexWithUV(minX, maxY, minZ, u0 + minX * du, v0 + maxY * dv);
        t.addVertexWithUV(maxX, maxY, minZ, u0 + maxX * du, v0 + maxY * dv);

        // ==== SOUTH (+Z) ====
        t.addVertexWithUV(minX, minY, maxZ, u0 + minX * du, v0 + minY * dv);
        t.addVertexWithUV(maxX, minY, maxZ, u0 + maxX * du, v0 + minY * dv);
        t.addVertexWithUV(maxX, maxY, maxZ, u0 + maxX * du, v0 + maxY * dv);
        t.addVertexWithUV(minX, maxY, maxZ, u0 + minX * du, v0 + maxY * dv);

        // ==== WEST (-X) ====
        t.addVertexWithUV(minX, minY, minZ, u0 + minZ * du, v0 + minY * dv);
        t.addVertexWithUV(minX, minY, maxZ, u0 + maxZ * du, v0 + minY * dv);
        t.addVertexWithUV(minX, maxY, maxZ, u0 + maxZ * du, v0 + maxY * dv);
        t.addVertexWithUV(minX, maxY, minZ, u0 + minZ * du, v0 + maxY * dv);

        // ==== EAST (+X) ====
        t.addVertexWithUV(maxX, minY, maxZ, u0 + maxZ * du, v0 + minY * dv);
        t.addVertexWithUV(maxX, minY, minZ, u0 + minZ * du, v0 + minY * dv);
        t.addVertexWithUV(maxX, maxY, minZ, u0 + minZ * du, v0 + maxY * dv);
        t.addVertexWithUV(maxX, maxY, maxZ, u0 + maxZ * du, v0 + maxY * dv);

        t.draw();
    }
}
