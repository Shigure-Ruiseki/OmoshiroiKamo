package ruiseki.omoshiroikamo.module.chickens.common.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.common.block.BlockOK;
import ruiseki.omoshiroikamo.common.util.Logger;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.module.chickens.common.block.BlockBreeder;
import ruiseki.omoshiroikamo.module.chickens.common.block.BlockRoost;
import ruiseki.omoshiroikamo.module.chickens.common.block.BlockRoostCollector;

public enum ChickensBlocks {

    // spotless: off

    ROOST(BlockRoost.create()),
    BREEDER(BlockBreeder.create()),
    ROOST_COLLECTOR(BlockRoostCollector.create()),

    ;
    // spotless: on

    public static final ChickensBlocks[] VALUES = values();

    public static void preInit() {
        for (ChickensBlocks block : VALUES) {
            if (BackportConfigs.useChicken) {
                try {
                    block.get()
                        .init();
                    Logger.info("Successfully initialized {}", block.name());
                } catch (Exception e) {
                    Logger.error("Failed to initialize block: +{}", block.name());
                }
            }
        }
    }

    private final BlockOK block;

    ChickensBlocks(BlockOK block) {
        this.block = block;
    }

    public BlockOK get() {
        return block;
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
        return new ItemStack(this.get(), count, meta);
    }
}
