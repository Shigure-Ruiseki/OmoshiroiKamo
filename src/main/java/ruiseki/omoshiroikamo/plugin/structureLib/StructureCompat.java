package ruiseki.omoshiroikamo.plugin.structureLib;

import ruiseki.omoshiroikamo.common.block.multiblock.quantumBeacon.QuantumBeaconStructure;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.ore.QuantumOreExtractorStructure;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.res.QuantumResExtractorStructure;
import ruiseki.omoshiroikamo.common.block.multiblock.solarArray.SolarArrayStructure;

public class StructureCompat {

    public static void postInit() {
        SolarArrayStructure.registerStructureInfo();
        QuantumOreExtractorStructure.registerStructureInfo();
        QuantumResExtractorStructure.registerStructureInfo();
        QuantumBeaconStructure.registerStructureInfo();
    }
}
