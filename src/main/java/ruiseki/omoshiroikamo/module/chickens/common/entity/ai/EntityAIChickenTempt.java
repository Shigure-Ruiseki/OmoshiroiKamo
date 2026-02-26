package ruiseki.omoshiroikamo.module.chickens.common.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.module.chickens.common.entity.EntityChickensChicken;

/**
 * Custom tempt AI for Chickens that uses isBreedingItem instead of a fixed
 * item.
 */
public class EntityAIChickenTempt extends EntityAIBase {

    private final EntityChickensChicken chicken;
    private final double speed;
    private EntityPlayer temptingPlayer;
    private int delay;
    private final boolean scaredByPlayerMovement;

    public EntityAIChickenTempt(EntityChickensChicken chicken, double speed, boolean scaredByPlayerMovement) {
        this.chicken = chicken;
        this.speed = speed;
        this.scaredByPlayerMovement = scaredByPlayerMovement;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (this.delay > 0) {
            --this.delay;
            return false;
        } else {
            this.temptingPlayer = this.chicken.worldObj.getClosestPlayerToEntity(this.chicken, 10.0D);

            if (this.temptingPlayer == null) {
                return false;
            } else {
                ItemStack itemstack = this.temptingPlayer.getCurrentEquippedItem();
                if (itemstack == null) {
                    return false;
                }
                return this.chicken.isBreedingItem(itemstack);
            }
        }
    }

    @Override
    public boolean continueExecuting() {
        if (this.scaredByPlayerMovement) {
            if (this.chicken.getDistanceSqToEntity(this.temptingPlayer) < 36.0D) {
                if (this.temptingPlayer.getDistanceSq(this.chicken.posX, this.chicken.posY, this.chicken.posZ)
                    > 0.010000000000000002D) {
                    return false;
                }

                if (Math.abs(this.temptingPlayer.rotationPitch) > 5.0F
                    || Math.abs(this.temptingPlayer.rotationYawHead - this.temptingPlayer.rotationYaw) > 5.0F) {
                    return false;
                }
            } else {
                return false;
            }
        }

        return this.shouldExecute();
    }

    @Override
    public void startExecuting() {
        this.delay = 0;
    }

    @Override
    public void resetTask() {
        this.temptingPlayer = null;
        this.chicken.getNavigator()
            .clearPathEntity();
        this.delay = 10;
    }

    @Override
    public void updateTask() {
        this.chicken.getLookHelper()
            .setLookPositionWithEntity(this.temptingPlayer, 30.0F, (float) this.chicken.getVerticalFaceSpeed());

        if (this.chicken.getDistanceSqToEntity(this.temptingPlayer) < 6.25D) {
            this.chicken.getNavigator()
                .clearPathEntity();
        } else {
            this.chicken.getNavigator()
                .tryMoveToEntityLiving(this.temptingPlayer, this.speed);
        }
    }
}
