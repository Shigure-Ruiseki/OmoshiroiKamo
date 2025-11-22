package ruiseki.omoshiroikamo.common.block.backpack.capabilities;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.common.block.backpack.handler.ExposedItemStackHandler;
import ruiseki.omoshiroikamo.common.util.item.ItemUtils;

public interface IBasicFilterable {

    String FILTER_ITEMS_TAG = "FilterItems";
    String FILTER_TYPE_TAG = "FilterType";

    ExposedItemStackHandler getFilterItems(ItemStack upgrade);

    void setFilterItems(ItemStack upgrade, ExposedItemStackHandler handler);

    FilterType getFilterType(ItemStack upgrade);

    void setFilterType(ItemStack stack, FilterType type);

    default boolean checkFilter(ItemStack upgrade, ItemStack check) {
        switch (getFilterType(upgrade)) {
            case WHITELIST:
                for (ItemStack s : getFilterItems(upgrade).getStacks()) {
                    if (ItemUtils.areItemsEqualIgnoreDurability(s, check)) {
                        return true;
                    }
                }
                return false;
            case BLACKLIST:
                for (ItemStack s : getFilterItems(upgrade).getStacks()) {
                    if (ItemUtils.areItemsEqualIgnoreDurability(s, check)) {
                        return false;
                    }
                }
                return true;
            default:
                return false;
        }
    }

    enum FilterType {
        WHITELIST,
        BLACKLIST;
    }

    class Impl implements IBasicFilterable {

        private final ExposedItemStackHandler filterItems = new ExposedItemStackHandler(0);
        private FilterType filterType = FilterType.WHITELIST;

        @Override
        public ExposedItemStackHandler getFilterItems(ItemStack upgrade) {
            return filterItems;
        }

        @Override
        public void setFilterItems(ItemStack upgrade, ExposedItemStackHandler handler) {

        }

        @Override
        public FilterType getFilterType(ItemStack upgrade) {
            return filterType;
        }

        @Override
        public void setFilterType(ItemStack stack, FilterType type) {

        }
    }
}
