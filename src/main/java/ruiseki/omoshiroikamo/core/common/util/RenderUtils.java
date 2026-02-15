package ruiseki.omoshiroikamo.core.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.enumerable.Flip;
import com.gtnewhorizon.structurelib.alignment.enumerable.Rotation;

public class RenderUtils {

    public static void renderCube(Tessellator t, double minX, double minY, double minZ, double maxX, double maxY,
        double maxZ, IIcon icon) {
        renderCube(t, (float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ, icon);
    }

    public static void renderCube(Tessellator t, float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
        IIcon icon) {

        float u0 = icon.getMinU();
        float u1 = icon.getMaxU();
        float v0 = icon.getMinV();
        float v1 = icon.getMaxV();

        float du = u1 - u0;
        float dv = v1 - v0;

        // ==== DOWN (Y-) ====
        t.setNormal(0.0F, -1.0F, 0.0F);
        t.addVertexWithUV(minX, minY, maxZ, u0 + minX * du, v0 + maxZ * dv);
        t.addVertexWithUV(maxX, minY, maxZ, u0 + maxX * du, v0 + maxZ * dv);
        t.addVertexWithUV(maxX, minY, minZ, u0 + maxX * du, v0 + minZ * dv);
        t.addVertexWithUV(minX, minY, minZ, u0 + minX * du, v0 + minZ * dv);

        // ==== UP (Y+) ====
        t.setNormal(0.0F, 1.0F, 0.0F);
        t.addVertexWithUV(minX, maxY, maxZ, u0 + minX * du, v0 + maxZ * dv);
        t.addVertexWithUV(maxX, maxY, maxZ, u0 + maxX * du, v0 + maxZ * dv);
        t.addVertexWithUV(maxX, maxY, minZ, u0 + maxX * du, v0 + minZ * dv);
        t.addVertexWithUV(minX, maxY, minZ, u0 + minX * du, v0 + minZ * dv);

        // ==== NORTH (-Z) ====
        t.setNormal(0.0F, 0.0F, -1.0F);
        t.addVertexWithUV(maxX, minY, minZ, u0 + maxX * du, v1 - minY * dv);
        t.addVertexWithUV(minX, minY, minZ, u0 + minX * du, v1 - minY * dv);
        t.addVertexWithUV(minX, maxY, minZ, u0 + minX * du, v1 - maxY * dv);
        t.addVertexWithUV(maxX, maxY, minZ, u0 + maxX * du, v1 - maxY * dv);

        // ==== SOUTH (+Z) ====
        t.setNormal(0.0F, 0.0F, 1.0F);
        t.addVertexWithUV(minX, minY, maxZ, u0 + minX * du, v1 - minY * dv);
        t.addVertexWithUV(maxX, minY, maxZ, u0 + maxX * du, v1 - minY * dv);
        t.addVertexWithUV(maxX, maxY, maxZ, u0 + maxX * du, v1 - maxY * dv);
        t.addVertexWithUV(minX, maxY, maxZ, u0 + minX * du, v1 - maxY * dv);

        // ==== WEST (-X) ====
        t.setNormal(-1.0F, 0.0F, 0.0F);
        t.addVertexWithUV(minX, minY, minZ, u0 + minZ * du, v1 - minY * dv);
        t.addVertexWithUV(minX, minY, maxZ, u0 + maxZ * du, v1 - minY * dv);
        t.addVertexWithUV(minX, maxY, maxZ, u0 + maxZ * du, v1 - maxY * dv);
        t.addVertexWithUV(minX, maxY, minZ, u0 + minZ * du, v1 - maxY * dv);

        // ==== EAST (+X) ====
        t.setNormal(1.0F, 0.0F, 0.0F);
        t.addVertexWithUV(maxX, minY, maxZ, u0 + maxZ * du, v1 - minY * dv);
        t.addVertexWithUV(maxX, minY, minZ, u0 + minZ * du, v1 - minY * dv);
        t.addVertexWithUV(maxX, maxY, minZ, u0 + minZ * du, v1 - maxY * dv);
        t.addVertexWithUV(maxX, maxY, maxZ, u0 + maxZ * du, v1 - maxY * dv);

    }

    public static void renderCubeRotatedTopBottom(Tessellator t, float minX, float minY, float minZ, float maxX,
        float maxY, float maxZ, IIcon icon) {

        float u0 = icon.getMinU();
        float u1 = icon.getMaxU();
        float v0 = icon.getMinV();
        float v1 = icon.getMaxV();

        float du = u1 - u0;
        float dv = v1 - v0;

        // ==== DOWN (Y-) ====
        t.setNormal(0.0F, -1.0F, 0.0F);
        t.addVertexWithUV(minX, minY, maxZ, u0 + maxX * du, v0 + maxZ * dv);
        t.addVertexWithUV(maxX, minY, maxZ, u0 + maxX * du, v0 + minZ * dv);
        t.addVertexWithUV(maxX, minY, minZ, u0 + minX * du, v0 + minZ * dv);
        t.addVertexWithUV(minX, minY, minZ, u0 + minX * du, v0 + maxZ * dv);

        // ==== UP (Y+) ====
        t.setNormal(0.0F, 1.0F, 0.0F);
        t.addVertexWithUV(minX, maxY, maxZ, u0 + maxX * du, v0 + maxZ * dv);
        t.addVertexWithUV(maxX, maxY, maxZ, u0 + maxX * du, v0 + minZ * dv);
        t.addVertexWithUV(maxX, maxY, minZ, u0 + minX * du, v0 + minZ * dv);
        t.addVertexWithUV(minX, maxY, minZ, u0 + minX * du, v0 + maxZ * dv);

        // ==== NORTH (-Z) ====
        t.setNormal(0.0F, 0.0F, -1.0F);
        t.addVertexWithUV(maxX, minY, minZ, u0 + maxX * du, v1 - minY * dv);
        t.addVertexWithUV(minX, minY, minZ, u0 + minX * du, v1 - minY * dv);
        t.addVertexWithUV(minX, maxY, minZ, u0 + minX * du, v1 - maxY * dv);
        t.addVertexWithUV(maxX, maxY, minZ, u0 + maxX * du, v1 - maxY * dv);

        // ==== SOUTH (+Z) ====
        t.setNormal(0.0F, 0.0F, 1.0F);
        t.addVertexWithUV(minX, minY, maxZ, u0 + minX * du, v1 - minY * dv);
        t.addVertexWithUV(maxX, minY, maxZ, u0 + maxX * du, v1 - minY * dv);
        t.addVertexWithUV(maxX, maxY, maxZ, u0 + maxX * du, v1 - maxY * dv);
        t.addVertexWithUV(minX, maxY, maxZ, u0 + minX * du, v1 - maxY * dv);

        // ==== WEST (-X) ====
        t.setNormal(-1.0F, 0.0F, 0.0F);
        t.addVertexWithUV(minX, minY, minZ, u0 + minZ * du, v1 - minY * dv);
        t.addVertexWithUV(minX, minY, maxZ, u0 + maxZ * du, v1 - minY * dv);
        t.addVertexWithUV(minX, maxY, maxZ, u0 + maxZ * du, v1 - maxY * dv);
        t.addVertexWithUV(minX, maxY, minZ, u0 + minZ * du, v1 - maxY * dv);

        // ==== EAST (+X) ====
        t.setNormal(1.0F, 0.0F, 0.0F);
        t.addVertexWithUV(maxX, minY, maxZ, u0 + maxZ * du, v1 - minY * dv);
        t.addVertexWithUV(maxX, minY, minZ, u0 + minZ * du, v1 - minY * dv);
        t.addVertexWithUV(maxX, maxY, minZ, u0 + minZ * du, v1 - maxY * dv);
        t.addVertexWithUV(maxX, maxY, maxZ, u0 + maxZ * du, v1 - maxY * dv);

    }

    public static TextureManager engine() {
        return Minecraft.getMinecraft().renderEngine;
    }

    public static void bindTexture(String string) {
        engine().bindTexture(new ResourceLocation(string));
    }

    public static void bindTexture(ResourceLocation tex) {
        engine().bindTexture(tex);
    }

    /**
     * Renders a single face with the specified icon, offset, and UV rotation.
     */
    public static void renderFace(Tessellator t, ForgeDirection dir, double x, double y, double z, IIcon icon,
        float offset, Rotation rotation) {
        renderFace(t, dir, x, y, z, icon, offset, rotation, Flip.NONE);
    }

    /**
     * Renders a single face with the specified icon, offset, UV rotation, and flip.
     *
     * @param t        The Tessellator instance
     * @param dir      The direction (face) to render
     * @param x        X coordinate
     * @param y        Y coordinate
     * @param z        Z coordinate
     * @param icon     The texture icon to use
     * @param offset   Offset from the block surface (to prevent Z-fighting)
     * @param rotation Rotation to apply to UV coordinates
     * @param flip     Flip to apply to UV coordinates
     */
    public static void renderFace(Tessellator t, ForgeDirection dir, double x, double y, double z, IIcon icon,
        float offset, Rotation rotation, Flip flip) {
        float minU = icon.getMinU();
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float maxV = icon.getMaxV();

        // Flip first
        if (flip == Flip.HORIZONTAL || flip == Flip.BOTH) {
            float tmp = minU;
            minU = maxU;
            maxU = tmp;
        }
        if (flip == Flip.VERTICAL || flip == Flip.BOTH) {
            float tmp = minV;
            minV = maxV;
            maxV = tmp;
        }

        float[] u = new float[4];
        float[] v = new float[4];

        // Then rotate
        switch (rotation) {
            case CLOCKWISE:
                u[0] = minU;
                v[0] = maxV;
                u[1] = minU;
                v[1] = minV;
                u[2] = maxU;
                v[2] = minV;
                u[3] = maxU;
                v[3] = maxV;
                break;
            case UPSIDE_DOWN:
                u[0] = maxU;
                v[0] = maxV;
                u[1] = minU;
                v[1] = maxV;
                u[2] = minU;
                v[2] = minV;
                u[3] = maxU;
                v[3] = minV;
                break;
            case COUNTER_CLOCKWISE:
                u[0] = maxU;
                v[0] = minV;
                u[1] = maxU;
                v[1] = maxV;
                u[2] = minU;
                v[2] = maxV;
                u[3] = minU;
                v[3] = minV;
                break;
            default:
                u[0] = minU;
                v[0] = minV;
                u[1] = maxU;
                v[1] = minV;
                u[2] = maxU;
                v[2] = maxV;
                u[3] = minU;
                v[3] = maxV;
                break;
        }

        double eps = offset;

        switch (dir) {
            case DOWN:
                t.addVertexWithUV(x, y - eps, z, u[0], v[0]);
                t.addVertexWithUV(x + 1, y - eps, z, u[1], v[1]);
                t.addVertexWithUV(x + 1, y - eps, z + 1, u[2], v[2]);
                t.addVertexWithUV(x, y - eps, z + 1, u[3], v[3]);
                break;
            case UP:
                t.addVertexWithUV(x, y + 1 + eps, z + 1, u[3], v[3]);
                t.addVertexWithUV(x + 1, y + 1 + eps, z + 1, u[2], v[2]);
                t.addVertexWithUV(x + 1, y + 1 + eps, z, u[1], v[1]);
                t.addVertexWithUV(x, y + 1 + eps, z, u[0], v[0]);
                break;
            case NORTH:
                t.addVertexWithUV(x + 1, y, z - eps, u[2], v[2]);
                t.addVertexWithUV(x, y, z - eps, u[3], v[3]);
                t.addVertexWithUV(x, y + 1, z - eps, u[0], v[0]);
                t.addVertexWithUV(x + 1, y + 1, z - eps, u[1], v[1]);
                break;
            case SOUTH:
                t.addVertexWithUV(x, y, z + 1 + eps, u[3], v[3]);
                t.addVertexWithUV(x + 1, y, z + 1 + eps, u[2], v[2]);
                t.addVertexWithUV(x + 1, y + 1, z + 1 + eps, u[1], v[1]);
                t.addVertexWithUV(x, y + 1, z + 1 + eps, u[0], v[0]);
                break;
            case WEST:
                t.addVertexWithUV(x - eps, y, z, u[3], v[3]);
                t.addVertexWithUV(x - eps, y, z + 1, u[2], v[2]);
                t.addVertexWithUV(x - eps, y + 1, z + 1, u[1], v[1]);
                t.addVertexWithUV(x - eps, y + 1, z, u[0], v[0]);
                break;
            case EAST:
                t.addVertexWithUV(x + 1 + eps, y, z + 1, u[2], v[2]);
                t.addVertexWithUV(x + 1 + eps, y, z, u[3], v[3]);
                t.addVertexWithUV(x + 1 + eps, y + 1, z, u[0], v[0]);
                t.addVertexWithUV(x + 1 + eps, y + 1, z + 1, u[1], v[1]);
                break;
            case UNKNOWN:
                break;
        }
    }

    /**
     * Renders a single face with chirality correction for NORTH/EAST/DOWN faces.
     */
    public static void renderFaceCorrected(Tessellator t, ForgeDirection dir, double x, double y, double z, IIcon icon,
        float offset, Rotation rotation, Flip flip) {

        // NORTH, EAST, DOWN: toggle H to compensate inherent UV mirror
        if (dir == ForgeDirection.NORTH || dir == ForgeDirection.EAST || dir == ForgeDirection.DOWN) {
            flip = Flip.VALUES[flip.ordinal() ^ 1];
        }

        // A single-axis flip (H or V) reverses visual rotation direction
        if (flip == Flip.HORIZONTAL || flip == Flip.VERTICAL) {
            rotation = switch (rotation) {
                case CLOCKWISE -> Rotation.COUNTER_CLOCKWISE;
                case COUNTER_CLOCKWISE -> Rotation.CLOCKWISE;
                default -> rotation;
            };
        }

        renderFace(t, dir, x, y, z, icon, offset, rotation, flip);
    }
}
