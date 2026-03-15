package ruiseki.omoshiroikamo.api.condition;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.TileNbtCondition.ComparisonOp;
import ruiseki.omoshiroikamo.test.RegistryMocker;

/**
 * TileNbtCondition のユニットテスト
 *
 * ============================================
 * TileEntityのNBT値比較の条件テスト
 * ============================================
 *
 * TileNbtCondition は、指定位置のTileEntityのNBTデータから
 * 数値を取得し、指定演算子で比較します。
 *
 * サポートされる比較演算子:
 * - GREATER_THAN (>)
 * - GREATER_OR_EQUAL (>=)
 * - LESS_THAN (<)
 * - LESS_OR_EQUAL (<=)
 * - EQUAL (==)
 *
 * バグ発見の優先度: ★★★★★
 *
 * ============================================
 */
@DisplayName("TileNbtCondition（TileEntityのNBT比較）のテスト")
public class TileNbtConditionTest {

    @BeforeAll
    public static void setUpAll() {
        RegistryMocker.mockAll();
    }

    // ========================================
    // 正常系: > (GREATER_THAN)
    // ========================================

    @Test
    @DisplayName("> : NBT値が閾値より大きい場合にtrue")
    public void test大なり正常() {
        TileNbtCondition condition = new TileNbtCondition("energy", ComparisonOp.GREATER_THAN, 100.0);

        TileEntity te = createTileEntityWithNBT("energy", 150.0);
        World world = new WorldStubWithTileEntity(te);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "energy=150 > 100 はtrueになるべき");
    }

    @Test
    @DisplayName("> : NBT値が閾値と等しい場合にfalse")
    public void test大なり等しい() {
        TileNbtCondition condition = new TileNbtCondition("energy", ComparisonOp.GREATER_THAN, 100.0);

        TileEntity te = createTileEntityWithNBT("energy", 100.0);
        World world = new WorldStubWithTileEntity(te);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertFalse(condition.isMet(context), "energy=100 > 100 はfalseになるべき");
    }

    // ========================================
    // 正常系: >= (GREATER_OR_EQUAL)
    // ========================================

    @Test
    @DisplayName(">= : NBT値が閾値以上の場合にtrue")
    public void test以上正常() {
        TileNbtCondition condition = new TileNbtCondition("level", ComparisonOp.GREATER_OR_EQUAL, 5.0);

        TileEntity te = createTileEntityWithNBT("level", 5.0);
        World world = new WorldStubWithTileEntity(te);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "level=5 >= 5 はtrueになるべき");
    }

    // ========================================
    // 正常系: < (LESS_THAN)
    // ========================================

    @Test
    @DisplayName("< : NBT値が閾値より小さい場合にtrue")
    public void test小なり正常() {
        TileNbtCondition condition = new TileNbtCondition("temperature", ComparisonOp.LESS_THAN, 100.0);

        TileEntity te = createTileEntityWithNBT("temperature", 50.0);
        World world = new WorldStubWithTileEntity(te);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "temperature=50 < 100 はtrueになるべき");
    }

    // ========================================
    // 正常系: <= (LESS_OR_EQUAL)
    // ========================================

    @Test
    @DisplayName("<= : NBT値が閾値以下の場合にtrue")
    public void test以下正常() {
        TileNbtCondition condition = new TileNbtCondition("count", ComparisonOp.LESS_OR_EQUAL, 64.0);

        TileEntity te = createTileEntityWithNBT("count", 64.0);
        World world = new WorldStubWithTileEntity(te);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "count=64 <= 64 はtrueになるべき");
    }

    // ========================================
    // 正常系: == (EQUAL)
    // ========================================

    @Test
    @DisplayName("== : NBT値が閾値と等しい場合にtrue")
    public void test等号正常() {
        TileNbtCondition condition = new TileNbtCondition("power", ComparisonOp.EQUAL, 1000.0);

        TileEntity te = createTileEntityWithNBT("power", 1000.0);
        World world = new WorldStubWithTileEntity(te);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "power=1000 == 1000 はtrueになるべき");
    }

    @Test
    @DisplayName("== : NBT値が閾値と異なる場合にfalse")
    public void test等号異なる() {
        TileNbtCondition condition = new TileNbtCondition("power", ComparisonOp.EQUAL, 1000.0);

        TileEntity te = createTileEntityWithNBT("power", 999.0);
        World world = new WorldStubWithTileEntity(te);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertFalse(condition.isMet(context), "power=999 == 1000 はfalseになるべき");
    }

    // ========================================
    // エッジケース: TileEntity=null
    // ========================================

    @Test
    @DisplayName("【エッジ】TileEntity=nullの場合、false")
    public void testTileEntityNull() {
        TileNbtCondition condition = new TileNbtCondition("energy", ComparisonOp.GREATER_THAN, 100.0);

        World world = new WorldStubWithTileEntity(null);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertFalse(condition.isMet(context), "TileEntity=nullの場合、falseになるべき");
    }

    // ========================================
    // エッジケース: キーなし
    // ========================================

    @Test
    @DisplayName("【エッジ】NBTにキーがない場合、false")
    public void testNBTキーなし() {
        TileNbtCondition condition = new TileNbtCondition("missing_key", ComparisonOp.GREATER_THAN, 100.0);

        TileEntity te = createTileEntityWithNBT("other_key", 200.0);
        World world = new WorldStubWithTileEntity(te);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertFalse(condition.isMet(context), "NBTにキーがない場合、falseになるべき");
    }

    // ========================================
    // エッジケース: 型変換（Float、Int）
    // ========================================

    @Test
    @DisplayName("【エッジ】NBT値がFloat型でも正しく読み取れる")
    public void testNBT型変換Float() {
        TileNbtCondition condition = new TileNbtCondition("ratio", ComparisonOp.EQUAL, 0.75);

        TileEntity te = new TileEntity() {

            @Override
            public void writeToNBT(NBTTagCompound tag) {
                tag.setFloat("ratio", 0.75f);
            }
        };

        World world = new WorldStubWithTileEntity(te);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "Float型のNBT値も正しく読み取れるべき");
    }

    @Test
    @DisplayName("【エッジ】NBT値がInt型でも正しく読み取れる")
    public void testNBT型変換Int() {
        TileNbtCondition condition = new TileNbtCondition("count", ComparisonOp.EQUAL, 10.0);

        TileEntity te = new TileEntity() {

            @Override
            public void writeToNBT(NBTTagCompound tag) {
                tag.setInteger("count", 10);
            }
        };

        World world = new WorldStubWithTileEntity(te);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "Int型のNBT値も正しく読み取れるべき");
    }

    // ========================================
    // エッジケース: 負数、ゼロ
    // ========================================

    @Test
    @DisplayName("【エッジ】負数の比較")
    public void test負数比較() {
        TileNbtCondition condition = new TileNbtCondition("offset", ComparisonOp.LESS_THAN, 0.0);

        TileEntity te = createTileEntityWithNBT("offset", -10.0);
        World world = new WorldStubWithTileEntity(te);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "offset=-10 < 0 はtrueになるべき");
    }

    @Test
    @DisplayName("【エッジ】ゼロとの比較")
    public void testゼロ比較() {
        TileNbtCondition condition = new TileNbtCondition("value", ComparisonOp.EQUAL, 0.0);

        TileEntity te = createTileEntityWithNBT("value", 0.0);
        World world = new WorldStubWithTileEntity(te);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "value=0 == 0 はtrueになるべき");
    }

    // ========================================
    // JSON読み込み・書き込みのテスト
    // ========================================

    @Test
    @DisplayName("JSON: fromJsonで正しく読み込み")
    public void testJSONFromJson() {
        JsonObject json = new JsonObject();
        json.addProperty("key", "energy");
        json.addProperty("op", "greater_than");
        json.addProperty("value", 100.0);

        ICondition condition = TileNbtCondition.fromJson(json);

        assertNotNull(condition);
        assertTrue(condition instanceof TileNbtCondition);
    }

    @Test
    @DisplayName("JSON: write()で正しく書き込み")
    public void testJSONWrite() {
        TileNbtCondition condition = new TileNbtCondition("energy", ComparisonOp.GREATER_THAN, 100.0);

        JsonObject json = new JsonObject();
        condition.write(json);

        assertEquals(
            "tile_nbt",
            json.get("type")
                .getAsString());
        assertEquals(
            "energy",
            json.get("key")
                .getAsString());
        assertEquals(
            "greater_than",
            json.get("op")
                .getAsString());
        assertEquals(
            100.0,
            json.get("value")
                .getAsDouble(),
            0.001);
    }

    @Test
    @DisplayName("JSON: ラウンドトリップ")
    public void testJSONラウンドトリップ() {
        TileNbtCondition originalCondition = new TileNbtCondition("power", ComparisonOp.EQUAL, 500.0);

        // 書き込み
        JsonObject json = new JsonObject();
        originalCondition.write(json);

        // 読み込み
        ICondition restoredCondition = TileNbtCondition.fromJson(json);

        // 検証
        TileEntity te = createTileEntityWithNBT("power", 500.0);
        World world = new WorldStubWithTileEntity(te);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(restoredCondition.isMet(context), "ラウンドトリップ後も正しく動作するべき");
    }

    // ========================================
    // getDescription() のテスト
    // ========================================

    @Test
    @DisplayName("getDescription: 説明文が取得できる")
    public void testGetDescription() {
        TileNbtCondition condition = new TileNbtCondition("energy", ComparisonOp.GREATER_THAN, 100.0);

        String description = condition.getDescription();

        assertNotNull(description);
    }

    // ========================================
    // ヘルパーメソッド
    // ========================================

    private TileEntity createTileEntityWithNBT(String key, double value) {
        return new TileEntity() {

            @Override
            public void writeToNBT(NBTTagCompound tag) {
                tag.setDouble(key, value);
            }
        };
    }

    // ========================================
    // テストダブル
    // ========================================

    private static class WorldStubWithTileEntity extends World {

        private final TileEntity tileEntity;

        public WorldStubWithTileEntity(TileEntity tileEntity) {
            super(
                new StubSaveHandler(),
                "TestWorld",
                new WorldSettings(0, WorldSettings.GameType.SURVIVAL, true, false, WorldType.DEFAULT),
                new StubWorldProvider(),
                new Profiler());
            this.tileEntity = tileEntity;
        }

        @Override
        public TileEntity getTileEntity(int x, int y, int z) {
            return tileEntity;
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
}
