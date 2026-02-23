package ruiseki.omoshiroikamo.core.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

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

    public static void rotateIfSneaking(EntityPlayer player) {
        if (player.isSneaking()) {
            applySneakingRotation();
        }
    }

    public static void applySneakingRotation() {
        GL11.glRotatef(28.65F, 1F, 0F, 0F);
    }

    public static void translateToChest() {
        GL11.glTranslatef(0F, 0.9F, 0F);
    }

    public static void translateToHead(EntityPlayer player) {
        GL11.glTranslated(
            0,
            (player != Minecraft.getMinecraft().thePlayer ? 1.62F : 0F) - player.getDefaultEyeHeight()
                + (player.isSneaking() ? 0.0625 : 0),
            0);
    }

    public static void translateToLeftArm() {
        GL11.glTranslatef(0.35F, 1.2F, 0F);
    }

    public static void translateToRightArm() {
        GL11.glTranslatef(-0.35F, 1.2F, 0F);
    }

    public static void translateToLegs() {
        GL11.glTranslatef(0F, 0.5F, 0F);
    }

    public static void translateToBoots() {
        GL11.glTranslatef(0F, 0.1F, 0F);
    }

    public static enum RenderType {

        BODY,

        HEAD;
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

        // UV corners: TL(0), TR(1), BR(2), BL(3)
        // Rotation cyclically shifts which corner maps to which vertex
        float[] cu = { minU, maxU, maxU, minU };
        float[] cv = { minV, minV, maxV, maxV };
        int r = rotation.ordinal();
        for (int i = 0; i < 4; i++) {
            int ci = (i - r) & 3;
            u[i] = cu[ci];
            v[i] = cv[ci];
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

        // A single-axis flip (H or V) reverses CW↔CCW (ordinal 1↔3 via XOR 2)
        if (flip == Flip.HORIZONTAL || flip == Flip.VERTICAL) {
            if (rotation == Rotation.CLOCKWISE || rotation == Rotation.COUNTER_CLOCKWISE) {
                rotation = Rotation.VALUES[rotation.ordinal() ^ 2];
            }
        }

        renderFace(t, dir, x, y, z, icon, offset, rotation, flip);
    }

    public static void renderOverlay(Tessellator t, IIcon icon) {
        if (icon == null) return;

        float min = 0f;
        float max = 1f;
        float eps = 0.001f;

        float u0 = icon.getMinU();
        float u1 = icon.getMaxU();
        float v0 = icon.getMinV();
        float v1 = icon.getMaxV();

        t.startDrawingQuads();

        // +Z (FRONT)
        t.addVertexWithUV(min, min, max + eps, u0, v1);
        t.addVertexWithUV(max, min, max + eps, u1, v1);
        t.addVertexWithUV(max, max, max + eps, u1, v0);
        t.addVertexWithUV(min, max, max + eps, u0, v0);

        // -Z (BACK)
        t.addVertexWithUV(max, min, min - eps, u0, v1);
        t.addVertexWithUV(min, min, min - eps, u1, v1);
        t.addVertexWithUV(min, max, min - eps, u1, v0);
        t.addVertexWithUV(max, max, min - eps, u0, v0);

        // -X (LEFT)
        t.addVertexWithUV(min - eps, min, min, u0, v1);
        t.addVertexWithUV(min - eps, min, max, u1, v1);
        t.addVertexWithUV(min - eps, max, max, u1, v0);
        t.addVertexWithUV(min - eps, max, min, u0, v0);

        // +X (RIGHT)
        t.addVertexWithUV(max + eps, min, max, u0, v1);
        t.addVertexWithUV(max + eps, min, min, u1, v1);
        t.addVertexWithUV(max + eps, max, min, u1, v0);
        t.addVertexWithUV(max + eps, max, max, u0, v0);

        // +Y (TOP)
        t.addVertexWithUV(min, max + eps, max, u0, v1);
        t.addVertexWithUV(max, max + eps, max, u1, v1);
        t.addVertexWithUV(max, max + eps, min, u1, v0);
        t.addVertexWithUV(min, max + eps, min, u0, v0);

        // -Y (BOTTOM)
        t.addVertexWithUV(min, min - eps, min, u0, v1);
        t.addVertexWithUV(max, min - eps, min, u1, v1);
        t.addVertexWithUV(max, min - eps, max, u1, v0);
        t.addVertexWithUV(min, min - eps, max, u0, v0);

        t.draw();
    }
}
