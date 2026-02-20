package ruiseki.omoshiroikamo.module.cows.common.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import lombok.Getter;
import ruiseki.omoshiroikamo.core.block.BlockOK;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.cows.common.block.BlockStall;

public enum CowsBlocks {

    // spotless: off

    STALL(BlockStall.create()),

    ;
    // spotless: on

    public static final CowsBlocks[] VALUES = values();

    public static void preInit() {
        for (CowsBlocks block : VALUES) {
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

    CowsBlocks(BlockOK block) {
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
