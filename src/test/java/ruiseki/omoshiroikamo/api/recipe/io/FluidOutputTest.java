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
 * FluidOutput のユニットテスト
 *
 * ============================================
 * Fluid出力のテスト
 * ============================================
 *
 * FluidOutput は、レシピの出力として Fluid を提供します。
 *
 * - fluidName: Fluid名（例: "water", "lava"）
 * - amount: 量（mB）
 *
 * バグ発見の優先度: ★★★☆☆
 *
 * ============================================
 */
@DisplayName("FluidOutput（Fluid出力）のテスト")
public class FluidOutputTest {

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
        FluidOutput output = new FluidOutput("water", 1000);

        assertEquals("water", output.getFluidName());
        assertEquals(1000, output.getAmount());
    }

    // ========================================
    // getPortType() のテスト
    // ========================================

    @Test
    @DisplayName("getPortType: FLUIDを返す")
    public void testGetPortType() {
        FluidOutput output = new FluidOutput("lava", 500);

        assertEquals(IPortType.Type.FLUID, output.getPortType());
    }

    // ========================================
    // validate() のテスト
    // ========================================

    @Test
    @DisplayName("validate: 正常な値でtrue")
    public void testValidate正常() {
        FluidOutput output = new FluidOutput("water", 1000);

        assertTrue(output.validate(), "正常な値の場合、trueを返すべき");
    }

    @Test
    @DisplayName("validate: 空のfluidNameでfalse")
    public void testValidate空のFluidName() {
        FluidOutput output = new FluidOutput("", 1000);

        assertFalse(output.validate(), "空のfluidNameの場合、falseを返すべき");
    }

    @Test
    @DisplayName("validate: nullのfluidNameでfalse")
    public void testValidateNullFluidName() {
        FluidOutput output = new FluidOutput(null, 1000);

        assertFalse(output.validate(), "nullのfluidNameの場合、falseを返すべき");
    }

    @Test
    @DisplayName("validate: amount=0でfalse")
    public void testValidateゼロAmount() {
        FluidOutput output = new FluidOutput("water", 0);

        assertFalse(output.validate(), "amount=0の場合、falseを返すべき");
    }

    @Test
    @DisplayName("validate: 負のamountでfalse")
    public void testValidate負のAmount() {
        FluidOutput output = new FluidOutput("water", -100);

        assertFalse(output.validate(), "負のamountの場合、falseを返すべき");
    }

    // ========================================
    // JSON読み込み・書き込みのテスト
    // ========================================

    @Test
    @DisplayName("JSON: fromJsonで正しく読み込み")
    public void testFromJson() {
        JsonObject json = new JsonObject();
        json.addProperty("fluid", "water");
        json.addProperty("amount", 2000);

        FluidOutput output = FluidOutput.fromJson(json);

        assertNotNull(output);
        assertEquals("water", output.getFluidName());
        assertEquals(2000, output.getAmount());
    }

    @Test
    @DisplayName("JSON: amountなしでデフォルト値1000")
    public void testFromJsonデフォルトAmount() {
        JsonObject json = new JsonObject();
        json.addProperty("fluid", "lava");

        FluidOutput output = FluidOutput.fromJson(json);

        assertNotNull(output);
        assertEquals("lava", output.getFluidName());
        assertEquals(1000, output.getAmount(), "amountがない場合、デフォルト値1000になるべき");
    }

    @Test
    @DisplayName("JSON: write()で正しく書き込み")
    public void testWrite() {
        FluidOutput output = new FluidOutput("water", 3000);

        JsonObject json = new JsonObject();
        output.write(json);

        assertEquals(
            "water",
            json.get("fluid")
                .getAsString());
        assertEquals(
            3000,
            json.get("amount")
                .getAsInt());
    }

    @Test
    @DisplayName("JSON: ラウンドトリップ")
    public void testJSONラウンドトリップ() {
        FluidOutput originalOutput = new FluidOutput("lava", 1500);

        // 書き込み
        JsonObject json = new JsonObject();
        originalOutput.write(json);

        // 読み込み
        FluidOutput restoredOutput = FluidOutput.fromJson(json);

        assertEquals(originalOutput.getFluidName(), restoredOutput.getFluidName());
        assertEquals(originalOutput.getAmount(), restoredOutput.getAmount());
    }

    // ========================================
    // NBT読み込み・書き込みのテスト
    // ========================================

    @Test
    @DisplayName("NBT: writeToNBTで正しく書き込み")
    public void testWriteToNBT() {
        FluidOutput output = new FluidOutput("water", 1000);

        NBTTagCompound nbt = new NBTTagCompound();
        output.writeToNBT(nbt);

        assertEquals("fluid", nbt.getString("id"));
        assertEquals("water", nbt.getString("fluid"));
        assertEquals(1000, nbt.getInteger("amount"));
    }

    @Test
    @DisplayName("NBT: readFromNBTで正しく読み込み")
    public void testReadFromNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("fluid", "lava");
        nbt.setInteger("amount", 2000);

        FluidOutput output = new FluidOutput("", 0);
        output.readFromNBT(nbt);

        assertEquals("lava", output.getFluidName());
        assertEquals(2000, output.getAmount());
    }

    // ========================================
    // copy() のテスト
    // ========================================

    @Test
    @DisplayName("copy: 同じ値のコピーを作成")
    public void testCopy() {
        FluidOutput original = new FluidOutput("water", 1000);

        IRecipeOutput copy = original.copy();

        assertTrue(copy instanceof FluidOutput);
        FluidOutput fluidCopy = (FluidOutput) copy;
        assertEquals(original.getFluidName(), fluidCopy.getFluidName());
        assertEquals(original.getAmount(), fluidCopy.getAmount());
    }

    @Test
    @DisplayName("copy(multiplier): 量が倍数になる")
    public void testCopyMultiplier() {
        FluidOutput original = new FluidOutput("water", 1000);

        IRecipeOutput copy = original.copy(3);

        assertTrue(copy instanceof FluidOutput);
        FluidOutput fluidCopy = (FluidOutput) copy;
        assertEquals(original.getFluidName(), fluidCopy.getFluidName());
        assertEquals(3000, fluidCopy.getAmount(), "量が3倍になるべき");
    }

    // ========================================
    // getRequiredAmount() のテスト
    // ========================================

    @Test
    @DisplayName("getRequiredAmount: amountを返す")
    public void testGetRequiredAmount() {
        FluidOutput output = new FluidOutput("water", 5000);

        assertEquals(5000, output.getRequiredAmount());
    }
}
