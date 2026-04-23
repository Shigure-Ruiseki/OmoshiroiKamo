package ruiseki.omoshiroikamo.module.backpack.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.storage.IStorageWrapper;
import ruiseki.omoshiroikamo.module.backpack.client.gui.syncHandler.DelegatedStackHandlerSH;
import ruiseki.omoshiroikamo.module.backpack.client.gui.widget.updateGroup.UpgradeSlotUpdateGroup;
import ruiseki.omoshiroikamo.core.helper.LangHelpers;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.backpack.client.gui.widget.upgrade.ExpandedTabWidget;
import ruiseki.omoshiroikamo.module.backpack.client.gui.widget.upgrade.VoidUpgradeWidget;
import ruiseki.omoshiroikamo.module.backpack.common.block.BackpackPanel;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.VoidUpgradeWrapper;

public class ItemVoidUpgrade extends ItemUpgrade<VoidUpgradeWrapper> {

    public ItemVoidUpgrade() {
        super(ModObject.BACKPACK_VOID_UPGRADE.name);
        setMaxStackSize(1);
        setTextureName("backpack/void_upgrade");
    }

    @Override
    public boolean hasTab() {
        return true;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LangHelpers.localize(LibResources.TOOLTIP + "backpack.void_upgrade"));
    }

    @Override
    public VoidUpgradeWrapper createWrapper(ItemStack stack, IStorageWrapper storage) {
        return new VoidUpgradeWrapper(stack, storage);
    }

    @Override
    public void updateWidgetDelegates(VoidUpgradeWrapper wrapper, UpgradeSlotUpdateGroup group) {
        DelegatedStackHandlerSH handler = group.get("common_filter_handler");
        if (handler == null) return;
        handler.setDelegatedStackHandler(wrapper::getFilterItems);
        handler.syncToServer(DelegatedStackHandlerSH.UPDATE_FILTERABLE);
    }

    @Override
    public ExpandedTabWidget getExpandedTabWidget(int slotIndex, VoidUpgradeWrapper wrapper, ItemStack stack,
        BackpackPanel panel, String titleKey) {
        return new VoidUpgradeWidget(slotIndex, wrapper, stack, panel, titleKey);
    }
}
