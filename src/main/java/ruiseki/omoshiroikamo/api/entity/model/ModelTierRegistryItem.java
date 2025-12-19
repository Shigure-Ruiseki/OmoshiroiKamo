package ruiseki.omoshiroikamo.api.entity.model;

import java.util.HashMap;
import java.util.Map;

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
    protected final int pristine;
    @Getter
    protected final int maxWave;
    @Getter
    protected final int affixes;
    @Getter
    protected final int glitchChance;
    @Getter
    protected Map<String, String> lang;

    public ModelTierRegistryItem(int tier, int killMultiplier, int dataToNext, boolean canSimulate, int pristineChance,
        int pristine, int maxWave, int affixes, int glitchChance) {
        this.tier = tier;
        this.killMultiplier = killMultiplier;
        this.dataToNext = dataToNext;
        this.canSimulate = canSimulate;
        this.pristineChance = pristineChance;
        this.pristine = pristine;
        this.maxWave = maxWave;
        this.affixes = affixes;
        this.glitchChance = glitchChance;
    }

    public String getTierName() {
        return "model.tier_" + tier + ".name";
    }

    public ModelTierRegistryItem setLang(String langCode, String value) {
        if (this.lang == null) {
            this.lang = new HashMap<>();
        }

        if (langCode != null && !langCode.isEmpty() && value != null && !value.isEmpty()) {
            this.lang.put(langCode, value);
        }

        return this;
    }
}
