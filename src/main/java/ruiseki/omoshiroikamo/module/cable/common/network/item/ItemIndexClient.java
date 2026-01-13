package ruiseki.omoshiroikamo.module.cable.common.network.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    private static String norm(String s) {
        return s == null ? "" : s.toLowerCase(Locale.ROOT);
    }

    public List<ItemStackKey> search(String query) {
        rebuildCacheIfNeeded();

        String q = norm(query);
        if (q.isEmpty()) return keyCache;

        ArrayList<ItemStackKey> result = new ArrayList<>();

        for (ItemStackKey key : keyCache) {
            if (matches(key, q)) {
                result.add(key);
            }
        }
        return result;
    }

    private boolean matches(ItemStackKey key, String q) {
        var item = key.item;

        String reg = item.getUnlocalizedName();
        if (reg != null && reg.toLowerCase()
            .contains(q)) return true;

        String name = key.toStack(1)
            .getDisplayName();
        return name != null && name.toLowerCase()
            .contains(q);
    }

    public void destroy() {
        db.clear();
        keyCache.clear();
        indexVersion = -1;
        cacheDirty = true;
    }
}
