package ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.ore;

import ruiseki.omoshiroikamo.common.structure.StructureManager;

public class QuantumOreExtractorShapes {

    /**
     * Retrieve the shape for the given tier, honoring custom overrides.
     *
     * @param tier tier number (1-6)
     * @return shape array; defaults are used when no custom shape exists
     */
    public static String[][] getShape(int tier) {
        String[][] custom = StructureManager.getOreMinerShape(tier);
        if (custom != null) {
            return custom;
        }
        return getDefaultShape(tier);
    }

    /**
     * Get the default shape for the given tier.
     */
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
            "       ",
            "       ",
            "       ",
            "   Q   ",
            "       ",
            "       ",
            "       "
        },
        {
            "       ",
            "       ",
            "   F   ",
            "  FCF  ",
            "   F   ",
            "       ",
            "       "
        },
        {
            "       ",
            "   F   ",
            "       ",
            " F L F ",
            "       ",
            "   F   ",
            "       "
        },
        {
            "  FFF  ",
            " FPPPF ",
            "FPPPPPF",
            "FPPCPPF",
            "FPPPPPF",
            " FPPPF ",
            "  FFF  "
        }};
    public static final String STRUCTURE_TIER_2 = "tier2";
    public static final String[][] SHAPE_TIER_2 = new String[][]{
        {
            "       ",
            "       ",
            "       ",
            "   Q   ",
            "       ",
            "       ",
            "       "
        },
        {
            "       ",
            "   F   ",
            "   F   ",
            " FFCFF ",
            "   F   ",
            "   F   ",
            "       "
        },
        {
            "   F   ",
            "       ",
            "       ",
            "F  C  F",
            "       ",
            "       ",
            "   F   "
        },
        {
            "   F   ",
            "       ",
            "       ",
            "F  L  F",
            "       ",
            "       ",
            "   F   "
        },
        {
            "  FFF  ",
            " FPAPF ",
            "FPPPPPF",
            "FAPCPAF",
            "FPPPPPF",
            " FPAPF ",
            "  FFF  "
        }};
    public static final String STRUCTURE_TIER_3 = "tier3";
    public static final String[][] SHAPE_TIER_3 = new String[][]{
        {
            "           ",
            "           ",
            "           ",
            "           ",
            "           ",
            "     Q     ",
            "           ",
            "           ",
            "           ",
            "           ",
            "           ",
        },
        {
            "           ",
            "     F     ",
            "     F     ",
            "     F     ",
            "     F     ",
            " FFFFCFFFF ",
            "     F     ",
            "     F     ",
            "     F     ",
            "     F     ",
            "           ",
        },
        {
            "     F     ",
            "           ",
            "           ",
            "           ",
            "           ",
            "F    C    F",
            "           ",
            "           ",
            "           ",
            "           ",
            "     F     ",
        },
        {
            "     F     ",
            "           ",
            "           ",
            "           ",
            "           ",
            "F    C    F",
            "           ",
            "           ",
            "           ",
            "           ",
            "     F     ",
        },
        {
            "     F     ",
            "           ",
            "           ",
            "           ",
            "           ",
            "F    L    F",
            "           ",
            "           ",
            "           ",
            "           ",
            "     F     ",
        },
        {
            "    FFF    ",
            "  FFPAPFF  ",
            " FAPPPPPAF ",
            " FPPPPPPPF ",
            "FPPPPPPPPPF",
            "FAPPPCPPPAF",
            "FPPPPPPPPPF",
            " FPPPPPPPF ",
            " FAPPPPPAF ",
            "  FFPAPFF  ",
            "    FFF    ",
        }};
    public static final String STRUCTURE_TIER_4 = "tier4";
    public static final String[][] SHAPE_TIER_4 = new String[][]{
        {
            "           ",
            "           ",
            "           ",
            "           ",
            "           ",
            "     Q     ",
            "           ",
            "           ",
            "           ",
            "           ",
            "           ",
        },
        {
            "           ",
            "     F     ",
            "     F     ",
            "     F     ",
            "     F     ",
            " FFFFCFFFF ",
            "     F     ",
            "     F     ",
            "     F     ",
            "     F     ",
            "           ",
        },
        {
            "     F     ",
            "           ",
            "           ",
            "           ",
            "           ",
            "F    C    F",
            "           ",
            "           ",
            "           ",
            "           ",
            "     F     ",
        },
        {
            "     F     ",
            "           ",
            "           ",
            "           ",
            "           ",
            "F    C    F",
            "           ",
            "           ",
            "           ",
            "           ",
            "     F     ",
        },
        {
            "     F     ",
            "           ",
            "           ",
            "           ",
            "           ",
            "F    C    F",
            "           ",
            "           ",
            "           ",
            "           ",
            "     F     ",
        },
        {
            "     F     ",
            "           ",
            "           ",
            "           ",
            "           ",
            "F    L    F",
            "           ",
            "           ",
            "           ",
            "           ",
            "     F     ",
        },
        {
            "   FFFFF   ",
            "  FPAAAPF  ",
            " FPPPPPPPF ",
            "FPPPPPPPPPF",
            "FAPPPPPPPAF",
            "FAPPPCPPPAF",
            "FAPPPPPPPAF",
            "FPPPPPPPPPF",
            " FPPPPPPPF ",
            "  FPAAAPF  ",
            "   FFFFF   ",
        }};
    public static final String STRUCTURE_TIER_5 = "tier5";
    public static final String[][] SHAPE_TIER_5 = new String[][]{
        {
            "           ",
            "           ",
            "           ",
            "           ",
            "           ",
            "     Q     ",
            "           ",
            "           ",
            "           ",
            "           ",
            "           ",
        },
        {
            "           ",
            "           ",
            "     F     ",
            "     F     ",
            "     F     ",
            "  FFFCFFF  ",
            "     F     ",
            "     F     ",
            "     F     ",
            "           ",
            "           ",
        },
        {
            "           ",
            "     F     ",
            "           ",
            "           ",
            "           ",
            " F   C   F ",
            "           ",
            "           ",
            "           ",
            "     F     ",
            "           ",
        },
        {
            "     F     ",
            "           ",
            "           ",
            "           ",
            "           ",
            "F    C    F",
            "           ",
            "           ",
            "           ",
            "           ",
            "     F     ",
        },
        {
            "     F     ",
            "           ",
            "           ",
            "           ",
            "           ",
            "F    C    F",
            "           ",
            "           ",
            "           ",
            "           ",
            "     F     ",
        },
        {
            "     F     ",
            "           ",
            "           ",
            "           ",
            "           ",
            "F    C    F",
            "           ",
            "           ",
            "           ",
            "           ",
            "     F     ",
        },
        {
            "   FFFFF   ",
            "  F     F  ",
            " F       F ",
            "F         F",
            "F         F",
            "F    L    F",
            "F         F",
            "F         F",
            " F       F ",
            "  F     F  ",
            "   FFFFF   ",
        },

        {
            "           ",
            "   PPFPP   ",
            "  PPAFAPP  ",
            " PPPAFAPPP ",
            " PAAPFPAAP ",
            " FFFFCFFFF ",
            " PAAPFPAAP ",
            " PPPAFAPPP ",
            "  PPAFAPP  ",
            "   PPFPP   ",
            "           ",
        }};
    public static final String STRUCTURE_TIER_6 = "tier6";
    public static final String[][] SHAPE_TIER_6 = new String[][]{
        {
            "             ",
            "             ",
            "             ",
            "             ",
            "             ",
            "             ",
            "      Q      ",
            "             ",
            "             ",
            "             ",
            "             ",
            "             ",
            "             ",
        },
        {
            "             ",
            "             ",
            "      F      ",
            "      F      ",
            "      F      ",
            "      F      ",
            "  FFFFCFFFF  ",
            "      F      ",
            "      F      ",
            "      F      ",
            "      F      ",
            "             ",
            "             ",
        },
        {
            "             ",
            "      F      ",
            "             ",
            "             ",
            "             ",
            "             ",
            " F    C    F ",
            "             ",
            "             ",
            "             ",
            "             ",
            "      F      ",
            "             ",
        },
        {
            "      F      ",
            "             ",
            "             ",
            "             ",
            "             ",
            "             ",
            "F     C     F",
            "             ",
            "             ",
            "             ",
            "             ",
            "             ",
            "      F      ",
        },
        {
            "      F      ",
            "             ",
            "             ",
            "             ",
            "             ",
            "             ",
            "F     C     F",
            "             ",
            "             ",
            "             ",
            "             ",
            "             ",
            "      F      ",
        },
        {
            "      F      ",
            "             ",
            "             ",
            "             ",
            "             ",
            "             ",
            "F     C     F",
            "             ",
            "             ",
            "             ",
            "             ",
            "             ",
            "      F      ",
        },
        {
            "    FFFFF    ",
            "  FF     FF  ",
            " F         F ",
            " F         F ",
            "F           F",
            "F           F",
            "F     L     F",
            "F           F",
            "F           F",
            " F         F ",
            " F         F ",
            "  FF     FF  ",
            "    FFFFF    ",
        },

        {
            "             ",
            "    PPFPP    ",
            "  PPPPFPPPP  ",
            "  PPPAFAPPP  ",
            " PPPAAFAAPPP ",
            " PPAAFFFAAPP ",
            " FFFFFCFFFFF ",
            " PPAAFFFAAPP ",
            " PPPAAFAAPPP ",
            "  PPPAFAPPP  ",
            "  PPPPFPPPP  ",
            "    PPFPP    ",
            "             ",
        }};
    // spotless:on

    public static final int[][] TIER_OFFSET = { { 3, 0, 3 }, { 3, 0, 3 }, { 5, 0, 5 }, { 5, 0, 5 }, { 5, 0, 5 },
        { 6, 0, 6 } };
}
