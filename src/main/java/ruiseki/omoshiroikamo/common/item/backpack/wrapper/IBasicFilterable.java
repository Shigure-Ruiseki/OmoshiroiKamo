package ruiseki.omoshiroikamo.common.item.backpack.wrapper;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.handler.ExposedItemStackHandler;
import ruiseki.omoshiroikamo.common.util.item.ItemUtils;

public interface IBasicFilterable {

    String FILTER_ITEMS_TAG = "FilterItems";
    String FILTER_TYPE_TAG = "FilterType";

    ExposedItemStackHandler getFilterItems();

    void setFilterItems(ExposedItemStackHandler handler);

    FilterType getFilterType();

    void setFilterType(FilterType type);

    enum FilterType {
        WHITELIST,
        BLACKLIST;
    }

    default boolean checkFilter(ItemStack check) {
        switch (getFilterType()) {
            case WHITELIST:
                for (ItemStack s : getFilterItems().getStacks()) {
                    if (ItemUtils.areItemsEqualIgnoreDurability(s, check)) {
                        return true;
                    }
                }
                return false;
            case BLACKLIST:
                for (ItemStack s : getFilterItems().getStacks()) {
                    if (ItemUtils.areItemsEqualIgnoreDurability(s, check)) {
                        return false;
                    }
                }
                return true;
            default:
                return false;
        }
    }
}
