package ruiseki.omoshiroikamo.api.storage.wrapper;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.storage.IStorageWrapper;
import ruiseki.omoshiroikamo.module.backpack.client.gui.widget.updateGroup.UpgradeSlotUpdateGroup;
import ruiseki.omoshiroikamo.module.backpack.client.gui.widget.upgrade.ExpandedTabWidget;
import ruiseki.omoshiroikamo.module.backpack.common.block.BackpackPanel;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.UpgradeWrapperBase;

public interface IUpgradeWrapperFactory<W extends UpgradeWrapperBase> {

    W createWrapper(ItemStack stack, IStorageWrapper storage);

    void updateWidgetDelegates(W wrapper, UpgradeSlotUpdateGroup group);

    ExpandedTabWidget getExpandedTabWidget(int slotIndex, W wrapper, ItemStack stack, BackpackPanel panel,
        String titleKey);
}
