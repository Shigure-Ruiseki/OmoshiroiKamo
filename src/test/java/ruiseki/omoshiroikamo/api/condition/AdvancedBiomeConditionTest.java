package ruiseki.omoshiroikamo.api.condition;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ruiseki.omoshiroikamo.test.RegistryMocker;

@DisplayName("高度なバイオーム条件のテスト")
public class AdvancedBiomeConditionTest {

    @BeforeAll
    public static void setUpAll() {
        RegistryMocker.mockAll();
        Conditions.registerDefaults();
    }

    @Test
    @DisplayName("OffsetCondition: オフセット位置での判定検証")
    public void testOffsetCondition() {
        // Biome at (0,0) is Desert, Biome at (1,0) is Forest
        World mockWorld = new WorldStub() {

            @Override
            public BiomeGenBase getBiomeGenForCoords(int x, int z) {
                if (x == 1 && z == 0) return BiomeGenBase.forest;
                return BiomeGenBase.desert;
            }
        };

        ConditionContext context = new ConditionContext(mockWorld, 0, 64, 0);

        // Condition: "Forest at x+1"
        BiomeCondition forestCond = new BiomeCondition(Collections.singletonList("Forest"));
        OffsetCondition offsetCond = new OffsetCondition(forestCond, 1, 0, 0);

        assertTrue(offsetCond.isMet(context), "x+1 should be Forest");
    }

    @Test
    @DisplayName("BiomePatternCondition: 3x3 パターンの検証")
    public void testBiomePatternCondition() {
        // Center: Desert, East: Forest, others: Desert
        World mockWorld = new WorldStub() {

            @Override
            public BiomeGenBase getBiomeGenForCoords(int x, int z) {
                if (x == 0 && z == 0) return BiomeGenBase.desert; // # center
                if (x == 1 && z == 0) return BiomeGenBase.forest; // E (x+1)
                return BiomeGenBase.desert;
            }
        };

        ConditionContext context = new ConditionContext(mockWorld, 0, 64, 0);

        String[] pattern = { "   ", " #E", "   " };
        Map<Character, ICondition> keys = new HashMap<>();
        keys.put('#', new BiomeCondition(Collections.singletonList("Desert")));
        keys.put('E', new BiomeCondition(Collections.singletonList("Forest")));

        BiomePatternCondition patternCond = new BiomePatternCondition(pattern, keys);

        assertTrue(patternCond.isMet(context), "Pattern should match");

        // Fail case: move context to where East is not Forest
        ConditionContext failContext = new ConditionContext(mockWorld, 1, 64, 0);
        assertFalse(patternCond.isMet(failContext), "Pattern should NOT match at (1,0)");
    }

    @Test
    @DisplayName("BiomeCondition: 気温・湿度判定の検証")
    public void testEnvironmentalConditions() {
        World mockWorld = new WorldStub() {

            @Override
            public BiomeGenBase getBiomeGenForCoords(int x, int z) {
                return BiomeGenBase.desert; // Desert has Temp 2.0, Humid 0.0
            }
        };

        ConditionContext context = new ConditionContext(mockWorld, 0, 64, 0);

        BiomeCondition tempCond = new BiomeCondition(Collections.emptyList()).withTemp(1.5, 2.5);
        assertTrue(tempCond.isMet(context), "Desert temp (2.0) should be in [1.5, 2.5]");

        BiomeCondition humidCond = new BiomeCondition(Collections.emptyList()).withHumid(0.1, 0.5);
        assertFalse(humidCond.isMet(context), "Desert humid (0.0) should NOT be in [0.1, 0.5]");
    }

    @Test
    @DisplayName("JSON 変換の検証")
    public void testJsonSerialization() {
        String jsonStr = "{\"type\":\"pattern\",\"pattern\":[\" # \"],\"keys\":{\"#\":{\"type\":\"biome\",\"biomes\":[\"Desert\"]}}}";
        JsonObject json = new JsonParser().parse(jsonStr)
            .getAsJsonObject();

        ICondition cond = BiomePatternCondition.fromJson(json);
        assertNotNull(cond);
        assertTrue(cond instanceof BiomePatternCondition);

        JsonObject outputJson = new JsonObject();
        cond.write(outputJson);
        assertEquals(json.get("type"), outputJson.get("type"));
        assertEquals(json.get("pattern"), outputJson.get("pattern"));
    }

    // Simplest possible World stub for testing
    private static class WorldStub extends World {

        public WorldStub() {
            super(
                new StubSaveHandler(),
                "Stub",
                new WorldSettings(0, WorldSettings.GameType.SURVIVAL, true, false, WorldType.DEFAULT),
                new StubWorldProvider(),
                new Profiler());
        }

        @Override
        public BiomeGenBase getBiomeGenForCoords(int x, int z) {
            return BiomeGenBase.plains;
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return null;
        }

        @Override
        public Entity getEntityByID(int p_73045_1_) {
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
            return new WorldInfo(
                new WorldSettings(0, WorldSettings.GameType.SURVIVAL, true, false, WorldType.DEFAULT),
                "Stub");
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
            return "Stub";
        }
    }
}
