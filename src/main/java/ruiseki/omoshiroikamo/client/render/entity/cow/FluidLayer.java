package ruiseki.omoshiroikamo.client.render.entity.cow;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;

import org.lwjgl.opengl.GL11;

import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistry;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;
import ruiseki.omoshiroikamo.client.models.ModelCowsCow;
import ruiseki.omoshiroikamo.common.entity.cow.EntityCowsCow;

public class FluidLayer {

    private final RenderLiving renderer;

    private static final ModelCowsCow modelCow = new ModelCowsCow();

    public FluidLayer(RenderLiving rendererIn) {
        this.renderer = rendererIn;
    }

    public void renderLayer(EntityCowsCow entity, float limbSwing, float limbSwingAmount, float ageInTicks,
        float netHeadYaw, float headPitch, float scale) {
        CowsRegistryItem cowsRegistryItem = CowsRegistry.INSTANCE.getByType(entity.getType());
        Fluid fluid = cowsRegistryItem.createMilkFluid()
            .getFluid();
        if (fluid == null) {
            return;
        }

        IIcon icon = Minecraft.getMinecraft()
            .getTextureMapBlocks()
            .getAtlasSprite(
                fluid.getStillIcon()
                    .toString());

        if (icon == null) {
            return;
        }

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int rgb = 0xff000000 | fluid.getColor();
        GL11.glColor4f(((rgb >> 16) & 0xFF) / 255F, ((rgb >> 8) & 0xFF) / 255F, ((rgb >> 0) & 0xFF) / 255F, 0.5F);

        modelCow.isChild = renderer.mainModel.isChild;
        modelCow.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
