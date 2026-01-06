package ruiseki.omoshiroikamo.module.cable.common.network;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.input.EnergyInputBus;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.output.EnergyOutputBus;

public class CablePartRegistry {

    private static final Map<String, Supplier<? extends ICablePart>> REGISTRY = new HashMap<>();

    static {
        CablePartRegistry.register("energy_input_bus", EnergyInputBus::new);
        CablePartRegistry.register("energy_output_bus", EnergyOutputBus::new);
    }

    public static void register(String id, Supplier<? extends ICablePart> factory) {
        REGISTRY.put(id, factory);
    }

    public static ICablePart create(String id) {
        Supplier<? extends ICablePart> sup = REGISTRY.get(id);
        return sup != null ? sup.get() : null;
    }
}
