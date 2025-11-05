package ruiseki.omoshiroikamo.common.entity.chicken;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityColoredEgg extends EntityEgg {

    private static final String TYPE_NBT = "Type";

    public EntityColoredEgg(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    public EntityColoredEgg(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public int getChickenTypeInternal() {
        return dataWatcher.getWatchableObjectInt(20);
    }

    public void setChickenTypeInternal(int t) {
        dataWatcher.updateObject(20, t);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(20, 0); // CHICKEN_TYPE
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger(TYPE_NBT, getChickenTypeInternal());

    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        setChickenTypeInternal(tagCompound.getInteger(TYPE_NBT));
    }

    @Override
    protected void onImpact(MovingObjectPosition result) {
        if (result.entityHit != null) {
            result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0.0F);
        }

        if (!this.worldObj.isRemote && this.rand.nextInt(8) == 0) {
            int i = 1;

            if (this.rand.nextInt(32) == 0) {
                i = 4;
            }

            for (int j = 0; j < i; ++j) {
                EntityChickensChicken entityChicken = new EntityChickensChicken(this.worldObj);
                entityChicken.setChickenType(getChickenTypeInternal());
                entityChicken.setGrowingAge(-24000);
                entityChicken.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
                this.worldObj.spawnEntityInWorld(entityChicken);
            }
        }

        for (int j = 0; j < 8; ++j) {
            this.worldObj.spawnParticle("snowballpoof", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
        }

        if (!this.worldObj.isRemote) {
            this.setDead();
        }
    }
}
