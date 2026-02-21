package ruiseki.omoshiroikamo.core.item;

import com.google.common.collect.ImmutableList;

public enum CraftingFilter {

    STORAGE,
    CRAFTABLE,
    BOTH,;

    private static final ImmutableList<CraftingFilter> values = ImmutableList.copyOf(values());

    public static CraftingFilter byIndex(int index) {
        if (index < 0 || index >= values.size()) return BOTH;

        return values.get(index);
    }

    public int getIndex() {
        return values.indexOf(this);
    }
}
