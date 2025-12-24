package ruiseki.omoshiroikamo.module.multiblock.common.block.solarArray;

import java.util.Map;

import ruiseki.omoshiroikamo.core.common.structure.StructureJsonLoader;
import ruiseki.omoshiroikamo.core.common.structure.StructureManager;

public class SolarArrayShapes {

    public static String[][] getShape(int tier) {
        String[][] custom = StructureManager.getSolarArrayShape(tier);
        if (custom != null) {
            return custom;
        }
        return getDefaultShape(tier);
    }

    /**
     * Get the shape with dynamic mappings for the specified tier.
     * Returns null if no custom shape/mappings are defined (use default behavior).
     */
    public static StructureJsonLoader.ShapeWithMappings getShapeWithMappings(int tier) {
        return StructureManager.getInstance()
            .getShapeWithMappings("solar_array", "solarArrayTier" + tier);
    }

    /**
     * Get all dynamic mappings as a Character -> Object map for use with
     * registerTierWithDynamicMappings.
     */
    public static Map<Character, Object> getDynamicMappings(int tier) {
        StructureJsonLoader.ShapeWithMappings swm = getShapeWithMappings(tier);
        if (swm != null) {
            return swm.dynamicMappings;
        }
        return null;
    }

    public static String[][] getDefaultShape(int tier) {
        switch (tier) {
            case 1:
                return SHAPE_TIER_1;
            case 2:
                return SHAPE_TIER_2;
            case 3:
                return SHAPE_TIER_3;
            case 4:
                return SHAPE_TIER_4;
            case 5:
                return SHAPE_TIER_5;
            case 6:
                return SHAPE_TIER_6;
            default:
                return SHAPE_TIER_1;
        }
    }

    public static String getStructureName(int tier) {
        switch (tier) {
            case 1:
                return STRUCTURE_TIER_1;
            case 2:
                return STRUCTURE_TIER_2;
            case 3:
                return STRUCTURE_TIER_3;
            case 4:
                return STRUCTURE_TIER_4;
            case 5:
                return STRUCTURE_TIER_5;
            case 6:
                return STRUCTURE_TIER_6;
            default:
                return STRUCTURE_TIER_1;
        }
    }

    // spotless:off
    public static final String STRUCTURE_TIER_1 = "tier1";
    public static final String[][] SHAPE_TIER_1 = new String[][]{
        {
            "FFFFF",
            "FGGGF",
            "FGGGF",
            "FGGGF",
            "FFFFF"},
        {
            "     ",
            " A A ",
            "  Q  ",
            " A A ",
            "     ",
        }};
    public static final String STRUCTURE_TIER_2 = "tier2";
    public static final String[][] SHAPE_TIER_2 = new String[][]{
        {
            "FFFFFFF",
            "FGGGGGF",
            "FGGGGGF",
            "FGGGGGF",
            "FGGGGGF",
            "FGGGGGF",
            "FFFFFFF"
        },
        {
            "       ",
            "       ",
            "  A A  ",
            "   Q   ",
            "  A A  ",
            "       ",
            "       "
        }};
    public static final String STRUCTURE_TIER_3 = "tier3";
    public static final String[][] SHAPE_TIER_3 = new String[][]{
        {
            "FFFFFFFFF",
            "FGGGGGGGF",
            "FGGGGGGGF",
            "FGGGGGGGF",
            "FGGGGGGGF",
            "FGGGGGGGF",
            "FGGGGGGGF",
            "FGGGGGGGF",
            "FFFFFFFFF"
        },
        {
            "         ",
            "         ",
            "   A A   ",
            "  A   A  ",
            "    Q    ",
            "  A   A  ",
            "   A A   ",
            "         ",
            "         "
        }};
    public static final String STRUCTURE_TIER_4 = "tier4";
    public static final String[][] SHAPE_TIER_4 = new String[][]{
        {
            "FFFFFFFFFFF",
            "FGGGGGGGGGF",
            "FGGGGGGGGGF",
            "FGGGGGGGGGF",
            "FGGGGGGGGGF",
            "FGGGGGGGGGF",
            "FGGGGGGGGGF",
            "FGGGGGGGGGF",
            "FGGGGGGGGGF",
            "FGGGGGGGGGF",
            "FFFFFFFFFFF"
        },
        {
            "           ",
            "           ",
            "           ",
            "    A A    ",
            "   A   A   ",
            "     Q     ",
            "   A   A   ",
            "    A A    ",
            "           ",
            "           ",
            "           "
        }};
    public static final String STRUCTURE_TIER_5 = "tier5";
    public static final String[][] SHAPE_TIER_5 = new String[][]{
        {
            "FFFFFFFFFFFFF",
            "FGGGGGGGGGGGF",
            "FGGGGGGGGGGGF",
            "FGGGGGGGGGGGF",
            "FGGGGGGGGGGGF",
            "FGGGGGGGGGGGF",
            "FGGGGGGGGGGGF",
            "FGGGGGGGGGGGF",
            "FGGGGGGGGGGGF",
            "FGGGGGGGGGGGF",
            "FGGGGGGGGGGGF",
            "FGGGGGGGGGGGF",
            "FFFFFFFFFFFFF"
        },
        {
            "             ",
            "             ",
            "             ",
            "             ",
            "    AA AA    ",
            "    A   A    ",
            "      Q      ",
            "    A   A    ",
            "    AA AA    ",
            "             ",
            "             ",
            "             ",
            "             "
        }};
    public static final String STRUCTURE_TIER_6 = "tier6";
    public static final String[][] SHAPE_TIER_6 = new String[][]{
        {
            "FFFFFFFFFFFFFFF",
            "FGGGGGGGGGGGGGF",
            "FGGGGGGGGGGGGGF",
            "FGGGGGGGGGGGGGF",
            "FGGGGGGGGGGGGGF",
            "FGGGGGGGGGGGGGF",
            "FGGGGGGGGGGGGGF",
            "FGGGGGGGGGGGGGF",
            "FGGGGGGGGGGGGGF",
            "FGGGGGGGGGGGGGF",
            "FGGGGGGGGGGGGGF",
            "FGGGGGGGGGGGGGF",
            "FGGGGGGGGGGGGGF",
            "FGGGGGGGGGGGGGF",
            "FFFFFFFFFFFFFFF"
        },
        {
            "               ",
            "               ",
            "               ",
            "               ",
            "               ",
            "     AA AA     ",
            "     A   A     ",
            "       Q       ",
            "     A   A     ",
            "     AA AA     ",
            "               ",
            "               ",
            "               ",
            "               ",
            "               "
        }};
    // spotless:on

    public static final int[][] TIER_OFFSET = { { 2, 1, 2 }, { 3, 1, 3 }, { 4, 1, 4 }, { 5, 1, 5 }, { 6, 1, 6 },
        { 7, 1, 7 } };
}
