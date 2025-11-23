package ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget;

import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.item.backpack.capabilities.FeedingUpgradeWrapper;

public class FeedingUpgradeWidget extends BasicExpandedTabWidget<FeedingUpgradeWrapper> {

    public FeedingUpgradeWidget(int slotIndex, FeedingUpgradeWrapper wrapper) {
        super(slotIndex, wrapper, ModItems.FEEDING_UPGRADE.newItemStack(), "gui.feeding_settings", "feeding_filter");
    }
}
