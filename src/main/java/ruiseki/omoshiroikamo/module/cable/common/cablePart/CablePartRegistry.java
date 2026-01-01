package ruiseki.omoshiroikamo.module.cable.common.cablePart;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import ruiseki.omoshiroikamo.api.cable.ICablePart;

public class CablePartRegistry {

    private static final Map<String, Supplier<ICablePart>> REGISTRY = new HashMap<>();

    public static void init() {
        CablePartRegistry.register(FluidInputBus.ID, FluidInputBus::new);
    }

    public static void register(String id, Supplier<ICablePart> factory) {
        REGISTRY.put(id, factory);
    }

    public static ICablePart create(String id) {
        Supplier<ICablePart> sup = REGISTRY.get(id);
        return sup != null ? sup.get() : null;
    }
}
