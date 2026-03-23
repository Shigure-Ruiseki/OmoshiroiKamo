package ruiseki.omoshiroikamo.module.storage.common.item.wrapper;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.cleanroommc.modularui.utils.item.IItemHandler;

import ruiseki.omoshiroikamo.core.item.ItemNBTUtils;
import ruiseki.omoshiroikamo.module.backpack.client.gui.handler.UpgradeItemStackHandler;

public class AdvancedFeedingUpgradeWrapper extends AdvancedUpgradeWrapper implements IFeedingUpgrade {

    private static final String HUNGER_FEEDING_STRATEGY_TAG = "HungerFeedingStrategy";
    private static final String HURT_FEEDING_STRATEGY_TAG = "HurtFeedingStrategy";

    public AdvancedFeedingUpgradeWrapper(ItemStack upgrade) {
        super(upgrade);
        handler = new UpgradeItemStackHandler(16) {

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return stack != null && stack.getItem() instanceof ItemFood;
            }

            @Override
            protected void onContentsChanged(int slot) {
                NBTTagCompound tag = ItemNBTUtils.getNBT(upgrade);
                tag.setTag(IBasicFilterable.FILTER_ITEMS_TAG, this.serializeNBT());
            }
        };
    }

    @Override
    public boolean checkFilter(ItemStack stack) {
        return stack.getItem() instanceof ItemFood && super.checkFilter(stack);
    }

    @Override
    public int getFoodSlot(IItemHandler handler, int foodLevel, float health, float maxHealth) {

        int missingHunger = 20 - foodLevel;
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (stack == null || stack.stackSize <= 0) continue;
            if (!checkFilter(stack)) continue;

            ItemFood item = stack.getItem() instanceof ItemFood ? (ItemFood) stack.getItem() : null;
            if (item == null) continue;

            int healingAmount = item.func_150905_g(stack);

            if (maxHealth > health && getHealthFeedingStrategy() == FeedingStrategy.HEALTH.ALWAYS) return slot;

            boolean flag = switch (getHungerFeedingStrategy()) {
                case FULL -> healingAmount <= missingHunger;
                case HALF -> healingAmount / 2 <= missingHunger;
                case ALWAYS -> foodLevel < 20;
                default -> false;
            };

            if (flag) return slot;
        }
        return -1;
    }

    public FeedingStrategy.Hunger getHungerFeedingStrategy() {
        int ord = ItemNBTUtils.getInt(upgrade, HUNGER_FEEDING_STRATEGY_TAG, FeedingStrategy.Hunger.FULL.ordinal());
        FeedingStrategy.Hunger[] vals = FeedingStrategy.Hunger.values();
        return (ord < 0 || ord >= vals.length) ? FeedingStrategy.Hunger.FULL : vals[ord];
    }

    public void setHungerFeedingStrategy(FeedingStrategy.Hunger strategy) {
        if (strategy == null) {
            strategy = FeedingStrategy.Hunger.FULL;
        }
        ItemNBTUtils.setInt(upgrade, HUNGER_FEEDING_STRATEGY_TAG, strategy.ordinal());
    }

    public FeedingStrategy.HEALTH getHealthFeedingStrategy() {
        int ord = ItemNBTUtils.getInt(upgrade, HURT_FEEDING_STRATEGY_TAG, FeedingStrategy.HEALTH.ALWAYS.ordinal());
        FeedingStrategy.HEALTH[] vals = FeedingStrategy.HEALTH.values();
        return (ord < 0 || ord >= vals.length) ? FeedingStrategy.HEALTH.ALWAYS : vals[ord];
    }

    public void setHealthFeedingStrategy(FeedingStrategy.HEALTH strategy) {
        if (strategy == null) {
            strategy = FeedingStrategy.HEALTH.ALWAYS;
        }
        ItemNBTUtils.setInt(upgrade, HURT_FEEDING_STRATEGY_TAG, strategy.ordinal());
    }

    public static class FeedingStrategy {

        public enum Hunger {
            FULL,
            HALF,
            ALWAYS;
        }

        public enum HEALTH {
            ALWAYS,
            IGNORE;
        }
    }
}
