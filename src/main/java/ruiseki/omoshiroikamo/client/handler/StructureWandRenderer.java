package ruiseki.omoshiroikamo.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.common.item.multiblock.ItemStructureWand;

/**
 * Structure Wand の選択範囲を半透明の水色枠で描画するレンダラー
 */
@SideOnly(Side.CLIENT)
public class StructureWandRenderer {

    public static final StructureWandRenderer INSTANCE = new StructureWandRenderer();

    // 半透明水色 (RGB: 0, 200, 255 / Alpha: 0.3)
    private static final float COLOR_R = 0.0f;
    private static final float COLOR_G = 0.78f;
    private static final float COLOR_B = 1.0f;
    private static final float COLOR_A_EDGE = 0.8f;
    private static final float COLOR_A_FACE = 0.2f;

    private StructureWandRenderer() {}

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;

        if (player == null) return;

        // プレイヤーが杖を持っているかチェック
        ItemStack heldItem = player.getHeldItem();
        if (heldItem == null || !(heldItem.getItem() instanceof ItemStructureWand)) {
            return;
        }

        // ポジションを取得
        ChunkCoordinates pos1 = ItemStructureWand.getPos1FromStack(heldItem);
        ChunkCoordinates pos2 = ItemStructureWand.getPos2FromStack(heldItem);

        // ディメンションチェック
        int dim = ItemStructureWand.getDimensionFromStack(heldItem);
        if (dim != player.worldObj.provider.dimensionId) {
            return;
        }

        // pos1のみ設定されている場合は点を描画
        if (pos1 != null && pos2 == null) {
            renderPoint(pos1, event.partialTicks, player, 0.0f, 0.78f, 1.0f); // 水色
        }

        // 両方設定されている場合は枠を描画
        if (pos1 != null && pos2 != null) {
            renderBox(pos1, pos2, event.partialTicks, player);
        }
    }

    private void renderPoint(ChunkCoordinates pos, float partialTicks, EntityPlayer player, float r, float g, float b) {
        double px = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double py = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double pz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        double minX = pos.posX - px;
        double minY = pos.posY - py;
        double minZ = pos.posZ - pz;
        double maxX = pos.posX + 1 - px;
        double maxY = pos.posY + 1 - py;
        double maxZ = pos.posZ + 1 - pz;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glLineWidth(3.0f);

        // 辺を描画
        GL11.glColor4f(r, g, b, COLOR_A_EDGE);
        drawBoxEdges(minX, minY, minZ, maxX, maxY, maxZ);

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private void renderBox(ChunkCoordinates pos1, ChunkCoordinates pos2, float partialTicks, EntityPlayer player) {
        double px = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double py = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double pz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        // ブロック座標からレンダリング座標に変換（+1してブロックの端まで含める）
        double minX = Math.min(pos1.posX, pos2.posX) - px;
        double minY = Math.min(pos1.posY, pos2.posY) - py;
        double minZ = Math.min(pos1.posZ, pos2.posZ) - pz;
        double maxX = Math.max(pos1.posX, pos2.posX) + 1 - px;
        double maxY = Math.max(pos1.posY, pos2.posY) + 1 - py;
        double maxZ = Math.max(pos1.posZ, pos2.posZ) + 1 - pz;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        // 面を半透明で描画
        GL11.glColor4f(COLOR_R, COLOR_G, COLOR_B, COLOR_A_FACE);
        drawBoxFaces(minX, minY, minZ, maxX, maxY, maxZ);

        // 辺を描画
        GL11.glLineWidth(2.0f);
        GL11.glColor4f(COLOR_R, COLOR_G, COLOR_B, COLOR_A_EDGE);
        drawBoxEdges(minX, minY, minZ, maxX, maxY, maxZ);

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private void drawBoxFaces(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        Tessellator tessellator = Tessellator.instance;

        // Bottom
        tessellator.startDrawingQuads();
        tessellator.addVertex(minX, minY, minZ);
        tessellator.addVertex(maxX, minY, minZ);
        tessellator.addVertex(maxX, minY, maxZ);
        tessellator.addVertex(minX, minY, maxZ);
        tessellator.draw();

        // Top
        tessellator.startDrawingQuads();
        tessellator.addVertex(minX, maxY, minZ);
        tessellator.addVertex(minX, maxY, maxZ);
        tessellator.addVertex(maxX, maxY, maxZ);
        tessellator.addVertex(maxX, maxY, minZ);
        tessellator.draw();

        // North (-Z)
        tessellator.startDrawingQuads();
        tessellator.addVertex(minX, minY, minZ);
        tessellator.addVertex(minX, maxY, minZ);
        tessellator.addVertex(maxX, maxY, minZ);
        tessellator.addVertex(maxX, minY, minZ);
        tessellator.draw();

        // South (+Z)
        tessellator.startDrawingQuads();
        tessellator.addVertex(minX, minY, maxZ);
        tessellator.addVertex(maxX, minY, maxZ);
        tessellator.addVertex(maxX, maxY, maxZ);
        tessellator.addVertex(minX, maxY, maxZ);
        tessellator.draw();

        // West (-X)
        tessellator.startDrawingQuads();
        tessellator.addVertex(minX, minY, minZ);
        tessellator.addVertex(minX, minY, maxZ);
        tessellator.addVertex(minX, maxY, maxZ);
        tessellator.addVertex(minX, maxY, minZ);
        tessellator.draw();

        // East (+X)
        tessellator.startDrawingQuads();
        tessellator.addVertex(maxX, minY, minZ);
        tessellator.addVertex(maxX, maxY, minZ);
        tessellator.addVertex(maxX, maxY, maxZ);
        tessellator.addVertex(maxX, minY, maxZ);
        tessellator.draw();
    }

    private void drawBoxEdges(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(GL11.GL_LINES);

        // Bottom edges
        tessellator.addVertex(minX, minY, minZ);
        tessellator.addVertex(maxX, minY, minZ);

        tessellator.addVertex(maxX, minY, minZ);
        tessellator.addVertex(maxX, minY, maxZ);

        tessellator.addVertex(maxX, minY, maxZ);
        tessellator.addVertex(minX, minY, maxZ);

        tessellator.addVertex(minX, minY, maxZ);
        tessellator.addVertex(minX, minY, minZ);

        // Top edges
        tessellator.addVertex(minX, maxY, minZ);
        tessellator.addVertex(maxX, maxY, minZ);

        tessellator.addVertex(maxX, maxY, minZ);
        tessellator.addVertex(maxX, maxY, maxZ);

        tessellator.addVertex(maxX, maxY, maxZ);
        tessellator.addVertex(minX, maxY, maxZ);

        tessellator.addVertex(minX, maxY, maxZ);
        tessellator.addVertex(minX, maxY, minZ);

        // Vertical edges
        tessellator.addVertex(minX, minY, minZ);
        tessellator.addVertex(minX, maxY, minZ);

        tessellator.addVertex(maxX, minY, minZ);
        tessellator.addVertex(maxX, maxY, minZ);

        tessellator.addVertex(maxX, minY, maxZ);
        tessellator.addVertex(maxX, maxY, maxZ);

        tessellator.addVertex(minX, minY, maxZ);
        tessellator.addVertex(minX, maxY, maxZ);

        tessellator.draw();
    }
}
