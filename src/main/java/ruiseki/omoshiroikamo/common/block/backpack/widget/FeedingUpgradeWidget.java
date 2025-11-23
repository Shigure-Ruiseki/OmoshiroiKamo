package ruiseki.omoshiroikamo.common.block.backpack.widget;

import ruiseki.omoshiroikamo.common.block.backpack.capabilities.FeedingUpgradeWrapper;
import ruiseki.omoshiroikamo.common.init.ModItems;

public class FeedingUpgradeWidget extends BasicExpandedTabWidget<FeedingUpgradeWrapper> {

    public FeedingUpgradeWidget(int slotIndex, FeedingUpgradeWrapper wrapper) {
        super(slotIndex, wrapper, ModItems.FEEDING_UPGRADE.newItemStack(), "gui.feeding_settings", "feeding_filter");
    }
}
