package ruiseki.omoshiroikamo.common.item.backpack.capabilities;

import net.minecraft.item.ItemStack;

public interface IUpgrade {

    String TAB_STATE_TAG = "TabState";

    void setTabOpened(ItemStack upgrade, boolean opened);

    boolean getTabOpened(ItemStack upgrade);

}
