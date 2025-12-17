package ruiseki.omoshiroikamo.common.item.backpack.wrapper;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.common.util.item.ItemNBTUtils;

public class FilterUpgradeWrapper extends BasicUpgradeWrapper implements IFilterUpgrade {

    public FilterUpgradeWrapper(ItemStack upgrade) {
        super(upgrade);
    }

    @Override
    public FilterWayType getfilterWay() {
        int ordinal = ItemNBTUtils.getInt(upgrade, FILTER_WAY_TAG, FilterWayType.IN_OUT.ordinal());
        FilterWayType[] types = FilterWayType.values();
        if (ordinal < 0 || ordinal >= types.length) {
            return FilterWayType.IN_OUT;
        }
        return types[ordinal];
    }

    @Override
    public void setFilterWay(FilterWayType filterWay) {
        if (filterWay == null) {
            filterWay = FilterWayType.IN_OUT;
        }
        ItemNBTUtils.setInt(upgrade, FILTER_WAY_TAG, filterWay.ordinal());
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        if (!isEnabled()) {
            return true;
        }
        return checkFilter(stack) && (getfilterWay() == FilterWayType.IN_OUT || getfilterWay() == FilterWayType.IN);
    }

    @Override
    public boolean canExtract(ItemStack stack) {
        if (!isEnabled()) {
            return true;
        }
        return checkFilter(stack) && (getfilterWay() == FilterWayType.IN_OUT || getfilterWay() == FilterWayType.OUT);
    }
}
