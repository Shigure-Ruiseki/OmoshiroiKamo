package ruiseki.omoshiroikamo.core.integration.structureLib;

import com.gtnewhorizon.structurelib.alignment.constructable.IMultiblockInfoContainer;

import ruiseki.omoshiroikamo.core.common.structure.CustomStructureRegistry;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumBeacon.QuantumBeaconStructure;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.ore.QuantumOreExtractorStructure;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.res.QuantumResExtractorStructure;
import ruiseki.omoshiroikamo.module.multiblock.common.block.solarArray.SolarArrayStructure;

public class StructureCompat {

    public static void postInit() {
        SolarArrayStructure.registerStructureInfo();
        QuantumOreExtractorStructure.registerStructureInfo();
        QuantumResExtractorStructure.registerStructureInfo();
        QuantumBeaconStructure.registerStructureInfo();

        // Register custom structures from JSON
        CustomStructureRegistry.registerAll();

        // Register StructureLib info container for the modular controller (NEI tab)
        IMultiblockInfoContainer.registerTileClass(TEMachineController.class, new MachineControllerInfoContainer());
    }

    /**
     * Reload all structure definitions from StructureManager.
     * This re-registers the structures with StructureLib using updated shapes.
     */
    public static void reload() {
        SolarArrayStructure.registerStructureInfo();
        QuantumOreExtractorStructure.registerStructureInfo();
        QuantumResExtractorStructure.registerStructureInfo();
        QuantumBeaconStructure.registerStructureInfo();

        // Reload custom structures from JSON
        CustomStructureRegistry.registerAll();

        Logger.info("StructureLib definitions reloaded");
    }
}
