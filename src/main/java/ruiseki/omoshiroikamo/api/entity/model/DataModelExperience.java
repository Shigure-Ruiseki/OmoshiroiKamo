package ruiseki.omoshiroikamo.api.entity.model;

public class DataModelExperience {

    public static ModelTierRegistryItem getTierItem(int tier) {
        return ModelTierRegistry.INSTANCE.getByType(tier);
    }

    public static String getTierName(int tier) {
        return getTierItem(tier).getTierName();
    }

    public static int getMaxTier() {
        return ModelTierRegistry.INSTANCE.getMaxTierValue();
    }

    public static boolean isMaxTier(int tier) {
        return ModelTierRegistry.INSTANCE.isMaxTier(tier);
    }

    public static boolean shouldIncreaseTier(int tier, int kc, int sc) {
        if (isMaxTier(tier)) return false;
        int roof = getTierRoof(tier, false);
        int killExperience = kc * getKillMultiplier(tier);
        return killExperience + sc >= roof;
    }

    public static double getCurrentTierKillCountWithSims(int tier, int kc, int sc) {
        if (isMaxTier(tier)) return 0;
        int multi = Math.max(getKillMultiplier(tier), 1);
        return kc + ((double) sc / multi);
    }

    public static int getCurrentTierSimulationCountWithKills(int tier, int kc, int sc) {
        if (isMaxTier(tier)) return 0;
        return sc + (kc * getTierItem(tier).getKillMultiplier());
    }

    public static double getKillsToNextTier(int tier, int kc, int sc) {
        if (isMaxTier(tier)) return 0;
        int killRoof = getTierRoof(tier, true);
        return killRoof - getCurrentTierKillCountWithSims(tier, kc, sc);
    }

    public static int getSimulationsToNextTier(int tier, int kc, int sc) {
        if (isMaxTier(tier)) return 0;
        int roof = getTierRoof(tier, false);
        return roof - getCurrentTierSimulationCountWithKills(tier, kc, sc);
    }

    public static int getTierRoof(int tier, boolean asKills) {
        if (isMaxTier(tier)) return 0;

        ModelTierRegistryItem tierItem = getTierItem(tier);
        return asKills ? tierItem.getDataToNext() / Math.max(tierItem.getKillMultiplier(), 1)
            : tierItem.getDataToNext();
    }

    public static int getKillMultiplier(int tier) {
        if (isMaxTier(tier)) return 0;

        ModelTierRegistryItem tierItem = getTierItem(tier);
        return tierItem.getKillMultiplier();
    }
}
