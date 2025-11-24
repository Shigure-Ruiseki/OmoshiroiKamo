package ruiseki.omoshiroikamo.common.item.backpack.wrapper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import lombok.Getter;
import ruiseki.omoshiroikamo.common.util.item.ItemNBTUtils;

public class UpgradeWrapper implements IUpgrade {

    @Getter
    protected final ItemStack upgrade;

    public UpgradeWrapper(ItemStack upgrade) {
        this.upgrade = upgrade;
    }

    @Override
    public void setTabOpened(boolean opened) {
        NBTTagCompound tag = ItemNBTUtils.getNBT(upgrade);
        tag.setBoolean(TAB_STATE_TAG, opened);
    }

    @Override
    public boolean getTabOpened() {
        NBTTagCompound tag = ItemNBTUtils.getNBT(upgrade);
        return tag.hasKey(TAB_STATE_TAG) && tag.getBoolean(TAB_STATE_TAG);
    }

    @Override
    public String getSettingLangKey() {
        return "";
    }

}
