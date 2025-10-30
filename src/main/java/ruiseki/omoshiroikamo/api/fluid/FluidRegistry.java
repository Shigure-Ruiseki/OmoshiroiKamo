package ruiseki.omoshiroikamo.api.fluid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class FluidRegistry {

    private static final Map<String, FluidEntry> REGISTRY = new LinkedHashMap<>();
    private static final Map<Integer, FluidEntry> META_LOOKUP = new HashMap<>();
    private static final List<FluidEntry> META_INDEXED = new ArrayList<>();

    public static void register(FluidEntry entry) {
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

    public static FluidEntry get(String name) {
        return REGISTRY.get(name);
    }

    public static FluidEntry fromMeta(int meta) {
        if (meta < 0 || meta >= META_INDEXED.size()) {
            return META_INDEXED.get(0);
        }
        return META_INDEXED.get(meta);
    }

    public static int indexOf(FluidEntry entry) {
        return entry.meta;
    }

    public static Collection<FluidEntry> all() {
        return Collections.unmodifiableList(
            META_INDEXED.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }

    public static boolean contains(String name) {
        return REGISTRY.containsKey(name);
    }

    public static void preInit() {
        register(new FluidEntry("Water", 0, 1000, 0.00089, 298.15, 0x3F76E4, false));

        register(new FluidEntry("Hydrogen", 50, 0.08988, 8.76e-6, 298.15, 0xE6FFFF, true));
        register(new FluidEntry("Oxygen", 51, 1.429, 2.08e-5, 298.15, 0xB0E0FF, true));
        register(new FluidEntry("Steam", 52, 0.6, 1.34e-5, 373.15, 0xEEEEEE, true));
        register(new FluidEntry("Nitrogen", 53, 1.2506, 1.76e-5, 298.15, 0xCFEFFB, true));
        register(new FluidEntry("Carbon Dioxide", 54, 1.977, 1.47e-5, 298.15, 0xE0FFF0, true));
        register(new FluidEntry("Methane", 55, 0.656, 1.10e-5, 298.15, 0xE3FFE5, true));
        register(new FluidEntry("Helium", 56, 0.1786, 1.96e-5, 298.15, 0xFFFFE0, true));
        register(new FluidEntry("Argon", 57, 1.784, 2.23e-5, 298.15, 0xD9F4FF, true));
        register(new FluidEntry("Neon", 58, 0.9002, 3.10e-5, 298.15, 0xE8FFFF, true));
        register(new FluidEntry("Ammonia", 59, 0.769, 9.82e-6, 298.15, 0xD0F0FF, true));
        register(new FluidEntry("Chlorine", 60, 3.214, 1.36e-5, 298.15, 0xD8FFBA, true));
        register(new FluidEntry("Sulfur Dioxide", 61, 2.926, 1.34e-5, 298.15, 0xE6FFF2, true));

    }
}
