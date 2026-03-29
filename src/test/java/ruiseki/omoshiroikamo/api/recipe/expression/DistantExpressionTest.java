package ruiseki.omoshiroikamo.api.recipe.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.context.IRecipeContext;
import ruiseki.omoshiroikamo.api.recipe.core.IMachineState;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.test.RegistryMocker;

public class DistantExpressionTest {

    private static World world;
    private ConditionContext context;
    private MockMachineState machineState;
    private MockRecipeContext recipeContext;

    @BeforeAll
    static void setupAll() {
        RegistryMocker.mockAll();
    }

    @BeforeEach
    void setup() {
        world = new VisionFunctionExpressionTest.WorldStub();
        machineState = new MockMachineState();
        recipeContext = new MockRecipeContext(world, machineState);
        context = new ConditionContext(world, 10, 10, 10, recipeContext, 12345L); // 12345 is the random_seed
    }

    @Test
    void testFacing() {
        recipeContext.setFacing(ForgeDirection.SOUTH); // 3
        WorldPropertyExpression expr = new WorldPropertyExpression("facing");
        assertEquals(3.0, expr.evaluate(context));

        recipeContext.setFacing(ForgeDirection.UP); // 1
        assertEquals(1.0, expr.evaluate(context));
    }

    @Test
    void testWorldSeed() {
        // WorldStub.getSeed() returns 0 by default, but let's check
        WorldPropertyExpression expr = new WorldPropertyExpression("world_seed");
        assertEquals((double) world.getSeed(), expr.evaluate(context));
    }

    @Test
    void testRandomSeed() {
        // Renamed from "seed"
        WorldPropertyExpression expr = new WorldPropertyExpression("random_seed");
        assertEquals(12345.0, expr.evaluate(context));
    }

    @Test
    void testCountBlocks() {
        // Setup blocks around (10, 10, 10)
        ((VisionFunctionExpressionTest.WorldStub) world).setTestBlock(11, 10, 10, Blocks.stone);
        ((VisionFunctionExpressionTest.WorldStub) world).setTestBlock(9, 10, 10, Blocks.stone);
        ((VisionFunctionExpressionTest.WorldStub) world).setTestBlock(10, 11, 10, Blocks.glass);

        // distance 1, all blocks (non-air) -> 3
        CountBlocksFunctionExpression all = new CountBlocksFunctionExpression(Arrays.asList(new ConstantExpression(1)));
        assertEquals(3.0, all.evaluate(context));

        // distance 1, only stone -> 2
        CountBlocksFunctionExpression stoneOnly = new CountBlocksFunctionExpression(
            Arrays.asList(new ConstantExpression(1), new StringLiteralExpression("minecraft:stone")));
        assertEquals(2.0, stoneOnly.evaluate(context));
    }

    @Test
    void testItemCount() {
        machineState.setItemCount(100);
        // Unified function: item("id")
        ResourceFunctionExpression expr = new ResourceFunctionExpression(
            ResourceFunctionExpression.Type.ITEM,
            new StringLiteralExpression("minecraft:iron_ingot"));
        assertEquals(100.0, expr.evaluate(context));

        // Machine property: item
        MachinePropertyExpression prop = new MachinePropertyExpression("item");
        assertEquals(100.0, prop.evaluate(context));
    }

    @Test
    void testItemSpace() {
        machineState.setItemSpace(10);
        // Unified function: item_f("id")
        ResourceFunctionExpression expr = new ResourceFunctionExpression(
            ResourceFunctionExpression.Type.ITEM_F,
            new StringLiteralExpression("minecraft:iron_ingot"));
        assertEquals(10.0, expr.evaluate(context));

        // Machine property: item_f
        MachinePropertyExpression prop = new MachinePropertyExpression("item_f");
        assertEquals(10.0, prop.evaluate(context));
    }

    // --- Mocks ---

    private static class MockMachineState implements IMachineState {

        private long itemCount = 0;
        private long filteredItemCount = 0;
        private long itemSpace = 0;

        public void setItemCount(long count) {
            this.itemCount = count;
        }

        public void setFilteredItemCount(long count) {
            this.filteredItemCount = count;
        }

        public void setItemSpace(long space) {
            this.itemSpace = space;
        }

        @Override
        public long getStoredEnergy() {
            return 0;
        }

        @Override
        public long getEnergyCapacity() {
            return 0;
        }

        @Override
        public int getEnergyPerTick() {
            return 0;
        }

        @Override
        public double getProgressPercent() {
            return 0;
        }

        @Override
        public boolean isRunning() {
            return false;
        }

        @Override
        public boolean isWaitingForOutput() {
            return false;
        }

        @Override
        public int getTier() {
            return 0;
        }

        @Override
        public long getTimePlaced() {
            return 0;
        }

        @Override
        public long getTimeContinuous() {
            return 0;
        }

        @Override
        public int getRecipeProcessedCount() {
            return 0;
        }

        @Override
        public int getRecipeProcessedTypesCount() {
            return 0;
        }

        @Override
        public long getStoredFluid() {
            return 0;
        }

        @Override
        public long getFluidCapacity() {
            return 0;
        }

        @Override
        public long getStoredFluid(String name) {
            return 0;
        }

        @Override
        public long getStoredMana() {
            return 0;
        }

        @Override
        public long getManaCapacity() {
            return 0;
        }

        @Override
        public long getStoredGas(String name) {
            return 0;
        }

        @Override
        public long getTotalStoredGas() {
            return 0;
        }

        @Override
        public long getGasCapacity() {
            return 0;
        }

        @Override
        public long getStoredEssentia(String aspect) {
            return 0;
        }

        @Override
        public long getEssentiaCapacity() {
            return 0;
        }

        @Override
        public long getStoredVis(String aspect) {
            return 0;
        }

        @Override
        public long getVisCapacity() {
            return 0;
        }

        @Override
        public int getBatchSize() {
            return 0;
        }

        @Override
        public double getSpeedMultiplier() {
            return 0;
        }

        @Override
        public double getEnergyMultiplier() {
            return 0;
        }

        @Override
        public long getItemCount(IPortType.Direction direction, String itemName) {
            return itemCount;
        }

        @Override
        public long getItemSpace(IPortType.Direction direction, String itemName) {
            return itemSpace;
        }

        @Override
        public int getItemSlotCount(IPortType.Direction direction, boolean emptyOnly) {
            return (int) (itemSpace / 64); // Simple mock
        }
    }

    private static class MockRecipeContext implements IRecipeContext {

        private final World world;
        private final IMachineState state;
        private ForgeDirection facing = ForgeDirection.NORTH;

        public MockRecipeContext(World world, IMachineState state) {
            this.world = world;
            this.state = state;
        }

        public void setFacing(ForgeDirection facing) {
            this.facing = facing;
        }

        @Override
        public World getWorld() {
            return world;
        }

        @Override
        public ChunkCoordinates getControllerPos() {
            return new ChunkCoordinates(10, 10, 10);
        }

        @Override
        public IStructureEntry getCurrentStructure() {
            return null;
        }

        @Override
        public ForgeDirection getFacing() {
            return facing;
        }

        @Override
        public List<ChunkCoordinates> getSymbolPositions(char symbol) {
            return Collections.emptyList();
        }

        @Override
        public ConditionContext getConditionContext() {
            return null;
        }

        @Override
        public IMachineState getMachineState() {
            return state;
        }

        @Override
        public int getRedstoneLevel() {
            return 0;
        }

        @Override
        public long getRecipeStartTick() {
            return 0;
        }
    }
}
