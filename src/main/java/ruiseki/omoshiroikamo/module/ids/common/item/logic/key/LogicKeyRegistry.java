package ruiseki.omoshiroikamo.module.ids.common.item.logic.key;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.texture.IIconRegister;

public class LogicKeyRegistry {

    private static final Map<String, LogicKey> REGISTRY = new HashMap<>();

    public static LogicKey register(LogicKey key) {
        if (REGISTRY.containsKey(key.getId())) {
            throw new IllegalStateException("LogicKey already registered: " + key.getId());
        }
        REGISTRY.put(key.getId(), key);
        return key;
    }

    public static LogicKey get(String id) {
        return REGISTRY.get(id);
    }

    public static LogicKey[] values() {
        return REGISTRY.values()
            .toArray(new LogicKey[0]);
    }

    public static void registerIcons(IIconRegister register) {
        for (LogicKey key : REGISTRY.values()) {
            key.registerIcons(register);
        }
    }
}
