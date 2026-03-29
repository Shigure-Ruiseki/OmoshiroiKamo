package ruiseki.omoshiroikamo.module.storage.common.item.wrapper;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.core.inventory.IStorageWrapper;
import ruiseki.omoshiroikamo.core.item.ItemNBTHelpers;

public class MagnetUpgradeWrapper extends PickupUpgradeWrapper implements IMagnetUpgrade {

    public MagnetUpgradeWrapper(ItemStack upgrade, IStorageWrapper storage) {
        super(upgrade, storage);
    }

    @Override
    public boolean isCollectItem() {
        return ItemNBTHelpers.getBoolean(upgrade, MAG_ITEM_TAG, true);
    }

    @Override
    public void setCollectItem(boolean enabled) {
        ItemNBTHelpers.setBoolean(upgrade, MAG_ITEM_TAG, enabled);
    }

    @Override
    public boolean isCollectExp() {
        return ItemNBTHelpers.getBoolean(upgrade, MAG_EXP_TAG, true);
    }

    @Override
    public void setCollectExp(boolean enabled) {
        ItemNBTHelpers.setBoolean(upgrade, MAG_EXP_TAG, enabled);
    }

    @Override
    public boolean canCollectItem(ItemStack stack) {
        return checkFilter(stack);
    }
}
