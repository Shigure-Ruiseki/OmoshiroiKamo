package ruiseki.omoshiroikamo.module.backpack.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.storage.IStorageWrapper;
import ruiseki.omoshiroikamo.api.storage.widget.UpgradeSlotUpdateGroup;
import ruiseki.omoshiroikamo.api.storage.wrapper.IUpgradeWrapperFactory;
import ruiseki.omoshiroikamo.api.storage.wrapper.UpgradeWrapperBase;
import ruiseki.omoshiroikamo.core.helper.LangHelpers;
import ruiseki.omoshiroikamo.core.item.ItemOK;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.backpack.client.gui.widget.ExpandedTabWidget;
import ruiseki.omoshiroikamo.module.backpack.common.block.BackpackPanel;

public class ItemUpgrade<T extends UpgradeWrapperBase> extends ItemOK implements IUpgradeWrapperFactory<T> {

    public ItemUpgrade(String name) {
        super(name);
        setNoRepair();
        setTextureName("backpack/base_upgrade");
    }

    public ItemUpgrade() {
        this(ModObject.BACKPACK_BASE_UPGRADE.name);
    }

    public boolean hasTab() {
        return false;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LangHelpers.localize(LibResources.TOOLTIP + "backpack.base_upgrade"));
    }

    @SuppressWarnings("unchecked")
    @Override
    public T createWrapper(ItemStack stack, IStorageWrapper storage) {
        return (T) new UpgradeWrapperBase(stack, storage);
    }

    @Override
    public void updateWidgetDelegates(T wrapper, UpgradeSlotUpdateGroup group) {

    }

    @Override
    public ExpandedTabWidget getExpandedTabWidget(int slotIndex, T wrapper, ItemStack stack, BackpackPanel panel,
        String titleKey) {
        return null;
    }
}
