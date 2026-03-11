package ruiseki.omoshiroikamo.module.cows.client.render;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistry;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;
import ruiseki.omoshiroikamo.module.cows.client.model.ModelCowsCow;
import ruiseki.omoshiroikamo.module.cows.common.entity.EntityCowsCow;

@SideOnly(Side.CLIENT)
public class RenderCowsCow extends RenderLiving {

    public RenderCowsCow() {
        super(new ModelCowsCow(), 0.7F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return getEntityTexture((EntityCowsCow) entity);
    }

    protected ResourceLocation getEntityTexture(EntityCowsCow cow) {
        return cow.getTexture();
    }

    public void doRenderCow(EntityCowsCow entity, double x, double y, double z, float yaw, float partialTicks) {
        super.doRender((EntityLiving) entity, x, y, z, yaw, partialTicks);
    }

    @Override
    public void doRender(EntityLiving entity, double x, double y, double z, float yaw, float partialTicks) {
        this.doRenderCow((EntityCowsCow) entity, x, y, z, yaw, partialTicks);
    }

    @Override
    public void doRender(EntityLivingBase entity, double x, double y, double z, float yaw, float partialTicks) {
        this.doRenderCow((EntityCowsCow) entity, x, y, z, yaw, partialTicks);
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        this.doRenderCow((EntityCowsCow) entity, x, y, z, yaw, partialTicks);
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase entity, int pass, float partialTicks) {
        if (pass == 0) {
            EntityCowsCow cow = (EntityCowsCow) entity;
            ResourceLocation overlay = cow.getTextureOverlay();
            if (overlay != null) {
                this.bindTexture(overlay);
                int color = cow.getTintColor();
                float r = (color >> 16 & 255) / 255.0F;
                float g = (color >> 8 & 255) / 255.0F;
                float b = (color & 255) / 255.0F;
                GL11.glColor3f(r, g, b);

                // Enable Blending for transparency
                GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
                GL11.glPolygonOffset(-1.0F, -10.0F);

                // Prevent Z-Fighting by slightly scaling up the overlay model
                GL11.glScalef(1.005F, 1.005F, 1.005F);

                this.setRenderPassModel(this.mainModel);
                return 1;
            }
        } else if (pass == 1) {
            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        }
        return -1;
    }

    @Override
    protected int getColorMultiplier(EntityLivingBase entity, float lightBrightness, float partialTickTime) {
        if (!(entity instanceof EntityCowsCow cow)) {
            return super.getColorMultiplier(entity, lightBrightness, partialTickTime);
        }

        // If we have an overlay, the base layer should be uncolored (White).
        if (cow.getTextureOverlay() != null) {
            return 0xFFFFFF;
        }

        CowsRegistryItem item = CowsRegistry.INSTANCE.getByType(cow.getType());
        if (item == null) {
            return super.getColorMultiplier(entity, lightBrightness, partialTickTime);
        }

        return rgbToArgb(item.getBgColor(), 50f);
    }

    public static int rgbToArgb(int rgb, float alphaPercent) {
        int alpha = Math.round(255 * (alphaPercent / 100f));
        return (alpha << 24) | (rgb & 0xFFFFFF);
    }
}
