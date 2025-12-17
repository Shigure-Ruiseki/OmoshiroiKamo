package ruiseki.omoshiroikamo.api.entity.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ModelRegistry {

    /**
     * Global registry instance.
     */
    public static final ModelRegistry INSTANCE = new ModelRegistry();

    public ModelRegistry() {}

    protected final Map<Integer, ModelRegistryItem> items = new HashMap<>();

    protected final Random rand = new Random();

    public void register(ModelRegistryItem entity) {
        validate(entity);
        items.put(entity.getId(), entity);
    }

    protected void validate(ModelRegistryItem entity) {
        for (ModelRegistryItem item : items.values()) {
            if (entity.getId() == item.getId()) {
                throw new RuntimeException(
                    "Duplicated ID [" + entity
                        .getId() + "] of [" + entity.getEntityName() + "] with [" + item.getEntityName() + "]!");
            }
            if (entity.getEntityName()
                .equalsIgnoreCase(item.getEntityName())) {
                throw new RuntimeException("Duplicated name [" + entity.getEntityName() + "]!");
            }
        }
    }

    public ModelRegistryItem getByType(int id) {
        return items.get(id);
    }

    public ModelRegistryItem getByName(String key) {
        for (ModelRegistryItem item : items.values()) {
            if (item.getEntityName()
                .equalsIgnoreCase(key)) {
                return item;
            }
        }
        return null;
    }

    public Collection<ModelRegistryItem> getItems() {
        List<ModelRegistryItem> result = new ArrayList<>();
        for (ModelRegistryItem t : items.values()) {
            if (t.isEnabled()) {
                result.add(t);
            }
        }
        return result;
    }
}
