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
 * GasOutput のユニットテスト
 *
 * ============================================
 * Gas出力のテスト（Mekanism連携）
 * ============================================
 *
 * GasOutput は、レシピの出力として Gas を提供します。
 * Mekanismの気体システムと連携します。
 *
 * - gasName: Gas名（例: "hydrogen", "oxygen"）
 * - amount: 量（単位はMekanismの規定に従う）
 *
 * バグ発見の優先度: ★★★☆☆
 *
 * ============================================
 */
@DisplayName("GasOutput（Gas出力）のテスト")
public class GasOutputTest {

    @BeforeAll
    public static void setUpAll() {
        RegistryMocker.mockAll();
    }

    // ========================================
    // コンストラクタのテスト
    // ========================================

    @Test
    @DisplayName("コンストラクタ: 正常に値を保持")
    public void testコンストラクタ() {
        GasOutput output = new GasOutput("hydrogen", 1000);

        assertEquals("hydrogen", output.getGasName());
        assertEquals(1000, output.getAmount());
    }

    // ========================================
    // getPortType() のテスト
    // ========================================

    @Test
    @DisplayName("getPortType: GASを返す")
    public void testGetPortType() {
        GasOutput output = new GasOutput("oxygen", 500);

        assertEquals(IPortType.Type.GAS, output.getPortType());
    }

    // ========================================
    // validate() のテスト
    // ========================================

    @Test
    @DisplayName("validate: 正常な値でtrue")
    public void testValidate正常() {
        GasOutput output = new GasOutput("hydrogen", 1000);

        assertTrue(output.validate(), "正常な値の場合、trueを返すべき");
    }

    @Test
    @DisplayName("validate: 空のgasNameでfalse")
    public void testValidate空のGasName() {
        GasOutput output = new GasOutput("", 1000);

        assertFalse(output.validate(), "空のgasNameの場合、falseを返すべき");
    }

    @Test
    @DisplayName("validate: nullのgasNameでfalse")
    public void testValidateNullGasName() {
        GasOutput output = new GasOutput(null, 1000);

        assertFalse(output.validate(), "nullのgasNameの場合、falseを返すべき");
    }

    @Test
    @DisplayName("validate: amount=0でfalse")
    public void testValidateゼロAmount() {
        GasOutput output = new GasOutput("hydrogen", 0);

        assertFalse(output.validate(), "amount=0の場合、falseを返すべき");
    }

    @Test
    @DisplayName("validate: 負のamountでfalse")
    public void testValidate負のAmount() {
        GasOutput output = new GasOutput("hydrogen", -100);

        assertFalse(output.validate(), "負のamountの場合、falseを返すべき");
    }

    // ========================================
    // JSON読み込み・書き込みのテスト
    // ========================================

    @Test
    @DisplayName("JSON: fromJsonで正しく読み込み")
    public void testFromJson() {
        JsonObject json = new JsonObject();
        json.addProperty("gas", "hydrogen");
        json.addProperty("amount", 2000);

        GasOutput output = GasOutput.fromJson(json);

        assertNotNull(output);
        assertEquals("hydrogen", output.getGasName());
        assertEquals(2000, output.getAmount());
    }

    @Test
    @DisplayName("JSON: amountなしでデフォルト値1000")
    public void testFromJsonデフォルトAmount() {
        JsonObject json = new JsonObject();
        json.addProperty("gas", "oxygen");

        GasOutput output = GasOutput.fromJson(json);

        assertNotNull(output);
        assertEquals("oxygen", output.getGasName());
        assertEquals(1000, output.getAmount(), "amountがない場合、デフォルト値1000になるべき");
    }

    @Test
    @DisplayName("JSON: write()で正しく書き込み")
    public void testWrite() {
        GasOutput output = new GasOutput("hydrogen", 3000);

        JsonObject json = new JsonObject();
        output.write(json);

        assertEquals(
            "hydrogen",
            json.get("gas")
                .getAsString());
        assertEquals(
            3000,
            json.get("amount")
                .getAsInt());
    }

    @Test
    @DisplayName("JSON: ラウンドトリップ")
    public void testJSONラウンドトリップ() {
        GasOutput originalOutput = new GasOutput("oxygen", 1500);

        // 書き込み
        JsonObject json = new JsonObject();
        originalOutput.write(json);

        // 読み込み
        GasOutput restoredOutput = GasOutput.fromJson(json);

        assertEquals(originalOutput.getGasName(), restoredOutput.getGasName());
        assertEquals(originalOutput.getAmount(), restoredOutput.getAmount());
    }

    // ========================================
    // NBT読み込み・書き込みのテスト
    // ========================================

    @Test
    @DisplayName("NBT: writeToNBTで正しく書き込み")
    public void testWriteToNBT() {
        GasOutput output = new GasOutput("hydrogen", 1000);

        NBTTagCompound nbt = new NBTTagCompound();
        output.writeToNBT(nbt);

        assertEquals("gas", nbt.getString("id"));
        assertEquals("hydrogen", nbt.getString("gas"));
        assertEquals(1000, nbt.getInteger("amount"));
    }

    @Test
    @DisplayName("NBT: readFromNBTで正しく読み込み")
    public void testReadFromNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("gas", "oxygen");
        nbt.setInteger("amount", 2000);

        GasOutput output = new GasOutput("", 0);
        output.readFromNBT(nbt);

        assertEquals("oxygen", output.getGasName());
        assertEquals(2000, output.getAmount());
    }

    // ========================================
    // copy() のテスト
    // ========================================

    @Test
    @DisplayName("copy: 同じ値のコピーを作成")
    public void testCopy() {
        GasOutput original = new GasOutput("hydrogen", 1000);

        IRecipeOutput copy = original.copy();

        assertTrue(copy instanceof GasOutput);
        GasOutput gasCopy = (GasOutput) copy;
        assertEquals(original.getGasName(), gasCopy.getGasName());
        assertEquals(original.getAmount(), gasCopy.getAmount());
    }

    @Test
    @DisplayName("copy(multiplier): 量が倍数になる")
    public void testCopyMultiplier() {
        GasOutput original = new GasOutput("hydrogen", 1000);

        IRecipeOutput copy = original.copy(3);

        assertTrue(copy instanceof GasOutput);
        GasOutput gasCopy = (GasOutput) copy;
        assertEquals(original.getGasName(), gasCopy.getGasName());
        assertEquals(3000, gasCopy.getAmount(), "量が3倍になるべき");
    }

    // ========================================
    // getRequiredAmount() のテスト
    // ========================================

    @Test
    @DisplayName("getRequiredAmount: amountを返す")
    public void testGetRequiredAmount() {
        GasOutput output = new GasOutput("hydrogen", 5000);

        assertEquals(5000, output.getRequiredAmount());
    }
}
