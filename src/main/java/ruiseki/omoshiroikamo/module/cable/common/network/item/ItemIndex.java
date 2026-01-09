package ruiseki.omoshiroikamo.module.cable.common.network.item;

import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ItemIndex {

    private final Map<ItemStackKey, Integer> items = new HashMap<>();

    public void clear() {
        items.clear();
    }

    public void add(ItemStack stack) {
        if (stack == null) return;
        ItemStackKey key = ItemStackKey.of(stack);
        items.merge(key, stack.stackSize, Integer::sum);
    }

    public Map<ItemStackKey, Integer> view() {
        return Collections.unmodifiableMap(items);
    }

    public Map<ItemStackKey, Integer> snapshot() {
        return Collections.unmodifiableMap(new HashMap<>(items));
    }
}
