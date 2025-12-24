package ruiseki.omoshiroikamo.module.backpack.common.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;
import lombok.Getter;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.backpack.common.item.ItemAdvancedFeedingUpgrade;
import ruiseki.omoshiroikamo.module.backpack.common.item.ItemAdvancedFilterUpgrade;
import ruiseki.omoshiroikamo.module.backpack.common.item.ItemAdvancedMagnetUpgrade;
import ruiseki.omoshiroikamo.module.backpack.common.item.ItemAdvancedPickupUpgrade;
import ruiseki.omoshiroikamo.module.backpack.common.item.ItemAdvancedVoidUpgrade;
import ruiseki.omoshiroikamo.module.backpack.common.item.ItemCraftingUpgrade;
import ruiseki.omoshiroikamo.module.backpack.common.item.ItemEverlastingUpgrade;
import ruiseki.omoshiroikamo.module.backpack.common.item.ItemFeedingUpgrade;
import ruiseki.omoshiroikamo.module.backpack.common.item.ItemFilterUpgrade;
import ruiseki.omoshiroikamo.module.backpack.common.item.ItemInceptionUpgrade;
import ruiseki.omoshiroikamo.module.backpack.common.item.ItemMagnetUpgrade;
import ruiseki.omoshiroikamo.module.backpack.common.item.ItemPickupUpgrade;
import ruiseki.omoshiroikamo.module.backpack.common.item.ItemStackUpgrade;
import ruiseki.omoshiroikamo.module.backpack.common.item.ItemUpgrade;
import ruiseki.omoshiroikamo.module.backpack.common.item.ItemVoidUpgrade;

public enum BackpackItems {

    // spotless: off

    BASE_UPGRADE(new ItemUpgrade<>()),
    STACK_UPGRADE(new ItemStackUpgrade()),
    CRAFTING_UPGRADE(new ItemCraftingUpgrade()),
    MAGNET_UPGRADE(new ItemMagnetUpgrade()),
    ADVANCED_MAGNET_UPGRADE(new ItemAdvancedMagnetUpgrade()),
    FEEDING_UPGRADE(new ItemFeedingUpgrade()),
    ADVANCED_FEEDING_UPGRADE(new ItemAdvancedFeedingUpgrade()),
    PICKUP_UPGRADE(new ItemPickupUpgrade()),
    ADVANCED_PICKUP_UPGRADE(new ItemAdvancedPickupUpgrade()),
    VOID_UPGRADE(new ItemVoidUpgrade()),
    ADVANCED_VOID_UPGRADE(new ItemAdvancedVoidUpgrade()),
    EVERLASTING_UPGRADE(new ItemEverlastingUpgrade()),
    INCEPTION_UPGRADE(new ItemInceptionUpgrade()),
    FILTER_UPGRADE(new ItemFilterUpgrade()),
    ADVANCED_FILTER_UPGRADE(new ItemAdvancedFilterUpgrade()),

    //
    ;
    // spotless: on

    public static final BackpackItems[] VALUES = values();

    public static void preInit() {
        for (BackpackItems item : VALUES) {
            try {
                GameRegistry.registerItem(item.getItem(), item.getName());
                Logger.info("Successfully initialized " + item.name());
            } catch (Exception e) {
                Logger.error("Failed to initialize item: +" + item.name());
            }
        }
    }

    @Getter
    private final Item item;

    BackpackItems(Item item) {
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
