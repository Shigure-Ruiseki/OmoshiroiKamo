package ruiseki.omoshiroikamo.module.storage.client.gui.widget;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.FeedingUpgradeWrapper;

public class FeedingUpgradeWidget extends BasicExpandedTabWidget<FeedingUpgradeWrapper> {

    public FeedingUpgradeWidget(int slotIndex, ItemStack stack, FeedingUpgradeWrapper wrapper) {
        super(slotIndex, stack, wrapper, "gui.backpack.feeding_settings", "feeding_filter");
    }
}
