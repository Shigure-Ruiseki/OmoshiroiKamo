package ruiseki.omoshiroikamo.api.storage.wrapper;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.storage.IStorageWrapper;
import ruiseki.omoshiroikamo.api.storage.widget.UpgradeSlotUpdateGroup;
import ruiseki.omoshiroikamo.module.backpack.client.gui.widget.ExpandedTabWidget;
import ruiseki.omoshiroikamo.module.backpack.common.block.BackpackPanel;

public class UpgradeWrapperFactory {

    @SuppressWarnings("unchecked")
    public static <W extends UpgradeWrapperBase> W createWrapper(ItemStack stack, IStorageWrapper storage) {
        if (stack == null || stack.getItem() == null) return null;
        if (!(stack.getItem() instanceof IUpgradeWrapperFactory<?>factory)) return null;
        return (W) factory.createWrapper(stack, storage);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void updateWidgetDelegates(ItemStack stack, UpgradeWrapperBase wrapper,
        UpgradeSlotUpdateGroup group) {
        if (stack == null || stack.getItem() == null || wrapper == null) return;
        if (!(stack.getItem() instanceof IUpgradeWrapperFactory factory)) return;
        factory.updateWidgetDelegates(wrapper, group);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static ExpandedTabWidget getExpandedTabWidget(ItemStack stack, int slotIndex, UpgradeWrapperBase wrapper,
        BackpackPanel panel, String titleKey) {
        if (stack == null || stack.getItem() == null || wrapper == null) return null;
        if (!(stack.getItem() instanceof IUpgradeWrapperFactory factory)) return null;
        return factory.getExpandedTabWidget(slotIndex, wrapper, stack, panel, titleKey);
    }

}
