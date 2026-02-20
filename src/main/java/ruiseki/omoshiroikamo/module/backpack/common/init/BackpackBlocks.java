package ruiseki.omoshiroikamo.module.backpack.common.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import lombok.Getter;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.config.backport.BackpackConfig;
import ruiseki.omoshiroikamo.core.block.BlockOK;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.backpack.common.block.BlockBackpack;

public enum BackpackBlocks {

    // spotless: off

    BACKPACK_BASE(BlockBackpack.create(
        ModObject.backpackLeather.unlocalisedName,
        BackpackConfig.leatherBackpackSlots,
        BackpackConfig.leatherUpgradeSlots)),
    BACKPACK_IRON(BlockBackpack.create(
        ModObject.blockBackpackIron.unlocalisedName,
        BackpackConfig.ironBackpackSlots,
        BackpackConfig.ironUpgradeSlots)),
    BACKPACK_GOLD(BlockBackpack.create(
        ModObject.blockBackpackGold.unlocalisedName,
        BackpackConfig.goldBackpackSlots,
        BackpackConfig.goldUpgradeSlots)),
    BACKPACK_DIAMOND(BlockBackpack.create(
        ModObject.blockBackpackDiamond.unlocalisedName,
        BackpackConfig.diamondBackpackSlots,
        BackpackConfig.diamondUpgradeSlots)),
    BACKPACK_OBSIDIAN(BlockBackpack.create(
        ModObject.blockBackpackObsidian.unlocalisedName,
        BackpackConfig.obsidianBackpackSlots,
        BackpackConfig.obsidianUpgradeSlots)),

    ;
    // spotless: on

    public static final BackpackBlocks[] VALUES = values();

    public static void preInit() {
        for (BackpackBlocks block : VALUES) {
            try {
                block.getBlock()
                    .init();
                Logger.info("Successfully initialized {}", block.name());
            } catch (Exception e) {
                Logger.error("Failed to initialize block: +{}", block.name());
            }
        }
    }

    @Getter
    private final BlockOK block;

    BackpackBlocks(BlockOK block) {
        this.block = block;
    }

    public Item getItem() {
        return Item.getItemFromBlock(block);
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
