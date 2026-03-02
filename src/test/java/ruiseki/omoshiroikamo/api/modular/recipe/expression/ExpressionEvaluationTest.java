package ruiseki.omoshiroikamo.api.modular.recipe.expression;

import static org.junit.jupiter.api.Assertions.*;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.storage.IPlayerFileData;

import java.io.File;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.test.RegistryMocker;

/**
 * Expression（式評価）の詳細な評価ロジックを検証するテスト
 */
@DisplayName("Expression（式評価）ロジック詳細テスト")
public class ExpressionEvaluationTest {

    @BeforeAll
    public static void setUpAll() {
        RegistryMocker.mockAll();
    }

    // ========================================
    // MapRangeExpression のテスト
    // ========================================

    @Test
    @DisplayName("MapRangeExpression: 線形マッピングの検証")
    public void testMapRangeLinear() {
        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        MapRangeExpression expr = new MapRangeExpression(new ConstantExpression(25), 0, 100, 0.0, 1.0, true);
        assertEquals(0.25, expr.evaluate(context), 0.001);

        expr = new MapRangeExpression(new ConstantExpression(75), 0, 100, 0.0, 1.0, true);
        assertEquals(0.75, expr.evaluate(context), 0.001);
    }

    @Test
    @DisplayName("MapRangeExpression: クランプ（範囲外）の検証")
    public void testMapRangeClamping() {
        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        MapRangeExpression expr = new MapRangeExpression(new ConstantExpression(150), 0, 100, 0.0, 1.0, true);
        assertEquals(1.0, expr.evaluate(context), 0.001);

        expr = new MapRangeExpression(new ConstantExpression(-50), 0, 100, 0.0, 1.0, true);
        assertEquals(0.0, expr.evaluate(context), 0.001);

        expr = new MapRangeExpression(new ConstantExpression(150), 0, 100, 0.0, 1.0, false);
        assertEquals(1.5, expr.evaluate(context), 0.001);
    }

    // ========================================
    // NbtExpression のテスト
    // ========================================

    @Test
    @DisplayName("NbtExpression: TileEntityからNBT値を取得")
    public void testNbtExpressionEvaluation() {
        TileEntity stubTE = new TileEntity() {
            @Override
            public void writeToNBT(NBTTagCompound tag) {
                // super.writeToNBT(tag); // マッピングエラーを避けるために呼ばない
                tag.setDouble("TestKey", 0.123);
            }
        };

        World wrappedWorld = new WorldStub(stubTE);

        ConditionContext context = new ConditionContext(wrappedWorld, 10, 64, 10);
        NbtExpression expr = new NbtExpression("TestKey", 0.0);

        assertEquals(0.123, expr.evaluate(context), 0.001);
    }

    @Test
    @DisplayName("NbtExpression: NBTが存在しない場合はデフォルト値を返す")
    public void testNbtExpressionMissingKey() {
        TileEntity stubTE = new TileEntity() {
            @Override
            public void writeToNBT(NBTTagCompound tag) {
                // super.writeToNBT(tag);
            }
        };

        World wrappedWorld = new WorldStub(stubTE);

        ConditionContext context = new ConditionContext(wrappedWorld, 10, 64, 10);
        NbtExpression expr = new NbtExpression("MissingKey", 0.99);

        assertEquals(0.99, expr.evaluate(context), 0.001);
    }

    private static class WorldStub extends World {
        private final TileEntity te;

        public WorldStub(TileEntity te) {
            super(new StubSaveHandler(), "Stub",
                    new WorldSettings(0, WorldSettings.GameType.SURVIVAL, true, false, WorldType.DEFAULT),
                    new StubWorldProvider(), new Profiler());
            this.te = te;
        }

        @Override
        public TileEntity getTileEntity(int x, int y, int z) {
            return te;
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
            return "Stub";
        }
    }

    private static class StubSaveHandler implements ISaveHandler {
        @Override
        public WorldInfo loadWorldInfo() {
            return new WorldInfo(new WorldSettings(0, WorldSettings.GameType.SURVIVAL, true, false, WorldType.DEFAULT),
                    "Stub");
        }

        @Override
        public void checkSessionLock() throws MinecraftException {
        }

        @Override
        public IChunkLoader getChunkLoader(WorldProvider provider) {
            return null;
        }

        @Override
        public void saveWorldInfoWithPlayer(WorldInfo info, NBTTagCompound tag) {
        }

        @Override
        public void saveWorldInfo(WorldInfo info) {
        }

        @Override
        public IPlayerFileData getSaveHandler() {
            return null;
        }

        @Override
        public void flush() {
        }

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
            return "Stub";
        }
    }
}
