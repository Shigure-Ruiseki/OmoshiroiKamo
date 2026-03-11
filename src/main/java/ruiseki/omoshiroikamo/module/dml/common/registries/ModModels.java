package ruiseki.omoshiroikamo.module.dml.common.registries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.entity.Entity;

import ruiseki.omoshiroikamo.api.entity.dml.LivingRegistry;
import ruiseki.omoshiroikamo.api.entity.dml.LivingRegistryItem;
import ruiseki.omoshiroikamo.api.entity.dml.ModelRegistry;
import ruiseki.omoshiroikamo.api.entity.dml.ModelRegistryItem;
import ruiseki.omoshiroikamo.api.entity.dml.ModelTierRegistry;
import ruiseki.omoshiroikamo.api.entity.dml.ModelTierRegistryItem;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.json.JsonUtils;

public class ModModels {

    public static void init() {
        Logger.info("Models Loading Config...");
        registerModAddons();
        loadConfiguration();
    }

    public static void postInit() {
        Logger.info("Resolving Entity Classes for Data Models...");
        ModelRegistry.INSTANCE.getItems()
            .forEach(model -> {
                if (model.getAssociatedMobs() != null) {
                    List<Class<? extends Entity>> resolved = JsonUtils.resolveEntityClasses(model.getAssociatedMobs());
                    model.setAssociatedMobsClasses(resolved);
                }
            });
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
            models.addAll(addon.tryRegisterModels());
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
