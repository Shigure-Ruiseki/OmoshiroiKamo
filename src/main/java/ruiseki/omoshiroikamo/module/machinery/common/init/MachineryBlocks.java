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
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockItemInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockItemOutputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockMachineCasing;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockMachineController;

/**
 * Block registration for the Machinery module.
 * Uses enum pattern consistent with other modules.
 * 
 * Optional mod blocks are initialized lazily in preInit() to avoid
 * ClassNotFoundException when the mod is not present.
 * Each mod's blocks are initialized in a separate helper class to ensure
 * the block classes are only loaded when the mod is present.
 */
public enum MachineryBlocks {

    // spotless: off

    // Core blocks (always available)
    MACHINE_CASING(BlockMachineCasing.create()),
    MACHINE_CONTROLLER(BlockMachineController.create()),
    ITEM_INPUT_PORT(BlockItemInputPort.create()),
    ITEM_OUTPUT_PORT(BlockItemOutputPort.create()),
    FLUID_INPUT_PORT(BlockFluidInputPort.create()),
    FLUID_OUTPUT_PORT(BlockFluidOutputPort.create()),
    ENERGY_INPUT_PORT(BlockEnergyInputPort.create()),
    ENERGY_OUTPUT_PORT(BlockEnergyOutputPort.create()),

    // Optional mod blocks (initialized in preInit via helper classes)
    ITEM_OUTPUT_PORT_ME,
    FLUID_OUTPUT_PORT_ME,
    MANA_INPUT_PORT,
    MANA_OUTPUT_PORT,
    GAS_INPUT_PORT,
    GAS_OUTPUT_PORT,
    VIS_INPUT_PORT,
    VIS_OUTPUT_PORT,
    ESSENTIA_INPUT_PORT,
    ESSENTIA_OUTPUT_PORT,
    ESSENTIA_INPUT_PORT_ME,

    ;
    // spotless: on

    public static final MachineryBlocks[] VALUES = values();

    public static void preInit() {
        // Initialize optional mod blocks via helper classes
        // Each helper class is only loaded when its mod is present
        if (LibMods.AppliedEnergistics2.isLoaded()) {
            AE2BlockHelper.init();
        }
        if (LibMods.AE2FluidCrafting.isLoaded()) {
            AE2FluidBlockHelper.init();
        }
        if (LibMods.Botania.isLoaded()) {
            BotaniaBlockHelper.init();
        }
        if (LibMods.Mekanism.isLoaded()) {
            MekanismBlockHelper.init();
        }
        if (LibMods.Thaumcraft.isLoaded()) {
            ThaumcraftBlockHelper.init();
        }
        if (LibMods.ThaumicEnergistics.isLoaded()) {
            ThaumicEnergisticsBlockHelper.init();
        }

        // Initialize all available blocks
        for (MachineryBlocks block : VALUES) {
            if (block.block == null) {
                continue;
            }
            try {
                block.getBlock()
                    .init();
                Logger.info("Successfully initialized {}", block.name());
            } catch (Exception e) {
                Logger.error("Failed to initialize block: {}", block.name());
            }
        }
    }

    // ==================== Enum Fields and Methods ====================

    private boolean enabled;
    private BlockOK block;

    MachineryBlocks() {
        this.enabled = false;
        this.block = null;
    }

    MachineryBlocks(BlockOK block) {
        this.enabled = true;
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

    // ==================== Helper Classes for Mod Blocks ====================
    // These classes are only loaded when their respective mod is present.
    // This ensures the Block classes they reference are not loaded prematurely.

    private static class AE2BlockHelper {

        static void init() {
            ITEM_OUTPUT_PORT_ME.block = ruiseki.omoshiroikamo.module.machinery.common.block.BlockItemOutputPortME
                .create();
        }
    }

    private static class AE2FluidBlockHelper {

        static void init() {
            FLUID_OUTPUT_PORT_ME.block = ruiseki.omoshiroikamo.module.machinery.common.block.BlockFluidOutputPortME
                .create();
        }
    }

    private static class BotaniaBlockHelper {

        static void init() {
            MANA_INPUT_PORT.block = ruiseki.omoshiroikamo.module.machinery.common.block.BlockManaInputPort.create();
            MANA_OUTPUT_PORT.block = ruiseki.omoshiroikamo.module.machinery.common.block.BlockManaOutputPort.create();
        }
    }

    private static class MekanismBlockHelper {

        static void init() {
            GAS_INPUT_PORT.block = ruiseki.omoshiroikamo.module.machinery.common.block.BlockGasInputPort.create();
            GAS_OUTPUT_PORT.block = ruiseki.omoshiroikamo.module.machinery.common.block.BlockGasOutputPort.create();
        }
    }

    private static class ThaumcraftBlockHelper {

        static void init() {
            VIS_INPUT_PORT.block = ruiseki.omoshiroikamo.module.machinery.common.block.BlockVisInputPort.create();
            VIS_OUTPUT_PORT.block = ruiseki.omoshiroikamo.module.machinery.common.block.BlockVisOutputPort.create();
            ESSENTIA_INPUT_PORT.block = ruiseki.omoshiroikamo.module.machinery.common.block.BlockEssentiaInputPort
                .create();
            ESSENTIA_OUTPUT_PORT.block = ruiseki.omoshiroikamo.module.machinery.common.block.BlockEssentiaOutputPort
                .create();
            ruiseki.omoshiroikamo.module.machinery.common.block.BlockVisBridge.create();
        }
    }

    private static class ThaumicEnergisticsBlockHelper {

        static void init() {
            ESSENTIA_INPUT_PORT_ME.block = ruiseki.omoshiroikamo.module.machinery.common.block.BlockEssentiaInputPortME
                .create();
        }
    }
}
