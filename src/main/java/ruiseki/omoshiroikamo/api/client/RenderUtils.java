package ruiseki.omoshiroikamo.api.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

public class RenderUtils {

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
        t.addVertexWithUV(maxX, minY, minZ, u0 + maxX * du, v1 - minY * dv);
        t.addVertexWithUV(minX, minY, minZ, u0 + minX * du, v1 - minY * dv);
        t.addVertexWithUV(minX, maxY, minZ, u0 + minX * du, v1 - maxY * dv);
        t.addVertexWithUV(maxX, maxY, minZ, u0 + maxX * du, v1 - maxY * dv);

        // ==== SOUTH (+Z) ====
        t.addVertexWithUV(minX, minY, maxZ, u0 + minX * du, v1 - minY * dv);
        t.addVertexWithUV(maxX, minY, maxZ, u0 + maxX * du, v1 - minY * dv);
        t.addVertexWithUV(maxX, maxY, maxZ, u0 + maxX * du, v1 - maxY * dv);
        t.addVertexWithUV(minX, maxY, maxZ, u0 + minX * du, v1 - maxY * dv);

        // ==== WEST (-X) ====
        t.addVertexWithUV(minX, minY, minZ, u0 + minZ * du, v1 - minY * dv);
        t.addVertexWithUV(minX, minY, maxZ, u0 + maxZ * du, v1 - minY * dv);
        t.addVertexWithUV(minX, maxY, maxZ, u0 + maxZ * du, v1 - maxY * dv);
        t.addVertexWithUV(minX, maxY, minZ, u0 + minZ * du, v1 - maxY * dv);

        // ==== EAST (+X) ====
        t.addVertexWithUV(maxX, minY, maxZ, u0 + maxZ * du, v1 - minY * dv);
        t.addVertexWithUV(maxX, minY, minZ, u0 + minZ * du, v1 - minY * dv);
        t.addVertexWithUV(maxX, maxY, minZ, u0 + minZ * du, v1 - maxY * dv);
        t.addVertexWithUV(maxX, maxY, maxZ, u0 + maxZ * du, v1 - maxY * dv);

        t.draw();
    }

    public static void renderInventoryCube(RenderBlocks renderer, Block block, int metadata) {
        renderer.renderFaceYNeg(block, 0, 0, 0, block.getIcon(0, metadata));
        renderer.renderFaceYPos(block, 0, 0, 0, block.getIcon(1, metadata));
        renderer.renderFaceZNeg(block, 0, 0, 0, block.getIcon(2, metadata));
        renderer.renderFaceZPos(block, 0, 0, 0, block.getIcon(3, metadata));
        renderer.renderFaceXNeg(block, 0, 0, 0, block.getIcon(4, metadata));
        renderer.renderFaceXPos(block, 0, 0, 0, block.getIcon(5, metadata));
    }

    public static void renderSingleFace(Tessellator t, int side, float minX, float minY, float minZ, float maxX,
        float maxY, float maxZ, IIcon icon) {
        float u0 = icon.getMinU();
        float u1 = icon.getMaxU();
        float v0 = icon.getMinV();
        float v1 = icon.getMaxV();

        switch (side) {
            case 0: // DOWN
                t.addVertexWithUV(minX, minY, maxZ, u0, v1);
                t.addVertexWithUV(maxX, minY, maxZ, u1, v1);
                t.addVertexWithUV(maxX, minY, minZ, u1, v0);
                t.addVertexWithUV(minX, minY, minZ, u0, v0);
                break;

            case 1: // UP
                t.addVertexWithUV(minX, maxY, minZ, u0, v0);
                t.addVertexWithUV(maxX, maxY, minZ, u1, v0);
                t.addVertexWithUV(maxX, maxY, maxZ, u1, v1);
                t.addVertexWithUV(minX, maxY, maxZ, u0, v1);
                break;

            case 2: // NORTH
                t.addVertexWithUV(maxX, minY, minZ, u1, v1);
                t.addVertexWithUV(minX, minY, minZ, u0, v1);
                t.addVertexWithUV(minX, maxY, minZ, u0, v0);
                t.addVertexWithUV(maxX, maxY, minZ, u1, v0);
                break;

            case 3: // SOUTH
                t.addVertexWithUV(minX, minY, maxZ, u0, v1);
                t.addVertexWithUV(maxX, minY, maxZ, u1, v1);
                t.addVertexWithUV(maxX, maxY, maxZ, u1, v0);
                t.addVertexWithUV(minX, maxY, maxZ, u0, v0);
                break;

            case 4: // WEST
                t.addVertexWithUV(minX, minY, minZ, u0, v1);
                t.addVertexWithUV(minX, minY, maxZ, u1, v1);
                t.addVertexWithUV(minX, maxY, maxZ, u1, v0);
                t.addVertexWithUV(minX, maxY, minZ, u0, v0);
                break;

            case 5: // EAST
                t.addVertexWithUV(maxX, minY, maxZ, u1, v1);
                t.addVertexWithUV(maxX, minY, minZ, u0, v1);
                t.addVertexWithUV(maxX, maxY, minZ, u0, v0);
                t.addVertexWithUV(maxX, maxY, maxZ, u1, v0);
                break;
        }
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
