package ruiseki.omoshiroikamo.module.machinery.common.init;

import net.minecraft.block.Block;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockEnergyInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockItemInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockItemOutputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockMachineCasing;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockMachineController;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEEnergyInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEItemInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEItemOutputPort;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;

/**
 * Block registration for the Machinery module.
 */
public class MachineryBlocks {

    public static Block MACHINE_CASING;
    public static Block MACHINE_CONTROLLER;
    public static Block ITEM_INPUT_PORT;
    public static Block ITEM_OUTPUT_PORT;
    public static Block ENERGY_INPUT_PORT;

    public static void preInit() {
        // Machine Casing
        MACHINE_CASING = new BlockMachineCasing();
        GameRegistry.registerBlock(MACHINE_CASING, "machineCasing");

        // Machine Controller
        MACHINE_CONTROLLER = new BlockMachineController();
        GameRegistry.registerBlock(MACHINE_CONTROLLER, "machineController");
        GameRegistry.registerTileEntity(TEMachineController.class, "omoshiroikamo:machineController");

        // Item Input Port
        ITEM_INPUT_PORT = new BlockItemInputPort();
        GameRegistry.registerBlock(ITEM_INPUT_PORT, "itemInputPort");
        GameRegistry.registerTileEntity(TEItemInputPort.class, "omoshiroikamo:itemInputPort");

        // Item Output Port
        ITEM_OUTPUT_PORT = new BlockItemOutputPort();
        GameRegistry.registerBlock(ITEM_OUTPUT_PORT, "itemOutputPort");
        GameRegistry.registerTileEntity(TEItemOutputPort.class, "omoshiroikamo:itemOutputPort");

        // Energy Input Port
        ENERGY_INPUT_PORT = new BlockEnergyInputPort();
        GameRegistry.registerBlock(ENERGY_INPUT_PORT, "energyInputPort");
        GameRegistry.registerTileEntity(TEEnergyInputPort.class, "omoshiroikamo:energyInputPort");
    }
}
