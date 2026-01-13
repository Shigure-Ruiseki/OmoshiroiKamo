package ruiseki.omoshiroikamo.module.cable.common.network.item;

import net.minecraft.item.ItemStack;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class ItemIndex {

    private final Object2IntOpenHashMap<ItemStackKey> items = new Object2IntOpenHashMap<>();

    public void clear() {
        items.clear();
    }

    public void add(ItemStack stack) {
        items.addTo(ItemStackKey.of(stack), stack.stackSize);
    }

    public int get(ItemStackKey key) {
        return items.getOrDefault(key, 0);
    }

    public Object2IntOpenHashMap<ItemStackKey> view() {
        return items;
    }

    public void mergeFrom(ItemIndex other) {
        items.clear();
        items.putAll(other.items);
    }
}
