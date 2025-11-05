package ruiseki.omoshiroikamo.client.render.block.quantumExtractor;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.lens.TELaserLens;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

@SideOnly(Side.CLIENT)
public class LaserLensTESR extends TileEntitySpecialRenderer implements IItemRenderer {

    private final IModelCustom model;
    private static final String MODEL = LibResources.PREFIX_MODEL + "laser_lens.obj";
    private static final ResourceLocation lensTexture = new ResourceLocation(
        LibResources.PREFIX_BLOCK + "laser_lens.png");
    private static final ResourceLocation lensClearTexture = new ResourceLocation(
        LibResources.PREFIX_BLOCK + "lens_clear.png");

    private static final float[][] COLORS = new float[][] { { 1f, 1f, 1f, 0.6f }, // clear (meta 0)

        { 0f, 0f, 0f, 0.6f }, // black
        { 1f, 0f, 0f, 0.6f }, // red
        { 0f, 0.5f, 0f, 0.6f }, // green
        { 0.5f, 0.25f, 0f, 0.6f }, // brown
        { 0f, 0f, 1f, 0.6f }, // blue
        { 0.5f, 0f, 0.5f, 0.6f }, // purple
        { 0f, 1f, 1f, 0.6f }, // cyan
        { 0.75f, 0.75f, 0.75f, 0.6f }, // silver (light_gray)
        { 0.25f, 0.25f, 0.25f, 0.6f }, // gray
        { 1f, 0.5f, 0.5f, 0.6f }, // pink
        { 0.5f, 1f, 0f, 0.6f }, // lime
        { 1f, 1f, 0f, 0.6f }, // yellow
        { 0.5f, 0.5f, 1f, 0.6f }, // light_blue
        { 1f, 0f, 1f, 0.6f }, // magenta
        { 1f, 0.5f, 0f, 0.6f }, // orange
        { 1f, 1f, 1f, 0.6f } // white
    };

    public LaserLensTESR() {
        model = AdvancedModelLoader.loadModel(new ResourceLocation(MODEL));
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        TELaserLens lens = (TELaserLens) te;
        int meta = lens.getMeta();
        float[] color = COLORS[MathHelper.clamp_int(meta, 0, COLORS.length - 1)];

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5f, (float) y, (float) z + 0.5f);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glColor4f(color[0], color[1], color[2], color[3]);
        RenderUtil.bindTexture(meta == 0 ? lensClearTexture : lensTexture);

        model.renderAll();

        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

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
        int meta = item.getItemDamage();
        float[] color = COLORS[MathHelper.clamp_int(meta, 0, COLORS.length - 1)];

        GL11.glPushMatrix();
        GL11.glTranslatef(0.5f, 0f, 0.5f);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glColor4f(color[0], color[1], color[2], color[3]);
        RenderUtil.bindTexture(meta == 0 ? lensClearTexture : lensTexture);

        model.renderAll();

        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glPopMatrix();
    }
}
