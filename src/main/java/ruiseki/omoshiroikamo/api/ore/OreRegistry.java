package ruiseki.omoshiroikamo.api.ore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class OreRegistry {

    private static final Map<String, OreEntry> REGISTRY = new LinkedHashMap<>();
    private static final Map<Integer, OreEntry> META_LOOKUP = new HashMap<>();
    private static final List<OreEntry> META_INDEXED = new ArrayList<>();

    public static void register(OreEntry entry) {
        if (REGISTRY.containsKey(entry.getName())) {
            throw new IllegalStateException("Duplicate ore: " + entry.getName());
        }

        REGISTRY.put(entry.getName(), entry);
        META_LOOKUP.put(entry.getMeta(), entry);

        while (META_INDEXED.size() <= entry.getMeta()) {
            META_INDEXED.add(null);
        }
        META_INDEXED.set(entry.getMeta(), entry);
    }

    public static OreEntry get(String name) {
        return REGISTRY.get(name);
    }

    public static OreEntry fromMeta(int meta) {
        if (meta < 0 || meta >= META_INDEXED.size()) {
            return META_INDEXED.get(0);
        }
        return META_INDEXED.get(meta);
    }

    public static int indexOf(OreEntry entry) {
        return entry.getMeta();
    }

    public static Collection<OreEntry> all() {
        return Collections.unmodifiableList(
            META_INDEXED.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }

    public static boolean contains(String name) {
        return REGISTRY.containsKey(name);
    }

    public static void preInit() {
        register(new OreEntry("Hematite", 0, 10, 12, 64, 0.4f, true, 0x7A1F1F, 5, 10, 2));
        register(new OreEntry("Magnetite", 1, 9, 8, 48, 0.35f, true, 0x1C1C1C, 5, 10, 2));

        register(new OreEntry("Chalcopyrite", 2, 12, 16, 64, 0.4f, true, 0xC27E3A, 3, 5, 1));
        register(new OreEntry("Malachite", 3, 10, 20, 48, 0.35f, true, 0x2E8B57, 3, 5, 1));
        register(new OreEntry("Bornite", 4, 8, 12, 40, 0.3f, true, 0xA0522D, 3, 6, 2));
        register(new OreEntry("Tetrahedrite", 5, 8, 18, 45, 0.28f, true, 0x806040, 3, 6, 2));
        register(new OreEntry("Cuprite", 6, 6, 10, 32, 0.25f, true, 0xB87333, 2, 4, 1));
        register(new OreEntry("Copper", 7, 4, 5, 24, 0.2f, true, 0xB45F04, 2, 4, 1));
    }

}
