package ruiseki.omoshiroikamo.module.cable.common.network.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;

public class ItemIndexClient {

    private final Object2IntOpenHashMap<ItemStackKey> db = new Object2IntOpenHashMap<>();
    private final ArrayList<ItemStackKey> keyCache = new ArrayList<>();

    private int indexVersion = -1;
    private boolean cacheDirty = true;

    public int getServerVersion() {
        return indexVersion;
    }

    public Object2IntOpenHashMap<ItemStackKey> view() {
        return db;
    }

    public int size() {
        return view().size();
    }

    public int get(ItemStackKey key) {
        return db.getOrDefault(key, 0);
    }

    public void replace(Object2IntOpenHashMap<ItemStackKey> newDb, int version) {
        db.clear();
        db.putAll(newDb);
        indexVersion = version;
        cacheDirty = true;
    }

    public void applyDelta(Object2IntOpenHashMap<ItemStackKey> added, ObjectSet<ItemStackKey> removed, int version) {
        db.putAll(added);
        removed.forEach(db::removeInt);
        indexVersion = version;
        cacheDirty = true;
    }

    private void rebuildCacheIfNeeded() {
        if (!cacheDirty) return;

        keyCache.clear();
        keyCache.ensureCapacity(db.size());
        keyCache.addAll(db.keySet());

        cacheDirty = false;
    }

    public List<ItemStackKey> viewGrid(int rowOffset, int columns, int visibleRows) {
        rebuildCacheIfNeeded();

        int startIndex = rowOffset * columns;
        int maxItems = columns * visibleRows;

        int total = keyCache.size();
        if (total == 0 || startIndex >= total) {
            return Collections.emptyList();
        }

        int to = Math.min(total, startIndex + maxItems);

        return keyCache.subList(startIndex, to);
    }
}
