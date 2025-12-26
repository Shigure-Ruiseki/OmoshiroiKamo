package ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.ore;

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

public class QuantumOreExtractorStructure {

    public static IStructureDefinition<TEQuantumOreExtractorT1> STRUCTURE_DEFINITION_TIER_1;
    public static IStructureDefinition<TEQuantumOreExtractorT2> STRUCTURE_DEFINITION_TIER_2;
    public static IStructureDefinition<TEQuantumOreExtractorT3> STRUCTURE_DEFINITION_TIER_3;
    public static IStructureDefinition<TEQuantumOreExtractorT4> STRUCTURE_DEFINITION_TIER_4;
    public static IStructureDefinition<TEQuantumOreExtractorT5> STRUCTURE_DEFINITION_TIER_5;
    public static IStructureDefinition<TEQuantumOreExtractorT6> STRUCTURE_DEFINITION_TIER_6;

    public static void registerStructureInfo() {
        STRUCTURE_DEFINITION_TIER_1 = StructureRegistrationUtils.registerTierWithDynamicMappings(
            TEQuantumOreExtractorT1.class,
            QuantumOreExtractorShapes.getShape(1),
            QuantumOreExtractorShapes.getDynamicMappings(1),
            QuantumOreExtractorShapes.STRUCTURE_TIER_1,
            MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.getBlock(),
            1,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_2 = StructureRegistrationUtils.registerTierWithDynamicMappings(
            TEQuantumOreExtractorT2.class,
            QuantumOreExtractorShapes.getShape(2),
            QuantumOreExtractorShapes.getDynamicMappings(2),
            QuantumOreExtractorShapes.STRUCTURE_TIER_2,
            MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.getBlock(),
            2,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_3 = StructureRegistrationUtils.registerTierWithDynamicMappings(
            TEQuantumOreExtractorT3.class,
            QuantumOreExtractorShapes.getShape(3),
            QuantumOreExtractorShapes.getDynamicMappings(3),
            QuantumOreExtractorShapes.STRUCTURE_TIER_3,
            MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.getBlock(),
            3,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_4 = StructureRegistrationUtils.registerTierWithDynamicMappings(
            TEQuantumOreExtractorT4.class,
            QuantumOreExtractorShapes.getShape(4),
            QuantumOreExtractorShapes.getDynamicMappings(4),
            QuantumOreExtractorShapes.STRUCTURE_TIER_4,
            MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.getBlock(),
            4,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_5 = StructureRegistrationUtils.registerTierWithDynamicMappings(
            TEQuantumOreExtractorT5.class,
            QuantumOreExtractorShapes.getShape(5),
            QuantumOreExtractorShapes.getDynamicMappings(5),
            QuantumOreExtractorShapes.STRUCTURE_TIER_5,
            MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.getBlock(),
            5,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_6 = StructureRegistrationUtils.registerTierWithDynamicMappings(
            TEQuantumOreExtractorT6.class,
            QuantumOreExtractorShapes.getShape(6),
            QuantumOreExtractorShapes.getDynamicMappings(6),
            QuantumOreExtractorShapes.STRUCTURE_TIER_6,
            MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.getBlock(),
            6,
            builder -> addCommonElements(builder));

        Logger.info("Register Quantum Ore Extractor Structure Info");
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
