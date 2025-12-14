package ruiseki.omoshiroikamo.common.item.backpack.wrapper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.handler.ExposedItemStackHandler;
import ruiseki.omoshiroikamo.common.util.item.ItemNBTUtils;

public class BasicUpgradeWrapper extends UpgradeWrapper implements IBasicFilterable, IToggleable {

    protected ExposedItemStackHandler handler;

    public BasicUpgradeWrapper(ItemStack upgrade) {
        super(upgrade);
        handler = new ExposedItemStackHandler(9, this);
    }

    @Override
    public FilterType getFilterType() {
        int ordinal = ItemNBTUtils.getInt(upgrade, FILTER_TYPE_TAG, FilterType.BLACKLIST.ordinal());
        FilterType[] types = FilterType.values();
        if (ordinal < 0 || ordinal >= types.length) {
            return FilterType.BLACKLIST;
        }
        return types[ordinal];
    }

    @Override
    public void setFilterType(FilterType type) {
        if (type == null) {
            type = FilterType.BLACKLIST;
        }
        ItemNBTUtils.setInt(upgrade, FILTER_TYPE_TAG, type.ordinal());
    }

    @Override
    public ExposedItemStackHandler getFilterItems() {
        NBTTagCompound handlerTag = ItemNBTUtils.getCompound(upgrade, FILTER_ITEMS_TAG, false);
        if (handlerTag != null) {
            handler.deserializeNBT(handlerTag);
        }
        return handler;
    }

    @Override
    public void setFilterItems(ExposedItemStackHandler handler) {
        if (handler != null) {
            ItemNBTUtils.setCompound(upgrade, FILTER_ITEMS_TAG, handler.serializeNBT());
        }
    }

    @Override
    public boolean checkFilter(ItemStack check) {
        return isEnabled() && IBasicFilterable.super.checkFilter(check);
    }

    @Override
    public boolean isEnabled() {
        return ItemNBTUtils.getBoolean(upgrade, ENABLED_TAG, true);
    }

    @Override
    public void setEnabled(boolean enabled) {
        ItemNBTUtils.setBoolean(upgrade, ENABLED_TAG, enabled);
    }

    @Override
    public void toggle() {
        setEnabled(!isEnabled());
    }
}
