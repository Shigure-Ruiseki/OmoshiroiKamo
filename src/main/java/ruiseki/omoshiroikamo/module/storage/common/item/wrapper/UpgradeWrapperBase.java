package ruiseki.omoshiroikamo.module.storage.common.item.wrapper;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.core.inventory.IStorageWrapper;
import ruiseki.omoshiroikamo.core.item.ItemNBTHelpers;

public class UpgradeWrapperBase implements IUpgradeWrapper {

    protected final ItemStack upgrade;
    protected final IStorageWrapper storage;

    public UpgradeWrapperBase(ItemStack upgrade, IStorageWrapper storage) {
        this.upgrade = upgrade;
        this.storage = storage;
    }

    @Override
    public void setTabOpened(boolean opened) {
        ItemNBTHelpers.setBoolean(upgrade, TAB_STATE_TAG, opened);
    }

    @Override
    public boolean isTabOpened() {
        return ItemNBTHelpers.getBoolean(upgrade, TAB_STATE_TAG, false);
    }

    @Override
    public String getSettingLangKey() {
        return "";
    }
}
