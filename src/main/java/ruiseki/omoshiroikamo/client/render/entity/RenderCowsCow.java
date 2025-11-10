package ruiseki.omoshiroikamo.client.render.entity;

import java.awt.*;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistry;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;
import ruiseki.omoshiroikamo.client.models.ModelCowsCow;
import ruiseki.omoshiroikamo.common.entity.cow.EntityCowsCow;

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
    protected int getColorMultiplier(EntityLivingBase entity, float lightBrightness, float partialTickTime) {
        if (!(entity instanceof EntityCowsCow cow)) {
            return super.getColorMultiplier(entity, lightBrightness, partialTickTime);
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
