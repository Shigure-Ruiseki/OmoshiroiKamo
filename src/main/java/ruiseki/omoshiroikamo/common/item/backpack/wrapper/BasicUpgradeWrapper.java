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
        NBTTagCompound tag = ItemNBTUtils.getNBT(upgrade);
        int ordinal = tag.getInteger(FILTER_TYPE_TAG);
        FilterType[] types = FilterType.values();
        if (ordinal < 0 || ordinal >= types.length) {
            return FilterType.WHITELIST;
        }
        return types[ordinal];
    }

    @Override
    public void setFilterType(FilterType type) {
        if (type == null) {
            type = FilterType.WHITELIST;
        }
        NBTTagCompound tag = ItemNBTUtils.getNBT(upgrade);
        tag.setInteger(FILTER_TYPE_TAG, type.ordinal());
    }

    @Override
    public ExposedItemStackHandler getFilterItems() {
        NBTTagCompound tag = ItemNBTUtils.getNBT(upgrade);
        NBTTagCompound handlerTag = tag.getCompoundTag(FILTER_ITEMS_TAG);
        handler.deserializeNBT(handlerTag);
        return handler;
    }

    @Override
    public void setFilterItems(ExposedItemStackHandler handler) {
        if (handler == null) {
            return;
        }
        NBTTagCompound tag = ItemNBTUtils.getNBT(upgrade);
        tag.setTag(FILTER_ITEMS_TAG, handler.serializeNBT());
    }

    @Override
    public boolean checkFilter(ItemStack check) {
        return isEnabled() && IBasicFilterable.super.checkFilter(check);
    }

    @Override
    public boolean isEnabled() {
        NBTTagCompound tag = ItemNBTUtils.getNBT(upgrade);
        return tag.hasKey(IUpgrade.TAB_STATE_TAG) && tag.getBoolean(ENABLED_TAG);
    }

    @Override
    public void setEnabled(boolean enabled) {
        NBTTagCompound tag = ItemNBTUtils.getNBT(upgrade);
        tag.setBoolean(ENABLED_TAG, enabled);
    }

    @Override
    public void toggle() {
        setEnabled(!isEnabled());
    }
}
