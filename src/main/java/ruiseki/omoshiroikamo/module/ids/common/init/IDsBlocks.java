package ruiseki.omoshiroikamo.module.ids.common.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.core.block.IBlock;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.ids.common.block.BlockMenrilDoor;
import ruiseki.omoshiroikamo.module.ids.common.block.BlockMenrilLeaves;
import ruiseki.omoshiroikamo.module.ids.common.block.BlockMenrilLog;
import ruiseki.omoshiroikamo.module.ids.common.block.BlockMenrilPlanks;
import ruiseki.omoshiroikamo.module.ids.common.block.BlockMenrilSapling;
import ruiseki.omoshiroikamo.module.ids.common.block.cable.BlockCable;
import ruiseki.omoshiroikamo.module.ids.common.block.programmer.BlockProgrammer;

public enum IDsBlocks {

    // spotless: off

    CABLE(new BlockCable()),
    PROGRAMMER(new BlockProgrammer()),
    MENRIL_LOG(new BlockMenrilLog()),
    MENRIL_SAPLING(new BlockMenrilSapling()),
    MENRIL_LEAVES(new BlockMenrilLeaves()),
    MENRIL_DOOR(new BlockMenrilDoor()),
    MENRIL_PLANKS(new BlockMenrilPlanks()),

    ;

    // spotless: on

    public static final IDsBlocks[] VALUES = values();

    public static void preInit() {
        for (IDsBlocks block : VALUES) {
            if (block.block == null) {
                continue;
            }
            try {
                block.block.init();
                Logger.info("Successfully initialized {}", block.name());
            } catch (Exception e) {
                Logger.error("Failed to initialize block: +{}", block.name());
            }
        }
    }

    private final boolean enabled;
    private final IBlock block;

    IDsBlocks(IBlock block) {
        this.enabled = true;
        this.block = block;
    }

    IDsBlocks(boolean enabled, IBlock block) {
        this.enabled = enabled;
        this.block = block;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Block getBlock() {
        return block.getBlock();
    }

    public Item getItem() {
        return block != null ? Item.getItemFromBlock(getBlock()) : null;
    }

    public ItemStack newItemStack() {
        return newItemStack(1);
    }

    public ItemStack newItemStack(int count) {
        return newItemStack(count, 0);
    }

    public ItemStack newItemStack(int count, int meta) {
        return block != null ? new ItemStack(this.getBlock(), count, meta) : null;
    }
}
