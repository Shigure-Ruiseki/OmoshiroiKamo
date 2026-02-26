package ruiseki.omoshiroikamo.module.ids.common.item.logic.type;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.texture.IIconRegister;

public class LogicTypeRegistry {

    private static final Map<String, LogicType<?>> REGISTRY = new HashMap<>();

    public static <T> LogicType<T> register(LogicType<T> type) {
        if (REGISTRY.containsKey(type.getId())) {
            throw new IllegalStateException("LogicType already registered: " + type.getId());
        }
        REGISTRY.put(type.getId(), type);
        return type;
    }

    public static LogicType<?> get(String id) {
        return REGISTRY.get(id);
    }

    public static LogicType<?>[] values() {
        return REGISTRY.values()
            .toArray(new LogicType[0]);
    }

    public static void registerIcons(IIconRegister register) {
        for (LogicType<?> key : REGISTRY.values()) {
            key.registerIcons(register);
        }
    }
}
