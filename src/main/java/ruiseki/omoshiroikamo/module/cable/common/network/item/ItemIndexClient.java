package ruiseki.omoshiroikamo.module.cable.common.network.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import ruiseki.omoshiroikamo.api.enums.SortType;

public class ItemIndexClient {

    private final Object2IntOpenHashMap<ItemStackKey> db = new Object2IntOpenHashMap<>();
    private final ArrayList<ItemStackKey> keyCache = new ArrayList<>();

    private int indexVersion = -1;
    private boolean cacheDirty = true;

    private SortType sortType = SortType.BY_NAME;
    private boolean sortOrder = false;

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
        keyCache.addAll(db.keySet());

        keyCache.sort(buildComparator());

        cacheDirty = false;
    }

    public void setSort(SortType type) {
        this.sortType = type;
        cacheDirty = true;
    }

    public SortType getSortType() {
        return sortType;
    }

    public void setSortOrder(boolean sortOrder) {
        this.sortOrder = sortOrder;
        cacheDirty = true;
    }

    public boolean getSortOrder() {
        return this.sortOrder;
    }


    private Comparator<ItemStackKey> buildComparator() {
        return (a, b) -> {
            if (a == null) return b == null ? 0 : 1;
            if (b == null) return -1;

            int r = 0;
            switch (getSortType()) {
                case BY_NAME:
                    r = a.getDisplayName().compareToIgnoreCase(b.getDisplayName());
                    break;

                case BY_MOD_ID:
                    r = a.getModId().compareToIgnoreCase(b.getModId());
                    break;

                case BY_COUNT:
                    r = Integer.compare(db.getInt(b), db.getInt(a));
                    break;
            }
            return getSortOrder() ? -r : r;
        };
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
