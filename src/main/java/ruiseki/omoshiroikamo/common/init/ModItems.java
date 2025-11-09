package ruiseki.omoshiroikamo.common.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.item.ItemAssembler;
import ruiseki.omoshiroikamo.common.item.ItemHammer;
import ruiseki.omoshiroikamo.common.item.ItemMaterial;
import ruiseki.omoshiroikamo.common.item.ItemOK;
import ruiseki.omoshiroikamo.common.item.backpack.ItemBackpack;
import ruiseki.omoshiroikamo.common.item.backpack.ItemBatteryUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemCraftingUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemEverlastingUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemFeedingUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemMagnetUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemStackUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemUpgrade;
import ruiseki.omoshiroikamo.common.item.chicken.ItemAnalyzer;
import ruiseki.omoshiroikamo.common.item.chicken.ItemChickenCatcher;
import ruiseki.omoshiroikamo.common.item.chicken.ItemChickenSpawnEgg;
import ruiseki.omoshiroikamo.common.item.chicken.ItemColoredEgg;
import ruiseki.omoshiroikamo.common.item.chicken.ItemLiquidEgg;
import ruiseki.omoshiroikamo.common.item.chicken.ItemSolidXp;
import ruiseki.omoshiroikamo.common.item.cow.ItemCowSpawnEgg;
import ruiseki.omoshiroikamo.common.ore.ItemOre;
import ruiseki.omoshiroikamo.common.util.Logger;

public enum ModItems {

    BACKPACK(true, new ItemBackpack()),
    BASE_UPGRADE(true, new ItemUpgrade()),
    STACK_UPGRADE(true, new ItemStackUpgrade()),
    CRAFTING_UPGRADE(true, new ItemCraftingUpgrade()),
    MAGNET_UPGRADE(true, new ItemMagnetUpgrade()),
    FEEDING_UPGRADE(true, new ItemFeedingUpgrade()),
    BATTERY_UPGRADE(true, new ItemBatteryUpgrade()),
    EVERLASTING_UPGRADE(true, new ItemEverlastingUpgrade()),
    ANALYZER(true, new ItemAnalyzer()),
    CHICKEN_CATCHER(true, new ItemChickenCatcher()),
    CHICKEN_SPAWN_EGG(true, new ItemChickenSpawnEgg()),
    COLORED_EGG(true, new ItemColoredEgg()),
    LIQUID_EGG(true, new ItemLiquidEgg()),
    COW_SPAWN_EGG(true, new ItemCowSpawnEgg()),
    SOLID_XP(true, new ItemSolidXp()),
    MATERIAL(true, new ItemMaterial()),
    ORE(true, new ItemOre()),
    HAMMER(true, new ItemHammer()),
    ASSEMBLER(true, new ItemAssembler()),
    STABILIZED_ENDER_PEAR(true, new ItemOK().setName(ModObject.itemStabilizedEnderPear)
        .setTextureName("ender_stabilized")),
    PHOTOVOLTAIC_CELL(true, new ItemOK().setName(ModObject.itemPhotovoltaicCell)
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
