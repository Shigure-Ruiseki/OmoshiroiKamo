package ruiseki.omoshiroikamo.module.chickens.common.item;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.joml.Vector3d;

import ruiseki.omoshiroikamo.api.entity.chicken.DataChicken;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.common.item.ItemOK;

public class ItemChickenCatcher extends ItemOK {

    public ItemChickenCatcher() {
        super(ModObject.itemChickenCatcher);
        setMaxDamage(64);
        setMaxStackSize(1);
        setTextureName("chicken_catcher");
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target) {
        Vector3d pos = new Vector3d(target.posX, target.posY, target.posZ);
        World world = target.worldObj;

        if (!captureChicken(target, world.isRemote)) {
            return false;
        }

        if (world.isRemote) {
            spawnParticles(pos, world);
        }

        return true;
    }

    private boolean captureChicken(EntityLivingBase entity, boolean isRemote) {
        DataChicken chickenData = DataChicken.getDataFromEntity(entity);

        if (chickenData == null) {
            return false;
        }

        if (!isRemote) {
            EntityItem item = entity.entityDropItem(chickenData.buildStack(), 1.0F);
            item.motionX = 0;
            item.motionY = 0.2D;
            item.motionZ = 0;
            entity.worldObj.removeEntity(entity);
        }

        return true;
    }

    private void spawnParticles(Vector3d pos, World world) {
        Random rand = new Random();

        for (int k = 0; k < 20; ++k) {
            double xCoord = pos.x + (rand.nextDouble() * 0.6D) - 0.3D;
            double yCoord = pos.y + (rand.nextDouble() * 0.6D);
            double zCoord = pos.z + (rand.nextDouble() * 0.6D) - 0.3D;
            double xSpeed = rand.nextGaussian() * 0.02D;
            double ySpeed = rand.nextGaussian() * 0.2D;
            double zSpeed = rand.nextGaussian() * 0.02D;
            world.spawnParticle("explode", xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed);
        }
    }

}
