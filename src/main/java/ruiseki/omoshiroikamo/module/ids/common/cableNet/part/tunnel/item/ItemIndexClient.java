package ruiseki.omoshiroikamo.module.ids.common.cableNet.part.tunnel.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import ruiseki.omoshiroikamo.api.enums.SortType;
import ruiseki.omoshiroikamo.core.common.search.SearchNode;
import ruiseki.omoshiroikamo.core.common.search.SearchParser;
import ruiseki.omoshiroikamo.core.item.CraftingFilter;
import ruiseki.omoshiroikamo.core.item.ItemStackKey;

public class ItemIndexClient {

    private final Object2IntOpenHashMap<ItemStackKey> storage = new Object2IntOpenHashMap<>();
    private final ObjectOpenHashSet<ItemStackKey> craftables = new ObjectOpenHashSet<>();

    private final ObjectOpenHashSet<ItemStackKey> allKeys = new ObjectOpenHashSet<>();

    private final ArrayList<ItemStackKey> keyCache = new ArrayList<>();

    private int indexVersion = -1;
    private boolean cacheDirty = true;

    private SortType sortType = SortType.BY_NAME;
    private boolean sortOrder = false;

    private SearchNode compiledSearch;
    private String search = "";

    private CraftingFilter craftingFilter = CraftingFilter.BOTH;

    public int getServerVersion() {
        return indexVersion;
    }

    public int size() {
        rebuildCacheIfNeeded();
        return keyCache.size();
    }

    public int getStored(ItemStackKey key) {
        return storage.getOrDefault(key, 0);
    }

    public boolean isCraftable(ItemStackKey key) {
        return craftables.contains(key);
    }

    public void replace(Object2IntOpenHashMap<ItemStackKey> newStorage, ObjectOpenHashSet<ItemStackKey> newCraftables,
        int version) {
        storage.clear();
        storage.putAll(newStorage);

        craftables.clear();
        craftables.addAll(newCraftables);

        rebuildAllKeys();

        indexVersion = version;
        cacheDirty = true;
    }

    public void applyDelta(Object2IntOpenHashMap<ItemStackKey> added, ObjectOpenHashSet<ItemStackKey> removed,
        ObjectOpenHashSet<ItemStackKey> newCraftables, int version) {
        storage.putAll(added);
        removed.forEach(storage::removeInt);

        craftables.clear();
        craftables.addAll(newCraftables);

        rebuildAllKeys();

        indexVersion = version;
        cacheDirty = true;
    }

    private void rebuildAllKeys() {
        allKeys.clear();
        allKeys.addAll(storage.keySet());
        allKeys.addAll(craftables);
    }

    private void rebuildCacheIfNeeded() {
        if (!cacheDirty) return;

        keyCache.clear();

        for (ItemStackKey key : allKeys) {
            if (!matchesCraftingFilter(key)) continue;
            if (!matchesSearch(key)) continue;
            keyCache.add(key);
        }

        keyCache.sort(buildComparator());
        cacheDirty = false;
    }

    public void setSortType(SortType type) {
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
        return sortOrder;
    }

    public void setCraftingFilter(CraftingFilter craftingFilter) {
        this.craftingFilter = craftingFilter;
        cacheDirty = true;
    }

    public CraftingFilter getCraftingFilter() {
        return craftingFilter;
    }

    public void setSearch(String s) {
        this.search = s;
        this.compiledSearch = SearchParser.parse(s);
        cacheDirty = true;
    }

    public String getSearch() {
        return search;
    }

    private boolean matchesCraftingFilter(ItemStackKey key) {
        boolean hasStorage = storage.containsKey(key);
        boolean hasCraft = craftables.contains(key);

        return switch (craftingFilter) {
            case STORAGE -> hasStorage;
            case CRAFTABLE -> hasCraft;
            case BOTH -> hasStorage || hasCraft;
        };
    }

    private boolean matchesSearch(ItemStackKey key) {
        return compiledSearch == null || compiledSearch.matches(key);
    }

    private Comparator<ItemStackKey> buildComparator() {
        SortType type = sortType;
        boolean reverse = sortOrder;

        return (a, b) -> {
            int r = switch (type) {
                case BY_NAME -> a.getDisplayName()
                    .compareToIgnoreCase(b.getDisplayName());
                case BY_MOD_ID -> a.getModId()
                    .compareToIgnoreCase(b.getModId());
                case BY_COUNT -> Integer.compare(storage.getOrDefault(b, 0), storage.getOrDefault(a, 0));
                case BY_ORE_DICT -> 0;
            };
            return reverse ? -r : r;
        };
    }

    public List<ItemStackKey> viewGrid(int rowOffset, int columns, int rows) {
        rebuildCacheIfNeeded();

        int start = rowOffset * columns;
        int max = columns * rows;

        if (start >= keyCache.size()) {
            return Collections.emptyList();
        }

        return keyCache.subList(start, Math.min(keyCache.size(), start + max));
    }
}
