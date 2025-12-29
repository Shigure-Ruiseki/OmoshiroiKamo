package ruiseki.omoshiroikamo.module.machinery.common.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.core.common.block.BlockOK;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.lib.LibMods;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockEnergyInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockEnergyOutputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockItemInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockItemOutputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockMachineCasing;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockMachineController;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockManaInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockManaOutputPort;

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
    ENERGY_OUTPUT_PORT(BlockEnergyOutputPort.create()),
    MANA_INPUT_PORT(LibMods.Botania.isLoaded(), BlockManaInputPort.create()),
    MANA_OUTPUT_PORT(LibMods.Botania.isLoaded(), BlockManaOutputPort.create()),

    ;
    // spotless: on

    public static final MachineryBlocks[] VALUES = values();

    public static void preInit() {
        for (MachineryBlocks block : VALUES) {
            if (block.isEnabled()) {
                try {
                    block.getBlock()
                        .init();
                    Logger.info("Successfully initialized {}", block.name());
                } catch (Exception e) {
                    Logger.error("Failed to initialize block: +{}", block.name());
                }
            }
        }
    }

    private final boolean enabled;
    private final BlockOK block;

    MachineryBlocks(BlockOK block) {
        this.enabled = true;
        this.block = block;
    }

    MachineryBlocks(boolean enabled, BlockOK block) {
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
