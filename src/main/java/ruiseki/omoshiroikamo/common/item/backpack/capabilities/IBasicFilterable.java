package ruiseki.omoshiroikamo.common.item.backpack.capabilities;

import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.handler.ExposedItemStackHandler;

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
