package ruiseki.omoshiroikamo.module.backpack.client.gui.widget;

import ruiseki.omoshiroikamo.module.backpack.common.init.BackpackItems;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.FeedingUpgradeWrapper;

public class FeedingUpgradeWidget extends BasicExpandedTabWidget<FeedingUpgradeWrapper> {

    public FeedingUpgradeWidget(int slotIndex, FeedingUpgradeWrapper wrapper) {
        super(
            slotIndex,
            wrapper,
            BackpackItems.FEEDING_UPGRADE.newItemStack(),
            "gui.backpack.feeding_settings",
            "feeding_filter");
    }
}
