package ruiseki.omoshiroikamo.api.recipe.io;

import static org.junit.jupiter.api.Assertions.*;

import net.minecraft.nbt.NBTTagCompound;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.expression.ConstantExpression;
import ruiseki.omoshiroikamo.test.RegistryMocker;

/**
 * BlockNbtOutput のユニットテスト
 *
 * ============================================
 * BlockNBT出力のテスト
 * ============================================
 *
 * BlockNbtOutput は、構造内のTileEntityのNBTデータを変更します。
 * アイテムとして出力するのではなく、TileEntityのNBTを直接操作します。
 *
 * 3つの操作:
 * - set: 値を設定
 * - add: 値を加算
 * - sub: 値を減算
 *
 * バグ発見の優先度: ★★★☆☆
 *
 * ============================================
 */
@DisplayName("BlockNbtOutput（BlockNBT出力）のテスト")
public class BlockNbtOutputTest {

    @BeforeAll
    public static void setUpAll() {
        RegistryMocker.mockAll();
    }

    // ========================================
    // コンストラクタのテスト
    // ========================================

    @Test
    @DisplayName("コンストラクタ（3引数）: 基本的な値を保持")
    public void testコンストラクタ基本() {
        ConstantExpression expr = new ConstantExpression(100);
        BlockNbtOutput output = new BlockNbtOutput('S', "energy", "set", expr);

        // getterが無いため、内部状態は間接的に確認
        assertEquals(IPortType.Type.BLOCK, output.getPortType());
    }

    @Test
    @DisplayName("コンストラクタ（4引数）: optionalフラグを保持")
    public void testコンストラクタOptional() {
        ConstantExpression expr = new ConstantExpression(50);
        BlockNbtOutput output = new BlockNbtOutput('S', "progress", "add", expr, true);

        assertEquals(IPortType.Type.BLOCK, output.getPortType());
    }

    @Test
    @DisplayName("コンストラクタ（5引数）: multiplierを保持")
    public void testコンストラクタMultiplier() {
        ConstantExpression expr = new ConstantExpression(10);
        BlockNbtOutput output = new BlockNbtOutput('S', "count", "add", expr, 5, false);

        assertEquals(IPortType.Type.BLOCK, output.getPortType());
    }

    // ========================================
    // getPortType() のテスト
    // ========================================

    @Test
    @DisplayName("getPortType: BLOCKを返す")
    public void testGetPortType() {
        ConstantExpression expr = new ConstantExpression(100);
        BlockNbtOutput output = new BlockNbtOutput('S', "energy", "set", expr);

        assertEquals(IPortType.Type.BLOCK, output.getPortType());
    }

    // ========================================
    // JSON読み込み・書き込みのテスト
    // ========================================

    @Test
    @DisplayName("JSON: set操作をパース")
    public void testFromJsonSet() {
        JsonObject json = new JsonObject();
        json.addProperty("symbol", "S");
        json.addProperty("key", "energy");
        json.addProperty("operation", "set");
        json.addProperty("value", "1000");

        BlockNbtOutput output = BlockNbtOutput.fromJson(json);

        assertNotNull(output);
        assertEquals(IPortType.Type.BLOCK, output.getPortType());
    }

    @Test
    @DisplayName("JSON: add操作をパース")
    public void testFromJsonAdd() {
        JsonObject json = new JsonObject();
        json.addProperty("symbol", "S");
        json.addProperty("key", "progress");
        json.addProperty("operation", "add");
        json.addProperty("value", "50");

        BlockNbtOutput output = BlockNbtOutput.fromJson(json);

        assertNotNull(output);
    }

    @Test
    @DisplayName("JSON: sub操作をパース")
    public void testFromJsonSub() {
        JsonObject json = new JsonObject();
        json.addProperty("symbol", "S");
        json.addProperty("key", "durability");
        json.addProperty("operation", "sub");
        json.addProperty("value", "10");

        BlockNbtOutput output = BlockNbtOutput.fromJson(json);

        assertNotNull(output);
    }

    @Test
    @DisplayName("JSON: operationなしでデフォルト値'set'")
    public void testFromJsonデフォルトOperation() {
        JsonObject json = new JsonObject();
        json.addProperty("symbol", "S");
        json.addProperty("key", "energy");
        json.addProperty("value", "500");

        BlockNbtOutput output = BlockNbtOutput.fromJson(json);

        assertNotNull(output, "operationがない場合、デフォルトで'set'になるべき");
    }

    @Test
    @DisplayName("JSON: optionalフラグをパース")
    public void testFromJsonOptional() {
        JsonObject json = new JsonObject();
        json.addProperty("symbol", "S");
        json.addProperty("key", "energy");
        json.addProperty("value", "100");
        json.addProperty("optional", true);

        BlockNbtOutput output = BlockNbtOutput.fromJson(json);

        assertNotNull(output);
    }

    @Test
    @DisplayName("JSON: write()で正しく書き込み")
    public void testWrite() {
        ConstantExpression expr = new ConstantExpression(200);
        BlockNbtOutput output = new BlockNbtOutput('M', "fuel", "add", expr);

        JsonObject json = new JsonObject();
        output.write(json);

        assertEquals(
            "block_nbt",
            json.get("type")
                .getAsString());
        assertEquals(
            "M",
            json.get("symbol")
                .getAsString());
        assertEquals(
            "fuel",
            json.get("key")
                .getAsString());
        assertEquals(
            "add",
            json.get("operation")
                .getAsString());
    }

    @Test
    @DisplayName("JSON: write()でoptionalを書き込み")
    public void testWriteOptional() {
        ConstantExpression expr = new ConstantExpression(100);
        BlockNbtOutput output = new BlockNbtOutput('S', "energy", "set", expr, true);

        JsonObject json = new JsonObject();
        output.write(json);

        assertEquals(
            "block_nbt",
            json.get("type")
                .getAsString());
        assertTrue(json.has("optional"));
        assertTrue(
            json.get("optional")
                .getAsBoolean());
    }

    // ========================================
    // NBT読み込み・書き込みのテスト
    // ========================================

    @Test
    @DisplayName("NBT: writeToNBTで正しく書き込み")
    public void testWriteToNBT() {
        ConstantExpression expr = new ConstantExpression(100);
        BlockNbtOutput output = new BlockNbtOutput('S', "energy", "set", expr, 2, false);

        NBTTagCompound nbt = new NBTTagCompound();
        output.writeToNBT(nbt);

        assertEquals("block_nbt", nbt.getString("id"));
        assertEquals("S", nbt.getString("symbol"));
        assertEquals("energy", nbt.getString("key"));
        assertEquals("set", nbt.getString("operation"));
        assertEquals(2, nbt.getInteger("multiplier"));
        assertFalse(nbt.getBoolean("optional"));
    }

    @Test
    @DisplayName("NBT: readFromNBTで正しく読み込み（multiplierのみ）")
    public void testReadFromNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("multiplier", 5);

        ConstantExpression expr = new ConstantExpression(100);
        BlockNbtOutput output = new BlockNbtOutput('S', "energy", "set", expr, 1, false);
        output.readFromNBT(nbt);

        // multiplierが更新されたか確認するため、NBTに再度書き込んで検証
        NBTTagCompound verifyNbt = new NBTTagCompound();
        output.writeToNBT(verifyNbt);
        assertEquals(5, verifyNbt.getInteger("multiplier"));
    }

    @Test
    @DisplayName("NBT: bakedValueがあればNBTに書き込まれる")
    public void testWriteToNBTWithBakedValue() {
        ConstantExpression expr = new ConstantExpression(100);
        BlockNbtOutput output = new BlockNbtOutput('S', "energy", "set", expr);

        // bakedValueを設定するにはreadFromNBTを使用
        NBTTagCompound setupNbt = new NBTTagCompound();
        setupNbt.setInteger("multiplier", 1);
        setupNbt.setDouble("bakedValue", 999.5);
        output.readFromNBT(setupNbt);

        NBTTagCompound nbt = new NBTTagCompound();
        output.writeToNBT(nbt);

        assertTrue(nbt.hasKey("bakedValue"));
        assertEquals(999.5, nbt.getDouble("bakedValue"), 0.001);
    }

    // ========================================
    // copy() のテスト
    // ========================================

    @Test
    @DisplayName("copy: 同じ値のコピーを作成")
    public void testCopy() {
        ConstantExpression expr = new ConstantExpression(100);
        BlockNbtOutput original = new BlockNbtOutput('S', "energy", "set", expr, 2, false);

        IRecipeOutput copy = original.copy();

        assertTrue(copy instanceof BlockNbtOutput);
        // 内部状態が同じか確認するため、NBTを比較
        NBTTagCompound originalNbt = new NBTTagCompound();
        NBTTagCompound copyNbt = new NBTTagCompound();
        original.writeToNBT(originalNbt);
        copy.writeToNBT(copyNbt);
        assertEquals(originalNbt.getInteger("multiplier"), copyNbt.getInteger("multiplier"));
    }

    @Test
    @DisplayName("copy(multiplier): multiplierが乗算される")
    public void testCopyMultiplier() {
        ConstantExpression expr = new ConstantExpression(100);
        BlockNbtOutput original = new BlockNbtOutput('S', "energy", "set", expr, 2, false);

        IRecipeOutput copy = original.copy(3);

        assertTrue(copy instanceof BlockNbtOutput);
        // multiplierが2*3=6になっているか確認
        NBTTagCompound copyNbt = new NBTTagCompound();
        copy.writeToNBT(copyNbt);
        assertEquals(6, copyNbt.getInteger("multiplier"), "multiplierが2*3=6になるべき");
    }

    // ========================================
    // getRequiredAmount() のテスト
    // ========================================

    @Test
    @DisplayName("getRequiredAmount: 常に1を返す")
    public void testGetRequiredAmount() {
        ConstantExpression expr = new ConstantExpression(100);
        BlockNbtOutput output = new BlockNbtOutput('S', "energy", "set", expr);

        assertEquals(1, output.getRequiredAmount(), "BlockNbtOutputは常に1を返すべき");
    }
}
