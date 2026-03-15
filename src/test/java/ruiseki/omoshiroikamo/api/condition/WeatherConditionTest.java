package ruiseki.omoshiroikamo.api.condition;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
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

import ruiseki.omoshiroikamo.api.condition.WeatherCondition.Weather;
import ruiseki.omoshiroikamo.test.RegistryMocker;

/**
 * WeatherCondition のユニットテスト
 *
 * ============================================
 * 天候判定の条件テスト
 * ============================================
 *
 * WeatherCondition は、現在の天候（晴れ、雨、雷）を判定します。
 *
 * - CLEAR: 雨も雷も降っていない
 * - RAIN: 雨が降っている
 * - THUNDER: 雷が鳴っている
 *
 * バグ発見の優先度: ★★★☆☆
 *
 * ============================================
 */
@DisplayName("WeatherCondition（天候判定）のテスト")
public class WeatherConditionTest {

    @BeforeAll
    public static void setUpAll() {
        RegistryMocker.mockAll();
    }

    // ========================================
    // 正常系: CLEAR判定
    // ========================================

    @Test
    @DisplayName("CLEAR: 雨も雷もない場合にtrue")
    public void testCLEAR正常() {
        WeatherCondition condition = new WeatherCondition(Weather.CLEAR);

        World world = new WorldStub(false, false);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "雨も雷もない場合、CLEARはtrueになるべき");
    }

    @Test
    @DisplayName("CLEAR: 雨が降っている場合にfalse")
    public void testCLEAR雨でfalse() {
        WeatherCondition condition = new WeatherCondition(Weather.CLEAR);

        World world = new WorldStub(true, false);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertFalse(condition.isMet(context), "雨が降っている場合、CLEARはfalseになるべき");
    }

    @Test
    @DisplayName("CLEAR: 雷が鳴っている場合にfalse")
    public void testCLEAR雷でfalse() {
        WeatherCondition condition = new WeatherCondition(Weather.CLEAR);

        World world = new WorldStub(false, true);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertFalse(condition.isMet(context), "雷が鳴っている場合、CLEARはfalseになるべき");
    }

    @Test
    @DisplayName("CLEAR: 雨も雷も両方ある場合にfalse")
    public void testCLEAR両方でfalse() {
        WeatherCondition condition = new WeatherCondition(Weather.CLEAR);

        World world = new WorldStub(true, true);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertFalse(condition.isMet(context), "雨も雷も両方ある場合、CLEARはfalseになるべき");
    }

    // ========================================
    // 正常系: RAIN判定
    // ========================================

    @Test
    @DisplayName("RAIN: 雨が降っている場合にtrue")
    public void testRAIN正常() {
        WeatherCondition condition = new WeatherCondition(Weather.RAIN);

        World world = new WorldStub(true, false);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "雨が降っている場合、RAINはtrueになるべき");
    }

    @Test
    @DisplayName("RAIN: 雨が降っていない場合にfalse")
    public void testRAIN雨なしでfalse() {
        WeatherCondition condition = new WeatherCondition(Weather.RAIN);

        World world = new WorldStub(false, false);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertFalse(condition.isMet(context), "雨が降っていない場合、RAINはfalseになるべき");
    }

    @Test
    @DisplayName("RAIN: 雷だけが鳴っている場合にfalse（雨なし）")
    public void testRAIN雷のみでfalse() {
        WeatherCondition condition = new WeatherCondition(Weather.RAIN);

        World world = new WorldStub(false, true);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertFalse(condition.isMet(context), "雷だけが鳴っている場合、RAINはfalseになるべき");
    }

    @Test
    @DisplayName("RAIN: 雨も雷も両方ある場合にtrue")
    public void testRAIN両方でtrue() {
        WeatherCondition condition = new WeatherCondition(Weather.RAIN);

        World world = new WorldStub(true, true);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "雨も雷も両方ある場合、RAINはtrueになるべき");
    }

    // ========================================
    // 正常系: THUNDER判定
    // ========================================

    @Test
    @DisplayName("THUNDER: 雷が鳴っている場合にtrue")
    public void testTHUNDER正常() {
        WeatherCondition condition = new WeatherCondition(Weather.THUNDER);

        World world = new WorldStub(false, true);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "雷が鳴っている場合、THUNDERはtrueになるべき");
    }

    @Test
    @DisplayName("THUNDER: 雷が鳴っていない場合にfalse")
    public void testTHUNDER雷なしでfalse() {
        WeatherCondition condition = new WeatherCondition(Weather.THUNDER);

        World world = new WorldStub(false, false);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertFalse(condition.isMet(context), "雷が鳴っていない場合、THUNDERはfalseになるべき");
    }

    @Test
    @DisplayName("THUNDER: 雨だけが降っている場合にfalse（雷なし）")
    public void testTHUNDER雨のみでfalse() {
        WeatherCondition condition = new WeatherCondition(Weather.THUNDER);

        World world = new WorldStub(true, false);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertFalse(condition.isMet(context), "雨だけが降っている場合、THUNDERはfalseになるべき");
    }

    @Test
    @DisplayName("THUNDER: 雨も雷も両方ある場合にtrue")
    public void testTHUNDER両方でtrue() {
        WeatherCondition condition = new WeatherCondition(Weather.THUNDER);

        World world = new WorldStub(true, true);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "雨も雷も両方ある場合、THUNDERはtrueになるべき");
    }

    // ========================================
    // エッジケース
    // ========================================

    @Test
    @DisplayName("【エッジ】World=nullの場合、常にfalse")
    public void testWorldNull() {
        WeatherCondition clearCondition = new WeatherCondition(Weather.CLEAR);
        WeatherCondition rainCondition = new WeatherCondition(Weather.RAIN);
        WeatherCondition thunderCondition = new WeatherCondition(Weather.THUNDER);

        ConditionContext context = new ConditionContext(null, 0, 64, 0);

        assertFalse(clearCondition.isMet(context), "World=nullの場合、CLEARはfalseになるべき");
        assertFalse(rainCondition.isMet(context), "World=nullの場合、RAINはfalseになるべき");
        assertFalse(thunderCondition.isMet(context), "World=nullの場合、THUNDERはfalseになるべき");
    }

    // ========================================
    // JSON読み込み・書き込みのテスト
    // ========================================

    @Test
    @DisplayName("JSON: 小文字\"clear\"からの読み込み")
    public void testJSON小文字clear() {
        JsonObject json = new JsonObject();
        json.addProperty("weather", "clear");

        ICondition condition = WeatherCondition.fromJson(json);

        assertNotNull(condition);
        assertTrue(condition instanceof WeatherCondition);

        World world = new WorldStub(false, false);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "小文字\"clear\"から読み込めるべき");
    }

    @Test
    @DisplayName("JSON: 大文字\"RAIN\"からの読み込み")
    public void testJSON大文字RAIN() {
        JsonObject json = new JsonObject();
        json.addProperty("weather", "RAIN");

        ICondition condition = WeatherCondition.fromJson(json);

        assertNotNull(condition);

        World world = new WorldStub(true, false);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "大文字\"RAIN\"から読み込めるべき");
    }

    @Test
    @DisplayName("JSON: 混合ケース\"Thunder\"からの読み込み")
    public void testJSON混合ケースThunder() {
        JsonObject json = new JsonObject();
        json.addProperty("weather", "Thunder");

        ICondition condition = WeatherCondition.fromJson(json);

        assertNotNull(condition);

        World world = new WorldStub(false, true);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "混合ケース\"Thunder\"から読み込めるべき");
    }

    @Test
    @DisplayName("JSON: 無効な値\"invalid\"でIllegalArgumentException")
    public void testJSON無効値() {
        JsonObject json = new JsonObject();
        json.addProperty("weather", "invalid");

        assertThrows(
            IllegalArgumentException.class,
            () -> { WeatherCondition.fromJson(json); },
            "無効な天候値はIllegalArgumentExceptionを投げるべき");
    }

    @Test
    @DisplayName("JSON: 書き込みと読み込みのラウンドトリップ（CLEAR）")
    public void testJSONラウンドトリップCLEAR() {
        WeatherCondition originalCondition = new WeatherCondition(Weather.CLEAR);

        // 書き込み
        JsonObject json = new JsonObject();
        originalCondition.write(json);

        // 読み込み
        ICondition restoredCondition = WeatherCondition.fromJson(json);

        // 検証
        World world = new WorldStub(false, false);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(restoredCondition.isMet(context), "ラウンドトリップ後もCLEARが正しく動作するべき");
    }

    @Test
    @DisplayName("JSON: 書き込みと読み込みのラウンドトリップ（RAIN）")
    public void testJSONラウンドトリップRAIN() {
        WeatherCondition originalCondition = new WeatherCondition(Weather.RAIN);

        JsonObject json = new JsonObject();
        originalCondition.write(json);

        ICondition restoredCondition = WeatherCondition.fromJson(json);

        World world = new WorldStub(true, false);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(restoredCondition.isMet(context), "ラウンドトリップ後もRAINが正しく動作するべき");
    }

    @Test
    @DisplayName("JSON: 書き込みと読み込みのラウンドトリップ（THUNDER）")
    public void testJSONラウンドトリップTHUNDER() {
        WeatherCondition originalCondition = new WeatherCondition(Weather.THUNDER);

        JsonObject json = new JsonObject();
        originalCondition.write(json);

        ICondition restoredCondition = WeatherCondition.fromJson(json);

        World world = new WorldStub(false, true);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(restoredCondition.isMet(context), "ラウンドトリップ後もTHUNDERが正しく動作するべき");
    }

    // ========================================
    // getDescription() のテスト
    // ========================================

    @Test
    @DisplayName("getDescription: 説明文が取得できる")
    public void testGetDescription() {
        WeatherCondition condition = new WeatherCondition(Weather.CLEAR);

        String description = condition.getDescription();

        assertNotNull(description);
        // StatCollector.translateToLocal() の結果が返される
        // 実際の翻訳テキストは言語ファイル次第だが、nullでないことを確認
    }

    // ========================================
    // テストダブル（WorldStub）
    // ========================================

    private static class WorldStub extends World {

        private final boolean raining;
        private final boolean thundering;

        public WorldStub(boolean raining, boolean thundering) {
            super(
                new StubSaveHandler(),
                "TestWorld",
                new WorldSettings(0, WorldSettings.GameType.SURVIVAL, true, false, WorldType.DEFAULT),
                new StubWorldProvider(),
                new Profiler());
            this.raining = raining;
            this.thundering = thundering;
        }

        @Override
        public boolean isRaining() {
            return raining;
        }

        @Override
        public boolean isThundering() {
            return thundering;
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
