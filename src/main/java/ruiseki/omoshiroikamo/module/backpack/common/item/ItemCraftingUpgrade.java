package ruiseki.omoshiroikamo.module.backpack.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.storage.IStorageWrapper;
import ruiseki.omoshiroikamo.module.backpack.client.gui.syncHandler.DelegatedCraftingStackHandlerSH;
import ruiseki.omoshiroikamo.module.backpack.client.gui.widget.updateGroup.UpgradeSlotUpdateGroup;
import ruiseki.omoshiroikamo.core.helper.LangHelpers;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.backpack.client.gui.widget.upgrade.CraftingUpgradeWidget;
import ruiseki.omoshiroikamo.module.backpack.client.gui.widget.upgrade.ExpandedTabWidget;
import ruiseki.omoshiroikamo.module.backpack.common.block.BackpackPanel;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.CraftingUpgradeWrapper;

public class ItemCraftingUpgrade extends ItemUpgrade<CraftingUpgradeWrapper> {

    public ItemCraftingUpgrade() {
        super(ModObject.BACKPACK_CRAFTING_UPGRADE.name);
        setMaxStackSize(1);
        setTextureName("backpack/crafting_upgrade");
    }

    @Override
    public boolean hasTab() {
        return true;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LangHelpers.localize(LibResources.TOOLTIP + "backpack.crafting_upgrade"));
    }

    @Override
    public CraftingUpgradeWrapper createWrapper(ItemStack stack, IStorageWrapper storage) {
        return new CraftingUpgradeWrapper(stack, storage);
    }

    @Override
    public void updateWidgetDelegates(CraftingUpgradeWrapper wrapper, UpgradeSlotUpdateGroup group) {
        DelegatedCraftingStackHandlerSH handler = group.get("crafting_handler");
        if (handler == null) return;
        handler.setDelegatedStackHandler(wrapper::getStorage);
        handler.syncToServer(DelegatedCraftingStackHandlerSH.UPDATE_CRAFTING);
    }

    @Override
    public ExpandedTabWidget getExpandedTabWidget(int slotIndex, CraftingUpgradeWrapper wrapper, ItemStack stack,
        BackpackPanel panel, String titleKey) {
        return new CraftingUpgradeWidget(slotIndex, wrapper, stack, panel, titleKey);
    }
}
