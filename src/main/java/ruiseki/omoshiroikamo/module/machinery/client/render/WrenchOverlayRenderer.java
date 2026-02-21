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
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.core.item.ItemWrench;
import ruiseki.omoshiroikamo.core.tileentity.ISidedIO;

@EventBusSubscriber(side = Side.CLIENT)
public class WrenchOverlayRenderer {

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

        // Calculate hit vector relative to the block center
        double hitX = mc.objectMouseOver.hitVec.xCoord - x;
        double hitY = mc.objectMouseOver.hitVec.yCoord - y;
        double hitZ = mc.objectMouseOver.hitVec.zCoord - z;

        double px = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.partialTicks;
        double py = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.partialTicks;
        double pz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.partialTicks;

        GL11.glPushMatrix();
        GL11.glTranslated(-px, -py, -pz);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_CULL_FACE);

        // Always draw static outlines
        drawFaceOutline(x, y, z, side);
        drawGridLines(x, y, z, side);

        // Draw dynamic highlight based on hover position
        drawHighlight(x, y, z, side, (float) hitX, (float) hitY, (float) hitZ);

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glPopMatrix();
    }

    private static void drawFaceOutline(int x, int y, int z, ForgeDirection side) {
        if (side == ForgeDirection.UNKNOWN) return;
        Tessellator t = Tessellator.instance;
        GL11.glLineWidth(2.5f);
        GL11.glColor4f(1.0f, 1.0f, 1f, 1f);

        t.startDrawing(GL11.GL_LINE_LOOP);
        float o = 0.005f;

        addVertex(t, x, y, z, side, 0, 0, o);
        addVertex(t, x, y, z, side, 1, 0, o);
        addVertex(t, x, y, z, side, 1, 1, o);
        addVertex(t, x, y, z, side, 0, 1, o);

        t.draw();
    }

    private static void drawGridLines(int x, int y, int z, ForgeDirection side) {
        if (side == ForgeDirection.UNKNOWN) return;
        Tessellator t = Tessellator.instance;
        GL11.glLineWidth(1.0f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        float min = 0.20f;
        float max = 0.80f;
        float o = 0.005f;

        t.startDrawing(GL11.GL_LINES);

        // Horizontal lines (v is constant)
        addVertex(t, x, y, z, side, 0, min, o);
        addVertex(t, x, y, z, side, 1, min, o);
        addVertex(t, x, y, z, side, 0, max, o);
        addVertex(t, x, y, z, side, 1, max, o);

        // Vertical lines (u is constant)
        addVertex(t, x, y, z, side, min, 0, o);
        addVertex(t, x, y, z, side, min, 1, o);
        addVertex(t, x, y, z, side, max, 0, o);
        addVertex(t, x, y, z, side, max, 1, o);

        t.draw();
    }

    private static void drawHighlight(int x, int y, int z, ForgeDirection side, float hitX, float hitY, float hitZ) {
        if (side == ForgeDirection.UNKNOWN) return;
        final float BORDER = 0.20f;

        // Determine UV coordinates based on side
        float uHit = 0, vHit = 0;
        switch (side) {
            case UP:
                uHit = hitX;
                vHit = hitZ;
                break;
            case DOWN:
                uHit = hitX;
                vHit = hitZ;
                break; // DOWN usually mirrors depending on rotation, but here we treat flat
            case NORTH:
                uHit = hitX;
                vHit = hitY;
                break;
            case SOUTH:
                uHit = hitX;
                vHit = hitY;
                break;
            case WEST:
                uHit = hitZ;
                vHit = hitY;
                break;
            case EAST:
                uHit = hitZ;
                vHit = hitY;
                break;
            default:
                return;
        }

        int hSection = getSection(uHit, BORDER);
        int vSection = getSection(vHit, BORDER);

        // Highlight color
        boolean isCenter = hSection == 1 && vSection == 1;
        float r = isCenter ? 0.0f : 1.0f;
        float g = 1.0f;
        float b = 0.0f;
        float alpha = 0.3f;

        Tessellator t = Tessellator.instance;
        GL11.glColor4f(r, g, b, alpha);
        t.startDrawing(GL11.GL_QUADS);

        // Determine drawing bounds based on section
        float hMin = hSection == 0 ? 0f : (hSection == 1 ? BORDER : 1 - BORDER);
        float hMax = hSection == 0 ? BORDER : (hSection == 1 ? 1 - BORDER : 1f);
        float vMin = vSection == 0 ? 0f : (vSection == 1 ? BORDER : 1 - BORDER);
        float vMax = vSection == 0 ? BORDER : (vSection == 1 ? 1 - BORDER : 1f);

        float o = 0.005f;

        addVertex(t, x, y, z, side, hMin, vMin, o);
        addVertex(t, x, y, z, side, hMax, vMin, o);
        addVertex(t, x, y, z, side, hMax, vMax, o);
        addVertex(t, x, y, z, side, hMin, vMax, o);

        t.draw();
    }

    private static int getSection(float hit, float border) {
        return hit < border ? 0 : (hit > 1 - border ? 2 : 1);
    }

    private static void addVertex(Tessellator t, int x, int y, int z, ForgeDirection side, double u, double v,
        double o) {
        switch (side) {
            case UP -> t.addVertex(x + u, y + 1 + o, z + v);
            case DOWN -> t.addVertex(x + u, y - o, z + v);
            case NORTH -> t.addVertex(x + u, y + v, z - o);
            case SOUTH -> t.addVertex(x + u, y + v, z + 1 + o);
            case WEST -> t.addVertex(x - o, y + v, z + u);
            case EAST -> t.addVertex(x + 1 + o, y + v, z + u);
            default -> {
                // Do nothing
            }
        }
    }

}
