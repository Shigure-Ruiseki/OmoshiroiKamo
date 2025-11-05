package ruiseki.omoshiroikamo.client.render.block.solarArray;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.DyeColor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.common.block.multiblock.solarArray.TESolarArray;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

@SideOnly(Side.CLIENT)
public class SolarArrayTESR extends TileEntitySpecialRenderer implements IItemRenderer {

    private final IModelCustom model;
    private static final String MODEL = LibResources.PREFIX_MODEL + "solar_array.obj";

    private static final ResourceLocation controllerBase = new ResourceLocation(
        LibResources.PREFIX_BLOCK + "basalt.png");
    private static final ResourceLocation brain = new ResourceLocation(LibResources.PREFIX_BLOCK + "solar_tex.png");

    private static final ResourceLocation TEX_IRON = new ResourceLocation(LibResources.PREFIX_BLOCK + "cont_tier.png");

    public SolarArrayTESR() {
        model = AdvancedModelLoader.loadModel(new ResourceLocation(MODEL));
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        TESolarArray solarArray = (TESolarArray) te;
        int tier = solarArray.getTier();

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5f, (float) y, (float) z + 0.5f);
        render(tier);
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
        int tier = item.getItemDamage() + 1;

        GL11.glPushMatrix();
        GL11.glTranslatef(0.5f, -0.1f, 0.5f);
        render(tier);
        GL11.glPopMatrix();
    }

    public void render(int tier) {

        RenderUtil.bindTexture(controllerBase);
        model.renderOnly("ControllerBase");

        RenderUtil.bindTexture(brain);
        model.renderOnly("Brain");

        int color = DyeColor.WHITE.getColor();
        switch (tier) {
            case 1:
                color = DyeColor.YELLOW.getColor();
                break;
            case 2:
                color = DyeColor.LIGHT_BLUE.getColor();
                break;
            case 3:
                color = DyeColor.CYAN.getColor();
                break;
            case 4:
                color = DyeColor.WHITE.getColor();
                break;
        }

        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        float brightnessFactor = 1.18f;
        r = Math.min(1.0f, r * brightnessFactor);
        g = Math.min(1.0f, g * brightnessFactor);
        b = Math.min(1.0f, b * brightnessFactor);

        GL11.glColor3f(r, g, b);
        RenderUtil.bindTexture(TEX_IRON);
        model.renderOnly("rod_1", "rod_2", "rod_3", "rod_4", "rod_5", "rod_6", "rod_7", "rod_8");
    }

}
