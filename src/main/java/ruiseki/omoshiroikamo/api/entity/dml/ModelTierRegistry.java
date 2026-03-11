package ruiseki.omoshiroikamo.api.entity.dml;

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

    public String getTierName(int tier) {
        ModelTierRegistryItem item = getByType(tier);
        return item != null ? item.getTierName() : "Unknown";
    }

    public boolean shouldIncreaseTier(int tier, int killCount, int simulationCount) {
        if (isMaxTier(tier)) return false;
        int roof = getTierRoof(tier, false);
        int killExperience = killCount * getKillMultiplier(tier);
        return killExperience + simulationCount >= roof;
    }

    public int getCurrentTierSimulationCountWithKills(int tier, int killCount, int simulationCount) {
        if (isMaxTier(tier)) return 0;
        ModelTierRegistryItem item = getByType(tier);
        return item != null ? simulationCount + (killCount * item.getKillMultiplier()) : simulationCount;
    }

    public int getSimulationsToNextTier(int tier, int killCount, int simulationCount) {
        if (isMaxTier(tier)) return 0;
        int roof = getTierRoof(tier, false);
        return roof - getCurrentTierSimulationCountWithKills(tier, killCount, simulationCount);
    }

    public int getTierRoof(int tier, boolean asKills) {
        if (isMaxTier(tier)) return 0;
        ModelTierRegistryItem item = getByType(tier);
        if (item == null) return 0;

        return asKills ? item.getDataToNext() / Math.max(item.getKillMultiplier(), 1) : item.getDataToNext();
    }

    public int getKillsToNextTier(int tier, int killCount, int simulationCount) {
        if (isMaxTier(tier)) return 0;
        int roof = getTierRoof(tier, true);
        double currentKills = killCount + ((double) simulationCount / Math.max(getKillMultiplier(tier), 1));
        return (int) Math.ceil(roof - currentKills);
    }

    public int getKillMultiplier(int tier) {
        if (isMaxTier(tier)) return 0;
        ModelTierRegistryItem item = getByType(tier);
        return item != null ? item.getKillMultiplier() : 0;
    }
}
