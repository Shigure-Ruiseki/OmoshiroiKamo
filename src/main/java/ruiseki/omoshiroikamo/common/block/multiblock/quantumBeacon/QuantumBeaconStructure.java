package ruiseki.omoshiroikamo.common.block.multiblock.quantumBeacon;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockAnyMeta;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static ruiseki.omoshiroikamo.plugin.structureLib.StructureLibUtils.ofBlockAdderWithPos;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import ruiseki.omoshiroikamo.common.block.multiblock.StructureRegistrationUtils;
import ruiseki.omoshiroikamo.common.init.ModBlocks;

public class QuantumBeaconStructure {

    public static IStructureDefinition<TEQuantumBeaconT1> STRUCTURE_DEFINITION_TIER_1;
    public static IStructureDefinition<TEQuantumBeaconT2> STRUCTURE_DEFINITION_TIER_2;
    public static IStructureDefinition<TEQuantumBeaconT3> STRUCTURE_DEFINITION_TIER_3;
    public static IStructureDefinition<TEQuantumBeaconT4> STRUCTURE_DEFINITION_TIER_4;
    public static IStructureDefinition<TEQuantumBeaconT5> STRUCTURE_DEFINITION_TIER_5;
    public static IStructureDefinition<TEQuantumBeaconT6> STRUCTURE_DEFINITION_TIER_6;

    public static void registerStructureInfo() {
        STRUCTURE_DEFINITION_TIER_1 = StructureRegistrationUtils.registerTier(
            TEQuantumBeaconT1.class,
            QuantumBeaconShapes.SHAPE_TIER_1,
            QuantumBeaconShapes.STRUCTURE_TIER_1,
            ModBlocks.QUANTUM_BEACON.get(),
            1,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_2 = StructureRegistrationUtils.registerTier(
            TEQuantumBeaconT2.class,
            QuantumBeaconShapes.SHAPE_TIER_2,
            QuantumBeaconShapes.STRUCTURE_TIER_2,
            ModBlocks.QUANTUM_BEACON.get(),
            2,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_3 = StructureRegistrationUtils.registerTier(
            TEQuantumBeaconT3.class,
            QuantumBeaconShapes.SHAPE_TIER_3,
            QuantumBeaconShapes.STRUCTURE_TIER_3,
            ModBlocks.QUANTUM_BEACON.get(),
            3,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_4 = StructureRegistrationUtils.registerTier(
            TEQuantumBeaconT4.class,
            QuantumBeaconShapes.SHAPE_TIER_4,
            QuantumBeaconShapes.STRUCTURE_TIER_4,
            ModBlocks.QUANTUM_BEACON.get(),
            4,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_5 = StructureRegistrationUtils.registerTier(
            TEQuantumBeaconT5.class,
            QuantumBeaconShapes.SHAPE_TIER_5,
            QuantumBeaconShapes.STRUCTURE_TIER_5,
            ModBlocks.QUANTUM_BEACON.get(),
            5,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_6 = StructureRegistrationUtils.registerTier(
            TEQuantumBeaconT6.class,
            QuantumBeaconShapes.SHAPE_TIER_6,
            QuantumBeaconShapes.STRUCTURE_TIER_6,
            ModBlocks.QUANTUM_BEACON.get(),
            6,
            builder -> addCommonElements(builder));
    }

    private static <T extends TEQuantumBeacon> void addCommonElements(StructureDefinition.Builder<T> builder) {
        builder.addElement('P', ofBlockAnyMeta(ModBlocks.MACHINE_BASE.get(), 0))
            .addElement(
                'A',
                ofChain(
                    ofBlockAdderWithPos(
                        (t, b, m, x, y, z) -> t.addToMachine(b, m, x, y, z),
                        ModBlocks.MODIFIER_NULL.get(),
                        0),
                    ofBlock(ModBlocks.MODIFIER_NULL.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_FIRE_RESISTANCE.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_FLIGHT.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_NIGHT_VISION.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_WATER_BREATHING.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_STRENGTH.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_HASTE.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_REGENERATION.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_SATURATION.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_RESISTANCE.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_JUMP_BOOST.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_SPEED.get(), 0)));
    }

}
