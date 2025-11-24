package ruiseki.omoshiroikamo.common.item.backpack.wrapper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.handler.ExposedItemStackHandler;
import ruiseki.omoshiroikamo.common.util.item.ItemNBTUtils;

public class AdvancedUpgradeWrapper extends UpgradeWrapper implements IAdvancedFilterable, IToggleable {

    protected ExposedItemStackHandler handler;

    public AdvancedUpgradeWrapper(ItemStack upgrade) {
        super(upgrade);
        this.handler = new ExposedItemStackHandler(16, this);
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
        ItemNBTUtils.getNBT(upgrade)
            .setInteger(FILTER_TYPE_TAG, type.ordinal());
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
        ItemNBTUtils.getNBT(upgrade)
            .setTag(FILTER_ITEMS_TAG, handler.serializeNBT());
    }

    @Override
    public MatchType getMatchType() {
        NBTTagCompound tag = ItemNBTUtils.getNBT(upgrade);
        int ordinal = tag.getInteger(MATCH_TYPE_TAG);
        MatchType[] types = MatchType.values();
        if (ordinal < 0 || ordinal >= types.length) {
            return MatchType.MOD;
        }
        return types[ordinal];
    }

    @Override
    public void setMatchType(MatchType matchType) {
        if (matchType == null) {
            matchType = MatchType.ITEM;
        }
        ItemNBTUtils.getNBT(upgrade)
            .setInteger(MATCH_TYPE_TAG, matchType.ordinal());
    }

    @Override
    public List<String> getOreDictEntries() {
        NBTTagCompound tag = ItemNBTUtils.getNBT(upgrade);
        if (!tag.hasKey(ORE_DICT_LIST_TAG)) {
            return new ArrayList<>();
        }
        NBTTagCompound listTag = tag.getCompoundTag(ORE_DICT_LIST_TAG);
        List<String> list = new ArrayList<>();
        for (String key : listTag.func_150296_c()) {
            list.add(listTag.getString(key));
        }
        return list;
    }

    @Override
    public void setOreDictEntries(List<String> entries) {
        if (entries == null) {
            return;
        }
        NBTTagCompound tag = ItemNBTUtils.getNBT(upgrade);
        NBTTagCompound listTag = new NBTTagCompound();
        for (int i = 0; i < entries.size(); i++) {
            listTag.setString("e" + i, entries.get(i));
        }
        tag.setTag(ORE_DICT_LIST_TAG, listTag);
    }

    @Override
    public boolean getIgnoreDurability() {
        NBTTagCompound tag = ItemNBTUtils.getNBT(upgrade);
        return tag.hasKey(IGNORE_DURABILITY_TAG) && tag.getBoolean(IGNORE_DURABILITY_TAG);
    }

    @Override
    public void setIgnoreDurability(boolean ignore) {
        ItemNBTUtils.getNBT(upgrade)
            .setBoolean(IGNORE_DURABILITY_TAG, ignore);
    }

    @Override
    public boolean getIgnoreNBT() {
        NBTTagCompound tag = ItemNBTUtils.getNBT(upgrade);
        return tag.hasKey(IGNORE_NBT_TAG) && tag.getBoolean(IGNORE_NBT_TAG);
    }

    @Override
    public void setIgnoreNBT(boolean ignore) {
        ItemNBTUtils.getNBT(upgrade)
            .setBoolean(IGNORE_NBT_TAG, ignore);
    }

    @Override
    public boolean checkFilter(ItemStack check) {
        return isEnabled() && IAdvancedFilterable.super.checkFilter(check);
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
