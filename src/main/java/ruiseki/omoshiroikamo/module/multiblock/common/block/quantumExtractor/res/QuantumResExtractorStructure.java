package ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.res;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockAnyMeta;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static ruiseki.omoshiroikamo.core.integration.structureLib.StructureLibUtils.ofBlockAdderWithPos;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import ruiseki.omoshiroikamo.core.common.structure.StructureRegistrationUtils;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.TEQuantumExtractor;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockBlocks;

public class QuantumResExtractorStructure {

    public static IStructureDefinition<TEQuantumResExtractorT1> STRUCTURE_DEFINITION_TIER_1;
    public static IStructureDefinition<TEQuantumResExtractorT2> STRUCTURE_DEFINITION_TIER_2;
    public static IStructureDefinition<TEQuantumResExtractorT3> STRUCTURE_DEFINITION_TIER_3;
    public static IStructureDefinition<TEQuantumResExtractorT4> STRUCTURE_DEFINITION_TIER_4;
    public static IStructureDefinition<TEQuantumResExtractorT5> STRUCTURE_DEFINITION_TIER_5;
    public static IStructureDefinition<TEQuantumResExtractorT6> STRUCTURE_DEFINITION_TIER_6;

    public static void registerStructureInfo() {
        STRUCTURE_DEFINITION_TIER_1 = StructureRegistrationUtils.registerTierWithDynamicMappings(
            TEQuantumResExtractorT1.class,
            QuantumResExtractorShapes.getShape(1),
            QuantumResExtractorShapes.getDynamicMappings(1),
            QuantumResExtractorShapes.STRUCTURE_TIER_1,
            MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.getBlock(),
            1,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_2 = StructureRegistrationUtils.registerTierWithDynamicMappings(
            TEQuantumResExtractorT2.class,
            QuantumResExtractorShapes.getShape(2),
            QuantumResExtractorShapes.getDynamicMappings(2),
            QuantumResExtractorShapes.STRUCTURE_TIER_2,
            MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.getBlock(),
            2,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_3 = StructureRegistrationUtils.registerTierWithDynamicMappings(
            TEQuantumResExtractorT3.class,
            QuantumResExtractorShapes.getShape(3),
            QuantumResExtractorShapes.getDynamicMappings(3),
            QuantumResExtractorShapes.STRUCTURE_TIER_3,
            MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.getBlock(),
            3,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_4 = StructureRegistrationUtils.registerTierWithDynamicMappings(
            TEQuantumResExtractorT4.class,
            QuantumResExtractorShapes.getShape(4),
            QuantumResExtractorShapes.getDynamicMappings(4),
            QuantumResExtractorShapes.STRUCTURE_TIER_4,
            MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.getBlock(),
            4,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_5 = StructureRegistrationUtils.registerTierWithDynamicMappings(
            TEQuantumResExtractorT5.class,
            QuantumResExtractorShapes.getShape(5),
            QuantumResExtractorShapes.getDynamicMappings(5),
            QuantumResExtractorShapes.STRUCTURE_TIER_5,
            MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.getBlock(),
            5,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_6 = StructureRegistrationUtils.registerTierWithDynamicMappings(
            TEQuantumResExtractorT6.class,
            QuantumResExtractorShapes.getShape(6),
            QuantumResExtractorShapes.getDynamicMappings(6),
            QuantumResExtractorShapes.STRUCTURE_TIER_6,
            MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.getBlock(),
            6,
            builder -> addCommonElements(builder));

        Logger.info("Register Quantum Res Extractor Structure Info");
    }

    private static <T extends TEQuantumExtractor> void addCommonElements(StructureDefinition.Builder<T> builder) {
        builder
            .addElement(
                'L',
                ofChain(
                    ofBlockAdderWithPos(TEQuantumExtractor::addToMachine, MultiBlockBlocks.LENS.getBlock(), 0),
                    ofBlock(MultiBlockBlocks.LENS.getBlock(), 0),
                    ofBlockAnyMeta(MultiBlockBlocks.COLORED_LENS.getBlock(), 0)))
            .addElement(
                'A',
                ofChain(
                    ofBlockAdderWithPos(
                        (t, b, m, x, y, z) -> t.addToMachine(b, m, x, y, z),
                        MultiBlockBlocks.MODIFIER_NULL.getBlock(),
                        0),
                    ofBlockAdderWithPos(
                        (t, b, m, x, y, z) -> t.addToMachine(b, m, x, y, z),
                        MultiBlockBlocks.MODIFIER_ACCURACY.getBlock(),
                        0),
                    ofBlockAdderWithPos(
                        (t, b, m, x, y, z) -> t.addToMachine(b, m, x, y, z),
                        MultiBlockBlocks.MODIFIER_SPEED.getBlock(),
                        0),
                    ofBlockAdderWithPos(
                        (t, b, m, x, y, z) -> t.addToMachine(b, m, x, y, z),
                        MultiBlockBlocks.MODIFIER_LUCK.getBlock(),
                        0),
                    ofBlock(MultiBlockBlocks.MODIFIER_NULL.getBlock(), 0),
                    ofBlock(MultiBlockBlocks.MODIFIER_ACCURACY.getBlock(), 0),
                    ofBlock(MultiBlockBlocks.MODIFIER_SPEED.getBlock(), 0),
                    ofBlock(MultiBlockBlocks.MODIFIER_LUCK.getBlock(), 0)));
    }
}
