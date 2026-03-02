package ruiseki.omoshiroikamo.module.multiblock.common.block.quantumBeacon;

import java.util.Map;

import ruiseki.omoshiroikamo.api.structure.core.StructureShapeWithMappings;
import ruiseki.omoshiroikamo.core.common.structure.StructureManager;

public class QuantumBeaconShapes {

    public static String[][] getShape(int tier) {
        String[][] custom = StructureManager.getBeaconShape(tier);
        if (custom != null) {
            return custom;
        }
        return getDefaultShape(tier);
    }

    /**
     * Get the shape with dynamic mappings for the specified tier.
     */
    public static StructureShapeWithMappings getShapeWithMappings(int tier) {
        return StructureManager.getInstance()
            .getShapeWithMappings("quantum_beacon", "beaconTier" + tier);
    }

    /**
     * Get all dynamic mappings for use with registerTierWithDynamicMappings.
     */
    public static Map<Character, Object> getDynamicMappings(int tier) {
        StructureShapeWithMappings swm = getShapeWithMappings(tier);
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
            "     ",
            " F F ",
            "  Q  ",
            " F F ",
            "     "
        },
        {
            "F   F",
            " AFA ",
            " F F ",
            " AFA ",
            "F   F"
        },
        {
            "PF FP",
            "FPPPF",
            " P P ",
            "FPPPF",
            "PF FP"
        }};
    public static final String STRUCTURE_TIER_2 = "tier2";
    public static final String[][] SHAPE_TIER_2 = new String[][]{
        {
            "       ",
            "       ",
            "  F F  ",
            "   Q   ",
            "  F F  ",
            "       ",
            "       "
        },
        {
            "       ",
            " F   F ",
            "  AFA  ",
            "  F F  ",
            "  AFA  ",
            " F   F ",
            "       "
        },
        {
            "F     F",
            " AF FA ",
            " FPPPF ",
            "  P P  ",
            " FPPPF ",
            " AF FA ",
            "F     F"
        },
        {
            "PF   FP",
            "FPPFPPF",
            " P   P ",
            " F   F ",
            " P   P ",
            "FPPFPPF",
            "PF   FP"
        }};
    public static final String STRUCTURE_TIER_3 = "tier3";
    public static final String[][] SHAPE_TIER_3 = new String[][]{
        {
            "         ",
            "         ",
            "         ",
            "   F F   ",
            "    Q    ",
            "   F F   ",
            "         ",
            "         ",
            "         "
        },
        {
            "         ",
            "         ",
            "  F   F  ",
            "   AFA   ",
            "   F F   ",
            "   AFA   ",
            "  F   F  ",
            "         ",
            "         "
        },
        {
            "         ",
            " F     F ",
            "  AF FA  ",
            "  FPPPF  ",
            "   P P   ",
            "  FPPPF  ",
            "  AF FA  ",
            " F     F ",
            "         "
        },
        {
            "F       F",
            " AF   FA ",
            " FPPFPPF ",
            "  P   P  ",
            "  F   F  ",
            "  P   P  ",
            " FPPFPPF ",
            " AF   FA ",
            "F       F"
        },
        {
            "PF     FP",
            "FPPF FPPF",
            " P  P  P ",
            " F     F ",
            "  P   P  ",
            " F     F ",
            " P  P  P ",
            "FPPF FPPF",
            "PF     FP"
        }};
    public static final String STRUCTURE_TIER_4 = "tier4";
    public static final String[][] SHAPE_TIER_4 = new String[][]{
        {
            "           ",
            "           ",
            "           ",
            "           ",
            "    F F    ",
            "     Q     ",
            "    F F    ",
            "           ",
            "           ",
            "           ",
            "           "
        },
        {
            "           ",
            "           ",
            "           ",
            "   F   F   ",
            "    AFA    ",
            "    F F    ",
            "    AFA    ",
            "   F   F   ",
            "           ",
            "           ",
            "           "
        },
        {
            "           ",
            "           ",
            "  F     F  ",
            "   AF FA   ",
            "   FPPPF   ",
            "    P P    ",
            "   FPPPF   ",
            "   AF FA   ",
            "  F     F  ",
            "           ",
            "           "
        },
        {
            "           ",
            " F       F ",
            "  AF   FA  ",
            "  FPPFPPF  ",
            "   P   P   ",
            "   F   F   ",
            "   P   P   ",
            "  FPPFPPF  ",
            "  AF   FA  ",
            " F       F ",
            "           "
        },
        {
            "F         F",
            " AF     FA ",
            " FPPF FPPF ",
            "  P  P  P  ",
            "  F     F  ",
            "   P   P   ",
            "  F     F  ",
            "  P  P  P  ",
            " FPPF FPPF ",
            " AF     FA ",
            "F         F"
        },
        {
            "PF       FP",
            "FPPF   FPPF",
            " P  PFP  P ",
            " F       F ",
            "  P     P  ",
            "  F     F  ",
            "  P     P  ",
            " F       F ",
            " P  PFP  P ",
            "FPPF   FPPF",
            "PF       FP"
        }};
    public static final String STRUCTURE_TIER_5 = "tier5";
    public static final String[][] SHAPE_TIER_5 = new String[][]{
        {
            "             ",
            "             ",
            "             ",
            "             ",
            "             ",
            "     F F     ",
            "      Q      ",
            "     F F     ",
            "             ",
            "             ",
            "             ",
            "             ",
            "             "
        },
        {
            "             ",
            "             ",
            "             ",
            "             ",
            "    F   F    ",
            "     AFA     ",
            "     F F     ",
            "     AFA     ",
            "    F   F    ",
            "             ",
            "             ",
            "             ",
            "             "
        },
        {
            "             ",
            "             ",
            "             ",
            "   F     F   ",
            "    AF FA    ",
            "    FPPPF    ",
            "     P P     ",
            "    FPPPF    ",
            "    AF FA    ",
            "   F     F   ",
            "             ",
            "             ",
            "             "
        },
        {
            "             ",
            "             ",
            "  F       F  ",
            "   AF   FA   ",
            "   FPPFPPF   ",
            "    P   P    ",
            "    F   F    ",
            "    P   P    ",
            "   FPPFPPF   ",
            "   AF   FA   ",
            "  F       F  ",
            "             ",
            "             "
        },
        {
            "             ",
            " F         F ",
            "  AF     FA  ",
            "  FPPF FPPF  ",
            "   P  P  P   ",
            "   F     F   ",
            "    P   P    ",
            "   F     F   ",
            "   P  P  P   ",
            "  FPPF FPPF  ",
            "  AF     FA  ",
            " F         F ",
            "             "
        },
        {
            "F           F",
            " AF       FA ",
            " FPPF   FPPF ",
            "  P  PFP  P  ",
            "  F       F  ",
            "   P     P   ",
            "   F     F   ",
            "   P     P   ",
            "  F       F  ",
            "  P  PFP  P  ",
            " FPPF   FPPF ",
            " AF       FA ",
            "F           F"
        },
        {
            "PF         FP",
            "FPPF     FPPF",
            " P  PF FP  P ",
            " F    P    F ",
            "  P       P  ",
            "  F       F  ",
            "   P     P   ",
            "  F       F  ",
            "  P       P  ",
            " F    P    F ",
            " P  PF FP  P ",
            "FPPF     FPPF",
            "PF         FP"
        }};
    public static final String STRUCTURE_TIER_6 = "tier6";
    public static final String[][] SHAPE_TIER_6 = new String[][]{
        {
            "               ",
            "               ",
            "               ",
            "               ",
            "               ",
            "               ",
            "      F F      ",
            "       Q       ",
            "      F F      ",
            "               ",
            "               ",
            "               ",
            "               ",
            "               ",
            "               "
        },
        {
            "               ",
            "               ",
            "               ",
            "               ",
            "               ",
            "     F   F     ",
            "      AFA      ",
            "      F F      ",
            "      AFA      ",
            "     F   F     ",
            "               ",
            "               ",
            "               ",
            "               ",
            "               "
        },
        {
            "               ",
            "               ",
            "               ",
            "               ",
            "    F     F    ",
            "     AF FA     ",
            "     FPPPF     ",
            "      P P      ",
            "     FPPPF     ",
            "     AF FA     ",
            "    F     F    ",
            "               ",
            "               ",
            "               ",
            "               "
        },
        {
            "               ",
            "               ",
            "               ",
            "   F       F   ",
            "    AF   FA    ",
            "    FPPFPPF    ",
            "     P   P     ",
            "     F   F     ",
            "     P   P     ",
            "    FPPFPPF    ",
            "    AF   FA    ",
            "   F       F   ",
            "               ",
            "               ",
            "               "
        },
        {
            "               ",
            "               ",
            "  F         F  ",
            "   AF     FA   ",
            "   FPPF FPPF   ",
            "    P  P  P    ",
            "    F     F    ",
            "     P   P     ",
            "    F     F    ",
            "    P  P  P    ",
            "   FPPF FPPF   ",
            "   AF     FA   ",
            "  F         F  ",
            "               ",
            "               "
        },
        {
            "               ",
            " F           F ",
            "  AF       FA  ",
            "  FPPF   FPPF  ",
            "   P  PFP  P   ",
            "   F       F   ",
            "    P     P    ",
            "    F     F    ",
            "    P     P    ",
            "   F       F   ",
            "   P  PFP  P   ",
            "  FPPF   FPPF  ",
            "  AF       FA  ",
            " F           F ",
            "               "
        },
        {
            "F             F",
            " AF         FA ",
            " FPPF     FPPF ",
            "  P  PF FP  P  ",
            "  F    P    F  ",
            "   P       P   ",
            "   F       F   ",
            "    P     P    ",
            "   F       F   ",
            "   P       P   ",
            "  F    P    F  ",
            "  P  PF FP  P  ",
            " FPPF     FPPF ",
            " AF         FA ",
            "F             F"
        },
        {
            "PF           FP",
            "FPPF       FPPF",
            " P  PF   FP  P ",
            " F    PFP    F ",
            "  P         P  ",
            "  F         F  ",
            "   P       P   ",
            "   F       F   ",
            "   P       P   ",
            "  F         F  ",
            "  P         P  ",
            " F    PFP    F ",
            " P  PF   FP  P ",
            "FPPF       FPPF",
            "PF           FP"
        }};
    // spotless:on

    public static final int[][] TIER_OFFSET = { { 2, 0, 2 }, { 3, 0, 3 }, { 4, 0, 4 }, { 5, 0, 5 }, { 6, 0, 6 },
        { 7, 0, 7 } };
}
