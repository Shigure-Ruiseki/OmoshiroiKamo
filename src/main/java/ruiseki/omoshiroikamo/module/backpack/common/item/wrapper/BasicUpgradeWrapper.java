package ruiseki.omoshiroikamo.module.backpack.common.item.wrapper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import ruiseki.omoshiroikamo.api.storage.IStorageWrapper;
import ruiseki.omoshiroikamo.api.storage.wrapper.IBasicFilterable;
import ruiseki.omoshiroikamo.api.storage.wrapper.IToggleable;
import ruiseki.omoshiroikamo.api.storage.wrapper.UpgradeWrapperBase;
import ruiseki.omoshiroikamo.core.client.gui.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.core.item.ItemNBTHelpers;

public class BasicUpgradeWrapper extends UpgradeWrapperBase implements IBasicFilterable, IToggleable {

    protected ItemStackHandlerBase handler;

    public BasicUpgradeWrapper(ItemStack upgrade, IStorageWrapper storage) {
        super(upgrade, storage);
        handler = new ItemStackHandlerBase(9) {

            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                NBTTagCompound tag = ItemNBTHelpers.getNBT(upgrade);
                tag.setTag(FILTER_ITEMS_TAG, handler.serializeNBT());
            }
        };
        NBTTagCompound handlerTag = ItemNBTHelpers.getCompound(upgrade, FILTER_ITEMS_TAG, false);
        if (handlerTag != null) handler.deserializeNBT(handlerTag);
    }

    @Override
    public FilterType getFilterType() {
        int ordinal = ItemNBTHelpers.getInt(upgrade, FILTER_TYPE_TAG, FilterType.BLACKLIST.ordinal());
        FilterType[] types = FilterType.values();
        if (ordinal < 0 || ordinal >= types.length) return FilterType.BLACKLIST;
        return types[ordinal];
    }

    @Override
    public void setFilterType(FilterType type) {
        if (type == null) type = FilterType.BLACKLIST;
        ItemNBTHelpers.setInt(upgrade, FILTER_TYPE_TAG, type.ordinal());
        markDirty();
    }

    @Override
    public ItemStackHandlerBase getFilterItems() {
        return handler;
    }

    @Override
    public boolean checkFilter(ItemStack check) {
        return isEnabled() && IBasicFilterable.super.checkFilter(check);
    }

    @Override
    public boolean isEnabled() {
        return ItemNBTHelpers.getBoolean(upgrade, ENABLED_TAG, true);
    }

    @Override
    public void setEnabled(boolean enabled) {
        ItemNBTHelpers.setBoolean(upgrade, ENABLED_TAG, enabled);
        markDirty();
    }

    @Override
    public void toggle() {
        setEnabled(!isEnabled());
    }
}
