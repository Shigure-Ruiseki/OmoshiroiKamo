package ruiseki.omoshiroikamo.module.machinery.common.tile;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ruiseki.omoshiroikamo.api.structure.core.ISymbolMapping;
import ruiseki.omoshiroikamo.api.structure.core.TieredBlockMapping;

/**
 * MachineTierRecognitionTest
 * 
 * StructureAgent が世界に配置されたブロックから
 * コンポーネントごとの Tier を正しく認識（集計）できるかを検証します。
 * 
 * コンパイラのバグ (NPE) を避けるため、TEMachineController クラスのロードを回避し、
 * メソッドオーバーライドによる外部依存の切り離し（手動 Mock）で検証します。
 */
@DisplayName("マシン Tier 認識ロジックのテスト")
public class MachineTierRecognitionTest {

    private TestStructureAgent agent;
    private MockWorld world;
    private Map<Character, List<ChunkCoordinates>> symbolPositions;

    @BeforeEach
    public void setup() {
        world = new MockWorld();
        symbolPositions = new HashMap<>();

        // TEMachineController (null) を渡すが、内部で使わないようにオーバーライドする
        agent = new TestStructureAgent(world, symbolPositions);
    }

    @Test
    @Disabled("MockWorld初期化でNullPointerExceptionが発生するため無効化")
    @DisplayName("(ignore) シンボルに対応するブロックから最小 Tier が算出されること")
    public void testUpdateComponentTiers() {
        // 1. Tier マッピングの準備
        Map<String, Integer> tiers = new HashMap<>();
        tiers.put("minecraft:iron_block:0", 1);
        tiers.put("minecraft:gold_block:0", 2);
        TieredBlockMapping mapping = new TieredBlockMapping('C', "casing", tiers);

        // 2. 構造体エントリの準備 (Symbol 'C' を使用)
        Map<Character, ISymbolMapping> mappings = new HashMap<>();
        mappings.put('C', mapping);
        StubStructureEntry stubEntry = new StubStructureEntry("test_machine", mappings);

        // 3. データ準備
        ChunkCoordinates pos1 = new ChunkCoordinates(10, 64, 10);
        ChunkCoordinates pos2 = new ChunkCoordinates(11, 64, 10);

        List<ChunkCoordinates> positions = new ArrayList<>();
        positions.add(pos1);
        positions.add(pos2);
        symbolPositions.put('C', positions);

        // 4. 世界にブロックを配置
        Block iron = Block.getBlockFromName("iron_block");
        Block gold = Block.getBlockFromName("gold_block");
        world.setBlock(pos1, iron, 0);
        world.setBlock(pos2, gold, 0);

        // 5. テスト対象の実行
        agent.setCustomStructureName("test_machine");
        agent.updateComponentTiersFromBlocks(stubEntry);

        assertEquals(1, agent.getComponentTier("casing"), "最小 Tier が採用されるべき");

        // 両方 Gold に変更
        world.setBlock(pos1, gold, 0);
        agent.updateComponentTiersFromBlocks(stubEntry);
        assertEquals(2, agent.getComponentTier("casing"), "全てのブロックが Tier 2 なら Tier 2 になるべき");
    }

    /**
     * Test用の StructureAgent サブクラス。
     * TEMachineController (controller フィールド) へのアクセスをすべて遮断・置換する。
     */
    private static class TestStructureAgent extends StructureAgent {

        private final World mockWorld;
        private final Map<Character, List<ChunkCoordinates>> mockSymbols;

        public TestStructureAgent(World world, Map<Character, List<ChunkCoordinates>> symbols) {
            super(null); // controller = null
            this.mockWorld = world;
            this.mockSymbols = symbols;
        }

        @Override
        public Map<Character, List<ChunkCoordinates>> getSymbolPositionsMap() {
            return mockSymbols;
        }

        @Override
        public World getWorldObj() {
            return mockWorld;
        }
    }
}
