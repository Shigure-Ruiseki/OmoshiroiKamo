package ruiseki.omoshiroikamo.module.storage.common.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.item.IItem;
import ruiseki.omoshiroikamo.core.item.ItemOK;
import ruiseki.omoshiroikamo.module.storage.common.item.ItemAdvancedFeedingUpgrade;
import ruiseki.omoshiroikamo.module.storage.common.item.ItemAdvancedFilterUpgrade;
import ruiseki.omoshiroikamo.module.storage.common.item.ItemAdvancedMagnetUpgrade;
import ruiseki.omoshiroikamo.module.storage.common.item.ItemAdvancedPickupUpgrade;
import ruiseki.omoshiroikamo.module.storage.common.item.ItemAdvancedVoidUpgrade;
import ruiseki.omoshiroikamo.module.storage.common.item.ItemCraftingUpgrade;
import ruiseki.omoshiroikamo.module.storage.common.item.ItemFeedingUpgrade;
import ruiseki.omoshiroikamo.module.storage.common.item.ItemFilterUpgrade;
import ruiseki.omoshiroikamo.module.storage.common.item.ItemMagnetUpgrade;
import ruiseki.omoshiroikamo.module.storage.common.item.ItemPickupUpgrade;
import ruiseki.omoshiroikamo.module.storage.common.item.ItemStackUpgrade;
import ruiseki.omoshiroikamo.module.storage.common.item.ItemUpgrade;
import ruiseki.omoshiroikamo.module.storage.common.item.ItemVoidUpgrade;

public enum StorageItems {

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
    FILTER_UPGRADE(new ItemFilterUpgrade()),
    ADVANCED_FILTER_UPGRADE(new ItemAdvancedFilterUpgrade()),

    ;
    // spotless: on

    public static final StorageItems[] VALUES = values();

    public static void preInit() {
        for (StorageItems block : VALUES) {
            try {
                block.item.init();
                Logger.info("Successfully initialized {}", block.name());
            } catch (Exception e) {
                Logger.error("Failed to initialize block: +{}", block.name());
            }
        }
    }

    private final IItem item;

    StorageItems(ItemOK item) {
        this.item = item;
    }

    public Item getItem() {
        return item.getItem();
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
