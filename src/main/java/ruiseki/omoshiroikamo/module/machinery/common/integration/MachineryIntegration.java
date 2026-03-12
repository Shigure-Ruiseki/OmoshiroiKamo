package ruiseki.omoshiroikamo.module.machinery.common.integration;

import net.minecraft.inventory.IInventory;

import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.common.structure.BlockResolver;
import ruiseki.omoshiroikamo.core.lib.LibMods;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockEssentiaInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockEssentiaInputPortME;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockEssentiaOutputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockFluidOutputPortME;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockGasInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockGasOutputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockItemOutputPortME;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockManaInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockManaOutputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockVisBridge;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockVisInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockVisOutputPort;
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryBlocks;
import ruiseki.omoshiroikamo.module.machinery.common.tile.proxy.ExternalEnergyProxy;
import ruiseki.omoshiroikamo.module.machinery.common.tile.proxy.ExternalEssentiaProxy;
import ruiseki.omoshiroikamo.module.machinery.common.tile.proxy.ExternalFluidProxy;
import ruiseki.omoshiroikamo.module.machinery.common.tile.proxy.ExternalGasProxy;
import ruiseki.omoshiroikamo.module.machinery.common.tile.proxy.ExternalItemProxy;
import ruiseki.omoshiroikamo.module.machinery.common.tile.proxy.ExternalManaProxy;
import ruiseki.omoshiroikamo.module.machinery.common.tile.proxy.ExternalVisProxy;

/**
 * Centrally manages external mod integrations for the Machinery module.
 * Registers blocks and proxy factories based on loaded mods.
 */
public class MachineryIntegration {

    public static void preInit() {
        // Register base machinery proxies (Item, Fluid)
        registerBaseProxies();

        // Register mod-specific components (Blocks and Proxies)
        if (LibMods.AppliedEnergistics2.isLoaded()) {
            AE2Integration.init();
        }
        if (LibMods.AE2FluidCrafting.isLoaded()) {
            AE2FluidIntegration.init();
        }
        if (LibMods.Mekanism.isLoaded()) {
            MekanismIntegration.init();
        }
        if (LibMods.Botania.isLoaded()) {
            BotaniaIntegration.init();
        }
        if (LibMods.Thaumcraft.isLoaded()) {
            ThaumcraftIntegration.init();
        }
        if (LibMods.ThaumicEnergistics.isLoaded()) {
            ThaumicEnergisticsIntegration.init();
        }
    }

    private static void registerBaseProxies() {
        BlockResolver.registerProxyFactory(IPortType.Type.ITEM, (controller, coords, tile, io) -> {
            if (tile instanceof IInventory) {
                return new ExternalItemProxy(controller, coords, io);
            }
            return null;
        });

        BlockResolver.registerProxyFactory(
            IPortType.Type.FLUID,
            (controller, coords, tile, io) -> new ExternalFluidProxy(controller, coords, io));

        BlockResolver.registerProxyFactory(
            IPortType.Type.ENERGY,
            (controller, coords, tile, io) -> new ExternalEnergyProxy(controller, coords, io));
    }

    // ==================== Mod-Specific Integration Helpers ====================
    // Facilitates lazy class loading to prevent NoClassDefFoundError.

    private static class AE2Integration {

        static void init() {
            MachineryBlocks.ITEM_OUTPUT_PORT_ME.setBlock(BlockItemOutputPortME.create());
        }
    }

    private static class AE2FluidIntegration {

        static void init() {
            MachineryBlocks.FLUID_OUTPUT_PORT_ME.setBlock(BlockFluidOutputPortME.create());
        }
    }

    private static class MekanismIntegration {

        static void init() {
            MachineryBlocks.GAS_INPUT_PORT.setBlock(BlockGasInputPort.create());
            MachineryBlocks.GAS_OUTPUT_PORT.setBlock(BlockGasOutputPort.create());

            BlockResolver.registerProxyFactory(
                IPortType.Type.GAS,
                (controller, coords, tile, io) -> new ExternalGasProxy(controller, coords, io));
        }
    }

    private static class BotaniaIntegration {

        static void init() {
            MachineryBlocks.MANA_INPUT_PORT.setBlock(BlockManaInputPort.create());
            MachineryBlocks.MANA_OUTPUT_PORT.setBlock(BlockManaOutputPort.create());

            BlockResolver.registerProxyFactory(
                IPortType.Type.MANA,
                (controller, coords, tile, io) -> new ExternalManaProxy(controller, coords, io));
        }
    }

    private static class ThaumcraftIntegration {

        static void init() {
            MachineryBlocks.VIS_INPUT_PORT.setBlock(BlockVisInputPort.create());
            MachineryBlocks.VIS_OUTPUT_PORT.setBlock(BlockVisOutputPort.create());
            MachineryBlocks.ESSENTIA_INPUT_PORT.setBlock(BlockEssentiaInputPort.create());
            MachineryBlocks.ESSENTIA_OUTPUT_PORT.setBlock(BlockEssentiaOutputPort.create());
            BlockVisBridge.create();

            BlockResolver.registerProxyFactory(
                IPortType.Type.ESSENTIA,
                (controller, coords, tile, io) -> new ExternalEssentiaProxy(controller, coords, io));
            BlockResolver.registerProxyFactory(
                IPortType.Type.VIS,
                (controller, coords, tile, io) -> new ExternalVisProxy(controller, coords, io));
        }
    }

    private static class ThaumicEnergisticsIntegration {

        static void init() {
            MachineryBlocks.ESSENTIA_INPUT_PORT_ME.setBlock(BlockEssentiaInputPortME.create());
        }
    }
}
