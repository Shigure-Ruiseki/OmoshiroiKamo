package ruiseki.omoshiroikamo.module.chickens.common.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.Level;

import ruiseki.okcore.block.IBlock;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
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
            try {
                block.block.init();
                OmoshiroiKamo.okLog(Level.INFO, "Successfully initialized {}", block.name());
            } catch (Exception e) {
                OmoshiroiKamo.okLog(Level.ERROR, "Failed to initialize block: +{}", block.name());
            }
        }
    }

    private final IBlock block;

    ChickensBlocks(IBlock block) {
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
