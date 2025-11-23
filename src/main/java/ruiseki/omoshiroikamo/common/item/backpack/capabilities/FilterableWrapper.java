package ruiseki.omoshiroikamo.common.item.backpack.capabilities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.handler.ExposedItemStackHandler;
import ruiseki.omoshiroikamo.common.util.item.ItemNBTUtils;
import ruiseki.omoshiroikamo.common.util.item.ItemUtils;

public class FilterableWrapper extends ToggleableWrapper implements IBasicFilterable {

    protected ExposedItemStackHandler handler;

    public FilterableWrapper(ItemStack upgrade) {
        super(upgrade);
        handler = new ExposedItemStackHandler(9, this);
    }

    @Override
    public FilterType getFilterType() {
        NBTTagCompound tag = ItemNBTUtils.getNBT(upgrade);
        int ordinal = tag.getInteger(FILTER_TYPE_TAG);
        IBasicFilterable.FilterType[] types = IBasicFilterable.FilterType.values();
        if (ordinal < 0 || ordinal >= types.length) {
            return IBasicFilterable.FilterType.WHITELIST;
        }
        return types[ordinal];
    }

    @Override
    public void setFilterType(IBasicFilterable.FilterType type) {
        if (type == null) {
            type = IBasicFilterable.FilterType.WHITELIST;
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

    public boolean checkFilter(ItemStack check) {
        switch (getFilterType()) {
            case WHITELIST:
                for (ItemStack s : getFilterItems().getStacks()) {
                    if (ItemUtils.areItemsEqualIgnoreDurability(s, check)) {
                        return true;
                    }
                }
                return false;
            case BLACKLIST:
                for (ItemStack s : getFilterItems().getStacks()) {
                    if (ItemUtils.areItemsEqualIgnoreDurability(s, check)) {
                        return false;
                    }
                }
                return true;
            default:
                return false;
        }
    }
}
