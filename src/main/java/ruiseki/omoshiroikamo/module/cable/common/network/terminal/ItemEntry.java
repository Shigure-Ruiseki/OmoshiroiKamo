package ruiseki.omoshiroikamo.module.cable.common.network.terminal;

import ruiseki.omoshiroikamo.api.item.ItemStackKey;

public final class ItemEntry {

    public final ItemStackKey displayStack;

    public final int storedAmount;
    public final boolean craftable;

    public ItemEntry(ItemStackKey displayStack, int storedAmount, boolean craftable) {
        this.displayStack = displayStack;
        this.storedAmount = storedAmount;
        this.craftable = craftable;
    }

    public boolean hasStorage() {
        return storedAmount > 0;
    }

    public boolean hasCraftable() {
        return craftable;
    }
}
