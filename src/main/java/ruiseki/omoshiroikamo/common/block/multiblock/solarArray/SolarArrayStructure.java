package ruiseki.omoshiroikamo.common.block.multiblock.solarArray;

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

import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.util.Logger;

public class SolarArrayStructure {

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
    // spotless:on

    public static final int[][] TIER_OFFSET = { { 2, 1, 2 }, { 3, 1, 3 }, { 4, 1, 4 }, { 5, 1, 5 } };
    public static IStructureDefinition<TESolarArrayT1> STRUCTURE_DEFINITION_TIER_1;
    public static IStructureDefinition<TESolarArrayT2> STRUCTURE_DEFINITION_TIER_2;
    public static IStructureDefinition<TESolarArrayT3> STRUCTURE_DEFINITION_TIER_3;
    public static IStructureDefinition<TESolarArrayT4> STRUCTURE_DEFINITION_TIER_4;

    @SuppressWarnings("unchecked")
    public static void registerStructureInfo() {
        StructureDefinition.Builder<TESolarArrayT1> builder1 = StructureDefinition.builder();
        StructureDefinition.Builder<TESolarArrayT2> builder2 = StructureDefinition.builder();
        StructureDefinition.Builder<TESolarArrayT3> builder3 = StructureDefinition.builder();
        StructureDefinition.Builder<TESolarArrayT4> builder4 = StructureDefinition.builder();

        builder1.addShape(STRUCTURE_TIER_1, transpose(SHAPE_TIER_1))
            .addElement('Q', ofBlock(ModBlocks.SOLAR_ARRAY.get(), 0))
            .addElement('G', ofBlockAnyMeta(ModBlocks.SOLAR_CELL.get(), 0))
            .addElement(
                'A',
                ofChain(
                    ofBlockAdderWithPos(TESolarArray::addToMachine, ModBlocks.MODIFIER_NULL.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_NULL.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_PIEZO.get(), 0)))
            .addElement(
                'F',
                ofChain(
                    ofBlock(ModBlocks.BASALT_STRUCTURE.get(), 0),
                    ofBlock(ModBlocks.HARDENED_STRUCTURE.get(), 0),
                    ofBlock(ModBlocks.ALABASTER_STRUCTURE.get(), 0)));

        builder2.addShape(STRUCTURE_TIER_2, transpose(SHAPE_TIER_2))
            .addElement('Q', ofBlock(ModBlocks.SOLAR_ARRAY.get(), 1))
            .addElement('G', ofBlockAnyMeta(ModBlocks.SOLAR_CELL.get(), 0))
            .addElement(
                'A',
                ofChain(
                    ofBlockAdderWithPos(TESolarArray::addToMachine, ModBlocks.MODIFIER_NULL.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_NULL.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_PIEZO.get(), 0)))
            .addElement(
                'F',
                ofChain(
                    ofBlock(ModBlocks.BASALT_STRUCTURE.get(), 1),
                    ofBlock(ModBlocks.HARDENED_STRUCTURE.get(), 1),
                    ofBlock(ModBlocks.ALABASTER_STRUCTURE.get(), 1)));

        builder3.addShape(STRUCTURE_TIER_3, transpose(SHAPE_TIER_3))
            .addElement('Q', ofBlock(ModBlocks.SOLAR_ARRAY.get(), 2))
            .addElement('G', ofBlockAnyMeta(ModBlocks.SOLAR_CELL.get(), 0))
            .addElement(
                'A',
                ofChain(
                    ofBlockAdderWithPos(TESolarArray::addToMachine, ModBlocks.MODIFIER_NULL.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_NULL.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_PIEZO.get(), 0)))
            .addElement(
                'F',
                ofChain(
                    ofBlock(ModBlocks.BASALT_STRUCTURE.get(), 2),
                    ofBlock(ModBlocks.HARDENED_STRUCTURE.get(), 2),
                    ofBlock(ModBlocks.ALABASTER_STRUCTURE.get(), 2)));

        builder4.addShape(STRUCTURE_TIER_4, transpose(SHAPE_TIER_4))
            .addElement('Q', ofBlock(ModBlocks.SOLAR_ARRAY.get(), 3))
            .addElement('G', ofBlockAnyMeta(ModBlocks.SOLAR_CELL.get(), 0))
            .addElement(
                'A',
                ofChain(
                    ofBlockAdderWithPos(TESolarArray::addToMachine, ModBlocks.MODIFIER_NULL.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_NULL.get(), 0),
                    ofBlock(ModBlocks.MODIFIER_PIEZO.get(), 0)))
            .addElement(
                'F',
                ofChain(
                    ofBlock(ModBlocks.BASALT_STRUCTURE.get(), 3),
                    ofBlock(ModBlocks.HARDENED_STRUCTURE.get(), 3),
                    ofBlock(ModBlocks.ALABASTER_STRUCTURE.get(), 3)));

        IStructureDefinition<TESolarArrayT1> definition1 = builder1.build();
        STRUCTURE_DEFINITION_TIER_1 = definition1;
        IStructureDefinition<TESolarArrayT2> definition2 = builder2.build();
        STRUCTURE_DEFINITION_TIER_2 = definition2;
        IStructureDefinition<TESolarArrayT3> definition3 = builder3.build();
        STRUCTURE_DEFINITION_TIER_3 = definition3;
        IStructureDefinition<TESolarArrayT4> definition4 = builder4.build();
        STRUCTURE_DEFINITION_TIER_4 = definition4;

        IMultiblockInfoContainer.registerTileClass(TESolarArrayT1.class, new MultiblockInfoContainerT1(definition1));
        IMultiblockInfoContainer.registerTileClass(TESolarArrayT2.class, new MultiblockInfoContainerT2(definition2));
        IMultiblockInfoContainer.registerTileClass(TESolarArrayT3.class, new MultiblockInfoContainerT3(definition3));
        IMultiblockInfoContainer.registerTileClass(TESolarArrayT4.class, new MultiblockInfoContainerT4(definition4));

        Logger.info("Register Solar Array Structure Info");
    }

    private static class MultiblockInfoContainerT1 implements IMultiblockInfoContainer<TESolarArrayT1> {

        private final IStructureDefinition<TESolarArrayT1> structure;

        public MultiblockInfoContainerT1(IStructureDefinition<TESolarArrayT1> structure) {
            this.structure = structure;
        }

        @Override
        public void construct(ItemStack triggerStack, boolean hintsOnly, TESolarArrayT1 ctx, ExtendedFacing aSide) {
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
            TESolarArrayT1 ctx, ExtendedFacing aSide) {
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

    private static class MultiblockInfoContainerT2 implements IMultiblockInfoContainer<TESolarArrayT2> {

        private final IStructureDefinition<TESolarArrayT2> structure;

        public MultiblockInfoContainerT2(IStructureDefinition<TESolarArrayT2> structure) {
            this.structure = structure;
        }

        @Override
        public void construct(ItemStack triggerStack, boolean hintsOnly, TESolarArrayT2 ctx, ExtendedFacing aSide) {
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
            TESolarArrayT2 ctx, ExtendedFacing aSide) {
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

    private static class MultiblockInfoContainerT3 implements IMultiblockInfoContainer<TESolarArrayT3> {

        private final IStructureDefinition<TESolarArrayT3> structure;

        public MultiblockInfoContainerT3(IStructureDefinition<TESolarArrayT3> structure) {
            this.structure = structure;
        }

        @Override
        public void construct(ItemStack triggerStack, boolean hintsOnly, TESolarArrayT3 ctx, ExtendedFacing aSide) {
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
            TESolarArrayT3 ctx, ExtendedFacing aSide) {
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

    private static class MultiblockInfoContainerT4 implements IMultiblockInfoContainer<TESolarArrayT4> {

        private final IStructureDefinition<TESolarArrayT4> structure;

        public MultiblockInfoContainerT4(IStructureDefinition<TESolarArrayT4> structure) {
            this.structure = structure;
        }

        @Override
        public void construct(ItemStack triggerStack, boolean hintsOnly, TESolarArrayT4 ctx, ExtendedFacing aSide) {
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
            TESolarArrayT4 ctx, ExtendedFacing aSide) {
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
