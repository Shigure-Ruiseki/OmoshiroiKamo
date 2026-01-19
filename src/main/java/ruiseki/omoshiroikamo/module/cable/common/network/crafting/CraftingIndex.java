package ruiseki.omoshiroikamo.module.cable.common.network.crafting;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import ruiseki.omoshiroikamo.api.item.ItemStackKey;

public class CraftingIndex {

    private final ObjectOpenHashSet<ItemStackKey> craftables = new ObjectOpenHashSet<>();

    public void clear() {
        craftables.clear();
    }

    public void addCraftable(ItemStackKey key) {
        craftables.add(key);
    }

    public boolean isCraftable(ItemStackKey key) {
        return craftables.contains(key);
    }

    public ObjectOpenHashSet<ItemStackKey> keys() {
        return craftables;
    }
}
