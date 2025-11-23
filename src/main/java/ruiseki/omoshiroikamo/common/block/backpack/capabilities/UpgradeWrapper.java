package ruiseki.omoshiroikamo.common.block.backpack.capabilities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import lombok.Getter;
import ruiseki.omoshiroikamo.common.util.item.ItemNBTUtils;

public class UpgradeWrapper {

    @Getter
    protected final ItemStack upgrade;

    public UpgradeWrapper(ItemStack upgrade) {
        this.upgrade = upgrade;
    }

    public void setTabOpened(boolean opened) {
        NBTTagCompound tag = ItemNBTUtils.getNBT(upgrade);
        tag.setBoolean(IUpgrade.TAB_STATE_TAG, opened);
    }

    public boolean getTabOpened() {
        NBTTagCompound tag = ItemNBTUtils.getNBT(upgrade);
        return tag.hasKey(IUpgrade.TAB_STATE_TAG) && tag.getBoolean(IUpgrade.TAB_STATE_TAG);
    }
}
