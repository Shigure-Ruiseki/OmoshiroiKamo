package ruiseki.omoshiroikamo.module.dml.common.registries;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.entity.dml.ModelTierRegistryItem;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.dml.recipe.DMLModelTierReader;

public class ModelTier {

    protected String configFileName;

    public ModelTier() {
        this.configFileName = "model_tiers.json";
    }

    public List<ModelTierRegistryItem> tryRegisterTiers() {
        File configFile = new File("config/" + LibMisc.MOD_ID + "/dml/" + configFileName);
        DMLModelTierReader reader = new DMLModelTierReader(configFile);

        if (!configFile.exists()) {
            List<ModelTierRegistryItem> defaultModels = registerTiers();
            return reader.readDefault(defaultModels);
        }

        try {
            return reader.read();
        } catch (IOException e) {
            Logger.error("Failed to read {}: {}", configFileName, e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<ModelTierRegistryItem> registerTiers() {
        List<ModelTierRegistryItem> allTiers = new ArrayList<>();

        ModelTierRegistryItem tier0 = addTier(0, 1, 6, false, 0, 2, 1, 0, 0);
        allTiers.add(tier0);

        ModelTierRegistryItem tier1 = addTier(1, 4, 48, true, 5, 5, 2, 1, 1);
        allTiers.add(tier1);

        ModelTierRegistryItem tier2 = addTier(2, 10, 300, true, 11, 8, 4, 1, 3);
        allTiers.add(tier2);

        ModelTierRegistryItem tier3 = addTier(3, 18, 900, true, 18, 24, 5, 2, 6);
        allTiers.add(tier3);

        ModelTierRegistryItem tier4 = addTier(4, 0, 0, true, 26, 42, 7, 3, 11);
        allTiers.add(tier4);

        return allTiers;
    }

    public ModelTierRegistryItem addTier(int tier, int killMultiplier, int dataToNext, boolean canSimulate,
        int pristineChance, int pristine, int maxWave, int affixes, int glitchChance) {
        return new ModelTierRegistryItem(
            tier,
            killMultiplier,
            dataToNext,
            canSimulate,
            pristineChance,
            pristine,
            maxWave,
            affixes,
            glitchChance);
    }
}
