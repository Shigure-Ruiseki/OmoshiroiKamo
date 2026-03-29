package ruiseki.omoshiroikamo.api.recipe.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.recipe.context.IRecipeContext;
import ruiseki.omoshiroikamo.api.recipe.core.IMachineState;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.test.RegistryMocker;

public class VisionFunctionExpressionTest {

    private ConditionContext context;
    private WorldStub world;

    @org.junit.jupiter.api.BeforeAll
    static void setUpAll() {
        RegistryMocker.mockAll();
    }

    @BeforeEach
    void setUp() {
        world = new WorldStub();
        IRecipeContext recipeContext = new StubRecipeContext(world);
        context = new ConditionContext(world, 10, 10, 10, recipeContext);
    }

    @Test
    void testCanSeeSky_AirOnly_Success() {
        // Setup: Everything is air above Y=10
        world.setTestBlock(10, 11, 10, Blocks.air);

        VisionFunctionExpression expr = new VisionFunctionExpression(
            VisionFunctionExpression.Direction.SKY,
            Collections.emptyList());

        assertEquals(1.0, expr.evaluate(context));
    }

    @Test
    void testCanSeeSky_AirOnly_BlockedByStone() {
        // Setup: Stone at Y=15
        world.setTestBlock(10, 15, 10, Blocks.stone);

        VisionFunctionExpression expr = new VisionFunctionExpression(
            VisionFunctionExpression.Direction.SKY,
            Collections.emptyList());

        assertEquals(0.0, expr.evaluate(context));
    }

    @Test
    void testCanSeeSky_TransparentAllowed() {
        // Setup: Glass (transparent) at Y=15
        world.setTestBlock(10, 15, 10, Blocks.glass);

        // Mode: transparent
        VisionFunctionExpression expr = new VisionFunctionExpression(
            VisionFunctionExpression.Direction.SKY,
            Arrays.asList(new StringLiteralExpression("transparent")));

        assertEquals(1.0, expr.evaluate(context));
    }

    @Test
    void testCanSeeSky_SpecificBlockAllowed() {
        // Setup: Stone at Y=15
        world.setTestBlock(10, 15, 10, Blocks.stone);

        // Mode: allow "minecraft:stone"
        VisionFunctionExpression expr = new VisionFunctionExpression(
            VisionFunctionExpression.Direction.SKY,
            Arrays.asList(new StringLiteralExpression("minecraft:stone")));

        assertEquals(1.0, expr.evaluate(context));
    }

    @Test
    void testCanSeeVoid_AirOnly_Success() {
        // Setup: Everything is air below Y=10
        for (int y = 0; y < 10; y++) world.setTestBlock(10, y, 10, Blocks.air);

        VisionFunctionExpression expr = new VisionFunctionExpression(
            VisionFunctionExpression.Direction.VOID,
            Collections.emptyList());

        assertEquals(1.0, expr.evaluate(context));
    }

    @Test
    void testCanSeeVoid_BlockedByOpaque() {
        // Setup: Stone at Y=5
        world.setTestBlock(10, 5, 10, Blocks.stone);

        VisionFunctionExpression expr = new VisionFunctionExpression(
            VisionFunctionExpression.Direction.VOID,
            Collections.emptyList());

        assertEquals(0.0, expr.evaluate(context));
    }

    @Test
    void testCanSeeVoid_BedrockAllowed() {
        // Setup: Bedrock at Y=0, air elsewhere
        world.setTestBlock(10, 0, 10, Blocks.bedrock);

        // Default mode bedrock is opaque, so fail
        VisionFunctionExpression def = new VisionFunctionExpression(
            VisionFunctionExpression.Direction.VOID,
            Collections.emptyList());
        assertEquals(0.0, def.evaluate(context));

        // Bedrock mode -> should succeed
        VisionFunctionExpression bedrock = new VisionFunctionExpression(
            VisionFunctionExpression.Direction.VOID,
            Arrays.asList(new StringLiteralExpression("minecraft:bedrock")));
        assertEquals(1.0, bedrock.evaluate(context));
    }

    @Test
    void testStrictMode() {
        // Setup: Glass at Y=5
        world.setTestBlock(10, 5, 10, Blocks.glass);

        // Default mode -> should succeed (glass is transparent)
        VisionFunctionExpression def = new VisionFunctionExpression(
            VisionFunctionExpression.Direction.VOID,
            Collections.emptyList());
        assertEquals(1.0, def.evaluate(context));

        // Strict mode -> should fail
        VisionFunctionExpression strict = new VisionFunctionExpression(
            VisionFunctionExpression.Direction.VOID,
            Arrays.asList(new StringLiteralExpression("strict")));
        assertEquals(0.0, strict.evaluate(context));
    }

    @Test
    void testMultipleAllowedBlocks() {
        // Setup: Stone at 15, Glass at 20
        world.setTestBlock(10, 15, 10, Blocks.stone);
        world.setTestBlock(10, 20, 10, Blocks.glass);

        // only stone + strict -> fail (because of glass)
        VisionFunctionExpression onlyStoneStrict = new VisionFunctionExpression(
            VisionFunctionExpression.Direction.SKY,
            Arrays.asList(new StringLiteralExpression("minecraft:stone"), new StringLiteralExpression("strict")));
        assertEquals(0.0, onlyStoneStrict.evaluate(context));

        // stone and transparent -> success
        VisionFunctionExpression both = new VisionFunctionExpression(
            VisionFunctionExpression.Direction.SKY,
            Arrays.asList(new StringLiteralExpression("minecraft:stone"), new StringLiteralExpression("transparent")));
        assertEquals(1.0, both.evaluate(context));
    }

    // --- Stubs for Testing ---

    private static class WorldStub extends World {

        private final Block[][][] blocks = new Block[32][256][32];

        public WorldStub() {
            super(
                new StubSaveHandler(),
                "TestWorld",
                new WorldSettings(0, WorldSettings.GameType.SURVIVAL, true, false, WorldType.DEFAULT),
                new StubWorldProvider(),
                new Profiler());
            // Fill with air
            for (int x = 0; x < 32; x++)
                for (int y = 0; y < 256; y++) for (int z = 0; z < 32; z++) blocks[x][y][z] = Blocks.air;
        }

        public void setTestBlock(int x, int y, int z, Block block) {
            blocks[x % 32][y][z] = block;
        }

        @Override
        public Block getBlock(int x, int y, int z) {
            return blocks[x % 32][y][z];
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
            return "Test";
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

        private final World world;

        public StubRecipeContext(World world) {
            this.world = world;
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
        public long getRecipeStartTick() {
            return 0;
        }

        @Override
        public int getRedstoneLevel() {
            return 0;
        }

        @Override
        public IMachineState getMachineState() {
            return null;
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
            return Collections.emptyList();
        }

        @Override
        public ConditionContext getConditionContext() {
            return null;
        }
    }
}
