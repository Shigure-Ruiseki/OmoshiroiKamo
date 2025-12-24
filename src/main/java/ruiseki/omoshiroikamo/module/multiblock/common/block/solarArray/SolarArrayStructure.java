package ruiseki.omoshiroikamo.module.multiblock.common.block.solarArray;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static ruiseki.omoshiroikamo.core.integration.structureLib.StructureLibUtils.ofBlockAdderWithPos;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import ruiseki.omoshiroikamo.core.common.structure.StructureRegistrationUtils;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockBlocks;

public class SolarArrayStructure {

    public static IStructureDefinition<TESolarArrayT1> STRUCTURE_DEFINITION_TIER_1;
    public static IStructureDefinition<TESolarArrayT2> STRUCTURE_DEFINITION_TIER_2;
    public static IStructureDefinition<TESolarArrayT3> STRUCTURE_DEFINITION_TIER_3;
    public static IStructureDefinition<TESolarArrayT4> STRUCTURE_DEFINITION_TIER_4;
    public static IStructureDefinition<TESolarArrayT5> STRUCTURE_DEFINITION_TIER_5;
    public static IStructureDefinition<TESolarArrayT6> STRUCTURE_DEFINITION_TIER_6;

    public static void registerStructureInfo() {
        STRUCTURE_DEFINITION_TIER_1 = StructureRegistrationUtils.registerTierWithDynamicMappings(
            TESolarArrayT1.class,
            SolarArrayShapes.getShape(1),
            SolarArrayShapes.getDynamicMappings(1),
            SolarArrayShapes.STRUCTURE_TIER_1,
            MultiBlockBlocks.SOLAR_ARRAY.getBlock(),
            1,
            builder -> addCommonElements(builder, 1));

        STRUCTURE_DEFINITION_TIER_2 = StructureRegistrationUtils.registerTierWithDynamicMappings(
            TESolarArrayT2.class,
            SolarArrayShapes.getShape(2),
            SolarArrayShapes.getDynamicMappings(2),
            SolarArrayShapes.STRUCTURE_TIER_2,
            MultiBlockBlocks.SOLAR_ARRAY.getBlock(),
            2,
            builder -> addCommonElements(builder, 2));

        STRUCTURE_DEFINITION_TIER_3 = StructureRegistrationUtils.registerTierWithDynamicMappings(
            TESolarArrayT3.class,
            SolarArrayShapes.getShape(3),
            SolarArrayShapes.getDynamicMappings(3),
            SolarArrayShapes.STRUCTURE_TIER_3,
            MultiBlockBlocks.SOLAR_ARRAY.getBlock(),
            3,
            builder -> addCommonElements(builder, 3));

        STRUCTURE_DEFINITION_TIER_4 = StructureRegistrationUtils.registerTierWithDynamicMappings(
            TESolarArrayT4.class,
            SolarArrayShapes.getShape(4),
            SolarArrayShapes.getDynamicMappings(4),
            SolarArrayShapes.STRUCTURE_TIER_4,
            MultiBlockBlocks.SOLAR_ARRAY.getBlock(),
            4,
            builder -> addCommonElements(builder, 4));

        STRUCTURE_DEFINITION_TIER_5 = StructureRegistrationUtils.registerTierWithDynamicMappings(
            TESolarArrayT5.class,
            SolarArrayShapes.getShape(5),
            SolarArrayShapes.getDynamicMappings(5),
            SolarArrayShapes.STRUCTURE_TIER_5,
            MultiBlockBlocks.SOLAR_ARRAY.getBlock(),
            5,
            builder -> addCommonElements(builder, 5));

        STRUCTURE_DEFINITION_TIER_6 = StructureRegistrationUtils.registerTierWithDynamicMappings(
            TESolarArrayT6.class,
            SolarArrayShapes.getShape(6),
            SolarArrayShapes.getDynamicMappings(6),
            SolarArrayShapes.STRUCTURE_TIER_6,
            MultiBlockBlocks.SOLAR_ARRAY.getBlock(),
            6,
            builder -> addCommonElements(builder, 6));

        Logger.info("Register Solar Array Structure Info");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void addCommonElements(StructureDefinition.Builder builder, int tier) {
        int defaultMeta = tier - 1; // Tier 1 = meta 0, Tier 2 = meta 1, etc.
        builder
            .addElement(
                'G',
                ofChain(
                    ofBlockAdderWithPos(
                        (t, b, m, x, y, z) -> ((TESolarArray) t).addToMachine(b, m, x, y, z),
                        MultiBlockBlocks.SOLAR_CELL.getBlock(),
                        defaultMeta),
                    ofBlock(MultiBlockBlocks.SOLAR_CELL.getBlock(), 0),
                    ofBlock(MultiBlockBlocks.SOLAR_CELL.getBlock(), 1),
                    ofBlock(MultiBlockBlocks.SOLAR_CELL.getBlock(), 2),
                    ofBlock(MultiBlockBlocks.SOLAR_CELL.getBlock(), 3),
                    ofBlock(MultiBlockBlocks.SOLAR_CELL.getBlock(), 4),
                    ofBlock(MultiBlockBlocks.SOLAR_CELL.getBlock(), 5)))
            .addElement(
                'A',
                ofChain(
                    ofBlockAdderWithPos(
                        (t, b, m, x, y, z) -> ((TESolarArray) t).addToMachine(b, m, x, y, z),
                        MultiBlockBlocks.MODIFIER_NULL.getBlock(),
                        0),
                    ofBlockAdderWithPos(
                        (t, b, m, x, y, z) -> ((TESolarArray) t).addToMachine(b, m, x, y, z),
                        MultiBlockBlocks.MODIFIER_PIEZO.getBlock(),
                        0),
                    ofBlock(MultiBlockBlocks.MODIFIER_NULL.getBlock(), 0),
                    ofBlock(MultiBlockBlocks.MODIFIER_PIEZO.getBlock(), 0)));
    }
}
