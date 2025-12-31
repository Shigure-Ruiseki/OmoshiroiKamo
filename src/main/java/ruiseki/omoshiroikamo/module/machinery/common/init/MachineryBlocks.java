package ruiseki.omoshiroikamo.module.machinery.common.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.core.common.block.BlockOK;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.lib.LibMods;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockEnergyInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockEnergyOutputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockFluidInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockFluidOutputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockFluidOutputPortME;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockGasInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockGasOutputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockItemInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockItemOutputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockItemOutputPortME;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockMachineCasing;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockMachineController;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockManaInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockManaOutputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockVisInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockVisOutputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockEssentiaInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockEssentiaOutputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockEssentiaInputPortME;

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
    ITEM_OUTPUT_PORT_ME(LibMods.AppliedEnergistics2.isLoaded(), BlockItemOutputPortME.create()),
    FLUID_OUTPUT_PORT_ME(LibMods.AE2FluidCrafting.isLoaded(), BlockFluidOutputPortME.create()),
    FLUID_INPUT_PORT(BlockFluidInputPort.create()),
    FLUID_OUTPUT_PORT(BlockFluidOutputPort.create()),
    ENERGY_INPUT_PORT(BlockEnergyInputPort.create()),
    ENERGY_OUTPUT_PORT(BlockEnergyOutputPort.create()),
    MANA_INPUT_PORT(LibMods.Botania.isLoaded(), BlockManaInputPort.create()),
    MANA_OUTPUT_PORT(LibMods.Botania.isLoaded(), BlockManaOutputPort.create()),
    GAS_INPUT_PORT(LibMods.Mekanism.isLoaded(), BlockGasInputPort.create()),
    GAS_OUTPUT_PORT(LibMods.Mekanism.isLoaded(), BlockGasOutputPort.create()),
    VIS_INPUT_PORT(LibMods.Thaumcraft.isLoaded(), BlockVisInputPort.create()),
    VIS_OUTPUT_PORT(LibMods.Thaumcraft.isLoaded(), BlockVisOutputPort.create()),
    ESSENTIA_INPUT_PORT(LibMods.Thaumcraft.isLoaded(), BlockEssentiaInputPort.create()),
    ESSENTIA_OUTPUT_PORT(LibMods.Thaumcraft.isLoaded(), BlockEssentiaOutputPort.create()),
    ESSENTIA_INPUT_PORT_ME(LibMods.ThaumicEnergistics.isLoaded(), BlockEssentiaInputPortME.create()),

    ;
    // spotless: on

    public static final MachineryBlocks[] VALUES = values();

    public static void preInit() {
        for (MachineryBlocks block : VALUES) {
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

    public boolean isAvailable() {
        return block != null;
    }
}
