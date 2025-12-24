package ruiseki.omoshiroikamo.module.multiblock.common.block.quantumBeacon;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static ruiseki.omoshiroikamo.core.integration.structureLib.StructureLibUtils.ofBlockAdderWithPos;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import ruiseki.omoshiroikamo.core.common.structure.StructureRegistrationUtils;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockBlocks;

public class QuantumBeaconStructure {

    public static IStructureDefinition<TEQuantumBeaconT1> STRUCTURE_DEFINITION_TIER_1;
    public static IStructureDefinition<TEQuantumBeaconT2> STRUCTURE_DEFINITION_TIER_2;
    public static IStructureDefinition<TEQuantumBeaconT3> STRUCTURE_DEFINITION_TIER_3;
    public static IStructureDefinition<TEQuantumBeaconT4> STRUCTURE_DEFINITION_TIER_4;
    public static IStructureDefinition<TEQuantumBeaconT5> STRUCTURE_DEFINITION_TIER_5;
    public static IStructureDefinition<TEQuantumBeaconT6> STRUCTURE_DEFINITION_TIER_6;

    public static void registerStructureInfo() {
        STRUCTURE_DEFINITION_TIER_1 = StructureRegistrationUtils.registerTierWithDynamicMappings(
            TEQuantumBeaconT1.class,
            QuantumBeaconShapes.getShape(1),
            QuantumBeaconShapes.getDynamicMappings(1),
            QuantumBeaconShapes.STRUCTURE_TIER_1,
            MultiBlockBlocks.QUANTUM_BEACON.getBlock(),
            1,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_2 = StructureRegistrationUtils.registerTierWithDynamicMappings(
            TEQuantumBeaconT2.class,
            QuantumBeaconShapes.getShape(2),
            QuantumBeaconShapes.getDynamicMappings(2),
            QuantumBeaconShapes.STRUCTURE_TIER_2,
            MultiBlockBlocks.QUANTUM_BEACON.getBlock(),
            2,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_3 = StructureRegistrationUtils.registerTierWithDynamicMappings(
            TEQuantumBeaconT3.class,
            QuantumBeaconShapes.getShape(3),
            QuantumBeaconShapes.getDynamicMappings(3),
            QuantumBeaconShapes.STRUCTURE_TIER_3,
            MultiBlockBlocks.QUANTUM_BEACON.getBlock(),
            3,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_4 = StructureRegistrationUtils.registerTierWithDynamicMappings(
            TEQuantumBeaconT4.class,
            QuantumBeaconShapes.getShape(4),
            QuantumBeaconShapes.getDynamicMappings(4),
            QuantumBeaconShapes.STRUCTURE_TIER_4,
            MultiBlockBlocks.QUANTUM_BEACON.getBlock(),
            4,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_5 = StructureRegistrationUtils.registerTierWithDynamicMappings(
            TEQuantumBeaconT5.class,
            QuantumBeaconShapes.getShape(5),
            QuantumBeaconShapes.getDynamicMappings(5),
            QuantumBeaconShapes.STRUCTURE_TIER_5,
            MultiBlockBlocks.QUANTUM_BEACON.getBlock(),
            5,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_6 = StructureRegistrationUtils.registerTierWithDynamicMappings(
            TEQuantumBeaconT6.class,
            QuantumBeaconShapes.getShape(6),
            QuantumBeaconShapes.getDynamicMappings(6),
            QuantumBeaconShapes.STRUCTURE_TIER_6,
            MultiBlockBlocks.QUANTUM_BEACON.getBlock(),
            6,
            builder -> addCommonElements(builder));
    }

    private static <T extends TEQuantumBeacon> void addCommonElements(StructureDefinition.Builder<T> builder) {
        builder.addElement(
            'A',
            ofChain(
                ofBlockAdderWithPos(
                    (t, b, m, x, y, z) -> t.addToMachine(b, m, x, y, z),
                    MultiBlockBlocks.MODIFIER_NULL.getBlock(),
                    0),
                ofBlockAdderWithPos(
                    (t, b, m, x, y, z) -> t.addToMachine(b, m, x, y, z),
                    MultiBlockBlocks.MODIFIER_FIRE_RESISTANCE.getBlock(),
                    0),
                ofBlockAdderWithPos(
                    (t, b, m, x, y, z) -> t.addToMachine(b, m, x, y, z),
                    MultiBlockBlocks.MODIFIER_FLIGHT.getBlock(),
                    0),
                ofBlockAdderWithPos(
                    (t, b, m, x, y, z) -> t.addToMachine(b, m, x, y, z),
                    MultiBlockBlocks.MODIFIER_NIGHT_VISION.getBlock(),
                    0),
                ofBlockAdderWithPos(
                    (t, b, m, x, y, z) -> t.addToMachine(b, m, x, y, z),
                    MultiBlockBlocks.MODIFIER_WATER_BREATHING.getBlock(),
                    0),
                ofBlockAdderWithPos(
                    (t, b, m, x, y, z) -> t.addToMachine(b, m, x, y, z),
                    MultiBlockBlocks.MODIFIER_STRENGTH.getBlock(),
                    0),
                ofBlockAdderWithPos(
                    (t, b, m, x, y, z) -> t.addToMachine(b, m, x, y, z),
                    MultiBlockBlocks.MODIFIER_HASTE.getBlock(),
                    0),
                ofBlockAdderWithPos(
                    (t, b, m, x, y, z) -> t.addToMachine(b, m, x, y, z),
                    MultiBlockBlocks.MODIFIER_REGENERATION.getBlock(),
                    0),
                ofBlockAdderWithPos(
                    (t, b, m, x, y, z) -> t.addToMachine(b, m, x, y, z),
                    MultiBlockBlocks.MODIFIER_SATURATION.getBlock(),
                    0),
                ofBlockAdderWithPos(
                    (t, b, m, x, y, z) -> t.addToMachine(b, m, x, y, z),
                    MultiBlockBlocks.MODIFIER_RESISTANCE.getBlock(),
                    0),
                ofBlockAdderWithPos(
                    (t, b, m, x, y, z) -> t.addToMachine(b, m, x, y, z),
                    MultiBlockBlocks.MODIFIER_JUMP_BOOST.getBlock(),
                    0),
                ofBlockAdderWithPos(
                    (t, b, m, x, y, z) -> t.addToMachine(b, m, x, y, z),
                    MultiBlockBlocks.MODIFIER_SPEED.getBlock(),
                    0),
                ofBlock(MultiBlockBlocks.MODIFIER_NULL.getBlock(), 0),
                ofBlock(MultiBlockBlocks.MODIFIER_FIRE_RESISTANCE.getBlock(), 0),
                ofBlock(MultiBlockBlocks.MODIFIER_FLIGHT.getBlock(), 0),
                ofBlock(MultiBlockBlocks.MODIFIER_NIGHT_VISION.getBlock(), 0),
                ofBlock(MultiBlockBlocks.MODIFIER_WATER_BREATHING.getBlock(), 0),
                ofBlock(MultiBlockBlocks.MODIFIER_STRENGTH.getBlock(), 0),
                ofBlock(MultiBlockBlocks.MODIFIER_HASTE.getBlock(), 0),
                ofBlock(MultiBlockBlocks.MODIFIER_REGENERATION.getBlock(), 0),
                ofBlock(MultiBlockBlocks.MODIFIER_SATURATION.getBlock(), 0),
                ofBlock(MultiBlockBlocks.MODIFIER_RESISTANCE.getBlock(), 0),
                ofBlock(MultiBlockBlocks.MODIFIER_JUMP_BOOST.getBlock(), 0),
                ofBlock(MultiBlockBlocks.MODIFIER_SPEED.getBlock(), 0)));
    }

}
