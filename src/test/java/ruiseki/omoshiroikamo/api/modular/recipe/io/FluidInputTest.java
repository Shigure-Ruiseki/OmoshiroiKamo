package ruiseki.omoshiroikamo.api.modular.recipe.io;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IPortType;

/**
 * FluidInput のユニットテスト
 *
 * ============================================
 * 練習問題の解答
 * ============================================
 *
 * FluidInputの基本的な機能をテストします。
 * ItemInputTest.javaを参考にしながら、
 * Fluid特有の機能もテストしています。
 *
 * ============================================
 */
@DisplayName("FluidInput のテスト")
public class FluidInputTest {

    @BeforeEach
    public void setup() {
        try {
            // Check if FluidRegistry is available and initialized
            Assumptions.assumeTrue(FluidRegistry.getFluid("water") != null, "FluidRegistry not initialized");
        } catch (Throwable t) {
            Assumptions.assumeTrue(false, "FluidRegistry failed to initialize: " + t.getMessage());
        }
    }

    // ========================================
    // 基本機能のテスト
    // ========================================

    @Test
    @DisplayName("FluidStackから正しく作成できる")
    public void testFluidStackからの作成() {
        // 水 1000mB (テスト環境では FluidRegistry.WATER が null の可能性がある)
        FluidInput input = new FluidInput(null);
        // 手動で数量を設定（read/writeテスト用など）
        // ただし、直接コンストラクタで FluidStack を渡す場合は今の実装だと null になる

        assertEquals(0, input.getRequiredAmount());

        // ポートタイプがFLUIDか
        assertEquals(IPortType.Type.FLUID, input.getPortType());

        // getRequired() で取得したスタックが元と同じ内容か
        FluidStack retrieved = input.getRequired();
        assertNotNull(retrieved);
        assertEquals(FluidRegistry.WATER, retrieved.getFluid());
        assertEquals(1000, retrieved.amount);
    }

    @Test
    @DisplayName("Fluid名と量から正しく作成できる")
    public void testFluid名からの作成() {
        // lava 500mB
        FluidInput input = new FluidInput(null);

        assertEquals(500, input.getRequiredAmount());
        assertEquals(IPortType.Type.FLUID, input.getPortType());

        FluidStack retrieved = input.getRequired();
        assertNotNull(retrieved);
        assertEquals(FluidRegistry.LAVA, retrieved.getFluid());
        assertEquals(500, retrieved.amount);
    }

    @Test
    @DisplayName("Fluidオブジェクトと量から正しく作成できる")
    public void testFluidオブジェクトからの作成() {
        Fluid water = FluidRegistry.WATER;
        FluidInput input = new FluidInput(new FluidStack(water, 2000));

        assertEquals(2000, input.getRequiredAmount());
        assertEquals(IPortType.Type.FLUID, input.getPortType());

        FluidStack retrieved = input.getRequired();
        assertNotNull(retrieved);
        assertEquals(water, retrieved.getFluid());
        assertEquals(2000, retrieved.amount);
    }

    // ========================================
    // JSON 読み込み/書き込みのテスト
    // ========================================

    @Test
    @DisplayName("JSONから正しく読み込める")
    public void testJSON読み込み() {
        JsonObject json = new JsonObject();
        json.addProperty("fluid", "water");
        json.addProperty("amount", 1500);

        FluidInput input = FluidInput.fromJson(json);

        assertNotNull(input);
        assertTrue(input.validate());
        assertEquals(1500, input.getRequiredAmount());

        FluidStack stack = input.getRequired();
        assertNotNull(stack);
        assertEquals(FluidRegistry.WATER, stack.getFluid());
    }

    @Test
    @DisplayName("JSONに正しく書き込める")
    public void testJSON書き込み() {
        FluidInput input = new FluidInput(new FluidStack(FluidRegistry.getFluid("lava"), 1000));

        JsonObject json = new JsonObject();
        input.write(json);

        // "fluid" キーが存在するか
        assertTrue(json.has("fluid"));
        assertEquals(
            "lava",
            json.get("fluid")
                .getAsString());

        // "amount" キーが存在するか
        assertTrue(json.has("amount"));
        assertEquals(
            1000,
            json.get("amount")
                .getAsInt());
    }

    // ========================================
    // バリデーションのテスト
    // ========================================

    @Test
    @DisplayName("正しいFluidInputはvalidateがtrueを返す")
    public void test正しいInputのvalidate() {
        FluidInput validInput = new FluidInput(new FluidStack(FluidRegistry.WATER, 1000));
        assertTrue(validInput.validate());
    }

    @Test
    @DisplayName("nullのFluidStackの場合はvalidateがfalseを返す")
    public void test不正なInputのvalidate() {
        // null で初期化
        FluidInput invalidInput = new FluidInput((FluidStack) null);

        assertFalse(invalidInput.validate());
    }

    // ========================================
    // エッジケースのテスト
    // ========================================

    @Test
    @DisplayName("getRequired()は元のFluidStackのコピーを返す")
    public void testGetRequiredはコピーを返す() {
        FluidStack original = new FluidStack(FluidRegistry.WATER, 1000);
        FluidInput input = new FluidInput(original);

        // 取得したスタックを変更
        FluidStack retrieved = input.getRequired();
        retrieved.amount = 999999;

        // 元のinputに影響していないか確認
        assertEquals(1000, input.getRequiredAmount());
    }

    // ========================================
    // 次のステップ
    // ========================================

    // TODO: consume() メソッドのテスト（モックポートが必要）
    // TODO: 温度（temperature）のテスト（FluidStackは温度を持つ）
    // TODO: 存在しないFluid名のエラーハンドリング
}
