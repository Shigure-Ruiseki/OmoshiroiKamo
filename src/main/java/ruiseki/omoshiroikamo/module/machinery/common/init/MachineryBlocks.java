package ruiseki.omoshiroikamo.module.machinery.common.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.core.common.block.BlockOK;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockEnergyInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockItemInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockItemOutputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockMachineCasing;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockMachineController;

/**
 * Block registration for the Machinery module.
 * Uses enum pattern consistent with other modules.
 */
public enum MachineryBlocks {

    // spotless: off

    MACHINE_CASING(BlockMachineCasing.create()),
    MACHINE_CONTROLLER(BlockMachineController.create()),
    ITEM_INPUT_PORT(BlockItemInputPort.create()),
    ITEM_OUTPUT_PORT(BlockItemOutputPort.create()),
    ENERGY_INPUT_PORT(BlockEnergyInputPort.create()),

    ;
    // spotless: on

    public static final MachineryBlocks[] VALUES = values();

    public static void preInit() {
        for (MachineryBlocks block : VALUES) {
            try {
                block.getBlock()
                    .init();
                Logger.info("Successfully initialized {}", block.name());
            } catch (Exception e) {
                Logger.error("Failed to initialize block: +{}", block.name());
            }
        }
    }

    private final BlockOK block;

    MachineryBlocks(BlockOK block) {
        this.block = block;
    }

    public BlockOK getBlock() {
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
        return new ItemStack(this.getBlock(), count, meta);
    }
}
