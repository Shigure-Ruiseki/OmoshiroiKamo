package ruiseki.omoshiroikamo.common.block.backpack.capabilities;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.utils.item.IItemHandler;

import ruiseki.omoshiroikamo.common.block.backpack.handler.ExposedItemStackHandler;

public class FeedingUpgradeWrapper extends FilterableWrapper {

    public FeedingUpgradeWrapper(ItemStack upgrade) {
        super(upgrade);
        handler = new ExposedItemStackHandler(9, this) {

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return getStackInSlot(slot).getItem() instanceof ItemFood;
            }

        };
    }

    public ItemStack getFeedingStack(IItemHandler handler, int foodLevel, float health, float maxHealth) {
        int size = handler.getSlots();
        for (int i = 0; i < size; i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack == null) {
                continue;
            }
            if (!(stack.getItem() instanceof ItemFood food)) {
                continue;
            }
            int healingAmount = food.func_150905_g(stack);

            if (healingAmount <= 20 - foodLevel && checkFilter(stack)) {
                return handler.extractItem(i, 1, false);
            }
        }
        return null;
    }

    @Override
    public boolean checkFilter(ItemStack check) {
        return check.getItem() instanceof ItemFood && super.checkFilter(check);
    }

}
