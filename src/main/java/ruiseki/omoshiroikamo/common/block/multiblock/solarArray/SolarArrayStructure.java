package ruiseki.omoshiroikamo.common.block.multiblock.solarArray;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static ruiseki.omoshiroikamo.plugin.structureLib.StructureLibUtils.ofBlockAdderWithPos;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.structure.StructureRegistrationUtils;
import ruiseki.omoshiroikamo.common.util.Logger;

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
            ModBlocks.SOLAR_ARRAY.get(),
            1,
            builder -> addCommonElements(builder, 1));

        STRUCTURE_DEFINITION_TIER_2 = StructureRegistrationUtils.registerTierWithDynamicMappings(
            TESolarArrayT2.class,
            SolarArrayShapes.getShape(2),
            SolarArrayShapes.getDynamicMappings(2),
            SolarArrayShapes.STRUCTURE_TIER_2,
            ModBlocks.SOLAR_ARRAY.get(),
            2,
            builder -> addCommonElements(builder, 2));

        STRUCTURE_DEFINITION_TIER_3 = StructureRegistrationUtils.registerTierWithDynamicMappings(
            TESolarArrayT3.class,
            SolarArrayShapes.getShape(3),
            SolarArrayShapes.getDynamicMappings(3),
            SolarArrayShapes.STRUCTURE_TIER_3,
            ModBlocks.SOLAR_ARRAY.get(),
            3,
            builder -> addCommonElements(builder, 3));

        STRUCTURE_DEFINITION_TIER_4 = StructureRegistrationUtils.registerTierWithDynamicMappings(
            TESolarArrayT4.class,
            SolarArrayShapes.getShape(4),
            SolarArrayShapes.getDynamicMappings(4),
            SolarArrayShapes.STRUCTURE_TIER_4,
            ModBlocks.SOLAR_ARRAY.get(),
            4,
            builder -> addCommonElements(builder, 4));

        STRUCTURE_DEFINITION_TIER_5 = StructureRegistrationUtils.registerTierWithDynamicMappings(
            TESolarArrayT5.class,
            SolarArrayShapes.getShape(5),
            SolarArrayShapes.getDynamicMappings(5),
            SolarArrayShapes.STRUCTURE_TIER_5,
            ModBlocks.SOLAR_ARRAY.get(),
            5,
            builder -> addCommonElements(builder, 5));

        STRUCTURE_DEFINITION_TIER_6 = StructureRegistrationUtils.registerTierWithDynamicMappings(
            TESolarArrayT6.class,
            SolarArrayShapes.getShape(6),
            SolarArrayShapes.getDynamicMappings(6),
            SolarArrayShapes.STRUCTURE_TIER_6,
            ModBlocks.SOLAR_ARRAY.get(),
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
                        ModBlocks.SOLAR_CELL.get(),
                        defaultMeta),
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
                        (t, b, m, x, y, z) -> ((TESolarArray) t).addToMachine(b, m, x, y, z),
                        ModBlocks.MODIFIER_NULL.get(),
                        0),
                    ofBlockAdderWithPos(
                        (t, b, m, x, y, z) -> ((TESolarArray) t).addToMachine(b, m, x, y, z),
                        ModBlocks.MODIFIER_PIEZO.get(),
                        0),
                    ofBlock(ModBlocks.MODIFIER_NULL.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_PIEZO.get(), 0)));
    }
}
