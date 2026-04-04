package ruiseki.omoshiroikamo.api.storage.wrapper;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.storage.IStorageWrapper;
import ruiseki.omoshiroikamo.api.storage.widget.UpgradeSlotUpdateGroup;
import ruiseki.omoshiroikamo.module.backpack.client.gui.widget.ExpandedTabWidget;
import ruiseki.omoshiroikamo.module.backpack.common.block.BackpackPanel;

public interface IUpgradeWrapperFactory<W extends UpgradeWrapperBase> {

    W createWrapper(ItemStack stack, IStorageWrapper storage);

    void updateWidgetDelegates(W wrapper, UpgradeSlotUpdateGroup group);

    ExpandedTabWidget getExpandedTabWidget(int slotIndex, W wrapper, ItemStack stack, BackpackPanel panel,
        String titleKey);
}
