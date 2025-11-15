package ruiseki.omoshiroikamo.common.init;

import static ruiseki.omoshiroikamo.config.backport.BackportConfigs.useBackpack;
import static ruiseki.omoshiroikamo.config.backport.BackportConfigs.useChicken;
import static ruiseki.omoshiroikamo.config.backport.BackportConfigs.useCow;
import static ruiseki.omoshiroikamo.config.backport.BackportConfigs.useEnvironmentalTech;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.item.ItemAssembler;
import ruiseki.omoshiroikamo.common.item.ItemOK;
import ruiseki.omoshiroikamo.common.item.backpack.ItemBackpack;
import ruiseki.omoshiroikamo.common.item.backpack.ItemBatteryUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemCraftingUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemEverlastingUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemFeedingUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemMagnetUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemStackUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemUpgrade;
import ruiseki.omoshiroikamo.common.item.chicken.ItemChicken;
import ruiseki.omoshiroikamo.common.item.chicken.ItemChickenCatcher;
import ruiseki.omoshiroikamo.common.item.chicken.ItemChickenSpawnEgg;
import ruiseki.omoshiroikamo.common.item.chicken.ItemColoredEgg;
import ruiseki.omoshiroikamo.common.item.chicken.ItemLiquidEgg;
import ruiseki.omoshiroikamo.common.item.chicken.ItemSolidXp;
import ruiseki.omoshiroikamo.common.item.cow.ItemCowSpawnEgg;
import ruiseki.omoshiroikamo.common.item.trait.ItemAnalyzer;
import ruiseki.omoshiroikamo.common.item.trait.ItemSyringe;
import ruiseki.omoshiroikamo.common.util.Logger;

public enum ModItems {

    BACKPACK(useBackpack, new ItemBackpack()),
    BASE_UPGRADE(useBackpack, new ItemUpgrade()),
    STACK_UPGRADE(useBackpack, new ItemStackUpgrade()),
    CRAFTING_UPGRADE(useBackpack, new ItemCraftingUpgrade()),
    MAGNET_UPGRADE(useBackpack, new ItemMagnetUpgrade()),
    FEEDING_UPGRADE(useBackpack, new ItemFeedingUpgrade()),
    BATTERY_UPGRADE(useBackpack, new ItemBatteryUpgrade()),
    EVERLASTING_UPGRADE(useBackpack, new ItemEverlastingUpgrade()),

    ANALYZER(useChicken || useCow, new ItemAnalyzer()),
    CHICKEN_CATCHER(useChicken, new ItemChickenCatcher()),
    CHICKEN_SPAWN_EGG(useChicken, new ItemChickenSpawnEgg()),
    CHICKEN(useChicken, new ItemChicken()),
    COLORED_EGG(useChicken, new ItemColoredEgg()),
    LIQUID_EGG(useChicken, new ItemLiquidEgg()),
    SOLID_XP(useChicken, new ItemSolidXp()),

    COW_SPAWN_EGG(useCow, new ItemCowSpawnEgg()),
    SYRINGE(useCow, new ItemSyringe()),

    ASSEMBLER(useEnvironmentalTech, new ItemAssembler()),
    STABILIZED_ENDER_PEAR(useEnvironmentalTech, new ItemOK().setName(ModObject.itemStabilizedEnderPear)
        .setTextureName("ender_stabilized")),
    PHOTOVOLTAIC_CELL(useEnvironmentalTech, new ItemOK().setName(ModObject.itemPhotovoltaicCell)
        .setTextureName("photovoltaic_cell")),;

    public static final ModItems[] VALUES = values();

    public static void preInit() {
        for (ModItems item : VALUES) {
            if (item.isEnabled()) {
                try {
                    GameRegistry.registerItem(item.get(), item.getName());
                    Logger.info("Successfully initialized " + item.name());
                } catch (Exception e) {
                    Logger.error("Failed to initialize item: +" + item.name());
                }
            }
        }
    }

    private final boolean enabled;
    private final Item item;

    ModItems(boolean enabled, Item item) {
        this.enabled = enabled;
        this.item = item;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Item get() {
        return item;
    }

    public String getName() {
        return get().getUnlocalizedName()
            .replace("item.", "");
    }

    public ItemStack newItemStack() {
        return newItemStack(1);
    }

    public ItemStack newItemStack(int count) {
        return newItemStack(count, 0);
    }

    public ItemStack newItemStack(int count, int meta) {
        return new ItemStack(this.get(), count, meta);
    }
}
