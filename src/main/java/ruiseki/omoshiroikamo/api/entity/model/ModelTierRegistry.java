package ruiseki.omoshiroikamo.api.entity.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ModelTierRegistry {

    public static final ModelTierRegistry INSTANCE = new ModelTierRegistry();

    public ModelTierRegistry() {}

    protected final Map<Integer, ModelTierRegistryItem> items = new HashMap<>();

    public void register(ModelTierRegistryItem entity) {
        validate(entity);
        items.put(entity.getTier(), entity);
    }

    protected void validate(ModelTierRegistryItem tier) {
        for (ModelTierRegistryItem item : items.values()) {
            if (tier.getTier() == item.getTier()) {
                throw new RuntimeException("Duplicated Tier [" + tier.getTier() + "]");
            }
        }
    }

    public ModelTierRegistryItem getByType(int id) {
        return items.get(id);
    }

    public Collection<ModelTierRegistryItem> getItems() {
        return new ArrayList<>(items.values());
    }

    public int getMaxTierValue() {
        int max = 0;
        for (ModelTierRegistryItem item : items.values()) {
            max = Math.max(max, item.getTier());
        }
        return max;
    }

    public boolean isMaxTier(int tier) {
        return !items.containsKey(tier) || tier >= getMaxTierValue();
    }
}
