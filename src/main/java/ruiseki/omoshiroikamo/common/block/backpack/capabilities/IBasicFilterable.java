package ruiseki.omoshiroikamo.common.block.backpack.capabilities;

import ruiseki.omoshiroikamo.common.block.backpack.handler.ExposedItemStackHandler;

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
}
