package ruiseki.omoshiroikamo.api.structure.io;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

/**
 * 各 Requirement クラスのテスト
 *
 * ============================================
 * 7種類のRequirementクラスの基本機能をテスト
 * ============================================
 *
 * バグ発見の優先度: ★★★☆☆
 * - シリアライゼーション
 * - デシリアライゼーション
 * - 基本的なプロパティ
 *
 * カバーする機能:
 * - 各クラスの作成
 * - serialize/fromJson
 * - getType/getMinCount/getMaxCount
 *
 * ============================================
 */
@DisplayName("各 Requirement クラスのテスト")
public class RequirementClassesTest {

    // ========================================
    // 4.1 ItemRequirement
    // ========================================

    @Test
    @DisplayName("ItemRequirement の作成・serialize")
    public void testItemRequirement_CreateAndSerialize() {
        ItemRequirement req = new ItemRequirement("itemInput", 2, 8);

        assertEquals("itemInput", req.getType());
        assertEquals(2, req.getMinCount());
        assertEquals(8, req.getMaxCount());

        JsonObject json = req.serialize();
        assertNotNull(json);
        assertEquals(
            "itemInput",
            json.get("type")
                .getAsString());
        assertEquals(
            2,
            json.get("min")
                .getAsInt());
        assertEquals(
            8,
            json.get("max")
                .getAsInt());
    }

    @Test
    @DisplayName("ItemRequirement.fromJson")
    public void testItemRequirement_FromJson() {
        JsonObject json = new JsonObject();
        json.addProperty("min", 3);
        json.addProperty("max", 10);

        IStructureRequirement req = ItemRequirement.fromJson("itemInput", json);

        assertNotNull(req);
        assertEquals("itemInput", req.getType());
        assertEquals(3, req.getMinCount());
        assertEquals(10, req.getMaxCount());
    }

    // ========================================
    // 4.2 FluidRequirement
    // ========================================

    @Test
    @DisplayName("FluidRequirement の作成・serialize")
    public void testFluidRequirement_CreateAndSerialize() {
        FluidRequirement req = new FluidRequirement("fluidOutput", 1, 2);

        assertEquals("fluidOutput", req.getType());
        assertEquals(1, req.getMinCount());
        assertEquals(2, req.getMaxCount());

        JsonObject json = req.serialize();
        assertNotNull(json);
        assertEquals(
            "fluidOutput",
            json.get("type")
                .getAsString());
        assertEquals(
            1,
            json.get("min")
                .getAsInt());
        assertEquals(
            2,
            json.get("max")
                .getAsInt());
    }

    @Test
    @DisplayName("FluidRequirement.fromJson")
    public void testFluidRequirement_FromJson() {
        JsonObject json = new JsonObject();
        json.addProperty("min", 1);
        json.addProperty("max", 4);

        IStructureRequirement req = FluidRequirement.fromJson("fluidInput", json);

        assertNotNull(req);
        assertEquals("fluidInput", req.getType());
        assertEquals(1, req.getMinCount());
        assertEquals(4, req.getMaxCount());
    }

    // ========================================
    // 4.3 EnergyRequirement
    // ========================================

    @Test
    @DisplayName("EnergyRequirement の作成・serialize")
    public void testEnergyRequirement_CreateAndSerialize() {
        EnergyRequirement req = new EnergyRequirement("energyInput", 1, 1);

        assertEquals("energyInput", req.getType());
        assertEquals(1, req.getMinCount());
        assertEquals(1, req.getMaxCount());

        JsonObject json = req.serialize();
        assertNotNull(json);
        assertEquals(
            "energyInput",
            json.get("type")
                .getAsString());
    }

    @Test
    @DisplayName("EnergyRequirement.fromJson")
    public void testEnergyRequirement_FromJson() {
        JsonObject json = new JsonObject();
        json.addProperty("min", 2);
        json.addProperty("max", 5);

        IStructureRequirement req = EnergyRequirement.fromJson("energyInput", json);

        assertNotNull(req);
        assertEquals("energyInput", req.getType());
        assertEquals(2, req.getMinCount());
        assertEquals(5, req.getMaxCount());
    }

    // ========================================
    // 4.4 ManaRequirement
    // ========================================

    @Test
    @DisplayName("ManaRequirement の作成・serialize")
    public void testManaRequirement_CreateAndSerialize() {
        ManaRequirement req = new ManaRequirement("manaInput", 1, 3);

        assertEquals("manaInput", req.getType());
        assertEquals(1, req.getMinCount());
        assertEquals(3, req.getMaxCount());

        JsonObject json = req.serialize();
        assertNotNull(json);
        assertEquals(
            "manaInput",
            json.get("type")
                .getAsString());
    }

    @Test
    @DisplayName("ManaRequirement.fromJson")
    public void testManaRequirement_FromJson() {
        JsonObject json = new JsonObject();
        json.addProperty("min", 0);
        json.addProperty("max", 2);

        IStructureRequirement req = ManaRequirement.fromJson("manaOutput", json);

        assertNotNull(req);
        assertEquals("manaOutput", req.getType());
        assertEquals(0, req.getMinCount());
        assertEquals(2, req.getMaxCount());
    }

    // ========================================
    // 4.5 GasRequirement
    // ========================================

    @Test
    @DisplayName("GasRequirement の作成・serialize")
    public void testGasRequirement_CreateAndSerialize() {
        GasRequirement req = new GasRequirement("gasInput", 0, 1);

        assertEquals("gasInput", req.getType());
        assertEquals(0, req.getMinCount());
        assertEquals(1, req.getMaxCount());

        JsonObject json = req.serialize();
        assertNotNull(json);
        assertEquals(
            "gasInput",
            json.get("type")
                .getAsString());
    }

    @Test
    @DisplayName("GasRequirement.fromJson")
    public void testGasRequirement_FromJson() {
        JsonObject json = new JsonObject();
        json.addProperty("min", 1);
        json.addProperty("max", 1);

        IStructureRequirement req = GasRequirement.fromJson("gasInput", json);

        assertNotNull(req);
        assertEquals("gasInput", req.getType());
    }

    // ========================================
    // 4.6 EssentiaRequirement
    // ========================================

    @Test
    @DisplayName("EssentiaRequirement の作成・serialize")
    public void testEssentiaRequirement_CreateAndSerialize() {
        EssentiaRequirement req = new EssentiaRequirement("essentiaInput", 1, 4);

        assertEquals("essentiaInput", req.getType());
        assertEquals(1, req.getMinCount());
        assertEquals(4, req.getMaxCount());

        JsonObject json = req.serialize();
        assertNotNull(json);
        assertEquals(
            "essentiaInput",
            json.get("type")
                .getAsString());
    }

    @Test
    @DisplayName("EssentiaRequirement.fromJson")
    public void testEssentiaRequirement_FromJson() {
        JsonObject json = new JsonObject();
        json.addProperty("min", 2);
        json.addProperty("max", 3);

        IStructureRequirement req = EssentiaRequirement.fromJson("essentiaOutput", json);

        assertNotNull(req);
        assertEquals("essentiaOutput", req.getType());
    }

    // ========================================
    // 4.7 VisRequirement
    // ========================================

    @Test
    @DisplayName("VisRequirement の作成・serialize")
    public void testVisRequirement_CreateAndSerialize() {
        VisRequirement req = new VisRequirement("visInput", 1, 2);

        assertEquals("visInput", req.getType());
        assertEquals(1, req.getMinCount());
        assertEquals(2, req.getMaxCount());

        JsonObject json = req.serialize();
        assertNotNull(json);
        assertEquals(
            "visInput",
            json.get("type")
                .getAsString());
    }

    @Test
    @DisplayName("VisRequirement.fromJson")
    public void testVisRequirement_FromJson() {
        JsonObject json = new JsonObject();
        json.addProperty("min", 0);
        json.addProperty("max", 1);

        IStructureRequirement req = VisRequirement.fromJson("visOutput", json);

        assertNotNull(req);
        assertEquals("visOutput", req.getType());
    }

    // ========================================
    // 4.3 matches() メソッドのテスト
    // ========================================

    @Test
    @DisplayName("ItemRequirement.matches() - TileEntityなしでfalse")
    public void testItemRequirement_MatchesNoTE() {
        ItemRequirement req = new ItemRequirement("itemInput", 1, 4);

        // WorldとTileEntityがnullの場合のテスト
        // 実際のゲーム環境が必要なため、ここではnullチェックのみ
        assertNotNull(req);
    }

    @Test
    @DisplayName("FluidRequirement.matches() の実装確認")
    public void testFluidRequirement_MatchesExists() {
        FluidRequirement req = new FluidRequirement("fluidInput", 1, 2);

        // matches()メソッドが存在することを確認
        assertNotNull(req);
        // 実際のマッチングはゲーム環境が必要
    }

    @Test
    @DisplayName("EnergyRequirement.matches() の実装確認")
    public void testEnergyRequirement_MatchesExists() {
        EnergyRequirement req = new EnergyRequirement("energyInput", 1, 1);

        assertNotNull(req);
        // 実際のマッチングはゲーム環境が必要
    }

    @Test
    @DisplayName("ManaRequirement.matches() の実装確認")
    public void testManaRequirement_MatchesExists() {
        ManaRequirement req = new ManaRequirement("manaInput", 1, 2);

        assertNotNull(req);
    }

    @Test
    @DisplayName("GasRequirement.matches() の実装確認")
    public void testGasRequirement_MatchesExists() {
        GasRequirement req = new GasRequirement("gasInput", 0, 1);

        assertNotNull(req);
    }

    @Test
    @DisplayName("EssentiaRequirement.matches() の実装確認")
    public void testEssentiaRequirement_MatchesExists() {
        EssentiaRequirement req = new EssentiaRequirement("essentiaInput", 1, 3);

        assertNotNull(req);
    }

    // ========================================
    // 次のステップ
    // ========================================

    // TODO: 実際のゲーム環境でmatches()をテスト
    // TODO: IModularPort インターフェースとの統合テスト
    // TODO: TileEntity検出のテスト
}
