package ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.ore;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockAnyMeta;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static ruiseki.omoshiroikamo.plugin.structureLib.StructureLibUtils.ofBlockAdderWithPos;

import net.minecraft.item.ItemStack;

import com.gtnewhorizon.structurelib.alignment.constructable.IMultiblockInfoContainer;
import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.TEQuantumExtractor;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.util.Logger;

public class QuantumOreExtractorStructure {

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
    public static IStructureDefinition<TEQuantumOreExtractorT1> STRUCTURE_DEFINITION_TIER_1;
    public static IStructureDefinition<TEQuantumOreExtractorT2> STRUCTURE_DEFINITION_TIER_2;
    public static IStructureDefinition<TEQuantumOreExtractorT3> STRUCTURE_DEFINITION_TIER_3;
    public static IStructureDefinition<TEQuantumOreExtractorT4> STRUCTURE_DEFINITION_TIER_4;
    public static IStructureDefinition<TEQuantumOreExtractorT5> STRUCTURE_DEFINITION_TIER_5;
    public static IStructureDefinition<TEQuantumOreExtractorT6> STRUCTURE_DEFINITION_TIER_6;

    @SuppressWarnings("unchecked")
    public static void registerStructureInfo() {
        StructureDefinition.Builder<TEQuantumOreExtractorT1> builder1 = StructureDefinition.builder();
        StructureDefinition.Builder<TEQuantumOreExtractorT2> builder2 = StructureDefinition.builder();
        StructureDefinition.Builder<TEQuantumOreExtractorT3> builder3 = StructureDefinition.builder();
        StructureDefinition.Builder<TEQuantumOreExtractorT4> builder4 = StructureDefinition.builder();
        StructureDefinition.Builder<TEQuantumOreExtractorT5> builder5 = StructureDefinition.builder();
        StructureDefinition.Builder<TEQuantumOreExtractorT6> builder6 = StructureDefinition.builder();

        builder1.addShape(STRUCTURE_TIER_1, transpose(SHAPE_TIER_1))
            .addElement('Q', ofBlock(ModBlocks.QUANTUM_ORE_EXTRACTOR.get(), 0))
            .addElement('C', ofBlock(ModBlocks.LASER_CORE.get(), 0))
            .addElement(
                'P',
                ofChain(
                    ofBlock(ModBlocks.MACHINE_BASE.get(), 0),
                    ofBlock(ModBlocks.MACHINE_BASE.get(), 1),
                    ofBlock(ModBlocks.MACHINE_BASE.get(), 2)))
            .addElement(
                'L',
                ofChain(
                    ofBlockAdderWithPos(TEQuantumExtractor::addToMachine, ModBlocks.LENS.get(), 0),
                    ofBlock(ModBlocks.LENS.get(), 0),
                    ofBlockAnyMeta(ModBlocks.COLORED_LENS.get(), 0)))
            .addElement(
                'A',
                ofChain(
                    ofBlockAdderWithPos(TEQuantumExtractor::addToMachine, ModBlocks.MODIFIER_NULL.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_NULL.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_ACCURACY.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_SPEED.get(), 0)))
            .addElement(
                'F',
                ofChain(
                    ofBlock(ModBlocks.BASALT_STRUCTURE.get(), 0),
                    ofBlock(ModBlocks.HARDENED_STRUCTURE.get(), 0),
                    ofBlock(ModBlocks.ALABASTER_STRUCTURE.get(), 0)));

        builder2.addShape(STRUCTURE_TIER_2, transpose(SHAPE_TIER_2))
            .addElement('Q', ofBlock(ModBlocks.QUANTUM_ORE_EXTRACTOR.get(), 1))
            .addElement('C', ofBlock(ModBlocks.LASER_CORE.get(), 0))
            .addElement(
                'P',
                ofChain(
                    ofBlock(ModBlocks.MACHINE_BASE.get(), 0),
                    ofBlock(ModBlocks.MACHINE_BASE.get(), 1),
                    ofBlock(ModBlocks.MACHINE_BASE.get(), 2)))
            .addElement(
                'L',
                ofChain(
                    ofBlockAdderWithPos(TEQuantumExtractor::addToMachine, ModBlocks.LENS.get(), 0),
                    ofBlock(ModBlocks.LENS.get(), 0),
                    ofBlockAnyMeta(ModBlocks.COLORED_LENS.get(), 0)))
            .addElement(
                'A',
                ofChain(
                    ofBlockAdderWithPos(TEQuantumExtractor::addToMachine, ModBlocks.MODIFIER_NULL.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_NULL.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_ACCURACY.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_SPEED.get(), 0)))
            .addElement(
                'F',
                ofChain(
                    ofBlock(ModBlocks.BASALT_STRUCTURE.get(), 1),
                    ofBlock(ModBlocks.HARDENED_STRUCTURE.get(), 1),
                    ofBlock(ModBlocks.ALABASTER_STRUCTURE.get(), 1)));

        builder3.addShape(STRUCTURE_TIER_3, transpose(SHAPE_TIER_3))
            .addElement('Q', ofBlock(ModBlocks.QUANTUM_ORE_EXTRACTOR.get(), 2))
            .addElement('C', ofBlock(ModBlocks.LASER_CORE.get(), 0))
            .addElement(
                'P',
                ofChain(
                    ofBlock(ModBlocks.MACHINE_BASE.get(), 0),
                    ofBlock(ModBlocks.MACHINE_BASE.get(), 1),
                    ofBlock(ModBlocks.MACHINE_BASE.get(), 2)))
            .addElement(
                'L',
                ofChain(
                    ofBlockAdderWithPos(TEQuantumExtractor::addToMachine, ModBlocks.LENS.get(), 0),
                    ofBlock(ModBlocks.LENS.get(), 0),
                    ofBlockAnyMeta(ModBlocks.COLORED_LENS.get(), 0)))
            .addElement(
                'A',
                ofChain(
                    ofBlockAdderWithPos(TEQuantumExtractor::addToMachine, ModBlocks.MODIFIER_NULL.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_NULL.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_ACCURACY.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_SPEED.get(), 0)))
            .addElement(
                'F',
                ofChain(
                    ofBlock(ModBlocks.BASALT_STRUCTURE.get(), 2),
                    ofBlock(ModBlocks.HARDENED_STRUCTURE.get(), 2),
                    ofBlock(ModBlocks.ALABASTER_STRUCTURE.get(), 2)));

        builder4.addShape(STRUCTURE_TIER_4, transpose(SHAPE_TIER_4))
            .addElement('Q', ofBlock(ModBlocks.QUANTUM_ORE_EXTRACTOR.get(), 3))
            .addElement('C', ofBlock(ModBlocks.LASER_CORE.get(), 0))
            .addElement(
                'P',
                ofChain(
                    ofBlock(ModBlocks.MACHINE_BASE.get(), 0),
                    ofBlock(ModBlocks.MACHINE_BASE.get(), 1),
                    ofBlock(ModBlocks.MACHINE_BASE.get(), 2)))
            .addElement(
                'L',
                ofChain(
                    ofBlockAdderWithPos(TEQuantumExtractor::addToMachine, ModBlocks.LENS.get(), 0),
                    ofBlock(ModBlocks.LENS.get(), 0),
                    ofBlockAnyMeta(ModBlocks.COLORED_LENS.get(), 0)))
            .addElement(
                'A',
                ofChain(
                    ofBlockAdderWithPos(TEQuantumExtractor::addToMachine, ModBlocks.MODIFIER_NULL.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_NULL.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_ACCURACY.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_SPEED.get(), 0)))
            .addElement(
                'F',
                ofChain(
                    ofBlock(ModBlocks.BASALT_STRUCTURE.get(), 3),
                    ofBlock(ModBlocks.HARDENED_STRUCTURE.get(), 3),
                    ofBlock(ModBlocks.ALABASTER_STRUCTURE.get(), 3)));

        builder5.addShape(STRUCTURE_TIER_5, transpose(SHAPE_TIER_5))
            .addElement('Q', ofBlock(ModBlocks.QUANTUM_ORE_EXTRACTOR.get(), 4))
            .addElement('C', ofBlock(ModBlocks.LASER_CORE.get(), 0))
            .addElement(
                'P',
                ofChain(
                    ofBlock(ModBlocks.MACHINE_BASE.get(), 0),
                    ofBlock(ModBlocks.MACHINE_BASE.get(), 1),
                    ofBlock(ModBlocks.MACHINE_BASE.get(), 2)))
            .addElement(
                'L',
                ofChain(
                    ofBlockAdderWithPos(TEQuantumExtractor::addToMachine, ModBlocks.LENS.get(), 0),
                    ofBlock(ModBlocks.LENS.get(), 0),
                    ofBlockAnyMeta(ModBlocks.COLORED_LENS.get(), 0)))
            .addElement(
                'A',
                ofChain(
                    ofBlockAdderWithPos(TEQuantumExtractor::addToMachine, ModBlocks.MODIFIER_NULL.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_NULL.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_ACCURACY.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_SPEED.get(), 0)))
            .addElement(
                'F',
                ofChain(
                    ofBlock(ModBlocks.BASALT_STRUCTURE.get(), 4),
                    ofBlock(ModBlocks.HARDENED_STRUCTURE.get(), 4),
                    ofBlock(ModBlocks.ALABASTER_STRUCTURE.get(), 4)));

        builder6.addShape(STRUCTURE_TIER_6, transpose(SHAPE_TIER_6))
            .addElement('Q', ofBlock(ModBlocks.QUANTUM_ORE_EXTRACTOR.get(), 5))
            .addElement('C', ofBlock(ModBlocks.LASER_CORE.get(), 0))
            .addElement(
                'P',
                ofChain(
                    ofBlock(ModBlocks.MACHINE_BASE.get(), 0),
                    ofBlock(ModBlocks.MACHINE_BASE.get(), 1),
                    ofBlock(ModBlocks.MACHINE_BASE.get(), 2)))
            .addElement(
                'L',
                ofChain(
                    ofBlockAdderWithPos(TEQuantumExtractor::addToMachine, ModBlocks.LENS.get(), 0),
                    ofBlock(ModBlocks.LENS.get(), 0),
                    ofBlockAnyMeta(ModBlocks.COLORED_LENS.get(), 0)))
            .addElement(
                'A',
                ofChain(
                    ofBlockAdderWithPos(TEQuantumExtractor::addToMachine, ModBlocks.MODIFIER_NULL.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_NULL.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_ACCURACY.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_SPEED.get(), 0)))
            .addElement(
                'F',
                ofChain(
                    ofBlock(ModBlocks.BASALT_STRUCTURE.get(), 5),
                    ofBlock(ModBlocks.HARDENED_STRUCTURE.get(), 5),
                    ofBlock(ModBlocks.ALABASTER_STRUCTURE.get(), 5)));

        IStructureDefinition<TEQuantumOreExtractorT1> definition1 = builder1.build();
        STRUCTURE_DEFINITION_TIER_1 = definition1;
        IStructureDefinition<TEQuantumOreExtractorT2> definition2 = builder2.build();
        STRUCTURE_DEFINITION_TIER_2 = definition2;
        IStructureDefinition<TEQuantumOreExtractorT3> definition3 = builder3.build();
        STRUCTURE_DEFINITION_TIER_3 = definition3;
        IStructureDefinition<TEQuantumOreExtractorT4> definition4 = builder4.build();
        STRUCTURE_DEFINITION_TIER_4 = definition4;
        IStructureDefinition<TEQuantumOreExtractorT5> definition5 = builder5.build();
        STRUCTURE_DEFINITION_TIER_5 = definition5;
        IStructureDefinition<TEQuantumOreExtractorT6> definition6 = builder6.build();
        STRUCTURE_DEFINITION_TIER_6 = definition6;

        IMultiblockInfoContainer
            .registerTileClass(TEQuantumOreExtractorT1.class, new MultiblockInfoContainerT1(definition1));
        IMultiblockInfoContainer
            .registerTileClass(TEQuantumOreExtractorT2.class, new MultiblockInfoContainerT2(definition2));
        IMultiblockInfoContainer
            .registerTileClass(TEQuantumOreExtractorT3.class, new MultiblockInfoContainerT3(definition3));
        IMultiblockInfoContainer
            .registerTileClass(TEQuantumOreExtractorT4.class, new MultiblockInfoContainerT4(definition4));
        IMultiblockInfoContainer
            .registerTileClass(TEQuantumOreExtractorT5.class, new MultiblockInfoContainerT5(definition5));
        IMultiblockInfoContainer
            .registerTileClass(TEQuantumOreExtractorT6.class, new MultiblockInfoContainerT6(definition6));

        Logger.info("Register Quantum Ore Extractor Structure Info");
    }

    private static class MultiblockInfoContainerT1 implements IMultiblockInfoContainer<TEQuantumOreExtractorT1> {

        private final IStructureDefinition<TEQuantumOreExtractorT1> structure;

        public MultiblockInfoContainerT1(IStructureDefinition<TEQuantumOreExtractorT1> structure) {
            this.structure = structure;
        }

        @Override
        public void construct(ItemStack triggerStack, boolean hintsOnly, TEQuantumOreExtractorT1 ctx,
            ExtendedFacing aSide) {
            int tier = ctx.getTier();

            this.structure.buildOrHints(
                ctx,
                triggerStack,
                ctx.getStructurePieceName(),
                ctx.getWorldObj(),
                ExtendedFacing.DEFAULT,
                ctx.xCoord,
                ctx.yCoord,
                ctx.zCoord,
                TIER_OFFSET[tier - 1][0],
                TIER_OFFSET[tier - 1][1],
                TIER_OFFSET[tier - 1][2],
                hintsOnly);
        }

        @Override
        public int survivalConstruct(ItemStack triggerStack, int elementBudget, ISurvivalBuildEnvironment env,
            TEQuantumOreExtractorT1 ctx, ExtendedFacing aSide) {

            int built = 0;
            int tier = ctx.getTier();
            built = this.structure.survivalBuild(
                ctx,
                triggerStack,
                ctx.getStructurePieceName(),
                ctx.getWorldObj(),
                ExtendedFacing.DEFAULT,
                ctx.xCoord,
                ctx.yCoord,
                ctx.zCoord,
                TIER_OFFSET[tier - 1][0],
                TIER_OFFSET[tier - 1][1],
                TIER_OFFSET[tier - 1][2],
                elementBudget,
                env,
                false);

            return built;
        }

        @Override
        public String[] getDescription(ItemStack stackSize) {
            return new String[0];
        }
    }

    private static class MultiblockInfoContainerT2 implements IMultiblockInfoContainer<TEQuantumOreExtractorT2> {

        private final IStructureDefinition<TEQuantumOreExtractorT2> structure;

        public MultiblockInfoContainerT2(IStructureDefinition<TEQuantumOreExtractorT2> structure) {
            this.structure = structure;
        }

        @Override
        public void construct(ItemStack triggerStack, boolean hintsOnly, TEQuantumOreExtractorT2 ctx,
            ExtendedFacing aSide) {
            int tier = ctx.getTier();

            this.structure.buildOrHints(
                ctx,
                triggerStack,
                ctx.getStructurePieceName(),
                ctx.getWorldObj(),
                ExtendedFacing.DEFAULT,
                ctx.xCoord,
                ctx.yCoord,
                ctx.zCoord,
                TIER_OFFSET[tier - 1][0],
                TIER_OFFSET[tier - 1][1],
                TIER_OFFSET[tier - 1][2],
                hintsOnly);
        }

        @Override
        public int survivalConstruct(ItemStack triggerStack, int elementBudget, ISurvivalBuildEnvironment env,
            TEQuantumOreExtractorT2 ctx, ExtendedFacing aSide) {

            int built = 0;
            int tier = ctx.getTier();
            built = this.structure.survivalBuild(
                ctx,
                triggerStack,
                ctx.getStructurePieceName(),
                ctx.getWorldObj(),
                ExtendedFacing.DEFAULT,
                ctx.xCoord,
                ctx.yCoord,
                ctx.zCoord,
                TIER_OFFSET[tier - 1][0],
                TIER_OFFSET[tier - 1][1],
                TIER_OFFSET[tier - 1][2],
                elementBudget,
                env,
                false);

            return built;
        }

        @Override
        public String[] getDescription(ItemStack stackSize) {
            return new String[0];
        }
    }

    private static class MultiblockInfoContainerT3 implements IMultiblockInfoContainer<TEQuantumOreExtractorT3> {

        private final IStructureDefinition<TEQuantumOreExtractorT3> structure;

        public MultiblockInfoContainerT3(IStructureDefinition<TEQuantumOreExtractorT3> structure) {
            this.structure = structure;
        }

        @Override
        public void construct(ItemStack triggerStack, boolean hintsOnly, TEQuantumOreExtractorT3 ctx,
            ExtendedFacing aSide) {
            int tier = ctx.getTier();

            this.structure.buildOrHints(
                ctx,
                triggerStack,
                ctx.getStructurePieceName(),
                ctx.getWorldObj(),
                ExtendedFacing.DEFAULT,
                ctx.xCoord,
                ctx.yCoord,
                ctx.zCoord,
                TIER_OFFSET[tier - 1][0],
                TIER_OFFSET[tier - 1][1],
                TIER_OFFSET[tier - 1][2],
                hintsOnly);
        }

        @Override
        public int survivalConstruct(ItemStack triggerStack, int elementBudget, ISurvivalBuildEnvironment env,
            TEQuantumOreExtractorT3 ctx, ExtendedFacing aSide) {

            int built = 0;
            int tier = ctx.getTier();
            built = this.structure.survivalBuild(
                ctx,
                triggerStack,
                ctx.getStructurePieceName(),
                ctx.getWorldObj(),
                ExtendedFacing.DEFAULT,
                ctx.xCoord,
                ctx.yCoord,
                ctx.zCoord,
                TIER_OFFSET[tier - 1][0],
                TIER_OFFSET[tier - 1][1],
                TIER_OFFSET[tier - 1][2],
                elementBudget,
                env,
                false);

            return built;
        }

        @Override
        public String[] getDescription(ItemStack stackSize) {
            return new String[0];
        }
    }

    private static class MultiblockInfoContainerT4 implements IMultiblockInfoContainer<TEQuantumOreExtractorT4> {

        private final IStructureDefinition<TEQuantumOreExtractorT4> structure;

        private MultiblockInfoContainerT4(IStructureDefinition<TEQuantumOreExtractorT4> structure) {
            this.structure = structure;
        }

        @Override
        public void construct(ItemStack triggerStack, boolean hintsOnly, TEQuantumOreExtractorT4 ctx,
            ExtendedFacing aSide) {
            int tier = ctx.getTier();

            this.structure.buildOrHints(
                ctx,
                triggerStack,
                ctx.getStructurePieceName(),
                ctx.getWorldObj(),
                ExtendedFacing.DEFAULT,
                ctx.xCoord,
                ctx.yCoord,
                ctx.zCoord,
                TIER_OFFSET[tier - 1][0],
                TIER_OFFSET[tier - 1][1],
                TIER_OFFSET[tier - 1][2],
                hintsOnly);
        }

        @Override
        public int survivalConstruct(ItemStack triggerStack, int elementBudget, ISurvivalBuildEnvironment env,
            TEQuantumOreExtractorT4 ctx, ExtendedFacing aSide) {

            int built = 0;
            int tier = ctx.getTier();
            built = this.structure.survivalBuild(
                ctx,
                triggerStack,

                ctx.getStructurePieceName(),
                ctx.getWorldObj(),
                ExtendedFacing.DEFAULT,
                ctx.xCoord,
                ctx.yCoord,
                ctx.zCoord,
                TIER_OFFSET[tier - 1][0],
                TIER_OFFSET[tier - 1][1],
                TIER_OFFSET[tier - 1][2],
                elementBudget,
                env,
                false);

            return built;
        }

        @Override
        public String[] getDescription(ItemStack stackSize) {
            return new String[0];
        }
    }

    private static class MultiblockInfoContainerT5 implements IMultiblockInfoContainer<TEQuantumOreExtractorT5> {

        private final IStructureDefinition<TEQuantumOreExtractorT5> structure;

        private MultiblockInfoContainerT5(IStructureDefinition<TEQuantumOreExtractorT5> structure) {
            this.structure = structure;
        }

        @Override
        public void construct(ItemStack triggerStack, boolean hintsOnly, TEQuantumOreExtractorT5 ctx,
            ExtendedFacing aSide) {
            int tier = ctx.getTier();

            this.structure.buildOrHints(
                ctx,
                triggerStack,
                ctx.getStructurePieceName(),
                ctx.getWorldObj(),
                ExtendedFacing.DEFAULT,
                ctx.xCoord,
                ctx.yCoord,
                ctx.zCoord,
                TIER_OFFSET[tier - 1][0],
                TIER_OFFSET[tier - 1][1],
                TIER_OFFSET[tier - 1][2],
                hintsOnly);
        }

        @Override
        public int survivalConstruct(ItemStack triggerStack, int elementBudget, ISurvivalBuildEnvironment env,
            TEQuantumOreExtractorT5 ctx, ExtendedFacing aSide) {

            int built = 0;
            int tier = ctx.getTier();
            built = this.structure.survivalBuild(
                ctx,
                triggerStack,

                ctx.getStructurePieceName(),
                ctx.getWorldObj(),
                ExtendedFacing.DEFAULT,
                ctx.xCoord,
                ctx.yCoord,
                ctx.zCoord,
                TIER_OFFSET[tier - 1][0],
                TIER_OFFSET[tier - 1][1],
                TIER_OFFSET[tier - 1][2],
                elementBudget,
                env,
                false);

            return built;
        }

        @Override
        public String[] getDescription(ItemStack stackSize) {
            return new String[0];
        }
    }

    private static class MultiblockInfoContainerT6 implements IMultiblockInfoContainer<TEQuantumOreExtractorT6> {

        private final IStructureDefinition<TEQuantumOreExtractorT6> structure;

        private MultiblockInfoContainerT6(IStructureDefinition<TEQuantumOreExtractorT6> structure) {
            this.structure = structure;
        }

        @Override
        public void construct(ItemStack triggerStack, boolean hintsOnly, TEQuantumOreExtractorT6 ctx,
            ExtendedFacing aSide) {
            int tier = ctx.getTier();

            this.structure.buildOrHints(
                ctx,
                triggerStack,
                ctx.getStructurePieceName(),
                ctx.getWorldObj(),
                ExtendedFacing.DEFAULT,
                ctx.xCoord,
                ctx.yCoord,
                ctx.zCoord,
                TIER_OFFSET[tier - 1][0],
                TIER_OFFSET[tier - 1][1],
                TIER_OFFSET[tier - 1][2],
                hintsOnly);
        }

        @Override
        public int survivalConstruct(ItemStack triggerStack, int elementBudget, ISurvivalBuildEnvironment env,
            TEQuantumOreExtractorT6 ctx, ExtendedFacing aSide) {

            int built = 0;
            int tier = ctx.getTier();
            built = this.structure.survivalBuild(
                ctx,
                triggerStack,

                ctx.getStructurePieceName(),
                ctx.getWorldObj(),
                ExtendedFacing.DEFAULT,
                ctx.xCoord,
                ctx.yCoord,
                ctx.zCoord,
                TIER_OFFSET[tier - 1][0],
                TIER_OFFSET[tier - 1][1],
                TIER_OFFSET[tier - 1][2],
                elementBudget,
                env,
                false);

            return built;
        }

        @Override
        public String[] getDescription(ItemStack stackSize) {
            return new String[0];
        }
    }
}
