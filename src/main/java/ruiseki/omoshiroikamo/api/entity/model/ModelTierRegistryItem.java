package ruiseki.omoshiroikamo.api.entity.model;

import lombok.Getter;

public class ModelTierRegistryItem {

    @Getter
    protected final int tier;
    @Getter
    protected final int killMultiplier;
    @Getter
    protected final int dataToNext;
    @Getter
    protected final boolean canSimulate;
    @Getter
    protected final int pristineChance;
    @Getter
    protected final String[] lang;
    @Getter
    protected final int pristine;
    @Getter
    protected final int maxWave;
    @Getter
    protected final int affixes;
    @Getter
    protected final int glitchChance;

    public ModelTierRegistryItem(int tier, int killMultiplier, int dataToNext, boolean canSimulate, int pristineChance,
        String[] lang, int pristine, int maxWave, int affixes, int glitchChance) {
        this.tier = tier;
        this.killMultiplier = killMultiplier;
        this.dataToNext = dataToNext;
        this.canSimulate = canSimulate;
        this.pristineChance = pristineChance;
        this.lang = lang;
        this.pristine = pristine;
        this.maxWave = maxWave;
        this.affixes = affixes;
        this.glitchChance = glitchChance;
    }

    public String getTierName() {
        return "model.tier_" + tier + ".name";
    }
}
