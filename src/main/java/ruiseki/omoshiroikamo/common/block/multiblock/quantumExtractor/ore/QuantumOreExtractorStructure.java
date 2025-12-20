package ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.ore;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockAnyMeta;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static ruiseki.omoshiroikamo.plugin.structureLib.StructureLibUtils.ofBlockAdderWithPos;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import ruiseki.omoshiroikamo.common.block.multiblock.StructureRegistrationUtils;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.TEQuantumExtractor;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.util.Logger;

public class QuantumOreExtractorStructure {

    public static IStructureDefinition<TEQuantumOreExtractorT1> STRUCTURE_DEFINITION_TIER_1;
    public static IStructureDefinition<TEQuantumOreExtractorT2> STRUCTURE_DEFINITION_TIER_2;
    public static IStructureDefinition<TEQuantumOreExtractorT3> STRUCTURE_DEFINITION_TIER_3;
    public static IStructureDefinition<TEQuantumOreExtractorT4> STRUCTURE_DEFINITION_TIER_4;
    public static IStructureDefinition<TEQuantumOreExtractorT5> STRUCTURE_DEFINITION_TIER_5;
    public static IStructureDefinition<TEQuantumOreExtractorT6> STRUCTURE_DEFINITION_TIER_6;

    public static void registerStructureInfo() {
        STRUCTURE_DEFINITION_TIER_1 = StructureRegistrationUtils.registerTier(
            TEQuantumOreExtractorT1.class,
            QuantumOreExtractorShapes.SHAPE_TIER_1,
            QuantumOreExtractorShapes.STRUCTURE_TIER_1,
            ModBlocks.QUANTUM_ORE_EXTRACTOR.get(),
            1,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_2 = StructureRegistrationUtils.registerTier(
            TEQuantumOreExtractorT2.class,
            QuantumOreExtractorShapes.SHAPE_TIER_2,
            QuantumOreExtractorShapes.STRUCTURE_TIER_2,
            ModBlocks.QUANTUM_ORE_EXTRACTOR.get(),
            2,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_3 = StructureRegistrationUtils.registerTier(
            TEQuantumOreExtractorT3.class,
            QuantumOreExtractorShapes.SHAPE_TIER_3,
            QuantumOreExtractorShapes.STRUCTURE_TIER_3,
            ModBlocks.QUANTUM_ORE_EXTRACTOR.get(),
            3,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_4 = StructureRegistrationUtils.registerTier(
            TEQuantumOreExtractorT4.class,
            QuantumOreExtractorShapes.SHAPE_TIER_4,
            QuantumOreExtractorShapes.STRUCTURE_TIER_4,
            ModBlocks.QUANTUM_ORE_EXTRACTOR.get(),
            4,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_5 = StructureRegistrationUtils.registerTier(
            TEQuantumOreExtractorT5.class,
            QuantumOreExtractorShapes.SHAPE_TIER_5,
            QuantumOreExtractorShapes.STRUCTURE_TIER_5,
            ModBlocks.QUANTUM_ORE_EXTRACTOR.get(),
            5,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_6 = StructureRegistrationUtils.registerTier(
            TEQuantumOreExtractorT6.class,
            QuantumOreExtractorShapes.SHAPE_TIER_6,
            QuantumOreExtractorShapes.STRUCTURE_TIER_6,
            ModBlocks.QUANTUM_ORE_EXTRACTOR.get(),
            6,
            builder -> addCommonElements(builder));

        Logger.info("Register Quantum Ore Extractor Structure Info");
    }

    private static <T extends TEQuantumExtractor> void addCommonElements(StructureDefinition.Builder<T> builder) {
        builder.addElement('C', ofBlock(ModBlocks.LASER_CORE.get(), 0))
            .addElement('P', ofBlockAnyMeta(ModBlocks.MACHINE_BASE.get(), 0))
            .addElement(
                'L',
                ofChain(
                    ofBlockAdderWithPos(TEQuantumExtractor::addToMachine, ModBlocks.LENS.get(), 0),
                    ofBlock(ModBlocks.LENS.get(), 0),
                    ofBlockAnyMeta(ModBlocks.COLORED_LENS.get(), 0)))
            .addElement(
                'A',
                ofChain(
                    ofBlockAdderWithPos(
                        (t, b, m, x, y, z) -> t.addToMachine(b, m, x, y, z),
                        ModBlocks.MODIFIER_NULL.get(),
                        0),
                    ofBlock(ModBlocks.MODIFIER_NULL.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_ACCURACY.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_SPEED.get(), 0)));
    }
}
