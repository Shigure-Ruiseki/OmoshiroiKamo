package ruiseki.omoshiroikamo.api.entity.dml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LivingRegistry {

    public static final LivingRegistry INSTANCE = new LivingRegistry();

    public LivingRegistry() {}

    protected final Map<Integer, LivingRegistryItem> items = new HashMap<>();

    public void register(LivingRegistryItem entity) {
        validate(entity);
        items.put(entity.getId(), entity);
    }

    protected void validate(LivingRegistryItem entity) {
        for (LivingRegistryItem item : items.values()) {
            if (entity.getId() == item.getId()) {
                throw new RuntimeException(
                    "Duplicated ID [" + entity
                        .getId() + "] of [" + entity.getDisplayName() + "] with [" + item.getDisplayName() + "]!");
            }
            if (entity.getDisplayName()
                .equalsIgnoreCase(item.getDisplayName())) {
                throw new RuntimeException("Duplicated name [" + entity.getDisplayName() + "]!");
            }
        }
    }

    public LivingRegistryItem getByType(int id) {
        return items.get(id);
    }

    public LivingRegistryItem getByName(String key) {
        for (LivingRegistryItem item : items.values()) {
            if (item.getDisplayName()
                .equalsIgnoreCase(key)) {
                return item;
            }
        }
        return null;
    }

    public Collection<LivingRegistryItem> getItems() {
        List<LivingRegistryItem> result = new ArrayList<>();
        for (LivingRegistryItem t : items.values()) {
            if (t.isEnabled()) {
                result.add(t);
            }
        }
        return result;
    }
}
