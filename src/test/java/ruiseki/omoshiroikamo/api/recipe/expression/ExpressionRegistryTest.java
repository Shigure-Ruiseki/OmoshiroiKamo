package ruiseki.omoshiroikamo.api.recipe.expression;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.context.IRecipeContext;
import ruiseki.omoshiroikamo.api.recipe.core.IMachineState;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.test.RegistryMocker;

/**
 * ExpressionRegistry に登録された新しい変数と関数の動作を検証するテスト
 */
@DisplayName("ExpressionRegistry 新規変数・関数テスト")
public class ExpressionRegistryTest {

    @BeforeAll
    public static void setUpAll() {
        RegistryMocker.mockAll();
    }

    @Test
    @DisplayName("リソース合計変数 (total_*) の評価テスト")
    public void testTotalResourceVariables() {
        MockMachineState state = new MockMachineState();
        state.energy = 1000;
        state.fluid = 500;
        state.gas = 100;
        state.essentia = 50;

        MockRecipeContext recipeContext = new MockRecipeContext(state);
        ConditionContext context = recipeContext.getConditionContext();

        assertEquals(1000.0, evaluate("total_energy", context));
        assertEquals(1000.0, evaluate("energy", context));
        assertEquals(500.0, evaluate("total_fluid", context));
        assertEquals(100.0, evaluate("gas('hydrogen')", context), 0.001);
        assertEquals(500.0, evaluate("fluid('water')", context), 0.001);
        assertEquals(50.0, evaluate("essentia('ignis')", context), 0.001);
    }

    @Test
    @DisplayName("リソース空き容量変数 (*_f) の評価テスト")
    public void testFreeResourceVariables() {
        MockMachineState state = new MockMachineState();
        state.energy = 400;
        state.maxEnergy = 1000;
        state.fluid = 100;
        state.maxFluid = 500;

        MockRecipeContext recipeContext = new MockRecipeContext(state);
        ConditionContext context = recipeContext.getConditionContext();

        assertEquals(600.0, evaluate("energy_f", context));
        assertEquals(400.0, evaluate("fluid_f", context));
    }

    @Test
    @DisplayName("リソース割合変数 (*_p) の評価テスト")
    public void testPathResourceVariables() {
        MockMachineState state = new MockMachineState();
        state.energy = 250;
        state.maxEnergy = 1000;

        MockRecipeContext recipeContext = new MockRecipeContext(state);
        ConditionContext context = recipeContext.getConditionContext();

        assertEquals(0.25, evaluate("energy_p", context), 0.001);
        assertEquals(0.25, evaluate("energy_percent", context), 0.001);
    }

    @Test
    @DisplayName("統計変数 (count_*) の評価テスト")
    public void testStatisticVariables() {
        MockMachineState state = new MockMachineState();
        state.recipeCount = 42;

        MockRecipeContext recipeContext = new MockRecipeContext(state);
        ConditionContext context = recipeContext.getConditionContext();

        assertEquals(42.0, evaluate("count_recipe", context));
        assertEquals(42.0, evaluate("recipeprocessed", context));
    }

    private double evaluate(String expression, ConditionContext context) {
        return ExpressionParser.parseExpression(expression)
            .evaluate(context)
            .asDouble();
    }

    /**
     * テスト用の IRecipeContext 実装
     */
    private static class MockRecipeContext implements IRecipeContext {

        private final MockMachineState state;

        public MockRecipeContext(MockMachineState state) {
            this.state = state;
        }

        @Override
        public World getWorld() {
            return null;
        }

        @Override
        public ChunkCoordinates getControllerPos() {
            return new ChunkCoordinates(0, 0, 0);
        }

        @Override
        public IStructureEntry getCurrentStructure() {
            return null;
        }

        @Override
        public ForgeDirection getFacing() {
            return ForgeDirection.NORTH;
        }

        @Override
        public List<ChunkCoordinates> getSymbolPositions(char symbol) {
            return new ArrayList<>();
        }

        @Override
        public ConditionContext getConditionContext() {
            return new ConditionContext(null, 0, 0, 0, this);
        }

        @Override
        public IMachineState getMachineState() {
            return state;
        }
    }

    /**
     * テスト用の IMachineState 実装
     */
    private static class MockMachineState implements IMachineState {

        long energy, maxEnergy;
        long fluid, maxFluid;
        long gas, maxGas;
        long mana, maxMana;
        double vis, maxVis;
        long essentia, maxEssentia;
        int recipeCount;

        @Override
        public long getStoredEnergy() {
            return energy;
        }

        @Override
        public long getEnergyCapacity() {
            return maxEnergy;
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
            return true;
        }

        @Override
        public boolean isWaitingForOutput() {
            return false;
        }

        @Override
        public int getTier() {
            return 1;
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
            return recipeCount;
        }

        @Override
        public int getRecipeProcessedTypesCount() {
            return 0;
        }

        @Override
        public long getStoredFluid() {
            return fluid;
        }

        @Override
        public long getFluidCapacity() {
            return maxFluid;
        }

        public long getStoredFluid(String name) {
            return "water".equals(name) ? 500 : 0;
        }

        @Override
        public long getStoredMana() {
            return mana;
        }

        @Override
        public long getManaCapacity() {
            return maxMana;
        }

        @Override
        public long getStoredGas(String name) {
            return gas;
        }

        @Override
        public long getTotalStoredGas() {
            return gas;
        }

        @Override
        public long getGasCapacity() {
            return maxGas;
        }

        @Override
        public long getStoredEssentia(String aspect) {
            return essentia;
        }

        @Override
        public long getEssentiaCapacity() {
            return maxEssentia;
        }

        @Override
        public long getStoredVis(String aspect) {
            return (long) vis;
        }

        @Override
        public long getVisCapacity() {
            return (long) maxVis;
        }

        @Override
        public int getBatchSize() {
            return 1;
        }

        @Override
        public double getSpeedMultiplier() {
            return 1.0;
        }

        @Override
        public double getEnergyMultiplier() {
            return 1.0;
        }

        @Override
        public long getFluidInput(String name) {
            return getStoredFluid(name);
        }

        @Override
        public long getFluidOutputSpace(String name) {
            return 0;
        }

        @Override
        public long getItemCount(IPortType.Direction direction, String itemName) {
            return 0;
        }

        @Override
        public long getItemSpace(IPortType.Direction direction, String itemName) {
            return 0;
        }

        @Override
        public long getRecipeStartTick() {
            return 0;
        }
    }
}
