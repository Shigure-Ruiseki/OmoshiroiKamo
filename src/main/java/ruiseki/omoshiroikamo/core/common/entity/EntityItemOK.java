package ruiseki.omoshiroikamo.core.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class EntityItemOK extends EntityItem {

    public EntityItemOK(World world, Entity original, ItemStack stack) {
        this(world, original.posX, original.posY, original.posZ, stack);
        this.delayBeforeCanPickup = 20;
        this.motionX = original.motionX;
        this.motionY = original.motionY;
        this.motionZ = original.motionZ;
        this.setEntityItemStack(stack);
    }

    public EntityItemOK(World world, double x, double y, double z, ItemStack stack) {
        super(world, x, y, z);
        this.setEntityItemStack(stack);
    }

    public EntityItemOK(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public EntityItemOK(World world) {
        super(world);
    }
}
