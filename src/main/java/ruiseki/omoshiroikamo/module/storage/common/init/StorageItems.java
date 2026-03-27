package ruiseki.omoshiroikamo.module.storage.common.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.item.IItem;
import ruiseki.omoshiroikamo.core.item.ItemOK;
import ruiseki.omoshiroikamo.module.storage.common.item.ItemCraftingUpgrade;
import ruiseki.omoshiroikamo.module.storage.common.item.ItemFeedingUpgrade;
import ruiseki.omoshiroikamo.module.storage.common.item.ItemFilterUpgrade;
import ruiseki.omoshiroikamo.module.storage.common.item.ItemStackUpgrade;

public enum StorageItems {

    // spotless: off

    STACK_UPGRADE(new ItemStackUpgrade()),
    CRAFTING_UPGRADE(new ItemCraftingUpgrade()),
    FILTER_UPGRADE(new ItemFilterUpgrade()),
    FEEDING_UPGRADE(new ItemFeedingUpgrade())

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
