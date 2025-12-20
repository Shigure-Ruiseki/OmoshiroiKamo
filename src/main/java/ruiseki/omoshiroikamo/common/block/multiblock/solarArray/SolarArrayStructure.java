package ruiseki.omoshiroikamo.common.block.multiblock.solarArray;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static ruiseki.omoshiroikamo.plugin.structureLib.StructureLibUtils.ofBlockAdderWithPos;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import ruiseki.omoshiroikamo.common.block.multiblock.StructureRegistrationUtils;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.util.Logger;

public class SolarArrayStructure {

    public static IStructureDefinition<TESolarArrayT1> STRUCTURE_DEFINITION_TIER_1;
    public static IStructureDefinition<TESolarArrayT2> STRUCTURE_DEFINITION_TIER_2;
    public static IStructureDefinition<TESolarArrayT3> STRUCTURE_DEFINITION_TIER_3;
    public static IStructureDefinition<TESolarArrayT4> STRUCTURE_DEFINITION_TIER_4;
    public static IStructureDefinition<TESolarArrayT5> STRUCTURE_DEFINITION_TIER_5;
    public static IStructureDefinition<TESolarArrayT6> STRUCTURE_DEFINITION_TIER_6;

    public static void registerStructureInfo() {
        STRUCTURE_DEFINITION_TIER_1 = StructureRegistrationUtils.registerTier(
            TESolarArrayT1.class,
            SolarArrayShapes.SHAPE_TIER_1,
            SolarArrayShapes.STRUCTURE_TIER_1,
            ModBlocks.SOLAR_ARRAY.get(),
            1,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_2 = StructureRegistrationUtils.registerTier(
            TESolarArrayT2.class,
            SolarArrayShapes.SHAPE_TIER_2,
            SolarArrayShapes.STRUCTURE_TIER_2,
            ModBlocks.SOLAR_ARRAY.get(),
            2,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_3 = StructureRegistrationUtils.registerTier(
            TESolarArrayT3.class,
            SolarArrayShapes.SHAPE_TIER_3,
            SolarArrayShapes.STRUCTURE_TIER_3,
            ModBlocks.SOLAR_ARRAY.get(),
            3,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_4 = StructureRegistrationUtils.registerTier(
            TESolarArrayT4.class,
            SolarArrayShapes.SHAPE_TIER_4,
            SolarArrayShapes.STRUCTURE_TIER_4,
            ModBlocks.SOLAR_ARRAY.get(),
            4,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_5 = StructureRegistrationUtils.registerTier(
            TESolarArrayT5.class,
            SolarArrayShapes.SHAPE_TIER_5,
            SolarArrayShapes.STRUCTURE_TIER_5,
            ModBlocks.SOLAR_ARRAY.get(),
            5,
            builder -> addCommonElements(builder));

        STRUCTURE_DEFINITION_TIER_6 = StructureRegistrationUtils.registerTier(
            TESolarArrayT6.class,
            SolarArrayShapes.SHAPE_TIER_6,
            SolarArrayShapes.STRUCTURE_TIER_6,
            ModBlocks.SOLAR_ARRAY.get(),
            6,
            builder -> addCommonElements(builder));

        Logger.info("Register Solar Array Structure Info");
    }

    private static <T extends TESolarArray> void addCommonElements(StructureDefinition.Builder<T> builder) {
        builder.addElement(
            'G',
            ofChain(
                ofBlockAdderWithPos((t, b, m, x, y, z) -> t.addToMachine(b, m, x, y, z), ModBlocks.SOLAR_CELL.get(), 0),
                ofBlock(ModBlocks.SOLAR_CELL.get(), 0),
                ofBlock(ModBlocks.SOLAR_CELL.get(), 1),
                ofBlock(ModBlocks.SOLAR_CELL.get(), 2),
                ofBlock(ModBlocks.SOLAR_CELL.get(), 3),
                ofBlock(ModBlocks.SOLAR_CELL.get(), 4),
                ofBlock(ModBlocks.SOLAR_CELL.get(), 5)))
            .addElement(
                'A',
                ofChain(
                    ofBlockAdderWithPos(
                        (t, b, m, x, y, z) -> t.addToMachine(b, m, x, y, z),
                        ModBlocks.MODIFIER_NULL.get(),
                        0),
                    ofBlock(ModBlocks.MODIFIER_NULL.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_PIEZO.get(), 0)));
    }
}
