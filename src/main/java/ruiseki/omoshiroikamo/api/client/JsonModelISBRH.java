package ruiseki.omoshiroikamo.api.client;

import static com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.properties.ModelQuadFacing.DIRECTIONS;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;
import com.gtnewhorizon.gtnhlib.client.model.baked.BakedModel;
import com.gtnewhorizon.gtnhlib.client.model.color.BlockColor;
import com.gtnewhorizon.gtnhlib.client.renderer.TessellatorManager;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.ModelQuadView;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.properties.ModelQuadFacing;
import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;

@ThreadSafeISBRH(perThread = true)
public class JsonModelISBRH extends ModelISBRH implements IItemRenderer {

    public static final JsonModelISBRH INSTANCE = new JsonModelISBRH();

    public final Random RAND = new Random();

    public JsonModelISBRH() {}

    @Override
    public void renderQuad(ModelQuadView quad, float x, float y, float z, Tessellator tessellator,
        @Nullable IIcon overrideIcon) {
        super.renderQuad(quad, x, y, z, tessellator, overrideIcon);
    }

    @Override
    @SuppressWarnings("unused")
    public BakedModel getModel(IBlockAccess world, Block block, int meta, int x, int y, int z) {
        return super.getModel(world, block, meta, x, y, z);
    }

    public static void renderToEntity(ItemStack stack) {

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

        int color = model.getColor(null, 0, 0, 0, block, meta, INSTANCE.RAND);

        for (ModelQuadFacing dir : DIRECTIONS) {

            final var quads = model.getQuads(null, 0, 0, 0, block, meta, dir, INSTANCE.RAND, -1, null);
            if (quads.isEmpty()) {
                continue;
            }

            for (ModelQuadView quad : quads) {
                int quadColor = color;
                if (stack.getItem() != null && quad.getColorIndex() != -1) {
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

        GL11.glRotated(180f, 0f, 1f, 0f);
        GL11.glRotatef(180f, 0f, 0f, 1f);

        tesselator.draw();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
