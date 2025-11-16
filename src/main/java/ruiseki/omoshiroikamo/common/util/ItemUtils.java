package ruiseki.omoshiroikamo.common.util;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.gtnewhorizon.gtnhlib.util.ItemUtil;

public class ItemUtils extends ItemUtil {

    public static boolean areStackMergable(ItemStack s1, ItemStack s2) {
        if (s1 == null || s2 == null || !s1.isStackable() || !s2.isStackable()) {
            return false;
        }
        if (!s1.isItemEqual(s2)) {
            return false;
        }
        return areStacksEqual(s1, s2);
    }

    public static void dropItems(World world, ItemStack stack, double x, double y, double z, boolean doRandomSpread) {
        if (stack == null || stack.stackSize <= 0) {
            return;
        }

        EntityItem entityitem = createEntityItem(world, stack, x, y, z, doRandomSpread);
        world.spawnEntityInWorld(entityitem);

    }

    public static EntityItem createEntityItem(World world, ItemStack stack, double x, double y, double z) {
        return createEntityItem(world, stack, x, y, z, true);
    }

    public static EntityItem createEntityItem(World world, ItemStack stack, double x, double y, double z,
        boolean doRandomSpread) {
        EntityItem entityitem;
        if (doRandomSpread) {
            float f1 = 0.7F;
            double d = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
            double d1 = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
            double d2 = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
            entityitem = new EntityItem(world, x + d, y + d1, z + d2, stack);
            entityitem.delayBeforeCanPickup = 10;
        } else {
            entityitem = new EntityItem(world, x, y, z, stack);
            entityitem.motionX = 0;
            entityitem.motionY = 0;
            entityitem.motionZ = 0;
            entityitem.delayBeforeCanPickup = 0;
        }
        return entityitem;
    }

    public static void dropItems(World world, ItemStack stack, int x, int y, int z, boolean doRandomSpread) {
        if (stack == null || stack.stackSize <= 0) {
            return;
        }

        if (doRandomSpread) {
            float f1 = 0.7F;
            double d = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
            double d1 = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
            double d2 = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
            EntityItem entityitem = new EntityItem(world, x + d, y + d1, z + d2, stack);
            entityitem.delayBeforeCanPickup = 10;
            world.spawnEntityInWorld(entityitem);
        } else {
            EntityItem entityitem = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, stack);
            entityitem.motionX = 0;
            entityitem.motionY = 0;
            entityitem.motionZ = 0;
            entityitem.delayBeforeCanPickup = 0;
            world.spawnEntityInWorld(entityitem);
        }

    }

    public static void dropItems(World world, ItemStack[] inventory, int x, int y, int z, boolean doRandomSpread) {
        if (inventory == null) {
            return;
        }
        for (ItemStack stack : inventory) {
            if (stack != null && stack.stackSize > 0) {
                dropItems(world, stack.copy(), x, y, z, doRandomSpread);
            }
        }
    }

    public static void dropItems(World world, IInventory inventory, int x, int y, int z, boolean doRandomSpread) {
        for (int l = 0; l < inventory.getSizeInventory(); ++l) {
            ItemStack items = inventory.getStackInSlot(l);

            if (items != null && items.stackSize > 0) {
                dropItems(
                    world,
                    inventory.getStackInSlot(l)
                        .copy(),
                    x,
                    y,
                    z,
                    doRandomSpread);
            }
        }
    }
}
