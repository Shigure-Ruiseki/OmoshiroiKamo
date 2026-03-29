package ruiseki.omoshiroikamo.api.recipe.expression;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.util.ForgeDirection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.context.IRecipeContext;
import ruiseki.omoshiroikamo.api.recipe.core.IMachineState;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.test.RegistryMocker;

/**
 * WorldPropertyExpression の詳細テスト
 *
 * ============================================
 * ワールドプロパティ（day, time, moon等）の評価テスト
 * ============================================
 *
 * WorldPropertyExpression は、ワールドの時間や月齢を取得します：
 * - day / total_days: 経過日数
 * - time: 現在時刻 (0～23999)
 * - moon_phase: 月の満ち欠け (0～7)
 *
 * バグ発見の優先度: ★★★☆☆
 *
 * ============================================
 */
@DisplayName("WorldPropertyExpression（ワールドプロパティ）の詳細テスト")
public class WorldPropertyExpressionTest {

    @BeforeAll
    public static void setUpAll() {
        RegistryMocker.mockAll();
    }

    // ========================================
    // time プロパティのテスト
    // ========================================

    @Test
    @DisplayName("time: 0～23999の範囲内")
    public void testTime範囲() {
        World world = new WorldStubWithTime(12000); // 正午
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        WorldPropertyExpression expr = new WorldPropertyExpression("time");

        double result = expr.evaluate(context)
            .asDouble();

        assertEquals(12000.0, result, 0.001, "time = 12000 であるべき");
    }

    @Test
    @DisplayName("time: 24000以上の値でもmod 24000で正規化")
    public void testTime正規化() {
        World world = new WorldStubWithTime(26000); // 24000 + 2000
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        WorldPropertyExpression expr = new WorldPropertyExpression("time");

        double result = expr.evaluate(context)
            .asDouble();

        assertEquals(2000.0, result, 0.001, "time = 26000 % 24000 = 2000 であるべき");
    }

    @Test
    @DisplayName("time: ゼロ時刻")
    public void testTimeゼロ() {
        World world = new WorldStubWithTime(0);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        WorldPropertyExpression expr = new WorldPropertyExpression("time");

        double result = expr.evaluate(context)
            .asDouble();

        assertEquals(0.0, result, 0.001, "time = 0 であるべき");
    }

    @Test
    @DisplayName("time: 最大値23999")
    public void testTime最大() {
        World world = new WorldStubWithTime(23999);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        WorldPropertyExpression expr = new WorldPropertyExpression("time");

        double result = expr.evaluate(context)
            .asDouble();

        assertEquals(23999.0, result, 0.001, "time = 23999 であるべき");
    }

    // ========================================
    // total_days / day プロパティのテスト
    // ========================================

    @Test
    @DisplayName("total_days: 経過日数の計算")
    public void testTotalDays() {
        World world = new WorldStubWithTotalTime(48000); // 2日
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        WorldPropertyExpression expr = new WorldPropertyExpression("total_days");

        double result = expr.evaluate(context)
            .asDouble();

        assertEquals(2.0, result, 0.001, "total_days = 48000 / 24000 = 2 であるべき");
    }

    @Test
    @DisplayName("day: total_daysと同じ値を返す")
    public void testDayはTotal_daysと同じ() {
        World world = new WorldStubWithTotalTime(72000); // 3日
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        WorldPropertyExpression exprDay = new WorldPropertyExpression("day");
        WorldPropertyExpression exprTotalDays = new WorldPropertyExpression("total_days");

        double resultDay = exprDay.evaluate(context)
            .asDouble();
        double resultTotalDays = exprTotalDays.evaluate(context)
            .asDouble();

        assertEquals(resultTotalDays, resultDay, 0.001, "day と total_days は同じ値を返すべき");
    }

    @Test
    @DisplayName("total_days: ゼロ日")
    public void testTotalDaysゼロ() {
        World world = new WorldStubWithTotalTime(0);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        WorldPropertyExpression expr = new WorldPropertyExpression("total_days");

        double result = expr.evaluate(context)
            .asDouble();

        assertEquals(0.0, result, 0.001, "total_days = 0 であるべき");
    }

    @Test
    @DisplayName("total_days: 整数除算（小数点以下切り捨て）")
    public void testTotalDays整数除算() {
        World world = new WorldStubWithTotalTime(36000); // 1.5日
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        WorldPropertyExpression expr = new WorldPropertyExpression("total_days");

        double result = expr.evaluate(context)
            .asDouble();

        assertEquals(1.0, result, 0.001, "total_days = 36000 / 24000 = 1（整数除算）であるべき");
    }

    // ========================================
    // moon_phase プロパティのテスト
    // ========================================

    @Test
    @DisplayName("moon_phase: 0～7の範囲")
    public void testMoonPhase範囲() {
        World world = new WorldStubWithMoonPhase(5);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        WorldPropertyExpression expr = new WorldPropertyExpression("moon_phase");

        double result = expr.evaluate(context)
            .asDouble();

        assertEquals(5.0, result, 0.001, "moon_phase = 5 であるべき");
    }

    @Test
    @DisplayName("moon: moon_phaseのエイリアス")
    public void testMoonエイリアス() {
        World world = new WorldStubWithMoonPhase(3);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        WorldPropertyExpression exprMoon = new WorldPropertyExpression("moon");
        WorldPropertyExpression exprMoonPhase = new WorldPropertyExpression("moon_phase");

        double resultMoon = exprMoon.evaluate(context)
            .asDouble();
        double resultMoonPhase = exprMoonPhase.evaluate(context)
            .asDouble();

        assertEquals(resultMoonPhase, resultMoon, 0.001, "moon と moon_phase は同じ値を返すべき");
    }

    // ========================================
    // Null安全性のテスト
    // ========================================

    @Test
    @DisplayName("【Null安全】context=nullの場合、0を返す")
    public void testContextNull() {
        WorldPropertyExpression expr = new WorldPropertyExpression("time");

        double result = expr.evaluate(null)
            .asDouble();

        assertEquals(0.0, result, 0.001, "context=nullの場合、0を返すべき");
    }

    @Test
    @DisplayName("【Null安全】world=nullの場合、0を返す")
    public void testWorldNull() {
        ConditionContext context = new ConditionContext(null, 0, 64, 0);

        WorldPropertyExpression expr = new WorldPropertyExpression("time");

        double result = expr.evaluate(context)
            .asDouble();

        assertEquals(0.0, result, 0.001, "world=nullの場合、0を返すべき");
    }

    // ========================================
    // 未知のプロパティのテスト
    // ========================================

    @Test
    @DisplayName("【エッジ】未知のプロパティ名で0を返す")
    public void test未知のプロパティ() {
        World world = new WorldStubWithTime(12000);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        WorldPropertyExpression expr = new WorldPropertyExpression("unknown_property");

        double result = expr.evaluate(context)
            .asDouble();

        assertEquals(0.0, result, 0.001, "未知のプロパティ名の場合、0を返すべき");
    }

    @Test
    @DisplayName("tick: ワールドの総経過時間を取得")
    public void testTick() {
        World world = new WorldStubWithTotalTime(123456);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        WorldPropertyExpression expr = new WorldPropertyExpression("tick");

        assertEquals(
            123456.0,
            expr.evaluate(context)
                .asDouble(),
            0.001);
    }

    @Test
    @DisplayName("recipe_tick: レシピ開始時からの経過時間")
    public void testRecipeTick() {
        World world = new WorldStubWithTotalTime(10050);
        StubRecipeContext recipeContext = new StubRecipeContext(10000);
        ConditionContext context = new ConditionContext(world, 0, 64, 0, recipeContext);

        WorldPropertyExpression expr = new WorldPropertyExpression("recipe_tick");

        assertEquals(
            50.0,
            expr.evaluate(context)
                .asDouble(),
            0.001,
            "10050 - 10000 = 50 であるべき");
    }

    @Test
    @DisplayName("progress_tick: レシピの進捗量")
    public void testProgressTick() {
        World world = new WorldStubWithTotalTime(10000);
        StubRecipeContext recipeContext = new StubRecipeContext(9000);
        recipeContext.setMachineState(new StubMachineState(500));
        ConditionContext context = new ConditionContext(world, 0, 64, 0, recipeContext);

        WorldPropertyExpression expr = new WorldPropertyExpression("progress_tick");

        assertEquals(
            500.0,
            expr.evaluate(context)
                .asDouble(),
            0.001,
            "progress = 500 であるべき");
    }

    @Test
    @DisplayName("redstone: コントローラの信号強度")
    public void testRedstone() {
        World world = new WorldStubWithTotalTime(10000);
        StubRecipeContext recipeContext = new StubRecipeContext(9000) {

            @Override
            public int getRedstoneLevel() {
                return 12;
            }
        };
        ConditionContext context = new ConditionContext(world, 0, 64, 0, recipeContext);

        WorldPropertyExpression expr = new WorldPropertyExpression("redstone");

        assertEquals(
            12.0,
            expr.evaluate(context)
                .asDouble(),
            0.001);
    }

    @Test
    @DisplayName("seed: 評価セッションのシード")
    public void testSeed() {
        World world = new WorldStubWithTotalTime(10000);
        ConditionContext context = new ConditionContext(world, 0, 64, 0, null, 12345L);

        WorldPropertyExpression expr = new WorldPropertyExpression("seed");

        assertEquals(
            12345.0,
            expr.evaluate(context)
                .asDouble(),
            0.001);
    }

    // ========================================
    // JSONシリアライゼーションのテスト
    // ========================================

    @Test
    @DisplayName("JSON: fromJson で生成")
    public void testFromJson() {
        JsonObject json = new JsonObject();
        json.addProperty("property", "time");

        IExpression expr = WorldPropertyExpression.fromJson(json);

        assertNotNull(expr);
        assertTrue(expr instanceof WorldPropertyExpression);
    }

    // ========================================
    // テストダブル
    // ========================================

    private static class WorldStubWithTime extends World {

        private final long worldTime;

        public WorldStubWithTime(long worldTime) {
            super(
                new StubSaveHandler(),
                "TestWorld",
                new WorldSettings(0, WorldSettings.GameType.SURVIVAL, true, false, WorldType.DEFAULT),
                new StubWorldProvider(),
                new Profiler());
            this.worldTime = worldTime;
        }

        @Override
        public long getWorldTime() {
            return worldTime;
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return null;
        }

        @Override
        public Entity getEntityByID(int id) {
            return null;
        }

        @Override
        public int func_152379_p() {
            return 0;
        }
    }

    private static class WorldStubWithTotalTime extends World {

        private final long totalWorldTime;

        public WorldStubWithTotalTime(long totalWorldTime) {
            super(
                new StubSaveHandler(),
                "TestWorld",
                new WorldSettings(0, WorldSettings.GameType.SURVIVAL, true, false, WorldType.DEFAULT),
                new StubWorldProvider(),
                new Profiler());
            this.totalWorldTime = totalWorldTime;
        }

        @Override
        public long getTotalWorldTime() {
            return totalWorldTime;
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return null;
        }

        @Override
        public Entity getEntityByID(int id) {
            return null;
        }

        @Override
        public int func_152379_p() {
            return 0;
        }
    }

    private static class WorldStubWithMoonPhase extends World {

        public WorldStubWithMoonPhase(int moonPhase) {
            super(
                new StubSaveHandler(),
                "TestWorld",
                new WorldSettings(0, WorldSettings.GameType.SURVIVAL, true, false, WorldType.DEFAULT),
                new StubWorldProviderWithMoon(moonPhase),
                new Profiler());
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return null;
        }

        @Override
        public Entity getEntityByID(int id) {
            return null;
        }

        @Override
        public int func_152379_p() {
            return 0;
        }
    }

    private static class StubWorldProvider extends WorldProvider {

        @Override
        public String getDimensionName() {
            return "TestDimension";
        }
    }

    private static class StubWorldProviderWithMoon extends WorldProvider {

        private final int moonPhase;

        public StubWorldProviderWithMoon(int moonPhase) {
            this.moonPhase = moonPhase;
        }

        @Override
        public int getMoonPhase(long worldTime) {
            return moonPhase;
        }

        @Override
        public String getDimensionName() {
            return "TestDimension";
        }
    }

    private static class StubSaveHandler implements ISaveHandler {

        @Override
        public WorldInfo loadWorldInfo() {
            return new WorldInfo(
                new WorldSettings(0, WorldSettings.GameType.SURVIVAL, true, false, WorldType.DEFAULT),
                "TestWorld");
        }

        @Override
        public void checkSessionLock() throws MinecraftException {}

        @Override
        public IChunkLoader getChunkLoader(WorldProvider provider) {
            return null;
        }

        @Override
        public void saveWorldInfoWithPlayer(WorldInfo info, NBTTagCompound tag) {}

        @Override
        public void saveWorldInfo(WorldInfo info) {}

        @Override
        public IPlayerFileData getSaveHandler() {
            return null;
        }

        @Override
        public void flush() {}

        @Override
        public File getWorldDirectory() {
            return null;
        }

        @Override
        public File getMapFileFromName(String name) {
            return null;
        }

        @Override
        public String getWorldDirectoryName() {
            return "TestWorld";
        }
    }

    private static class StubRecipeContext implements IRecipeContext {

        private long startTick;
        private IMachineState machineState;

        public StubRecipeContext(long startTick) {
            this.startTick = startTick;
        }

        public void setMachineState(IMachineState state) {
            this.machineState = state;
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
            return null;
        }

        @Override
        public ConditionContext getConditionContext() {
            return null;
        }

        @Override
        public long getRecipeStartTick() {
            return startTick;
        }

        @Override
        public int getRedstoneLevel() {
            return 0;
        }

        @Override
        public IMachineState getMachineState() {
            return machineState;
        }
    }

    private static class StubMachineState implements IMachineState {

        private long progress;

        public StubMachineState(long progress) {
            this.progress = progress;
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
        public long getProgress() {
            return progress;
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
        public long getRecipeStartTick() {
            return 0;
        }

        @Override
        public long getFluidInput(String name) {
            return 0;
        }

        @Override
        public long getFluidOutputSpace(String name) {
            return 0;
        }

        @Override
        public long getItemCount(IPortType.Direction direction, String itemName) {
            return 0;
        }
    }
}
