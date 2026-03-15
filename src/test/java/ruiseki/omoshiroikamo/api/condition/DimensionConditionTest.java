package ruiseki.omoshiroikamo.api.condition;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import ruiseki.omoshiroikamo.test.RegistryMocker;

/**
 * DimensionCondition のユニットテスト
 *
 * ============================================
 * ディメンションID判定の条件テスト
 * ============================================
 *
 * DimensionCondition は、マシンが設置されているディメンションIDを
 * 許可リストと照合します。
 *
 * - Overworld: ID = 0
 * - Nether: ID = -1
 * - End: ID = 1
 * - Modディメンション: その他のID
 *
 * バグ発見の優先度: ★★★☆☆
 *
 * ============================================
 */
@DisplayName("DimensionCondition（ディメンション判定）のテスト")
public class DimensionConditionTest {

    @BeforeAll
    public static void setUpAll() {
        RegistryMocker.mockAll();
    }

    // ========================================
    // 正常系: 単一ID
    // ========================================

    @Test
    @DisplayName("単一ID: Overworld（ID=0）でマッチング")
    public void test単一IDOverworld() {
        List<Integer> allowedDimensions = Collections.singletonList(0);
        DimensionCondition condition = new DimensionCondition(allowedDimensions);

        World world = new WorldStub(0);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "ディメンションID=0はマッチするべき");
    }

    @Test
    @DisplayName("単一ID: Nether（ID=-1）でマッチング")
    public void test単一IDNether() {
        List<Integer> allowedDimensions = Collections.singletonList(-1);
        DimensionCondition condition = new DimensionCondition(allowedDimensions);

        World world = new WorldStub(-1);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "ディメンションID=-1はマッチするべき");
    }

    @Test
    @DisplayName("単一ID: End（ID=1）でマッチング")
    public void test単一IDEnd() {
        List<Integer> allowedDimensions = Collections.singletonList(1);
        DimensionCondition condition = new DimensionCondition(allowedDimensions);

        World world = new WorldStub(1);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "ディメンションID=1はマッチするべき");
    }

    @Test
    @DisplayName("単一ID: マッチしない場合")
    public void test単一IDマッチしない() {
        List<Integer> allowedDimensions = Collections.singletonList(0);
        DimensionCondition condition = new DimensionCondition(allowedDimensions);

        World world = new WorldStub(-1);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertFalse(condition.isMet(context), "ディメンションID=-1は許可リスト[0]にマッチしないべき");
    }

    // ========================================
    // 正常系: 複数ID
    // ========================================

    @Test
    @DisplayName("複数ID: リスト内の最初のIDでマッチング")
    public void test複数ID最初でマッチ() {
        List<Integer> allowedDimensions = Arrays.asList(0, -1, 1);
        DimensionCondition condition = new DimensionCondition(allowedDimensions);

        World world = new WorldStub(0);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "ディメンションID=0はリスト[0,-1,1]の最初にマッチするべき");
    }

    @Test
    @DisplayName("複数ID: リスト内の中間のIDでマッチング")
    public void test複数ID中間でマッチ() {
        List<Integer> allowedDimensions = Arrays.asList(0, -1, 1);
        DimensionCondition condition = new DimensionCondition(allowedDimensions);

        World world = new WorldStub(-1);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "ディメンションID=-1はリスト[0,-1,1]の中間にマッチするべき");
    }

    @Test
    @DisplayName("複数ID: リスト内の最後のIDでマッチング")
    public void test複数ID最後でマッチ() {
        List<Integer> allowedDimensions = Arrays.asList(0, -1, 1);
        DimensionCondition condition = new DimensionCondition(allowedDimensions);

        World world = new WorldStub(1);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "ディメンションID=1はリスト[0,-1,1]の最後にマッチするべき");
    }

    @Test
    @DisplayName("複数ID: リストにないIDはマッチしない")
    public void test複数IDマッチしない() {
        List<Integer> allowedDimensions = Arrays.asList(0, -1, 1);
        DimensionCondition condition = new DimensionCondition(allowedDimensions);

        World world = new WorldStub(7); // Twilight Forest等
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertFalse(condition.isMet(context), "ディメンションID=7はリスト[0,-1,1]にマッチしないべき");
    }

    // ========================================
    // エッジケース
    // ========================================

    @Test
    @DisplayName("【エッジ】空リスト: どのIDでもfalse")
    public void test空リスト() {
        List<Integer> emptyList = Collections.emptyList();
        DimensionCondition condition = new DimensionCondition(emptyList);

        World world = new WorldStub(0);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertFalse(condition.isMet(context), "空リストの場合、すべてのディメンションでfalseになるべき");
    }

    @Test
    @DisplayName("【エッジ】負数ID: ID=-100でもマッチング")
    public void test負数ID() {
        List<Integer> allowedDimensions = Collections.singletonList(-100);
        DimensionCondition condition = new DimensionCondition(allowedDimensions);

        World world = new WorldStub(-100);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "負数ディメンションID=-100もマッチするべき");
    }

    @Test
    @DisplayName("【エッジ】大きいID: ID=Integer.MAX_VALUEでもマッチング")
    public void test大きいID() {
        List<Integer> allowedDimensions = Collections.singletonList(Integer.MAX_VALUE);
        DimensionCondition condition = new DimensionCondition(allowedDimensions);

        World world = new WorldStub(Integer.MAX_VALUE);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "Integer.MAX_VALUEのディメンションIDもマッチするべき");
    }

    @Test
    @DisplayName("【エッジ】小さいID: ID=Integer.MIN_VALUEでもマッチング")
    public void test小さいID() {
        List<Integer> allowedDimensions = Collections.singletonList(Integer.MIN_VALUE);
        DimensionCondition condition = new DimensionCondition(allowedDimensions);

        World world = new WorldStub(Integer.MIN_VALUE);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "Integer.MIN_VALUEのディメンションIDもマッチするべき");
    }

    // ========================================
    // JSON読み込み・書き込みのテスト
    // ========================================

    @Test
    @DisplayName("JSON: IDなし（空配列）")
    public void testJSONIDなし() {
        JsonObject json = new JsonObject();
        json.add("ids", new JsonArray());

        ICondition condition = DimensionCondition.fromJson(json);

        assertNotNull(condition);
        assertTrue(condition instanceof DimensionCondition);

        World world = new WorldStub(0);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertFalse(condition.isMet(context), "空配列の場合、falseになるべき");
    }

    @Test
    @DisplayName("JSON: 複数IDの読み込み")
    public void testJSON複数ID() {
        JsonObject json = new JsonObject();
        JsonArray array = new JsonArray();
        array.add(new JsonPrimitive(0));
        array.add(new JsonPrimitive(-1));
        array.add(new JsonPrimitive(1));
        json.add("ids", array);

        ICondition condition = DimensionCondition.fromJson(json);

        assertNotNull(condition);

        World world = new WorldStub(-1);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "JSONから読み込んだ複数IDでマッチするべき");
    }

    @Test
    @DisplayName("JSON: 書き込みと読み込みのラウンドトリップ")
    public void testJSONラウンドトリップ() {
        List<Integer> originalIds = Arrays.asList(0, -1, 7);
        DimensionCondition originalCondition = new DimensionCondition(originalIds);

        // 書き込み
        JsonObject json = new JsonObject();
        originalCondition.write(json);

        // 読み込み
        ICondition restoredCondition = DimensionCondition.fromJson(json);

        // 検証
        World world = new WorldStub(7);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(restoredCondition.isMet(context), "ラウンドトリップ後もマッチするべき");

        World world2 = new WorldStub(100);
        ConditionContext context2 = new ConditionContext(world2, 0, 64, 0);

        assertFalse(restoredCondition.isMet(context2), "ラウンドトリップ後も非マッチングが正しいべき");
    }

    @Test
    @DisplayName("JSON: idsフィールドがない場合")
    public void testJSONidsフィールドなし() {
        JsonObject json = new JsonObject();
        // "ids" フィールドなし

        ICondition condition = DimensionCondition.fromJson(json);

        assertNotNull(condition);

        World world = new WorldStub(0);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertFalse(condition.isMet(context), "idsフィールドがない場合、空リストとして扱われるべき");
    }

    // ========================================
    // getDescription() のテスト
    // ========================================

    @Test
    @DisplayName("getDescription: 説明文が取得できる")
    public void testGetDescription() {
        List<Integer> allowedDimensions = Arrays.asList(0, -1, 1);
        DimensionCondition condition = new DimensionCondition(allowedDimensions);

        String description = condition.getDescription();

        assertNotNull(description);
        // StatCollector.translateToLocalFormatted() の結果が返される
        // 実際の翻訳テキストは言語ファイル次第だが、nullでないことを確認
    }

    // ========================================
    // テストダブル（WorldStub）
    // ========================================

    private static class WorldStub extends World {

        public WorldStub(int dimensionId) {
            super(
                new StubSaveHandler(),
                "TestWorld",
                new WorldSettings(0, WorldSettings.GameType.SURVIVAL, true, false, WorldType.DEFAULT),
                new StubWorldProvider(dimensionId),
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

        public StubWorldProvider(int dimensionId) {
            this.dimensionId = dimensionId;
        }

        @Override
        public String getDimensionName() {
            return "TestDimension_" + dimensionId;
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
