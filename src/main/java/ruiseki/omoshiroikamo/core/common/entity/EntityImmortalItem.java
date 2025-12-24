package ruiseki.omoshiroikamo.core.common.entity;

import java.util.Iterator;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemExpireEvent;

import lombok.Getter;
import lombok.Setter;

public class EntityImmortalItem extends EntityItemOK {

    @Getter
    private boolean immortal = false;
    @Setter
    @Getter
    private boolean immuneToExplosions = false;
    @Setter
    @Getter
    private boolean immuneToCactus = false;
    @Setter
    @Getter
    private boolean immuneToLava = false;
    @Setter
    private boolean immuneToFire = false;
    @Setter
    @Getter
    private boolean noDespawn = false;
    @Setter
    @Getter
    private boolean floatOnLava = false;
    @Setter
    @Getter
    private boolean preventVoidFall = false;

    public EntityImmortalItem(World world, Entity original, ItemStack stack) {
        super(world, original, stack);
        applyImmortalFlags();
    }

    /* ===== IMMORTAL SETTINGS ===== */

    public void setImmortal(boolean value) {
        this.immortal = value;
        applyImmortalFlags();
    }

    private void applyImmortalFlags() {
        this.isImmuneToFire = immortal;
        this.immuneToExplosions = immortal;
        this.immuneToCactus = immortal;
        this.immuneToLava = immortal;
        this.noDespawn = immortal;
        this.floatOnLava = immortal;
        this.preventVoidFall = immortal;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (immortal) {
            return false;
        }

        if ((immuneToExplosions && source.isExplosion()) || (immuneToCactus && source == DamageSource.cactus)
            || (immuneToFire && source == DamageSource.inFire)
            || (immuneToFire && source == DamageSource.onFire)
            || (immuneToLava && source == DamageSource.lava)) {
            return false;
        }

        return super.attackEntityFrom(source, amount);
    }

    @Override
    protected void dealFireDamage(int amount) {
        if (!immuneToFire && !immortal) {
            super.dealFireDamage(amount);
        }
    }

    @Override
    public boolean isEntityInvulnerable() {
        if (immortal) {
            return true;
        }
        return super.isEntityInvulnerable();
    }

    @Override
    public void onUpdate() {
        ItemStack stack = this.getDataWatcher()
            .getWatchableObjectItemStack(10);
        if (stack != null && stack.getItem() != null) {
            if (stack.getItem()
                .onEntityItemUpdate(this)) {
                return;
            }
        }

        if (this.getEntityItem() == null) {
            this.setDead();
        } else {
            super.onUpdate();

            if (this.delayBeforeCanPickup > 0) {
                --this.delayBeforeCanPickup;
            }

            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            this.motionY -= 0.03999999910593033D;
            this.noClip = this
                .func_145771_j(this.posX, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0D, this.posZ);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            boolean flag = (int) this.prevPosX != (int) this.posX || (int) this.prevPosY != (int) this.posY
                || (int) this.prevPosZ != (int) this.posZ;

            if (flag || this.ticksExisted % 25 == 0) {
                if (this.isInsideOfMaterial(Material.lava)) {
                    if (floatOnLava) {
                        this.motionY = Math.max(this.motionY, 0.05D);
                        this.motionX *= 0.5D;
                        this.motionZ *= 0.5D;
                    } else if (!immuneToLava && !immortal) {
                        this.motionY = 0.1D;
                        this.playSound("random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
                        this.setFire(3);
                    }
                    this.fallDistance = 0.0F;
                    if (this.motionY < -0.02D) {
                        this.motionY = -0.02D;
                    }
                }

                if (!this.worldObj.isRemote) {
                    this.searchForOtherItemsNearby2();
                }
            }

            float f = 0.98F;

            if (this.onGround) {
                f = this.worldObj.getBlock(
                    MathHelper.floor_double(this.posX),
                    MathHelper.floor_double(this.boundingBox.minY) - 1,
                    MathHelper.floor_double(this.posZ)).slipperiness * 0.98F;
            }

            this.motionX *= (double) f;
            this.motionY *= 0.9800000190734863D;
            this.motionZ *= (double) f;

            if (this.onGround) {
                this.motionY *= -0.5D;
            }

            ++this.age;

            if ((immortal || noDespawn) && !this.worldObj.isRemote) {
                this.age = 0;
            }

            if (preventVoidFall && this.posY < 1) {
                this.posY = 1;
                this.motionY = 0;
            }

            ItemStack item = getDataWatcher().getWatchableObjectItemStack(10);

            if (!this.worldObj.isRemote && this.age >= lifespan) {
                if (item != null) {
                    if (!(immortal || noDespawn)) {
                        ItemExpireEvent event = new ItemExpireEvent(
                            this,
                            (item.getItem() == null ? 6000
                                : item.getItem()
                                    .getEntityLifespan(item, worldObj)));
                        if (MinecraftForge.EVENT_BUS.post(event)) {
                            lifespan += event.extraLife;
                        } else {
                            this.setDead();
                        }
                    }
                } else {
                    this.setDead();
                }
            }

            if (item != null && item.stackSize <= 0) {
                this.setDead();
            }
        }
    }

    private void searchForOtherItemsNearby2() {
        Iterator<EntityItem> iterator = this.worldObj
            .getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(0.5D, 0.0D, 0.5D))
            .iterator();

        while (iterator.hasNext()) {
            EntityItem entityitem = iterator.next();
            this.combineItems(entityitem);
        }
    }
}
