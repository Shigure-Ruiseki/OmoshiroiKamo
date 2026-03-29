package ruiseki.omoshiroikamo.api.recipe.io;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.recipe.context.IRecipeContext;
import ruiseki.omoshiroikamo.api.recipe.core.IMachineState;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.test.RegistryMocker;

/**
 * ItemInput における動的数量・メタデータ評価のテスト
 */
@DisplayName("ItemInput 動的評価テスト")
public class ItemInputDynamicTest {

    @BeforeAll
    public static void setUpAll() {
        RegistryMocker.mockAll();
    }

    @Test
    @DisplayName("式によるアイテム数量の動的評価テスト")
    public void testDynamicAmount() {
        JsonObject json = new JsonObject();
        json.addProperty("item", "minecraft:iron_ingot");
        json.addProperty("amount", "tier * 2");

        ItemInput input = ItemInput.fromJson(json);

        // Mock context with tier = 3
        ConditionContext context = createMockContext(3);
        assertEquals(6, input.getRequiredAmount(context));

        // Mock context with tier = 5
        context = createMockContext(5);
        assertEquals(10, input.getRequiredAmount(context));
    }

    @Test
    @DisplayName("式によるメタデータの動的評価テスト")
    public void testDynamicMeta() {
        JsonObject json = new JsonObject();
        json.addProperty("item", "minecraft:dye");
        json.addProperty("meta", "tier");

        ItemInput input = ItemInput.fromJson(json);

        // Test with tier 4 (Yellow dye)
        ConditionContext context = createMockContext(4);
        assertTrue(input.stacksMatch(new ItemStack(Items.dye, 1, 4), context));
        assertFalse(input.stacksMatch(new ItemStack(Items.dye, 1, 0), context));

        // Test with tier 1 (Red dye)
        context = createMockContext(1);
        assertTrue(input.stacksMatch(new ItemStack(Items.dye, 1, 1), context));
        assertFalse(input.stacksMatch(new ItemStack(Items.dye, 1, 4), context));
    }

    private ConditionContext createMockContext(int tier) {
        IMachineState state = new MockMachineState(tier);
        IRecipeContext recipeContext = new MockRecipeContext(state);
        return new ConditionContext(null, 0, 0, 0, recipeContext);
    }

    private static class MockMachineState implements IMachineState {

        private final int tier;

        public MockMachineState(int tier) {
            this.tier = tier;
        }

        @Override
        public int getTier() {
            return tier;
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
            return true;
        }

        @Override
        public boolean isWaitingForOutput() {
            return false;
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
    }

    private static class MockRecipeContext implements IRecipeContext {

        private final IMachineState state;

        public MockRecipeContext(IMachineState state) {
            this.state = state;
        }

        @Override
        public long getRecipeStartTick() {
            return 0;
        }

        @Override
        public int getRedstoneLevel() {
            return 0;
        }

        @Override
        public IMachineState getMachineState() {
            return state;
        }

        @Override
        public World getWorld() {
            return null;
        }

        @Override
        public ChunkCoordinates getControllerPos() {
            return null;
        }

        @Override
        public IStructureEntry getCurrentStructure() {
            return null;
        }

        @Override
        public ForgeDirection getFacing() {
            return null;
        }

        @Override
        public List<ChunkCoordinates> getSymbolPositions(char symbol) {
            return new ArrayList<>();
        }

        @Override
        public ConditionContext getConditionContext() {
            return new ConditionContext(null, 0, 0, 0, this);
        }
    }
}
