package ruiseki.omoshiroikamo.api.recipe.io;

import static org.junit.jupiter.api.Assertions.*;

import net.minecraft.nbt.NBTTagCompound;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.test.RegistryMocker;

/**
 * BlockOutput のユニットテスト
 *
 * ============================================
 * Block出力のテスト
 * ============================================
 *
 * BlockOutput は、レシピの出力として構造内のブロックを変更します。
 * アイテムとして出力するのではなく、ワールドのブロックを直接操作します。
 *
 * 3つのモード:
 * - 生成モード (block指定のみ): 空気/置換可能な位置にブロックを設置
 * - 置換モード (block + replace指定): 特定ブロックを別ブロックに置換
 * - 削除モード (block未指定): ブロックを空気に変更
 *
 * バグ発見の優先度: ★★★★☆
 *
 * ============================================
 */
@DisplayName("BlockOutput（Block出力）のテスト")
public class BlockOutputTest {

    @BeforeAll
    public static void setUpAll() {
        RegistryMocker.mockAll();
    }

    // ========================================
    // コンストラクタのテスト
    // ========================================

    @Test
    @DisplayName("コンストラクタ（4引数）: 基本的な値を保持")
    public void testコンストラクタ基本() {
        BlockOutput output = new BlockOutput('S', "minecraft:stone", null, 1);

        assertEquals('S', output.getSymbol());
        assertEquals("minecraft:stone", output.getBlock());
        assertNull(output.getReplace());
        assertEquals(1, output.getAmount());
    }

    @Test
    @DisplayName("コンストラクタ（6引数）: すべての値を保持")
    public void testコンストラクタ完全() {
        BlockOutput output = new BlockOutput('S', "minecraft:stone", "minecraft:dirt", 2, true, null);

        assertEquals('S', output.getSymbol());
        assertEquals("minecraft:stone", output.getBlock());
        assertEquals("minecraft:dirt", output.getReplace());
        assertEquals(2, output.getAmount());
    }

    // ========================================
    // getPortType() のテスト
    // ========================================

    @Test
    @DisplayName("getPortType: BLOCKを返す")
    public void testGetPortType() {
        BlockOutput output = new BlockOutput('S', "minecraft:stone", null, 1);

        assertEquals(IPortType.Type.BLOCK, output.getPortType());
    }

    // ========================================
    // JSON読み込み・書き込みのテスト
    // ========================================

    @Test
    @DisplayName("JSON: 生成モード（block指定のみ）をパース")
    public void testFromJson生成モード() {
        JsonObject json = new JsonObject();
        json.addProperty("symbol", "S");
        json.addProperty("block", "minecraft:stone");
        json.addProperty("amount", 5);

        BlockOutput output = BlockOutput.fromJson(json);

        assertNotNull(output);
        assertEquals('S', output.getSymbol());
        assertEquals("minecraft:stone", output.getBlock());
        assertNull(output.getReplace());
        assertEquals(5, output.getAmount());
    }

    @Test
    @DisplayName("JSON: 置換モード（block + replace）をパース")
    public void testFromJson置換モード() {
        JsonObject json = new JsonObject();
        json.addProperty("symbol", "S");
        json.addProperty("block", "minecraft:diamond_block");
        json.addProperty("replace", "minecraft:stone");
        json.addProperty("amount", 3);

        BlockOutput output = BlockOutput.fromJson(json);

        assertNotNull(output);
        assertEquals('S', output.getSymbol());
        assertEquals("minecraft:diamond_block", output.getBlock());
        assertEquals("minecraft:stone", output.getReplace());
        assertEquals(3, output.getAmount());
    }

    @Test
    @DisplayName("JSON: 削除モード（block未指定）をパース")
    public void testFromJson削除モード() {
        JsonObject json = new JsonObject();
        json.addProperty("symbol", "S");
        json.addProperty("replace", "minecraft:dirt");
        json.addProperty("amount", 2);

        BlockOutput output = BlockOutput.fromJson(json);

        assertNotNull(output);
        assertEquals('S', output.getSymbol());
        assertNull(output.getBlock());
        assertEquals("minecraft:dirt", output.getReplace());
        assertEquals(2, output.getAmount());
    }

    @Test
    @DisplayName("JSON: amountなしでデフォルト値1")
    public void testFromJsonデフォルトAmount() {
        JsonObject json = new JsonObject();
        json.addProperty("symbol", "S");
        json.addProperty("block", "minecraft:glass");

        BlockOutput output = BlockOutput.fromJson(json);

        assertNotNull(output);
        assertEquals(1, output.getAmount(), "amountがない場合、デフォルト値1になるべき");
    }

    @Test
    @DisplayName("JSON: optionalフラグをパース")
    public void testFromJsonOptional() {
        JsonObject json = new JsonObject();
        json.addProperty("symbol", "S");
        json.addProperty("block", "minecraft:stone");
        json.addProperty("optional", true);

        BlockOutput output = BlockOutput.fromJson(json);

        assertNotNull(output);
        // optionalは現在getterがないため、内部状態の確認は省略
    }

    @Test
    @DisplayName("JSON: write()で正しく書き込み（生成モード）")
    public void testWrite生成モード() {
        BlockOutput output = new BlockOutput('S', "minecraft:stone", null, 5);

        JsonObject json = new JsonObject();
        output.write(json);

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
        assertFalse(json.has("replace"));
        assertEquals(
            5,
            json.get("amount")
                .getAsInt());
    }

    @Test
    @DisplayName("JSON: write()で正しく書き込み（置換モード）")
    public void testWrite置換モード() {
        BlockOutput output = new BlockOutput('S', "minecraft:diamond_block", "minecraft:stone", 3);

        JsonObject json = new JsonObject();
        output.write(json);

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
    @DisplayName("JSON: ラウンドトリップ")
    public void testJSONラウンドトリップ() {
        BlockOutput originalOutput = new BlockOutput('M', "minecraft:gold_block", "minecraft:iron_block", 7);

        // 書き込み
        JsonObject json = new JsonObject();
        originalOutput.write(json);

        // 読み込み
        BlockOutput restoredOutput = BlockOutput.fromJson(json);

        assertEquals(originalOutput.getSymbol(), restoredOutput.getSymbol());
        assertEquals(originalOutput.getBlock(), restoredOutput.getBlock());
        assertEquals(originalOutput.getReplace(), restoredOutput.getReplace());
        assertEquals(originalOutput.getAmount(), restoredOutput.getAmount());
    }

    // ========================================
    // NBT書き込みのテスト
    // ========================================

    @Test
    @DisplayName("NBT: writeToNBTで正しく書き込み")
    public void testWriteToNBT() {
        BlockOutput output = new BlockOutput('S', "minecraft:stone", "minecraft:dirt", 3);

        NBTTagCompound nbt = new NBTTagCompound();
        output.writeToNBT(nbt);

        assertEquals("S", nbt.getString("symbol"));
        assertEquals("minecraft:stone", nbt.getString("block"));
        assertEquals("minecraft:dirt", nbt.getString("replace"));
        assertEquals(3, nbt.getInteger("amount"));
    }

    @Test
    @DisplayName("NBT: blockがnullの場合は書き込まない")
    public void testWriteToNBT削除モード() {
        BlockOutput output = new BlockOutput('S', null, "minecraft:dirt", 1);

        NBTTagCompound nbt = new NBTTagCompound();
        output.writeToNBT(nbt);

        assertEquals("S", nbt.getString("symbol"));
        assertFalse(nbt.hasKey("block"), "blockがnullの場合、NBTに書き込まれないべき");
        assertTrue(nbt.hasKey("replace"));
    }

    // ========================================
    // copy() のテスト
    // ========================================

    @Test
    @DisplayName("copy: 同じ値のコピーを作成")
    public void testCopy() {
        BlockOutput original = new BlockOutput('S', "minecraft:stone", null, 5);

        IRecipeOutput copy = original.copy();

        assertTrue(copy instanceof BlockOutput);
        BlockOutput blockCopy = (BlockOutput) copy;
        assertEquals(original.getSymbol(), blockCopy.getSymbol());
        assertEquals(original.getBlock(), blockCopy.getBlock());
        assertEquals(original.getAmount(), blockCopy.getAmount());
    }

    @Test
    @DisplayName("copy(multiplier): 量が倍数になる")
    public void testCopyMultiplier() {
        BlockOutput original = new BlockOutput('S', "minecraft:stone", null, 5);

        IRecipeOutput copy = original.copy(3);

        assertTrue(copy instanceof BlockOutput);
        BlockOutput blockCopy = (BlockOutput) copy;
        assertEquals(original.getSymbol(), blockCopy.getSymbol());
        assertEquals(original.getBlock(), blockCopy.getBlock());
        assertEquals(15, blockCopy.getAmount(), "量が3倍になるべき");
    }

    // ========================================
    // getRequiredAmount() のテスト
    // ========================================

    @Test
    @DisplayName("getRequiredAmount: amountを返す")
    public void testGetRequiredAmount() {
        BlockOutput output = new BlockOutput('S', "minecraft:stone", null, 10);

        assertEquals(10, output.getRequiredAmount());
    }

    // ========================================
    // Getter のテスト
    // ========================================

    @Test
    @DisplayName("getSymbol: 正しいシンボルを返す")
    public void testGetSymbol() {
        BlockOutput output = new BlockOutput('X', "minecraft:stone", null, 1);

        assertEquals('X', output.getSymbol());
    }

    @Test
    @DisplayName("getBlock: 正しいブロックIDを返す")
    public void testGetBlock() {
        BlockOutput output = new BlockOutput('S', "minecraft:diamond_block", null, 1);

        assertEquals("minecraft:diamond_block", output.getBlock());
    }

    @Test
    @DisplayName("getReplace: 正しい置換対象を返す")
    public void testGetReplace() {
        BlockOutput output = new BlockOutput('S', "minecraft:stone", "minecraft:dirt", 1);

        assertEquals("minecraft:dirt", output.getReplace());
    }

    @Test
    @DisplayName("getAmount: 正しい量を返す")
    public void testGetAmount() {
        BlockOutput output = new BlockOutput('S', "minecraft:stone", null, 7);

        assertEquals(7, output.getAmount());
    }
}
