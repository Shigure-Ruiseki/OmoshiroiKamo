package ruiseki.omoshiroikamo.module.ids.common.cableNet.part.tunnel.item;

import net.minecraft.item.ItemStack;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import ruiseki.omoshiroikamo.core.item.ItemStackKey;
import ruiseki.omoshiroikamo.core.item.ItemStackKeyPool;

public class ItemIndex {

    private final Object2IntOpenHashMap<ItemStackKey> items = new Object2IntOpenHashMap<>();

    public static final ItemIndex EMPTY = new ItemIndex();

    public void clear() {
        items.clear();
    }

    public void add(ItemStack stack) {
        items.addTo(ItemStackKeyPool.get(stack), stack.stackSize);
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
