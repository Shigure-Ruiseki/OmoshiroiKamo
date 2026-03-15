package ruiseki.omoshiroikamo.module.machinery.client.render;

import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.core.item.ItemWrench;
import ruiseki.omoshiroikamo.core.tileentity.ISidedIO;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;

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

        // Link external ports rendering
        if (player.getHeldItem()
            .hasTagCompound()
            && player.getHeldItem()
                .getTagCompound()
                .hasKey("LinkedX")) {
            int cx = player.getHeldItem()
                .getTagCompound()
                .getInteger("LinkedX");
            int cy = player.getHeldItem()
                .getTagCompound()
                .getInteger("LinkedY");
            int cz = player.getHeldItem()
                .getTagCompound()
                .getInteger("LinkedZ");
            int cDim = player.getHeldItem()
                .getTagCompound()
                .getInteger("LinkedDim");

            if (player.worldObj.provider.dimensionId == cDim) {
                TileEntity cte = player.worldObj.getTileEntity(cx, cy, cz);
                if (cte instanceof TEMachineController) {
                    TEMachineController controller = (TEMachineController) cte;
                    drawLinkedPorts(controller, event.partialTicks, player);
                }
            }
        }

        if (mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return;

        int x = mc.objectMouseOver.blockX;
        int y = mc.objectMouseOver.blockY;
        int z = mc.objectMouseOver.blockZ;

        TileEntity te = player.worldObj.getTileEntity(x, y, z);
        if (!(te instanceof ISidedIO) || te instanceof TEMachineController) return;

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

    private static void drawLinkedPorts(TEMachineController controller, float partialTicks, EntityPlayer player) {
        double px = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double py = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double pz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        Map<ChunkCoordinates, Map<IPortType.Type, EnumIO>> configs = controller.getExternalPortConfigs();
        if (configs == null || configs.isEmpty()) return;

        GL11.glPushMatrix();
        GL11.glTranslated(-px, -py, -pz);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        double cX = controller.xCoord + 0.5;
        double cY = controller.yCoord + 0.5;
        double cZ = controller.zCoord + 0.5;

        Tessellator t = Tessellator.instance;
        GL11.glLineWidth(3.0f);

        for (Map.Entry<ChunkCoordinates, Map<IPortType.Type, EnumIO>> entry : configs.entrySet()) {
            ChunkCoordinates port = entry.getKey();
            double pX = port.posX + 0.5;
            double pY = port.posY + 0.5;
            double pZ = port.posZ + 0.5;

            EnumIO firstIo = EnumIO.NONE;
            for (EnumIO io : entry.getValue()
                .values()) {
                if (io != EnumIO.NONE) {
                    firstIo = io;
                    break;
                }
            }

            switch (firstIo) {
                case INPUT:
                    GL11.glColor4f(0.2f, 0.6f, 1.0f, 0.8f);
                    break;
                case OUTPUT:
                    GL11.glColor4f(1.0f, 0.6f, 0.2f, 0.8f);
                    break;
                case BOTH:
                    GL11.glColor4f(0.8f, 0.2f, 0.8f, 0.8f);
                    break;
                default:
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
                    break;
            }

            t.startDrawing(GL11.GL_LINES);
            t.addVertex(cX, cY, cZ);
            t.addVertex(pX, pY, pZ);
            t.draw();
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();

        RenderManager rm = RenderManager.instance;
        for (Map.Entry<ChunkCoordinates, Map<IPortType.Type, EnumIO>> entry : configs.entrySet()) {
            ChunkCoordinates port = entry.getKey();

            int lineOffset = 0;
            IStructureEntry entryProps = controller.getStructureAgent()
                .getCustomProperties();
            Map<Character, EnumIO> fixedPorts = entryProps != null ? entryProps.getFixedExternalPorts() : null;

            for (Map.Entry<IPortType.Type, EnumIO> typeEntry : entry.getValue()
                .entrySet()) {

                String fixedSuffix = "";
                if (fixedPorts != null) {
                    // Find which symbol corresponds to this ChunkCoordinates
                    for (Map.Entry<Character, List<ChunkCoordinates>> symEntry : controller.getSymbolPositionsMap()
                        .entrySet()) {
                        if (symEntry.getValue()
                            .contains(port)) {
                            if (fixedPorts.containsKey(symEntry.getKey())) {
                                fixedSuffix = " (Fixed)";
                            }
                            break;
                        }
                    }
                }

                String text = "[ " + typeEntry.getKey()
                    .name()
                    + " : "
                    + typeEntry.getValue()
                        .name()
                    + fixedSuffix
                    + " ]";

                double d0 = port.posX + 0.5 - px;
                // Move downward natively (y moves pos when scaled, note normal rendering)
                double d1 = port.posY + 0.75 - Math.min(0.7, (lineOffset * 0.3)) - py;
                double d2 = port.posZ + 0.5 - pz;

                GL11.glPushMatrix();
                GL11.glTranslated(d0, d1, d2);
                GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
                GL11.glScalef(-0.02666667F, -0.02666667F, 0.02666667F);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDepthMask(false);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                FontRenderer font = Minecraft.getMinecraft().fontRenderer;
                int width = font.getStringWidth(text) / 2;

                GL11.glDisable(GL11.GL_TEXTURE_2D);
                t.startDrawingQuads();
                t.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.4F);
                t.addVertex(-width - 1, -1, 0.0D);
                t.addVertex(-width - 1, 8, 0.0D);
                t.addVertex(width + 1, 8, 0.0D);
                t.addVertex(width + 1, -1, 0.0D);
                t.draw();
                GL11.glEnable(GL11.GL_TEXTURE_2D);

                int color = 0xFFFFFF;
                switch (typeEntry.getValue()) {
                    case INPUT:
                        color = 0x3399FF;
                        break;
                    case OUTPUT:
                        color = 0xFF9933;
                        break;
                    case BOTH:
                        color = 0xCC33CC;
                        break;
                    default:
                        break;
                }
                font.drawString(text, -width, 0, color);

                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glDepthMask(true);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();

                lineOffset++;
            }
        }
    }

}
