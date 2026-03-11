package ruiseki.omoshiroikamo.api.entity.dml;

import java.util.Map;

import com.google.gson.JsonObject;

import lombok.Getter;
import ruiseki.omoshiroikamo.core.json.AbstractJsonMaterial;

public class ModelTierRegistryItem extends AbstractJsonMaterial {

    @Getter
    protected int tier;
    @Getter
    protected int killMultiplier;
    @Getter
    protected int dataToNext;
    @Getter
    protected boolean canSimulate;
    @Getter
    protected int pristineChance;
    @Getter
    protected int pristine;
    @Getter
    protected int maxWave;
    @Getter
    protected int affixes;
    @Getter
    protected int glitchChance;

    @Getter
    protected Map<String, String> lang;

    public ModelTierRegistryItem() {}

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

    @Override
    public void read(JsonObject json) {
        this.tier = getInt(json, "tier", 0);
        this.killMultiplier = getInt(json, "killMultiplier", 1);
        this.dataToNext = getInt(json, "dataToNext", 10);
        this.canSimulate = getBoolean(json, "canSimulate", true);
        this.pristineChance = getInt(json, "pristineChance", 5);
        this.pristine = getInt(json, "pristine", 0);
        this.maxWave = getInt(json, "maxWave", 0);
        this.affixes = getInt(json, "affixes", 0);
        this.glitchChance = getInt(json, "glitchChance", 0);
        this.lang = getMap(json, "lang");
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("tier", tier);
        json.addProperty("killMultiplier", killMultiplier);
        json.addProperty("dataToNext", dataToNext);
        json.addProperty("canSimulate", canSimulate);
        json.addProperty("pristineChance", pristineChance);
        json.addProperty("pristine", pristine);
        json.addProperty("maxWave", maxWave);
        json.addProperty("affixes", affixes);
        json.addProperty("glitchChance", glitchChance);
        writeMap(json, "lang", lang);
    }

    @Override
    public boolean validate() {
        if (tier < 0) {
            logValidationError("Tier must be non-negative");
            return false;
        }
        return true;
    }

    public String getTierName() {
        return "model.tier_" + tier + ".name";
    }

}
