package ruiseki.omoshiroikamo.module.backpack.client.gui.widget.upgrade;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.module.backpack.common.block.BackpackPanel;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.FeedingUpgradeWrapper;

public class FeedingUpgradeWidget extends BasicExpandedTabWidget<FeedingUpgradeWrapper> {

    public FeedingUpgradeWidget(int slotIndex, FeedingUpgradeWrapper wrapper, ItemStack stack, BackpackPanel panel,
        String titleKey) {
        super(slotIndex, wrapper, stack, titleKey, "feeding_filter");
    }
}
