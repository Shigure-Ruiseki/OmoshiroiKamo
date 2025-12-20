package ruiseki.omoshiroikamo.common.item.backpack.wrapper;

import net.minecraft.item.ItemStack;

import lombok.Getter;
import ruiseki.omoshiroikamo.api.item.ItemNBTUtils;

public class UpgradeWrapper implements IUpgrade {

    @Getter
    protected final ItemStack upgrade;

    public UpgradeWrapper(ItemStack upgrade) {
        this.upgrade = upgrade;
    }

    @Override
    public void setTabOpened(boolean opened) {
        ItemNBTUtils.setBoolean(upgrade, TAB_STATE_TAG, opened);
    }

    @Override
    public boolean isTabOpened() {
        return ItemNBTUtils.getBoolean(upgrade, TAB_STATE_TAG, false);
    }

    @Override
    public String getSettingLangKey() {
        return "";
    }

}
