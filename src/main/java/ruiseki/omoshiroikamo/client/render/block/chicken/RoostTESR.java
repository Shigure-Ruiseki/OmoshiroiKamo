package ruiseki.omoshiroikamo.client.render.block.chicken;

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
import ruiseki.omoshiroikamo.client.models.ModelChickensChicken;
import ruiseki.omoshiroikamo.common.block.chicken.TERoost;
import ruiseki.omoshiroikamo.common.item.chicken.DataChicken;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

@SideOnly(Side.CLIENT)
public class RoostTESR extends TileEntitySpecialRenderer implements IItemRenderer {

    private final IModelCustom model;
    private static final String MODEL = LibResources.PREFIX_MODEL + "roost.obj";
    private final ModelChickensChicken modelChicken = new ModelChickensChicken();

    private static final ResourceLocation texture = new ResourceLocation(LibResources.PREFIX_MODEL + "roost.png");

    public RoostTESR() {
        model = AdvancedModelLoader.loadModel(new ResourceLocation(MODEL));
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {

        if (!(te instanceof TERoost tile)) {
            return;
        }

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y, z + 0.5);
        GL11.glRotatef(getRotationAngle(tile.getFacing()), 0F, 1F, 0F);
        RenderUtil.bindTexture(texture);
        model.renderAll();

        DataChicken chicken = tile.getChickenData(0);
        if (chicken != null && chicken.getChicken() != null) {

            GL11.glPushMatrix();

            ResourceLocation CHICKEN_TEXTURE = chicken.getChicken()
                .getTexture();
            RenderUtil.bindTexture(CHICKEN_TEXTURE);

            GL11.glTranslatef(0F, 1.30F, 0F);
            GL11.glRotatef(getRotationAngle(tile.getFacing()), 0F, 1F, 0F);
            GL11.glRotatef(180F, 0F, 0F, 1F);
            GL11.glRotatef(180F, 0F, 1F, 0F);

            modelChicken.isChild = false;
            modelChicken.renderRoost(0.0625F);

            GL11.glPopMatrix();
        }

        GL11.glPopMatrix();
    }

    private float getRotationAngle(int facing) {
        switch (facing) {
            case 2:
                return 180f;
            case 3:
                return 0f;
            case 4:
                return 270f;
            case 5:
                return 90f;
        }
        return 0f;
    }

    @Override
    public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item,
        IItemRenderer.ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        switch (type) {
            case ENTITY:
                GL11.glTranslatef(0f, -0.5f, 0f);
                GL11.glRotatef(90, 0F, 1F, 0F);
                break;
            default:
                GL11.glTranslatef(0.5f, -0.1f, 0.5f);
                break;
        }
        RenderUtil.bindTexture(texture);
        model.renderAll();
        GL11.glPopMatrix();
    }
}
