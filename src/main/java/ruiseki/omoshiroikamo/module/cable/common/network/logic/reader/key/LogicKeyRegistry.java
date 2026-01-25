package ruiseki.omoshiroikamo.module.cable.common.network.logic.reader.key;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.type.LogicType;

public class LogicKeyRegistry {

    private static final Map<String, LogicKey> REGISTRY = new HashMap<>();

    private LogicKeyRegistry() {}

    public static LogicKey register(String id, LogicType<?> defaultType) {
        id = id.toLowerCase();
        if (REGISTRY.containsKey(id)) {
            throw new IllegalStateException("LogicKey already registered: " + id);
        }
        LogicKey key = new LogicKey(id, defaultType);
        REGISTRY.put(id, key);
        return key;
    }

    public static LogicKey get(String id) {
        return REGISTRY.get(id.toLowerCase());
    }

    public static Map<String, LogicKey> all() {
        return Collections.unmodifiableMap(REGISTRY);
    }
}
