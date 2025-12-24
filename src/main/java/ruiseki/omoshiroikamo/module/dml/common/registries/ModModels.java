package ruiseki.omoshiroikamo.module.dml.common.registries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ruiseki.omoshiroikamo.api.entity.dml.LivingRegistry;
import ruiseki.omoshiroikamo.api.entity.dml.LivingRegistryItem;
import ruiseki.omoshiroikamo.api.entity.dml.ModelRegistry;
import ruiseki.omoshiroikamo.api.entity.dml.ModelRegistryItem;
import ruiseki.omoshiroikamo.api.entity.dml.ModelTierRegistry;
import ruiseki.omoshiroikamo.api.entity.dml.ModelTierRegistryItem;
import ruiseki.omoshiroikamo.core.common.util.Logger;

public class ModModels {

    public static void init() {
        registerModAddons();
    }

    public static void postInit() {
        loadConfiguration();
    }

    public static ArrayList<BaseModelHandler> registeredModAddons = new ArrayList<>();

    private static void registerModAddons() {
        addModAddon(new BaseModels());
        addModAddon(new OriginalModels());
    }

    public static void addModAddon(BaseModelHandler addon) {
        if (addon == null) {
            Logger.error("Tried to add null mod addon");
            return;
        }

        registeredModAddons.add(addon);
    }

    private static List<ModelRegistryItem> generateDefaultModels() {
        List<ModelRegistryItem> models = new ArrayList<>();

        for (BaseModelHandler addon : registeredModAddons) {
            models = addon.tryRegisterModels(models);
        }

        return models;

    }

    private static void loadConfiguration() {
        Logger.info("Models Loading Config...");
        Collection<LivingRegistryItem> allLivings = new ModLivingMatters().tryRegisterLivings();
        for (LivingRegistryItem model : allLivings) {
            LivingRegistry.INSTANCE.register(model);
        }

        Collection<ModelRegistryItem> allModels = generateDefaultModels();
        for (ModelRegistryItem model : allModels) {
            ModelRegistry.INSTANCE.register(model);
        }

        Collection<ModelTierRegistryItem> allTiers = new ModelTier().tryRegisterTiers();
        for (ModelTierRegistryItem tier : allTiers) {
            ModelTierRegistry.INSTANCE.register(tier);
        }
    }
}
