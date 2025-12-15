package ruiseki.omoshiroikamo.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

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

    public void doRenderChicken(EntityChickensChicken entity, double x, double y, double z, float yaw,
        float partialTicks) {
        super.doRender((EntityLiving) entity, x, y, z, yaw, partialTicks);
    }

    @Override
    public void doRender(EntityLiving entity, double x, double y, double z, float yaw, float partialTicks) {
        this.doRenderChicken((EntityChickensChicken) entity, x, y, z, yaw, partialTicks);
    }

    @Override
    public void doRender(EntityLivingBase entity, double x, double y, double z, float yaw, float partialTicks) {
        this.doRenderChicken((EntityChickensChicken) entity, x, y, z, yaw, partialTicks);
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        this.doRenderChicken((EntityChickensChicken) entity, x, y, z, yaw, partialTicks);
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase entity, int pass, float partialTicks) {
        if (pass == 0) {
            EntityChickensChicken chicken = (EntityChickensChicken) entity;
            ResourceLocation overlay = chicken.getTextureOverlay();
            if (overlay != null) {
                this.bindTexture(overlay);
                int color = chicken.getTintColor();
                float r = (color >> 16 & 255) / 255.0F;
                float g = (color >> 8 & 255) / 255.0F;
                float b = (color & 255) / 255.0F;
                GL11.glColor3f(r, g, b);

                // Enable Blending for transparency
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                // Prevent Z-Fighting by slightly scaling up the overlay model
                GL11.glScalef(1.01F, 1.01F, 1.01F);

                this.setRenderPassModel(this.mainModel);
                return 1;
            }
        }
        return -1;
    }

    @Override
    protected int getColorMultiplier(EntityLivingBase entity, float light, float partialTicks) {
        return getColorMultiplier((EntityChickensChicken) entity, light, partialTicks);
    }

    protected int getColorMultiplier(EntityChickensChicken entity, float light, float partialTicks) {
        // If we have an overlay, the base layer should be uncolored (White).
        // If no overlay, we use the tint on the base layer (Legacy behavior).
        if (entity.getTextureOverlay() != null) {
            return 0xFFFFFF;
        }
        return entity.getTintColor();
    }

}
