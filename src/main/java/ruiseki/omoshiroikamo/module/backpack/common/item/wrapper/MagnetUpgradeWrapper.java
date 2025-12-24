package ruiseki.omoshiroikamo.module.backpack.common.item.wrapper;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.item.ItemNBTUtils;

public class MagnetUpgradeWrapper extends PickupUpgradeWrapper implements IMagnetUpgrade {

    public MagnetUpgradeWrapper(ItemStack upgrade) {
        super(upgrade);
    }

    @Override
    public boolean isCollectItem() {
        return ItemNBTUtils.getBoolean(upgrade, MAG_ITEM_TAG, true);
    }

    @Override
    public void setCollectItem(boolean enabled) {
        ItemNBTUtils.setBoolean(upgrade, MAG_ITEM_TAG, enabled);
    }

    @Override
    public boolean isCollectExp() {
        return ItemNBTUtils.getBoolean(upgrade, MAG_EXP_TAG, true);
    }

    @Override
    public void setCollectExp(boolean enabled) {
        ItemNBTUtils.setBoolean(upgrade, MAG_EXP_TAG, enabled);
    }

    @Override
    public boolean canCollectItem(ItemStack stack) {
        return checkFilter(stack);
    }
}
