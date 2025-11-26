package ruiseki.omoshiroikamo.client.render.item.backpack;

import static com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.diffuseLight;
import static com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.properties.ModelQuadFacing.DIRECTIONS;
import static ruiseki.omoshiroikamo.api.client.JsonModelISBRH.INSTANCE;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.client.model.baked.BakedModel;
import com.gtnewhorizon.gtnhlib.client.renderer.TessellatorManager;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.ModelQuadView;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.properties.ModelQuadFacing;

public class ItemBackpackRenderer implements IItemRenderer {

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

        Block block = Block.getBlockFromItem(stack.getItem());
        if (block == null) return;

        int meta = stack.getItemDamage();
        BakedModel model = INSTANCE.getModel(null, block, meta, 0, 0, 0);

        Tessellator tesselator = TessellatorManager.get();

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_LIGHTING);

        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(TextureMap.locationBlocksTexture);

        tesselator.startDrawingQuads();

        for (ModelQuadFacing dir : DIRECTIONS) {

            final var quads = model.getQuads(null, 0, 0, 0, block, meta, dir, INSTANCE.RAND, -1, null);
            if (quads.isEmpty()) {
                continue;
            }

            for (ModelQuadView quad : quads) {
                int color;
                if (stack.getItem() != null && quad.getColorIndex() != -1) {
                    color = stack.getItem()
                        .getColorFromItemStack(stack, quad.getColorIndex());
                } else {
                    color = block.getRenderColor(meta);
                }

                float r = (color & 0xFF) / 255f;
                float g = (color >> 8 & 0xFF) / 255f;
                float b = (color >> 16 & 0xFF) / 255f;

                final float shade = diffuseLight(quad.getComputedFaceNormal());
                tesselator.setColorOpaque_F(r * shade, g * shade, b * shade);
                INSTANCE.renderQuad(quad, -0.5f, -0.5f, -0.5f, tesselator, null);
            }
        }

        GL11.glRotated(-90f, 0f, 1f, 0f);

        if (type == ItemRenderType.EQUIPPED) {
            GL11.glTranslatef(1f, 0f, -1f);
            GL11.glRotated(40f, 0f, 1f, 0f);
            GL11.glScaled(2f, 2f, 2f);
        }

        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glTranslatef(0.5f, 0.5f, -0.5f);
            GL11.glScaled(2f, 2f, 2f);
        }

        tesselator.draw();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
