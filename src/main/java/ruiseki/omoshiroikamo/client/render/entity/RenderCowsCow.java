package ruiseki.omoshiroikamo.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
}
