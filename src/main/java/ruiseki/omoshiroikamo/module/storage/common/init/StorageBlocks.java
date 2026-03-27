package ruiseki.omoshiroikamo.module.storage.common.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.config.backport.StorageConfig;
import ruiseki.omoshiroikamo.core.block.BlockOK;
import ruiseki.omoshiroikamo.core.block.IBlock;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.storage.common.block.BlockBarrel;

public enum StorageBlocks {

    // spotless: off

    BARREL(new BlockBarrel(ModObject.BARREL.name, StorageConfig.baseSlots, StorageConfig.baseUpgradeSlots)),
    IRON_BARREL(new BlockBarrel(ModObject.IRON_BARREL.name, StorageConfig.ironSlots, StorageConfig.ironUpgradeSlots)),
    GOLD_BARREL(new BlockBarrel(ModObject.GOLD_BARREL.name, StorageConfig.goldSlots, StorageConfig.goldUpgradeSlots)),
    DIAMOND_BARREL(new BlockBarrel(ModObject.DIAMOND_BARREL.name, StorageConfig.diamondSlots, StorageConfig.diamondUpgradeSlots)),
    OBSIDIAN_BARREL(new BlockBarrel(ModObject.OBSIDIAN_BARREL.name, StorageConfig.obsidianSlots, StorageConfig.obsidianUpgradeSlots)),

    ;
    // spotless: on

    public static final StorageBlocks[] VALUES = values();

    public static void preInit() {
        for (StorageBlocks block : VALUES) {
            try {
                block.block.init();
                Logger.info("Successfully initialized {}", block.name());
            } catch (Exception e) {
                Logger.error("Failed to initialize block: +{}", block.name());
            }
        }
    }

    private final IBlock block;

    StorageBlocks(BlockOK block) {
        this.block = block;
    }

    public Block getBlock() {
        return block.getBlock();
    }

    public Item getItem() {
        return Item.getItemFromBlock(getBlock());
    }

    public ItemStack newItemStack() {
        return newItemStack(1);
    }

    public ItemStack newItemStack(int count) {
        return newItemStack(count, 0);
    }

    public ItemStack newItemStack(int count, int meta) {
        return new ItemStack(this.getBlock(), count, meta);
    }
}
