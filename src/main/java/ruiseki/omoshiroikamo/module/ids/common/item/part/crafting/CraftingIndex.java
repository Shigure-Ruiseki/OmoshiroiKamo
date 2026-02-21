package ruiseki.omoshiroikamo.module.ids.common.item.part.crafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import ruiseki.omoshiroikamo.core.item.ItemStackKey;

public class CraftingIndex {

    private final Object2ObjectOpenHashMap<ItemStackKey, List<ICraftingPattern>> outputMap = new Object2ObjectOpenHashMap<>();

    public void clear() {
        outputMap.clear();
    }

    public void addPattern(ICraftingPattern pattern) {
        for (IngredientStack output : pattern.getOutputs()) {
            ItemStackKey key = output.getKey();

            outputMap.computeIfAbsent(key, k -> new ArrayList<>())
                .add(pattern);
        }
    }

    public List<ICraftingPattern> getPatterns(Object key) {
        return outputMap.getOrDefault(key, Collections.emptyList());
    }

    public boolean isCraftable(ItemStackKey key) {
        return outputMap.containsKey(key);
    }

    public ObjectOpenHashSet<ItemStackKey> getKeys() {
        return new ObjectOpenHashSet<>(outputMap.keySet());
    }
}
