package ruiseki.omoshiroikamo.client.render.block;

import static com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.properties.ModelQuadFacing.DIRECTIONS;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;
import com.gtnewhorizon.gtnhlib.client.model.baked.BakedModel;
import com.gtnewhorizon.gtnhlib.client.model.loading.ModelRegistry;
import com.gtnewhorizon.gtnhlib.client.renderer.TessellatorManager;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.ModelQuadView;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.properties.ModelQuadFacing;
import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;

import cpw.mods.fml.client.registry.RenderingRegistry;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;

@ThreadSafeISBRH(perThread = true)
public class JsonModelISBRH extends ModelISBRH {

    /**
     * Any blocks using a JSON model should return this for {@link Block#getRenderType()}.
     */
    public static final int JSON_ISBRH_ID = RenderingRegistry.getNextAvailableRenderId();

    private final Random RAND = new Random();

    public JsonModelISBRH() {
        ModelRegistry.registerModid(LibMisc.MOD_ID);
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        final Tessellator tesselator = TessellatorManager.get();
        final BakedModel model = getModel(null, block, metadata, 0, 0, 0);

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        tesselator.startDrawingQuads();

        int color = model.getColor(null, 0, 0, 0, block, metadata, RAND);

        float ox = -0.5F, oy = -0.5F, oz = -0.5F;

        for (ModelQuadFacing dir : DIRECTIONS) {
            final var quads = model.getQuads(null, 0, 0, 0, block, metadata, dir, RAND, -1, null);
            if (quads.isEmpty()) {
                continue;
            }
            for (ModelQuadView quad : quads) {

                if (quad.getColorIndex() != -1 && color == -1) {
                    color = block.getRenderColor(metadata);
                }
                final float r = (color & 255) / 255f;
                final float g = (color >> 8 & 255) / 255f;
                final float b = (color >> 16 & 255) / 255f;

                final float shade = diffuseLight(quad.getComputedFaceNormal());
                tesselator.setColorOpaque_F(r * shade, g * shade, b * shade);
                renderQuad(quad, ox, oy, oz, tesselator, null);
            }
        }

        tesselator.draw();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return JSON_ISBRH_ID;
    }
}
