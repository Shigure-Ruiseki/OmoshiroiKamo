package ruiseki.omoshiroikamo.api.material;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MaterialRegistry {

    private static final Map<String, MaterialEntry> REGISTRY = new LinkedHashMap<>();
    private static final Map<Integer, MaterialEntry> META_LOOKUP = new HashMap<>();
    private static final List<MaterialEntry> META_INDEXED = new ArrayList<>();

    public static void register(MaterialEntry entry) {
        if (REGISTRY.containsKey(entry.getName())) {
            throw new IllegalStateException("Duplicate material: " + entry.getName());
        }

        REGISTRY.put(entry.getName(), entry);
        META_LOOKUP.put(entry.meta, entry);

        while (META_INDEXED.size() <= entry.meta) {
            META_INDEXED.add(null);
        }
        META_INDEXED.set(entry.meta, entry);
    }

    public static MaterialEntry get(String name) {
        return REGISTRY.get(name);
    }

    public static MaterialEntry fromMeta(int meta) {
        if (meta < 0 || meta >= META_INDEXED.size()) {
            return META_INDEXED.get(0);
        }
        return META_INDEXED.get(meta);
    }

    public static int indexOf(MaterialEntry entry) {
        return entry.meta;
    }

    public static Collection<MaterialEntry> all() {
        return Collections.unmodifiableList(
            META_INDEXED.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }

    public static boolean contains(String name) {
        return REGISTRY.containsKey(name);
    }

    public static void preInit() {

        register(new MaterialEntry("Iron", 0, 7870, 449, 80.2, 1811, 25, 1.0e7, 0xD8D8D8, 0xFF5500));
        register(new MaterialEntry("Copper", 1, 8960, 385, 401, 1358, 21, 5.96e7, 0xF08048, 0xFF7700));
        register(new MaterialEntry("Silver", 2, 10490, 235, 429, 1234, 15, 6.30e7, 0xF0F0F0, 0xFFD700));
        register(new MaterialEntry("Gold", 3, 19300, 129, 318, 1337, 15, 4.52e7, 0xFFE14A, 0xFFCC00));
        register(new MaterialEntry("Zinc", 4, 7135, 388, 116, 692, 10, 1.69e7, 0xBBBBBB, 0xFFAA33));
        register(new MaterialEntry("Aluminum", 5, 2700, 900, 237, 933, 20, 3.77e7, 0xE5E5E5, 0xFFDDCC));
        register(new MaterialEntry("Lead", 6, 11340, 128, 35, 600, 5, 4.80e6, 0x5C5C5C, 0xAA2222));
        register(new MaterialEntry("Titanium", 7, 4507, 522, 21.9, 1941, 50, 2.38e6, 0xC8D8EF, 0xFF3300));
        register(new MaterialEntry("Chromium", 8, 7190, 449, 93.9, 2180, 40, 7.90e6, 0xB0FF5A, 0xFF2200));
        register(new MaterialEntry("Nickel", 9, 8908, 440, 90.9, 1728, 35, 1.43e7, 0xCCCCCC, 0xFF7700));
        register(new MaterialEntry("Carbon Steel", 10, 7850, 486, 50, 1698, 35, 6.00e6, 0x707070, 0xAA5500));
        register(new MaterialEntry("Stainless Steel", 11, 8000, 500, 16, 1783, 40, 1.35e6, 0xA0A0A0, 0xDD7700));
        register(new MaterialEntry("Brass", 12, 8500, 380, 109, 1203, 20, 1.60e7, 0xF2D56B, 0xFFBB33));
        register(new MaterialEntry("Inconel 625", 13, 8440, 435, 15, 1623, 55, 2.00e6, 0x8A8A8A, 0x993300));
        register(new MaterialEntry("Tungsten", 14, 19250, 134, 173, 3695, 60, 1.82e7, 0x3A3A3A, 0xFF0000));
        register(new MaterialEntry("Tungsten Carbide", 15, 15000, 180, 84, 3143, 80, 5.00e6, 0x555555, 0xCC2200));
        register(new MaterialEntry("Niobium", 16, 8570, 265, 54, 2741, 45, 6.70e6, 0xAFAFFF, 0xCC3300));
    }

}
