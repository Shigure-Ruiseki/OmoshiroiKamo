package ruiseki.omoshiroikamo.client.render.block.quantumExtractor;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

@SideOnly(Side.CLIENT)
public class LaserCoreTESR extends TileEntitySpecialRenderer implements IItemRenderer {

    private final IModelCustom model;
    private static final String MODEL = LibResources.PREFIX_MODEL + "laser_core.obj";

    private static final ResourceLocation laser = new ResourceLocation(LibResources.PREFIX_BLOCK + "laser_core.png");

    private static final ResourceLocation TEX_IRON = new ResourceLocation("minecraft:textures/blocks/iron_block.png");

    public LaserCoreTESR() {
        model = AdvancedModelLoader.loadModel(new ResourceLocation(MODEL));
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5f, (float) y, (float) z + 0.5f);

        RenderUtil.bindTexture(laser);
        model.renderAllExcept(
            "HSNorthBot",
            "HSWestBot",
            "HSEastBot",
            "HSSouthBot",
            "HSNothTop",
            "HSEastTop",
            "HSSouthTop",
            "HSWestTop");

        RenderUtil.bindTexture(TEX_IRON);
        model.renderOnly(
            "HSNorthBot",
            "HSWestBot",
            "HSEastBot",
            "HSSouthBot",
            "HSNothTop",
            "HSEastTop",
            "HSSouthTop",
            "HSWestTop");
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

        GL11.glPushMatrix();
        GL11.glTranslatef(0.5f, 0f, 0.5f);

        RenderUtil.bindTexture(laser);
        model.renderAllExcept(
            "HSNorthBot",
            "HSWestBot",
            "HSEastBot",
            "HSSouthBot",
            "HSNothTop",
            "HSEastTop",
            "HSSouthTop",
            "HSWestTop");

        RenderUtil.bindTexture(TEX_IRON);
        model.renderOnly(
            "HSNorthBot",
            "HSWestBot",
            "HSEastBot",
            "HSSouthBot",
            "HSNothTop",
            "HSEastTop",
            "HSSouthTop",
            "HSWestTop");
        GL11.glPopMatrix();
    }
}
