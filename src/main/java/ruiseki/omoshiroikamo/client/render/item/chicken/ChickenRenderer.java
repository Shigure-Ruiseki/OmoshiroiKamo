package ruiseki.omoshiroikamo.client.render.item.chicken;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;
import com.gtnewhorizon.gtnhlib.client.renderer.TessellatorManager;

import ruiseki.omoshiroikamo.api.entity.chicken.DataChicken;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class ChickenRenderer implements IItemRenderer {

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        DataChicken chicken = DataChicken.getDataFromStack(item);
        if (chicken == null) {
            return;
        }

        String texturePath = LibResources.PREFIX_MOD + "chicken/" + chicken.getName() + ".png";
        ResourceLocation texture = new ResourceLocation(texturePath);
        RenderUtil.bindTexture(texture);

        GL11.glPushMatrix();

        if (type == ItemRenderType.INVENTORY) {
            GL11.glDisable(GL11.GL_LIGHTING);

            Tessellator tess = TessellatorManager.get();
            tess.startDrawingQuads();

            tess.addVertexWithUV(0, 16, 0, 0, 1);
            tess.addVertexWithUV(16, 16, 0, 1, 1);
            tess.addVertexWithUV(16, 0, 0, 1, 0);
            tess.addVertexWithUV(0, 0, 0, 0, 0);

            tess.draw();
            GL11.glEnable(GL11.GL_LIGHTING);
        }
    }

}
