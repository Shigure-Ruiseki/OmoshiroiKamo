package ruiseki.omoshiroikamo.common.init;

import static ruiseki.omoshiroikamo.config.backport.BackportConfigs.useBackpack;
import static ruiseki.omoshiroikamo.config.backport.BackportConfigs.useChicken;
import static ruiseki.omoshiroikamo.config.backport.BackportConfigs.useCow;
import static ruiseki.omoshiroikamo.config.backport.BackportConfigs.useDeepMobLearning;
import static ruiseki.omoshiroikamo.config.backport.BackportConfigs.useEnvironmentalTech;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;
import lombok.Getter;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.item.ItemOK;
import ruiseki.omoshiroikamo.common.item.backpack.ItemAdvancedFeedingUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemAdvancedFilterUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemAdvancedMagnetUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemAdvancedPickupUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemAdvancedVoidUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemCraftingUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemEverlastingUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemFeedingUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemFilterUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemInceptionUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemMagnetUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemPickupUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemStackUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemVoidUpgrade;
import ruiseki.omoshiroikamo.common.item.chicken.ItemChicken;
import ruiseki.omoshiroikamo.common.item.chicken.ItemChickenCatcher;
import ruiseki.omoshiroikamo.common.item.chicken.ItemChickenSpawnEgg;
import ruiseki.omoshiroikamo.common.item.chicken.ItemColoredEgg;
import ruiseki.omoshiroikamo.common.item.chicken.ItemLiquidEgg;
import ruiseki.omoshiroikamo.common.item.chicken.ItemSolidXp;
import ruiseki.omoshiroikamo.common.item.cow.ItemCowHalter;
import ruiseki.omoshiroikamo.common.item.cow.ItemCowSpawnEgg;
import ruiseki.omoshiroikamo.common.item.deepMobLearning.ItemCreativeModelLearner;
import ruiseki.omoshiroikamo.common.item.deepMobLearning.ItemDataModel;
import ruiseki.omoshiroikamo.common.item.deepMobLearning.ItemDataModelBlank;
import ruiseki.omoshiroikamo.common.item.deepMobLearning.ItemDeepLearner;
import ruiseki.omoshiroikamo.common.item.multiblock.ItemAssembler;
import ruiseki.omoshiroikamo.common.item.multiblock.ItemCrystal;
import ruiseki.omoshiroikamo.common.item.multiblock.ItemStructureWand;
import ruiseki.omoshiroikamo.common.item.trait.ItemAnalyzer;
import ruiseki.omoshiroikamo.common.item.trait.ItemSyringe;
import ruiseki.omoshiroikamo.common.util.Logger;

public enum ModItems {

    BASE_UPGRADE(useBackpack, new ItemUpgrade<>()),
    STACK_UPGRADE(useBackpack, new ItemStackUpgrade()),
    CRAFTING_UPGRADE(useBackpack, new ItemCraftingUpgrade()),
    MAGNET_UPGRADE(useBackpack, new ItemMagnetUpgrade()),
    ADVANCED_MAGNET_UPGRADE(useBackpack, new ItemAdvancedMagnetUpgrade()),
    FEEDING_UPGRADE(useBackpack, new ItemFeedingUpgrade()),
    ADVANCED_FEEDING_UPGRADE(useBackpack, new ItemAdvancedFeedingUpgrade()),
    PICKUP_UPGRADE(useBackpack, new ItemPickupUpgrade()),
    ADVANCED_PICKUP_UPGRADE(useBackpack, new ItemAdvancedPickupUpgrade()),
    VOID_UPGRADE(useBackpack, new ItemVoidUpgrade()),
    ADVANCED_VOID_UPGRADE(useBackpack, new ItemAdvancedVoidUpgrade()),
    EVERLASTING_UPGRADE(useBackpack, new ItemEverlastingUpgrade()),
    INCEPTION_UPGRADE(useBackpack, new ItemInceptionUpgrade()),
    FILTER_UPGRADE(useBackpack, new ItemFilterUpgrade()),
    ADVANCED_FILTER_UPGRADE(useBackpack, new ItemAdvancedFilterUpgrade()),

    CRYSTAL(useEnvironmentalTech, new ItemCrystal()),
    ASSEMBLER(useEnvironmentalTech, new ItemAssembler()),
    STABILIZED_ENDER_PEAR(useEnvironmentalTech, new ItemOK().setName(ModObject.itemStabilizedEnderPear)
        .setTextureName("ender_stabilized")),
    PHOTOVOLTAIC_CELL(useEnvironmentalTech, new ItemOK().setName(ModObject.itemPhotovoltaicCell)
        .setTextureName("photovoltaic_cell")),

    ANALYZER(useChicken || useCow, new ItemAnalyzer()),
    CHICKEN_CATCHER(useChicken, new ItemChickenCatcher()),
    CHICKEN_SPAWN_EGG(useChicken, new ItemChickenSpawnEgg()),
    CHICKEN(useChicken, new ItemChicken()),
    COLORED_EGG(useChicken, new ItemColoredEgg()),
    LIQUID_EGG(useChicken, new ItemLiquidEgg()),
    SOLID_XP(useChicken, new ItemSolidXp()),
    COW_HALTER(useCow, new ItemCowHalter()),
    COW_SPAWN_EGG(useCow, new ItemCowSpawnEgg()),

    SYRINGE(useCow, new ItemSyringe()),

    CREATIVE_MODEL_LEARNER(useDeepMobLearning, new ItemCreativeModelLearner()),
    DEEP_LEARNER(useDeepMobLearning, new ItemDeepLearner()),
    DATA_MODEL(useDeepMobLearning, new ItemDataModel()),
    DATA_MODEL_BLANK(useDeepMobLearning, new ItemDataModelBlank()),

    STRUCTURE_WAND(true, new ItemStructureWand()),
    //
    ;

    public static final ModItems[] VALUES = values();

    public static void preInit() {
        for (ModItems item : VALUES) {
            if (item.isEnabled()) {
                try {
                    GameRegistry.registerItem(item.getItem(), item.getName());
                    Logger.info("Successfully initialized " + item.name());
                } catch (Exception e) {
                    Logger.error("Failed to initialize item: +" + item.name());
                }
            }
        }
    }

    @Getter
    private final boolean enabled;
    @Getter
    private final Item item;

    ModItems(boolean enabled, Item item) {
        this.enabled = enabled;
        this.item = item;
    }

    public String getName() {
        return getItem().getUnlocalizedName()
            .replace("item.", "");
    }

    public ItemStack newItemStack() {
        return newItemStack(1);
    }

    public ItemStack newItemStack(int count) {
        return newItemStack(count, 0);
    }

    public ItemStack newItemStack(int count, int meta) {
        return new ItemStack(this.getItem(), count, meta);
    }
}
