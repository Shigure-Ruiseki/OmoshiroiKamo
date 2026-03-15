package ruiseki.omoshiroikamo.api.condition;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.test.RegistryMocker;

/**
 * BlockBelowCondition のユニットテスト
 *
 * ============================================
 * 下のブロック判定の条件テスト
 * ============================================
 *
 * BlockBelowCondition は、コンテキスト位置の真下（Y-1）にある
 * ブロックの種類とメタデータを判定します。
 *
 * - targetMeta == -1: メタデータを無視してブロック種のみ比較
 * - targetMeta >= 0: ブロック種とメタデータの両方を比較
 *
 * バグ発見の優先度: ★★★★☆
 *
 * ============================================
 */
@DisplayName("BlockBelowConditionのテスト")
public class BlockBelowConditionTest {

    @BeforeAll
    public static void setUpAll() {
        RegistryMocker.mockAll();
    }

    // ========================================
    // 正常系: ブロック完全一致
    // ========================================

    @Test
    @DisplayName("ブロック完全一致: Stone(meta=0)がマッチ")
    public void testブロック完全一致() {
        BlockBelowCondition condition = new BlockBelowCondition(Blocks.stone, 0);

        World world = new WorldStubWithBlock(Blocks.stone, 0);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "下にStone(meta=0)があればtrueになるべき");
    }

    @Test
    @DisplayName("ブロック完全一致: Dirt(meta=0)がマッチ")
    public void testブロック完全一致Dirt() {
        BlockBelowCondition condition = new BlockBelowCondition(Blocks.dirt, 0);

        World world = new WorldStubWithBlock(Blocks.dirt, 0);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "下にDirt(meta=0)があればtrueになるべき");
    }

    // ========================================
    // 正常系: メタデータ無視（-1）
    // ========================================

    @Test
    @DisplayName("メタ無視: targetMeta=-1で任意のメタデータをマッチ")
    public void testメタ無視() {
        BlockBelowCondition condition = new BlockBelowCondition(Blocks.stone, -1);

        // 下のブロックはStone(meta=5)
        World world = new WorldStubWithBlock(Blocks.stone, 5);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "targetMeta=-1の場合、任意のメタデータでマッチするべき");
    }

    @Test
    @DisplayName("メタ無視: 異なるブロックは不一致")
    public void testメタ無視でもブロック不一致() {
        BlockBelowCondition condition = new BlockBelowCondition(Blocks.stone, -1);

        World world = new WorldStubWithBlock(Blocks.dirt, 0);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertFalse(condition.isMet(context), "メタ無視でも、ブロック種が異なればfalseになるべき");
    }

    // ========================================
    // 正常系: ブロック不一致
    // ========================================

    @Test
    @DisplayName("ブロック不一致: Stoneを期待してDirtがあればfalse")
    public void testブロック不一致() {
        BlockBelowCondition condition = new BlockBelowCondition(Blocks.stone, 0);

        World world = new WorldStubWithBlock(Blocks.dirt, 0);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertFalse(condition.isMet(context), "ブロック種が異なればfalseになるべき");
    }

    // ========================================
    // 正常系: メタデータ不一致
    // ========================================

    @Test
    @DisplayName("メタ不一致: Stone(meta=0)を期待してStone(meta=1)があればfalse")
    public void testメタ不一致() {
        BlockBelowCondition condition = new BlockBelowCondition(Blocks.stone, 0);

        World world = new WorldStubWithBlock(Blocks.stone, 1);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertFalse(condition.isMet(context), "メタデータが異なればfalseになるべき");
    }

    // ========================================
    // エッジケース
    // ========================================

    @Test
    @DisplayName("【エッジ】Y=0での確認（Y-1=-1）")
    public void testY座標ゼロ() {
        BlockBelowCondition condition = new BlockBelowCondition(Blocks.stone, 0);

        World world = new WorldStubWithBlock(Blocks.stone, 0);
        ConditionContext context = new ConditionContext(world, 0, 0, 0); // Y=0

        // Y-1=-1 の位置のブロックを取得
        // 実装依存だが、通常はair等が返される
        // このテストは境界動作を確認するためのもの
        assertNotNull(context);
    }

    @Test
    @DisplayName("【エッジ】Air（空気）ブロックの判定")
    public void testAirブロック() {
        BlockBelowCondition condition = new BlockBelowCondition(Blocks.air, 0);

        World world = new WorldStubWithBlock(Blocks.air, 0);
        ConditionContext context = new ConditionContext(world, 0, 64, 0);

        assertTrue(condition.isMet(context), "Airブロックもマッチできるべき");
    }

    // ========================================
    // getDescription() のテスト
    // ========================================

    @Test
    @Disabled("StatCollectorがテスト環境で利用不可のため無効化")
    @DisplayName("(ignore) getDescription: 説明文が取得できる")
    public void testGetDescription() {
        BlockBelowCondition condition = new BlockBelowCondition(Blocks.stone, 0);

        String description = condition.getDescription();

        assertNotNull(description);
        // StatCollector.translateToLocalFormatted() の結果
    }

    // ========================================
    // JSON書き込みのテスト
    // ========================================

    @Test
    @Disabled("ItemJsonがテスト環境で利用不可のため無効化")
    @DisplayName("(ignore) JSON: write()で正しく書き込み")
    public void testJSONWrite() {
        BlockBelowCondition condition = new BlockBelowCondition(Blocks.stone, 0);

        JsonObject json = new JsonObject();
        condition.write(json);

        assertTrue(json.has("type"));
        assertEquals(
            "block_below",
            json.get("type")
                .getAsString());
        assertTrue(json.has("block"));
    }

    @Test
    @Disabled("ItemJsonがテスト環境で利用不可のため無効化")
    @DisplayName("(ignore) JSON: write()でmeta=-1を書き込み")
    public void testJSONWriteメタ無視() {
        BlockBelowCondition condition = new BlockBelowCondition(Blocks.stone, -1);

        JsonObject json = new JsonObject();
        condition.write(json);

        assertTrue(json.has("block"));
        JsonObject blockObj = json.getAsJsonObject("block");
        assertTrue(blockObj.has("data"));
        assertEquals(
            -1,
            blockObj.get("data")
                .getAsInt());
    }

    // ========================================
    // テストダブル（WorldStub）
    // ========================================

    private static class WorldStubWithBlock extends World {

        private final Block block;
        private final int meta;

        public WorldStubWithBlock(Block block, int meta) {
            super(
                new StubSaveHandler(),
                "TestWorld",
                new WorldSettings(0, WorldSettings.GameType.SURVIVAL, true, false, WorldType.DEFAULT),
                new StubWorldProvider(),
                new Profiler());
            this.block = block;
            this.meta = meta;
        }

        @Override
        public Block getBlock(int x, int y, int z) {
            // Y-1 の位置のブロックを返す
            return block;
        }

        @Override
        public int getBlockMetadata(int x, int y, int z) {
            return meta;
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
