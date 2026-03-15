package ruiseki.omoshiroikamo.api.condition;

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

import ruiseki.omoshiroikamo.api.recipe.context.IRecipeContext;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.test.RegistryMocker;

/**
 * ConditionContext のユニットテスト
 *
 * ============================================
 * ConditionとExpressionの基盤クラス
 * ============================================
 *
 * ConditionContext は、すべての Condition と Expression が使用する
 * コンテキスト情報を保持します。
 *
 * - World: ワールド情報
 * - x, y, z: 座標情報
 * - IRecipeContext: レシピ実行時のコンテキスト
 *
 * バグ発見の優先度: ★★★★★（最重要）
 *
 * ============================================
 */
@DisplayName("ConditionContext（コンテキスト基盤）のテスト")
public class ConditionContextTest {

    @BeforeAll
    public static void setUpAll() {
        RegistryMocker.mockAll();
    }

    // ========================================
    // コンストラクタのテスト
    // ========================================

    @Test
    @DisplayName("コンストラクタ（5引数）: 正常に値を保持")
    public void testコンストラクタ5引数() {
        World world = new WorldStub();
        IRecipeContext recipeContext = new MockRecipeContext();

        ConditionContext context = new ConditionContext(world, 10, 64, 20, recipeContext);

        assertNotNull(context);
        assertEquals(world, context.getWorld());
        assertEquals(10, context.getX());
        assertEquals(64, context.getY());
        assertEquals(20, context.getZ());
        assertEquals(recipeContext, context.getRecipeContext());
    }

    @Test
    @DisplayName("コンストラクタ（4引数）: recipeContextがnull")
    public void testコンストラクタ4引数() {
        World world = new WorldStub();

        ConditionContext context = new ConditionContext(world, 5, 32, 15);

        assertNotNull(context);
        assertEquals(world, context.getWorld());
        assertEquals(5, context.getX());
        assertEquals(32, context.getY());
        assertEquals(15, context.getZ());
        assertNull(context.getRecipeContext());
    }

    // ========================================
    // Null安全性のテスト
    // ========================================

    @Test
    @DisplayName("【Null安全】World=null でも動作する")
    public void testWorldNull() {
        ConditionContext context = new ConditionContext(null, 0, 0, 0, null);

        assertNotNull(context);
        assertNull(context.getWorld());
        assertEquals(0, context.getX());
        assertEquals(0, context.getY());
        assertEquals(0, context.getZ());
        assertNull(context.getRecipeContext());
    }

    @Test
    @DisplayName("【Null安全】recipeContext=null でも動作する")
    public void testRecipeContextNull() {
        World world = new WorldStub();

        ConditionContext context = new ConditionContext(world, 10, 20, 30, null);

        assertNotNull(context);
        assertEquals(world, context.getWorld());
        assertNull(context.getRecipeContext());
    }

    @Test
    @DisplayName("【Null安全】すべてnull でも動作する")
    public void testAllNull() {
        ConditionContext context = new ConditionContext(null, 0, 0, 0, null);

        assertNotNull(context);
        assertNull(context.getWorld());
        assertNull(context.getRecipeContext());
    }

    // ========================================
    // 座標値のテスト
    // ========================================

    @Test
    @DisplayName("座標値: 負の座標も保持できる")
    public void test負の座標() {
        World world = new WorldStub();

        ConditionContext context = new ConditionContext(world, -10, -64, -20);

        assertEquals(-10, context.getX());
        assertEquals(-64, context.getY());
        assertEquals(-20, context.getZ());
    }

    @Test
    @DisplayName("座標値: 非常に大きな座標も保持できる")
    public void test大きな座標() {
        World world = new WorldStub();

        ConditionContext context = new ConditionContext(world, 30000000, 255, 30000000);

        assertEquals(30000000, context.getX());
        assertEquals(255, context.getY());
        assertEquals(30000000, context.getZ());
    }

    @Test
    @DisplayName("座標値: ゼロ座標")
    public void testゼロ座標() {
        World world = new WorldStub();

        ConditionContext context = new ConditionContext(world, 0, 0, 0);

        assertEquals(0, context.getX());
        assertEquals(0, context.getY());
        assertEquals(0, context.getZ());
    }

    // ========================================
    // Getter動作確認
    // ========================================

    @Test
    @DisplayName("Getter: getWorld() が正しく動作する")
    public void testGetWorld() {
        World world = new WorldStub();
        ConditionContext context = new ConditionContext(world, 0, 0, 0);

        World retrievedWorld = context.getWorld();

        assertNotNull(retrievedWorld);
        assertSame(world, retrievedWorld);
    }

    @Test
    @DisplayName("Getter: getRecipeContext() が正しく動作する")
    public void testGetRecipeContext() {
        World world = new WorldStub();
        IRecipeContext recipeContext = new MockRecipeContext();
        ConditionContext context = new ConditionContext(world, 0, 0, 0, recipeContext);

        IRecipeContext retrieved = context.getRecipeContext();

        assertNotNull(retrieved);
        assertSame(recipeContext, retrieved);
    }

    // ========================================
    // テストダブル（WorldStub）
    // ========================================

    private static class WorldStub extends World {

        public WorldStub() {
            super(
                new StubSaveHandler(),
                "TestWorld",
                new WorldSettings(0, WorldSettings.GameType.SURVIVAL, true, false, WorldType.DEFAULT),
                new StubWorldProvider(),
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

    /**
     * Mock implementation of IRecipeContext for testing
     */
    private static class MockRecipeContext implements IRecipeContext {

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
    }
}
