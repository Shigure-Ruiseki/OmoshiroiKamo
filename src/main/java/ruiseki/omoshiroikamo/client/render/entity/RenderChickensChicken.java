package ruiseki.omoshiroikamo.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.client.models.ModelChickensChicken;
import ruiseki.omoshiroikamo.common.entity.chicken.EntityChickensChicken;

@SideOnly(Side.CLIENT)
public class RenderChickensChicken extends RenderLiving {

    public RenderChickensChicken() {
        super(new ModelChickensChicken(), 0.3F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return getEntityTexture((EntityChickensChicken) entity);
    }

    protected ResourceLocation getEntityTexture(EntityChickensChicken chicken) {
        return chicken.getTexture();
    }

    @Override
    protected float handleRotationFloat(EntityLivingBase living, float partialTicks) {
        return handleRotationFloat((EntityChickensChicken) living, partialTicks);
    }

    protected float handleRotationFloat(EntityChickensChicken chicken, float partialTicks) {
        float flap = chicken.field_70888_h + (chicken.field_70886_e - chicken.field_70888_h) * partialTicks;
        float flapSpeed = chicken.field_70884_g + (chicken.destPos - chicken.field_70884_g) * partialTicks;
        return (MathHelper.sin(flap) + 1.0F) * flapSpeed;
    }

    public void doRender(EntityChickensChicken entity, double x, double y, double z, float yaw, float partialTicks) {
        super.doRender((EntityLiving) entity, x, y, z, yaw, partialTicks);
    }

    @Override
    public void doRender(EntityLiving entity, double x, double y, double z, float yaw, float partialTicks) {
        super.doRender((EntityChickensChicken) entity, x, y, z, yaw, partialTicks);
    }

    @Override
    public void doRender(EntityLivingBase entity, double x, double y, double z, float yaw, float partialTicks) {
        super.doRender((EntityChickensChicken) entity, x, y, z, yaw, partialTicks);
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        super.doRender((EntityChickensChicken) entity, x, y, z, yaw, partialTicks);
    }

}
