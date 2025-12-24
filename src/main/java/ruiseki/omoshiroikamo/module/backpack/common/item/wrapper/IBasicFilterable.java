package ruiseki.omoshiroikamo.module.backpack.common.item.wrapper;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.item.ItemUtils;
import ruiseki.omoshiroikamo.module.backpack.client.gui.handler.UpgradeItemStackHandler;

public interface IBasicFilterable {

    String FILTER_ITEMS_TAG = "FilterItems";
    String FILTER_TYPE_TAG = "FilterType";

    UpgradeItemStackHandler getFilterItems();

    void setFilterItems(UpgradeItemStackHandler handler);

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
