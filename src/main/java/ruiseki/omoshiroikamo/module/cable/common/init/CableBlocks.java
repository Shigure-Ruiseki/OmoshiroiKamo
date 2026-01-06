package ruiseki.omoshiroikamo.module.cable.common.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.core.common.block.BlockOK;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.cable.common.cable.BlockCable;

public enum CableBlocks {

    // spotless: off

    CABLE(new BlockCable());
    // spotless: on

    public static final CableBlocks[] VALUES = values();

    public static void preInit() {
        for (CableBlocks block : VALUES) {
            if (block.block == null) {
                continue;
            }
            try {
                block.getBlock()
                    .init();
                Logger.info("Successfully initialized {}", block.name());
            } catch (Exception e) {
                Logger.error("Failed to initialize block: +{}", block.name());
            }
        }
    }

    private final boolean enabled;
    private final BlockOK block;

    CableBlocks(BlockOK block) {
        this.enabled = true;
        this.block = block;
    }

    CableBlocks(boolean enabled, BlockOK block) {
        this.enabled = enabled;
        this.block = block;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public BlockOK getBlock() {
        return block;
    }

    public Item getItem() {
        return block != null ? Item.getItemFromBlock(block) : null;
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
