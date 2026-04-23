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
import ruiseki.omoshiroikamo.module.backpack.client.gui.widget.upgrade.AdvancedFilterUpgradeWidget;
import ruiseki.omoshiroikamo.module.backpack.client.gui.widget.upgrade.ExpandedTabWidget;
import ruiseki.omoshiroikamo.module.backpack.common.block.BackpackPanel;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.AdvancedFilterUpgradeWrapper;

public class ItemAdvancedFilterUpgrade extends ItemUpgrade<AdvancedFilterUpgradeWrapper> {

    public ItemAdvancedFilterUpgrade() {
        super(ModObject.BACKPACK_ADVANCED_FILTER_UPGRADE.name);
        setMaxStackSize(1);
        setTextureName("backpack/advanced_filter_upgrade");
    }

    @Override
    public boolean hasTab() {
        return true;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LangHelpers.localize(LibResources.TOOLTIP + "backpack.advanced_filter_upgrade"));
    }

    @Override
    public AdvancedFilterUpgradeWrapper createWrapper(ItemStack stack, IStorageWrapper storage) {
        return new AdvancedFilterUpgradeWrapper(stack, storage);
    }

    @Override
    public void updateWidgetDelegates(AdvancedFilterUpgradeWrapper wrapper, UpgradeSlotUpdateGroup group) {
        DelegatedStackHandlerSH handler = group.get("adv_common_filter_handler");
        if (handler == null) return;
        handler.setDelegatedStackHandler(wrapper::getFilterItems);
        handler.syncToServer(DelegatedStackHandlerSH.UPDATE_FILTERABLE);

        DelegatedStackHandlerSH oreDictHandler = group.get("ore_dict_handler");
        if (oreDictHandler == null) return;
        oreDictHandler.setDelegatedStackHandler(wrapper::getOreDictItem);
        oreDictHandler.syncToServer(DelegatedStackHandlerSH.UPDATE_ORE_DICT);
    }

    @Override
    public ExpandedTabWidget getExpandedTabWidget(int slotIndex, AdvancedFilterUpgradeWrapper wrapper, ItemStack stack,
        BackpackPanel panel, String titleKey) {
        return new AdvancedFilterUpgradeWidget(slotIndex, wrapper, stack, panel, titleKey);
    }
}
