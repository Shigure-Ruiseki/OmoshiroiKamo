package ruiseki.omoshiroikamo.client.render.item.backpack;

import static com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.diffuseLight;
import static com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.properties.ModelQuadFacing.DIRECTIONS;
import static ruiseki.omoshiroikamo.api.client.JsonModelISBRH.INSTANCE;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.client.model.baked.BakedModel;
import com.gtnewhorizon.gtnhlib.client.model.color.BlockColor;
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

        final Tessellator tesselator = TessellatorManager.get();
        final BakedModel model = INSTANCE.getModel(null, block, meta, 0, 0, 0);

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_LIGHTING);
        tesselator.startDrawingQuads();

        int color = model.getColor(null, 0, 0, 0, block, meta, INSTANCE.RAND);

        for (ModelQuadFacing dir : DIRECTIONS) {

            final var quads = model.getQuads(null, 0, 0, 0, block, meta, dir, INSTANCE.RAND, color, null);
            if (quads.isEmpty()) {
                continue;
            }

            for (ModelQuadView quad : quads) {
                int quadColor = color;

                // If true use tintIndex color
                if (quad.getColorIndex() != -1) {
                    quadColor = BlockColor.getColor(block, stack, quad.getColorIndex());
                }

                float r = (quadColor & 0xFF) / 255f;
                float g = (quadColor >> 8 & 0xFF) / 255f;
                float b = (quadColor >> 16 & 0xFF) / 255f;

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

        if (type == ItemRenderType.ENTITY) {
            GL11.glScaled(2f, 2f, 2f);
            GL11.glTranslatef(0f, 1f, 0f);
        }
        tesselator.draw();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
