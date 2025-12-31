package ruiseki.omoshiroikamo.module.machinery.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import ruiseki.omoshiroikamo.api.io.ISidedIO;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;

@EventBusSubscriber(side = Side.CLIENT)
public class PortOverlayRenderer {

    @EventBusSubscriber.Condition
    public static boolean shouldSubscribe() {
        return BackportConfigs.useMachinery;
    }

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        if (player == null) return;

        double px = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.partialTicks;
        double py = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.partialTicks;
        double pz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.partialTicks;

        GL11.glPushMatrix();
        GL11.glTranslated(-px, -py, -pz);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glPolygonOffset(-1.0f, -10.0f);

        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();

        for (Object obj : mc.theWorld.loadedTileEntityList) {
            if (!(obj instanceof IModularPort port)) continue;

            TileEntity te = (TileEntity) obj;

            if (player.getDistanceSq(te.xCoord + 0.5, te.yCoord + 0.5, te.zCoord + 0.5) > 64 * 64)
                continue;

            for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
                drawPortFace(port, te.xCoord, te.yCoord, te.zCoord, side, t);
            }
        }

        t.draw();

        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    private static void drawPortFace(IModularPort port, int x, int y, int z, ForgeDirection side, Tessellator t) {
        if (port.getSideIO(side) == ISidedIO.IO.NONE) return;

        if (port.getPortOverlay() == null) return;
        Minecraft.getMinecraft().getTextureManager().bindTexture(port.getPortOverlay());

        float o = 0.0075f;

        switch (side) {
            case UP -> {
                t.addVertexWithUV(x, y + 1 + o, z + 1, 0, 1);
                t.addVertexWithUV(x + 1, y + 1 + o, z + 1, 1, 1);
                t.addVertexWithUV(x + 1, y + 1 + o, z, 1, 0);
                t.addVertexWithUV(x, y + 1 + o, z, 0, 0);
            }
            case DOWN -> {
                t.addVertexWithUV(x, y - o, z, 0, 0);
                t.addVertexWithUV(x + 1, y - o, z, 1, 0);
                t.addVertexWithUV(x + 1, y - o, z + 1, 1, 1);
                t.addVertexWithUV(x, y - o, z + 1, 0, 1);
            }
            case NORTH -> {
                t.addVertexWithUV(x + 1, y, z - o, 1, 0);
                t.addVertexWithUV(x, y, z - o, 0, 0);
                t.addVertexWithUV(x, y + 1, z - o, 0, 1);
                t.addVertexWithUV(x + 1, y + 1, z - o, 1, 1);
            }
            case SOUTH -> {
                t.addVertexWithUV(x, y, z + 1 + o, 0, 0);
                t.addVertexWithUV(x + 1, y, z + 1 + o, 1, 0);
                t.addVertexWithUV(x + 1, y + 1, z + 1 + o, 1, 1);
                t.addVertexWithUV(x, y + 1, z + 1 + o, 0, 1);
            }
            case WEST -> {
                t.addVertexWithUV(x - o, y, z, 0, 0);
                t.addVertexWithUV(x - o, y, z + 1, 1, 0);
                t.addVertexWithUV(x - o, y + 1, z + 1, 1, 1);
                t.addVertexWithUV(x - o, y + 1, z, 0, 1);
            }
            case EAST -> {
                t.addVertexWithUV(x + 1 + o, y, z + 1, 1, 0);
                t.addVertexWithUV(x + 1 + o, y, z, 0, 0);
                t.addVertexWithUV(x + 1 + o, y + 1, z, 0, 1);
                t.addVertexWithUV(x + 1 + o, y + 1, z + 1, 1, 1);
            }
        }
    }

}
