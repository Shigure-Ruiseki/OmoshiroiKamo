package ruiseki.omoshiroikamo.module.storage.common.item.wrapper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.utils.item.IItemHandler;

public interface IFeedingUpgrade {

    int getFoodSlot(IItemHandler handler, int foodLevel, float health, float maxHealth);

    default boolean feed(EntityPlayer entity, IItemHandler handler) {

        if (!entity.canEat(false)) return false;

        int slot = getFoodSlot(
            handler,
            entity.getFoodStats()
                .getFoodLevel(),
            entity.getHealth(),
            entity.getMaxHealth());

        if (slot < 0 || slot >= handler.getSlots()) {
            return false;
        }

        ItemStack food = handler.extractItem(slot, 1, false);
        if (food == null || !(food.getItem() instanceof ItemFood foodItem)) {
            return false;
        }

        foodItem.onEaten(food, entity.worldObj, entity);

        return true;
    }
}
