package ruiseki.omoshiroikamo.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.omoshiroikamo.client.render.DamageParticleRenderer;
import ruiseki.omoshiroikamo.config.general.DamageIndicatorsConfig;

public class DameEvents {

    @SubscribeEvent
    public void displayDamage(LivingUpdateEvent event) {
        displayDamageDealt(event.entityLiving);
    }

    public void displayDamageDealt(EntityLivingBase entity) {
        if (!entity.worldObj.isRemote) {
            return;
        }

        if (!DamageIndicatorsConfig.showDamageParticles) {
            return;
        }
        int currentHealth = (int) Math.ceil(entity.getHealth());
        NBTTagCompound data = entity.getEntityData();

        if (data.hasKey("health")) {
            int previousHealth = data.getInteger("health");
            if (previousHealth != currentHealth) {
                int delta = currentHealth - previousHealth;
                displayParticle(entity, -delta);
            }
        }

        data.setInteger("health", currentHealth);
    }

    private void displayParticle(Entity entity, int damage) {
        if (damage == 0) {
            return;
        }

        World world = entity.worldObj;
        double motionX = world.rand.nextGaussian() * 0.02;
        double motionY = 0.5f;
        double motionZ = world.rand.nextGaussian() * 0.02;
        DamageParticleRenderer damageIndicator = new DamageParticleRenderer(
            damage,
            world,
            entity.posX,
            entity.posY + entity.height,
            entity.posZ,
            motionX,
            motionY,
            motionZ);
        Minecraft.getMinecraft().effectRenderer.addEffect(damageIndicator);

    }
}
