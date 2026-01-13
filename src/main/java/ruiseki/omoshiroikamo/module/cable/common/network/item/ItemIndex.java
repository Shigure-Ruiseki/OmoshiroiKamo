package ruiseki.omoshiroikamo.module.cable.common.network.item;

import net.minecraft.item.ItemStack;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class ItemIndex {

    private final Object2IntOpenHashMap<ItemStackKey> items = new Object2IntOpenHashMap<>();

    public void clear() {
        items.clear();
    }

    public void add(ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) return;
        items.addTo(ItemStackKey.of(stack), stack.stackSize);
    }

    /** Internal view – do NOT modify outside network */
    public Object2IntOpenHashMap<ItemStackKey> view() {
        return items;
    }

    /** Snapshot for sync / diff */
    public Object2IntOpenHashMap<ItemStackKey> snapshot() {
        return new Object2IntOpenHashMap<>(items);
    }

    public int get(ItemStackKey key) {
        return items.getOrDefault(key, 0);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
