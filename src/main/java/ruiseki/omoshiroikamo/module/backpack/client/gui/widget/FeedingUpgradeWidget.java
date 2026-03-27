package ruiseki.omoshiroikamo.module.backpack.client.gui.widget;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.FeedingUpgradeWrapper;

public class FeedingUpgradeWidget extends BasicExpandedTabWidget<FeedingUpgradeWrapper> {

    public FeedingUpgradeWidget(int slotIndex, ItemStack stack, FeedingUpgradeWrapper wrapper) {
        super(slotIndex, stack, wrapper, "gui.backpack.feeding_settings", "feeding_filter");
    }
}
