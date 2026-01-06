package ruiseki.omoshiroikamo.module.cable.client.render;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.client.renderer.TessellatorManager;

import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.api.cable.ICablePartItem;
import ruiseki.omoshiroikamo.core.common.util.RenderUtils;

public class ItemPartRenderer implements IItemRenderer {

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        Item item = stack.getItem();
        if (!(item instanceof ICablePartItem partItem)) return;
        ICablePart part = partItem.createPart();
        part.setSide(ForgeDirection.NORTH);
        RenderUtils.bindTexture(part.getIcon());
        AxisAlignedBB bb = part.getCollisionBox();

        GL11.glPushMatrix();
        Tessellator tess = TessellatorManager.get();

        float brightness = 1.0f;
        tess.setColorOpaque_F(brightness, brightness, brightness);

        int light = 0xF000F0; // max light
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (light >> 16) & 0xFFFF, light & 0xFFFF);

        switch (type) {
            case INVENTORY:
            case EQUIPPED:
            case EQUIPPED_FIRST_PERSON:
                GL11.glTranslated(0, 0, 0.5);
                break;
            case ENTITY:
                GL11.glTranslated(-0.5, 0, -0.5);
                break;
        }

        renderCubeWithCollisionBB(bb, tess);
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
        t.addVertexWithUV(minX, minY, maxZ, u0 + minX * 16 * du, v0 + maxZ * 16 * dv);
        t.addVertexWithUV(maxX, minY, maxZ, u0 + maxX * 16 * du, v0 + maxZ * 16 * dv);
        t.addVertexWithUV(maxX, minY, minZ, u0 + maxX * 16 * du, v0 + minZ * 16 * dv);
        t.addVertexWithUV(minX, minY, minZ, u0 + minX * 16 * du, v0 + minZ * 16 * dv);

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
