package ruiseki.omoshiroikamo.module.machinery.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import ruiseki.omoshiroikamo.api.block.ISidedIO;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.core.common.item.ItemWrench;

@EventBusSubscriber(side = Side.CLIENT)
public class WrenchOverlayRenderer {
    // TODO: add back-side support

    @EventBusSubscriber.Condition
    public static boolean shouldSubscribe() {
        return BackportConfigs.enableMachinery;
    }

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;

        if (player == null || mc.objectMouseOver == null) return;
        if (!(player.getHeldItem() != null && player.getHeldItem()
            .getItem() instanceof ItemWrench)) return;

        if (mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return;

        int x = mc.objectMouseOver.blockX;
        int y = mc.objectMouseOver.blockY;
        int z = mc.objectMouseOver.blockZ;

        TileEntity te = player.worldObj.getTileEntity(x, y, z);
        if (!(te instanceof ISidedIO)) return;

        ForgeDirection side = ForgeDirection.getOrientation(mc.objectMouseOver.sideHit);

        double px = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.partialTicks;
        double py = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.partialTicks;
        double pz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.partialTicks;

        GL11.glPushMatrix();
        GL11.glTranslated(-px, -py, -pz);

        drawFaceOutline(x, y, z, side);
        drawCenterFace(x, y, z, side);
        drawXFace(x, y, z, side);

        GL11.glPopMatrix();
    }

    private static void drawFaceOutline(int x, int y, int z, ForgeDirection side) {
        Tessellator t = Tessellator.instance;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glLineWidth(2.5f);
        GL11.glColor4f(1.0f, 1.0f, 1f, 1f);

        t.startDrawing(GL11.GL_LINE_LOOP);

        float min = 0f;
        float max = 1f;
        float o = 0.005f;

        switch (side) {
            case UP -> {
                t.addVertex(x, y + 1 + o, z);
                t.addVertex(x + 1, y + 1 + o, z);
                t.addVertex(x + 1, y + 1 + o, z + 1);
                t.addVertex(x, y + 1 + o, z + 1);
            }
            case DOWN -> {
                t.addVertex(x, y - o, z);
                t.addVertex(x + 1, y - o, z);
                t.addVertex(x + 1, y - o, z + 1);
                t.addVertex(x, y - o, z + 1);
            }
            case NORTH -> {
                t.addVertex(x, y, z - o);
                t.addVertex(x + 1, y, z - o);
                t.addVertex(x + 1, y + 1, z - o);
                t.addVertex(x, y + 1, z - o);
            }
            case SOUTH -> {
                t.addVertex(x, y, z + 1 + o);
                t.addVertex(x + 1, y, z + 1 + o);
                t.addVertex(x + 1, y + 1, z + 1 + o);
                t.addVertex(x, y + 1, z + 1 + o);
            }
            case WEST -> {
                t.addVertex(x - o, y, z);
                t.addVertex(x - o, y, z + 1);
                t.addVertex(x - o, y + 1, z + 1);
                t.addVertex(x - o, y + 1, z);
            }
            case EAST -> {
                t.addVertex(x + 1 + o, y, z);
                t.addVertex(x + 1 + o, y, z + 1);
                t.addVertex(x + 1 + o, y + 1, z + 1);
                t.addVertex(x + 1 + o, y + 1, z);
            }
        }

        t.draw();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    private static void drawCenterFace(int x, int y, int z, ForgeDirection side) {
        Tessellator t = Tessellator.instance;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glLineWidth(2.0f);

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        float min = 0.15f;
        float max = 0.85f;
        float o = 0.005f;

        t.startDrawing(GL11.GL_LINE_LOOP);

        switch (side) {
            case UP -> {
                t.addVertex(x + min, y + 1 + o, z + min);
                t.addVertex(x + max, y + 1 + o, z + min);
                t.addVertex(x + max, y + 1 + o, z + max);
                t.addVertex(x + min, y + 1 + o, z + max);
            }
            case DOWN -> {
                t.addVertex(x + min, y - o, z + min);
                t.addVertex(x + max, y - o, z + min);
                t.addVertex(x + max, y - o, z + max);
                t.addVertex(x + min, y - o, z + max);
            }
            case NORTH -> {
                t.addVertex(x + min, y + min, z - o);
                t.addVertex(x + max, y + min, z - o);
                t.addVertex(x + max, y + max, z - o);
                t.addVertex(x + min, y + max, z - o);
            }
            case SOUTH -> {
                t.addVertex(x + min, y + min, z + 1 + o);
                t.addVertex(x + max, y + min, z + 1 + o);
                t.addVertex(x + max, y + max, z + 1 + o);
                t.addVertex(x + min, y + max, z + 1 + o);
            }
            case WEST -> {
                t.addVertex(x - o, y + min, z + min);
                t.addVertex(x - o, y + min, z + max);
                t.addVertex(x - o, y + max, z + max);
                t.addVertex(x - o, y + max, z + min);
            }
            case EAST -> {
                t.addVertex(x + 1 + o, y + min, z + min);
                t.addVertex(x + 1 + o, y + min, z + max);
                t.addVertex(x + 1 + o, y + max, z + max);
                t.addVertex(x + 1 + o, y + max, z + min);
            }
        }

        t.draw();
    }

    private static void drawXFace(int x, int y, int z, ForgeDirection side) {
        Tessellator t = Tessellator.instance;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glLineWidth(2.5f);
        GL11.glColor4f(1f, 1f, 1f, 1f);

        float o = 0.005f;

        t.startDrawing(GL11.GL_LINES);

        switch (side) {
            case UP -> {
                // \ diagonal
                t.addVertex(x, y + 1 + o, z);
                t.addVertex(x + 1, y + 1 + o, z + 1);

                // / diagonal
                t.addVertex(x + 1, y + 1 + o, z);
                t.addVertex(x, y + 1 + o, z + 1);
            }
            case DOWN -> {
                t.addVertex(x, y - o, z);
                t.addVertex(x + 1, y - o, z + 1);

                t.addVertex(x + 1, y - o, z);
                t.addVertex(x, y - o, z + 1);
            }
            case NORTH -> {
                t.addVertex(x, y, z - o);
                t.addVertex(x + 1, y + 1, z - o);

                t.addVertex(x + 1, y, z - o);
                t.addVertex(x, y + 1, z - o);
            }
            case SOUTH -> {
                t.addVertex(x, y, z + 1 + o);
                t.addVertex(x + 1, y + 1, z + 1 + o);

                t.addVertex(x + 1, y, z + 1 + o);
                t.addVertex(x, y + 1, z + 1 + o);
            }
            case WEST -> {
                t.addVertex(x - o, y, z);
                t.addVertex(x - o, y + 1, z + 1);

                t.addVertex(x - o, y, z + 1);
                t.addVertex(x - o, y + 1, z);
            }
            case EAST -> {
                t.addVertex(x + 1 + o, y, z);
                t.addVertex(x + 1 + o, y + 1, z + 1);

                t.addVertex(x + 1 + o, y, z + 1);
                t.addVertex(x + 1 + o, y + 1, z);
            }
        }

        t.draw();
    }

}
