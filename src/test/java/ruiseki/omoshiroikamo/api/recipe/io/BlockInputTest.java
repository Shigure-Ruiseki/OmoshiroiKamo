package ruiseki.omoshiroikamo.api.recipe.io;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.test.RegistryMocker;

/**
 * BlockInput のユニットテスト
 *
 * ============================================
 * Block入力のテスト
 * ============================================
 *
 * BlockInput は、構造内の特定ブロックの存在を確認します。
 * ブロックを消費せず、存在のみを検証することも、
 * 消費したり置換したりすることも可能です。
 *
 * 3つのモード:
 * - チェックモード (replace指定のみ): ブロックの存在を確認
 * - 消費モード (consume=true): ブロックを空気に変更
 * - 置換モード (replace + block指定): 特定ブロックを別ブロックに置換
 *
 * バグ発見の優先度: ★★★★☆
 *
 * ============================================
 */
@DisplayName("BlockInput（Block入力）のテスト")
public class BlockInputTest {

    @BeforeAll
    public static void setUpAll() {
        RegistryMocker.mockAll();
    }

    // ========================================
    // コンストラクタのテスト
    // ========================================

    @Test
    @DisplayName("コンストラクタ（4引数レガシー）: 基本的な値を保持")
    public void testコンストラクタレガシー() {
        BlockInput input = new BlockInput('S', "minecraft:stone", 5, true);

        assertEquals("minecraft:stone", input.getBlock());
        assertNull(input.getReplace());
        assertEquals(5, input.getAmount());
        assertTrue(input.isConsume());
        assertFalse(input.isOptional());
    }

    @Test
    @DisplayName("コンストラクタ（6引数）: すべての値を保持")
    public void testコンストラクタ完全() {
        BlockInput input = new BlockInput('S', "minecraft:diamond_block", "minecraft:stone", 3, false, true);

        assertEquals("minecraft:diamond_block", input.getBlock());
        assertEquals("minecraft:stone", input.getReplace());
        assertEquals(3, input.getAmount());
        assertFalse(input.isConsume());
        assertTrue(input.isOptional());
    }

    // ========================================
    // getPortType() のテスト
    // ========================================

    @Test
    @DisplayName("getPortType: BLOCKを返す")
    public void testGetPortType() {
        BlockInput input = new BlockInput('S', "minecraft:stone", 1, false);

        assertEquals(IPortType.Type.BLOCK, input.getPortType());
    }

    // ========================================
    // JSON読み込み・書き込みのテスト
    // ========================================

    @Test
    @DisplayName("JSON: チェックモード（replaceのみ）をパース")
    public void testFromJsonチェックモード() {
        JsonObject json = new JsonObject();
        json.addProperty("symbol", "S");
        json.addProperty("replace", "minecraft:stone");
        json.addProperty("amount", 5);

        BlockInput input = BlockInput.fromJson(json);

        assertNotNull(input);
        assertNull(input.getBlock());
        assertEquals("minecraft:stone", input.getReplace());
        assertEquals(5, input.getAmount());
        assertFalse(input.isConsume());
        assertFalse(input.isOptional());
    }

    @Test
    @DisplayName("JSON: 置換モード（block + replace）をパース")
    public void testFromJson置換モード() {
        JsonObject json = new JsonObject();
        json.addProperty("symbol", "S");
        json.addProperty("block", "minecraft:diamond_block");
        json.addProperty("replace", "minecraft:stone");
        json.addProperty("amount", 3);

        BlockInput input = BlockInput.fromJson(json);

        assertNotNull(input);
        assertEquals("minecraft:diamond_block", input.getBlock());
        assertEquals("minecraft:stone", input.getReplace());
        assertEquals(3, input.getAmount());
    }

    @Test
    @DisplayName("JSON: 消費モード（consume=true）をパース")
    public void testFromJson消費モード() {
        JsonObject json = new JsonObject();
        json.addProperty("symbol", "S");
        json.addProperty("block", "minecraft:dirt");
        json.addProperty("amount", 2);
        json.addProperty("consume", true);

        BlockInput input = BlockInput.fromJson(json);

        assertNotNull(input);
        assertEquals("minecraft:dirt", input.getBlock());
        assertEquals(2, input.getAmount());
        assertTrue(input.isConsume());
    }

    @Test
    @DisplayName("JSON: amountなしでデフォルト値1")
    public void testFromJsonデフォルトAmount() {
        JsonObject json = new JsonObject();
        json.addProperty("symbol", "S");
        json.addProperty("block", "minecraft:glass");

        BlockInput input = BlockInput.fromJson(json);

        assertNotNull(input);
        assertEquals(1, input.getAmount(), "amountがない場合、デフォルト値1になるべき");
    }

    @Test
    @DisplayName("JSON: optionalフラグをパース")
    public void testFromJsonOptional() {
        JsonObject json = new JsonObject();
        json.addProperty("symbol", "S");
        json.addProperty("block", "minecraft:stone");
        json.addProperty("optional", true);

        BlockInput input = BlockInput.fromJson(json);

        assertNotNull(input);
        assertTrue(input.isOptional());
    }

    @Test
    @DisplayName("JSON: write()で正しく書き込み（基本）")
    public void testWrite基本() {
        BlockInput input = new BlockInput('S', "minecraft:stone", 5, false);

        JsonObject json = new JsonObject();
        input.write(json);

        assertEquals(
            "block",
            json.get("type")
                .getAsString());
        assertEquals(
            "S",
            json.get("symbol")
                .getAsString());
        assertEquals(
            "minecraft:stone",
            json.get("block")
                .getAsString());
        assertEquals(
            5,
            json.get("amount")
                .getAsInt());
        assertFalse(json.has("consume"), "consume=falseの場合、JSONに含まれないべき");
        assertFalse(json.has("optional"), "optional=falseの場合、JSONに含まれないべき");
    }

    @Test
    @DisplayName("JSON: write()で正しく書き込み（置換モード）")
    public void testWrite置換モード() {
        BlockInput input = new BlockInput('S', "minecraft:diamond_block", "minecraft:stone", 3, false, false);

        JsonObject json = new JsonObject();
        input.write(json);

        assertEquals(
            "block",
            json.get("type")
                .getAsString());
        assertEquals(
            "S",
            json.get("symbol")
                .getAsString());
        assertEquals(
            "minecraft:diamond_block",
            json.get("block")
                .getAsString());
        assertEquals(
            "minecraft:stone",
            json.get("replace")
                .getAsString());
        assertEquals(
            3,
            json.get("amount")
                .getAsInt());
    }

    @Test
    @DisplayName("JSON: write()で正しく書き込み（consume + optional）")
    public void testWriteフラグ() {
        BlockInput input = new BlockInput('S', "minecraft:dirt", null, 2, true, true);

        JsonObject json = new JsonObject();
        input.write(json);

        assertEquals(
            "block",
            json.get("type")
                .getAsString());
        assertTrue(json.has("consume"));
        assertTrue(
            json.get("consume")
                .getAsBoolean());
        assertTrue(json.has("optional"));
        assertTrue(
            json.get("optional")
                .getAsBoolean());
    }

    @Test
    @DisplayName("JSON: ラウンドトリップ")
    public void testJSONラウンドトリップ() {
        BlockInput originalInput = new BlockInput('M', "minecraft:gold_block", "minecraft:iron_block", 7, false, true);

        // 書き込み
        JsonObject json = new JsonObject();
        originalInput.write(json);

        // 読み込み
        BlockInput restoredInput = BlockInput.fromJson(json);

        assertEquals(originalInput.getBlock(), restoredInput.getBlock());
        assertEquals(originalInput.getReplace(), restoredInput.getReplace());
        assertEquals(originalInput.getAmount(), restoredInput.getAmount());
        assertEquals(originalInput.isConsume(), restoredInput.isConsume());
        assertEquals(originalInput.isOptional(), restoredInput.isOptional());
    }

    // ========================================
    // getRequiredAmount() のテスト
    // ========================================

    @Test
    @DisplayName("getRequiredAmount: amountを返す")
    public void testGetRequiredAmount() {
        BlockInput input = new BlockInput('S', "minecraft:stone", 10, false);

        assertEquals(10, input.getRequiredAmount());
    }

    // ========================================
    // Getter のテスト
    // ========================================

    @Test
    @DisplayName("getBlock: 正しいブロックIDを返す")
    public void testGetBlock() {
        BlockInput input = new BlockInput('S', "minecraft:diamond_block", 1, false);

        assertEquals("minecraft:diamond_block", input.getBlock());
    }

    @Test
    @DisplayName("getReplace: 正しい置換対象を返す")
    public void testGetReplace() {
        BlockInput input = new BlockInput('S', "minecraft:stone", "minecraft:dirt", 1, false, false);

        assertEquals("minecraft:dirt", input.getReplace());
    }

    @Test
    @DisplayName("getAmount: 正しい量を返す")
    public void testGetAmount() {
        BlockInput input = new BlockInput('S', "minecraft:stone", 7, false);

        assertEquals(7, input.getAmount());
    }

    @Test
    @DisplayName("isConsume: 正しい値を返す")
    public void testIsConsume() {
        BlockInput consumeInput = new BlockInput('S', "minecraft:stone", 1, true);
        BlockInput noConsumeInput = new BlockInput('S', "minecraft:stone", 1, false);

        assertTrue(consumeInput.isConsume());
        assertFalse(noConsumeInput.isConsume());
    }

    @Test
    @DisplayName("isOptional: 正しい値を返す")
    public void testIsOptional() {
        BlockInput optionalInput = new BlockInput('S', "minecraft:stone", null, 1, false, true);
        BlockInput requiredInput = new BlockInput('S', "minecraft:stone", null, 1, false, false);

        assertTrue(optionalInput.isOptional());
        assertFalse(requiredInput.isOptional());
    }
}
