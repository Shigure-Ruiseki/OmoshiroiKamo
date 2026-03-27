package ruiseki.omoshiroikamo.module.backpack.common.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.config.backport.BackpackConfig;
import ruiseki.omoshiroikamo.core.block.BlockOK;
import ruiseki.omoshiroikamo.core.block.IBlock;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.backpack.common.block.BlockBackpack;
import ruiseki.omoshiroikamo.module.backpack.common.block.BlockSleepingBag;

public enum BackpackBlocks {

    // spotless: off

    BACKPACK_BASE(new BlockBackpack(
        ModObject.LEATHER_BACKPACK.name,
        BackpackConfig.leatherBackpackSlots,
        BackpackConfig.leatherUpgradeSlots)),
    BACKPACK_IRON(new BlockBackpack(
        ModObject.IRON_BACKPACK.name,
        BackpackConfig.ironBackpackSlots,
        BackpackConfig.ironUpgradeSlots)),
    BACKPACK_GOLD(new BlockBackpack(
        ModObject.GOLD_BACKPACK.name,
        BackpackConfig.goldBackpackSlots,
        BackpackConfig.goldUpgradeSlots)),
    BACKPACK_DIAMOND(new BlockBackpack(
        ModObject.DIAMOND_BACKPACK.name,
        BackpackConfig.diamondBackpackSlots,
        BackpackConfig.diamondUpgradeSlots)),
    BACKPACK_OBSIDIAN(new BlockBackpack(
        ModObject.OBSIDIAN_BACKPACK.name,
        BackpackConfig.obsidianBackpackSlots,
        BackpackConfig.obsidianUpgradeSlots)),
    SLEEPING_BAG(new BlockSleepingBag()),

    ;
    // spotless: on

    public static final BackpackBlocks[] VALUES = values();

    public static void preInit() {
        for (BackpackBlocks block : VALUES) {
            try {
                block.block.init();
                Logger.info("Successfully initialized {}", block.name());
            } catch (Exception e) {
                Logger.error("Failed to initialize block: +{}", block.name());
            }
        }
    }

    private final IBlock block;

    BackpackBlocks(BlockOK block) {
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
