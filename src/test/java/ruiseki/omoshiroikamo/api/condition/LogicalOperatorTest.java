package ruiseki.omoshiroikamo.api.condition;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.test.RegistryMocker;

/**
 * LogicalOperator (OpAnd, OpOr, OpNot, OpNand, OpNor, OpXor) のユニットテスト
 *
 * ============================================
 * 論理演算子の包括的テスト
 * ============================================
 *
 * 6つの論理演算子をテストします:
 * - OpAnd: すべてtrueならtrue（論理積）
 * - OpOr: いずれかtrueならtrue（論理和）
 * - OpNot: 反転（否定）
 * - OpNand: すべてtrueならfalse（否定積）
 * - OpNor: いずれかtrueならfalse（否定和）
 * - OpXor: ちょうど1つtrueならtrue（排他的論理和）
 *
 * バグ発見の優先度: ★★★★★（最重要）
 *
 * ============================================
 */
@DisplayName("LogicalOperator（論理演算子）の包括テスト")
public class LogicalOperatorTest {

    @BeforeAll
    public static void setUpAll() {
        RegistryMocker.mockAll();
    }

    // ========================================
    // OpAnd（論理積）のテスト
    // ========================================

    @Test
    @DisplayName("OpAnd: すべてtrueの場合にtrue")
    public void testOpAndすべてTrue() {
        List<ICondition> conditions = Arrays.asList(new AlwaysTrue(), new AlwaysTrue(), new AlwaysTrue());
        OpAnd opAnd = new OpAnd(conditions);

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertTrue(opAnd.isMet(context), "すべてtrueの場合、Andはtrueになるべき");
    }

    @Test
    @DisplayName("OpAnd: 1つでもfalseならfalse")
    public void testOpAnd1つFalse() {
        List<ICondition> conditions = Arrays.asList(new AlwaysTrue(), new AlwaysFalse(), new AlwaysTrue());
        OpAnd opAnd = new OpAnd(conditions);

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertFalse(opAnd.isMet(context), "1つでもfalseがあれば、Andはfalseになるべき");
    }

    @Test
    @DisplayName("OpAnd: すべてfalseの場合にfalse")
    public void testOpAndすべてFalse() {
        List<ICondition> conditions = Arrays.asList(new AlwaysFalse(), new AlwaysFalse(), new AlwaysFalse());
        OpAnd opAnd = new OpAnd(conditions);

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertFalse(opAnd.isMet(context), "すべてfalseの場合、Andはfalseになるべき");
    }

    @Test
    @DisplayName("【重要】OpAnd: 空リストの場合にtrue")
    public void testOpAnd空リスト() {
        List<ICondition> emptyList = Collections.emptyList();
        OpAnd opAnd = new OpAnd(emptyList);

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        // 仕様確認: 空リストの場合、Andの単位元はtrueなのでtrueを返す
        assertTrue(opAnd.isMet(context), "空リストの場合、And単位元としてtrueになる");
    }

    // ========================================
    // OpOr（論理和）のテスト
    // ========================================

    @Test
    @DisplayName("OpOr: いずれかtrueならtrue")
    public void testOpOr1つTrue() {
        List<ICondition> conditions = Arrays.asList(new AlwaysFalse(), new AlwaysTrue(), new AlwaysFalse());
        OpOr opOr = new OpOr(conditions);

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertTrue(opOr.isMet(context), "1つでもtrueがあれば、Orはtrueになるべき");
    }

    @Test
    @DisplayName("OpOr: すべてfalseの場合にfalse")
    public void testOpOrすべてFalse() {
        List<ICondition> conditions = Arrays.asList(new AlwaysFalse(), new AlwaysFalse(), new AlwaysFalse());
        OpOr opOr = new OpOr(conditions);

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertFalse(opOr.isMet(context), "すべてfalseの場合、Orはfalseになるべき");
    }

    @Test
    @DisplayName("OpOr: すべてtrueの場合にtrue")
    public void testOpOrすべてTrue() {
        List<ICondition> conditions = Arrays.asList(new AlwaysTrue(), new AlwaysTrue(), new AlwaysTrue());
        OpOr opOr = new OpOr(conditions);

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertTrue(opOr.isMet(context), "すべてtrueの場合、Orはtrueになるべき");
    }

    @Test
    @DisplayName("OpOr: 空リストの場合にfalse")
    public void testOpOr空リスト() {
        List<ICondition> emptyList = Collections.emptyList();
        OpOr opOr = new OpOr(emptyList);

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertFalse(opOr.isMet(context), "空リストの場合、Orはfalseになるべき");
    }

    // ========================================
    // OpNot（否定）のテスト
    // ========================================

    @Test
    @DisplayName("OpNot: trueを反転してfalse")
    public void testOpNotTrue反転() {
        ICondition child = new AlwaysTrue();
        OpNot opNot = new OpNot(child);

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertFalse(opNot.isMet(context), "trueを反転してfalseになるべき");
    }

    @Test
    @DisplayName("OpNot: falseを反転してtrue")
    public void testOpNotFalse反転() {
        ICondition child = new AlwaysFalse();
        OpNot opNot = new OpNot(child);

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertTrue(opNot.isMet(context), "falseを反転してtrueになるべき");
    }

    @Test
    @DisplayName("OpNot: 二重否定でtrueに戻る")
    public void testOpNot二重否定() {
        ICondition child = new AlwaysTrue();
        OpNot opNot1 = new OpNot(child);
        OpNot opNot2 = new OpNot(opNot1);

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertTrue(opNot2.isMet(context), "二重否定でtrueに戻るべき");
    }

    // ========================================
    // OpNand（否定積）のテスト
    // ========================================

    @Test
    @DisplayName("OpNand: [T,T] → false")
    public void testOpNandTT() {
        List<ICondition> conditions = Arrays.asList(new AlwaysTrue(), new AlwaysTrue());
        OpNand opNand = new OpNand(conditions);

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertFalse(opNand.isMet(context), "NAND(T,T) = F になるべき");
    }

    @Test
    @DisplayName("OpNand: [T,F] → true")
    public void testOpNandTF() {
        List<ICondition> conditions = Arrays.asList(new AlwaysTrue(), new AlwaysFalse());
        OpNand opNand = new OpNand(conditions);

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertTrue(opNand.isMet(context), "NAND(T,F) = T になるべき");
    }

    @Test
    @DisplayName("OpNand: [F,F] → true")
    public void testOpNandFF() {
        List<ICondition> conditions = Arrays.asList(new AlwaysFalse(), new AlwaysFalse());
        OpNand opNand = new OpNand(conditions);

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertTrue(opNand.isMet(context), "NAND(F,F) = T になるべき");
    }

    @Test
    @DisplayName("OpNand: 空リストの場合にfalse")
    public void testOpNand空リスト() {
        List<ICondition> emptyList = Collections.emptyList();
        OpNand opNand = new OpNand(emptyList);

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        // 空リスト = すべてtrue扱い → NAND = false
        assertFalse(opNand.isMet(context), "空リストの場合、Nandはfalseになるべき");
    }

    // ========================================
    // OpNor（否定和）のテスト
    // ========================================

    @Test
    @DisplayName("OpNor: [F,F] → true")
    public void testOpNorFF() {
        List<ICondition> conditions = Arrays.asList(new AlwaysFalse(), new AlwaysFalse());
        OpNor opNor = new OpNor(conditions);

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertTrue(opNor.isMet(context), "NOR(F,F) = T になるべき");
    }

    @Test
    @DisplayName("OpNor: [T,F] → false")
    public void testOpNorTF() {
        List<ICondition> conditions = Arrays.asList(new AlwaysTrue(), new AlwaysFalse());
        OpNor opNor = new OpNor(conditions);

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertFalse(opNor.isMet(context), "NOR(T,F) = F になるべき");
    }

    @Test
    @DisplayName("OpNor: [T,T] → false")
    public void testOpNorTT() {
        List<ICondition> conditions = Arrays.asList(new AlwaysTrue(), new AlwaysTrue());
        OpNor opNor = new OpNor(conditions);

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertFalse(opNor.isMet(context), "NOR(T,T) = F になるべき");
    }

    @Test
    @DisplayName("OpNor: 空リストの場合にtrue")
    public void testOpNor空リスト() {
        List<ICondition> emptyList = Collections.emptyList();
        OpNor opNor = new OpNor(emptyList);

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        // 空リスト = すべてfalse扱い → NOR = true
        assertTrue(opNor.isMet(context), "空リストの場合、Norはtrueになるべき");
    }

    // ========================================
    // OpXor（排他的論理和）のテスト
    // ========================================

    @Test
    @DisplayName("OpXor: [T,F] → true（1つだけtrue）")
    public void testOpXorTF() {
        List<ICondition> conditions = Arrays.asList(new AlwaysTrue(), new AlwaysFalse());
        OpXor opXor = new OpXor(conditions);

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertTrue(opXor.isMet(context), "XOR(T,F) = T （1つだけtrueの場合true）");
    }

    @Test
    @DisplayName("OpXor: [T,T] → false（2つtrue）")
    public void testOpXorTT() {
        List<ICondition> conditions = Arrays.asList(new AlwaysTrue(), new AlwaysTrue());
        OpXor opXor = new OpXor(conditions);

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertFalse(opXor.isMet(context), "XOR(T,T) = F （2つtrueの場合false）");
    }

    @Test
    @DisplayName("OpXor: [F,F] → false（ゼロtrue）")
    public void testOpXorFF() {
        List<ICondition> conditions = Arrays.asList(new AlwaysFalse(), new AlwaysFalse());
        OpXor opXor = new OpXor(conditions);

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertFalse(opXor.isMet(context), "XOR(F,F) = F （ゼロtrueの場合false）");
    }

    @Test
    @DisplayName("OpXor: [T,F,F] → true（1つだけtrue）")
    public void testOpXorTFF() {
        List<ICondition> conditions = Arrays.asList(new AlwaysTrue(), new AlwaysFalse(), new AlwaysFalse());
        OpXor opXor = new OpXor(conditions);

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertTrue(opXor.isMet(context), "XOR(T,F,F) = T （3つの中で1つだけtrue）");
    }

    @Test
    @DisplayName("OpXor: [T,T,T] → false（3つtrue）")
    public void testOpXorTTT() {
        List<ICondition> conditions = Arrays.asList(new AlwaysTrue(), new AlwaysTrue(), new AlwaysTrue());
        OpXor opXor = new OpXor(conditions);

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertFalse(opXor.isMet(context), "XOR(T,T,T) = F （3つtrueの場合false）");
    }

    @Test
    @DisplayName("OpXor: 空リストの場合にfalse")
    public void testOpXor空リスト() {
        List<ICondition> emptyList = Collections.emptyList();
        OpXor opXor = new OpXor(emptyList);

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertFalse(opXor.isMet(context), "空リストの場合、Xorはfalseになるべき（metCount=0）");
    }

    // ========================================
    // 複合テスト
    // ========================================

    @Test
    @DisplayName("【複合】And(Or(T,F), Or(T,T)) → true")
    public void test複合AndOr() {
        OpOr or1 = new OpOr(Arrays.asList(new AlwaysTrue(), new AlwaysFalse()));
        OpOr or2 = new OpOr(Arrays.asList(new AlwaysTrue(), new AlwaysTrue()));
        OpAnd opAnd = new OpAnd(Arrays.asList(or1, or2));

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertTrue(opAnd.isMet(context), "And(Or(T,F), Or(T,T)) = And(T,T) = T");
    }

    @Test
    @DisplayName("【複合】Not(And(T,F)) → true")
    public void test複合NotAnd() {
        OpAnd opAnd = new OpAnd(Arrays.asList(new AlwaysTrue(), new AlwaysFalse()));
        OpNot opNot = new OpNot(opAnd);

        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        assertTrue(opNot.isMet(context), "Not(And(T,F)) = Not(F) = T");
    }

    // ========================================
    // テストダブル（AlwaysTrue / AlwaysFalse）
    // ========================================

    private static class AlwaysTrue implements ICondition {

        @Override
        public boolean isMet(ConditionContext context) {
            return true;
        }

        @Override
        public String getDescription() {
            return "AlwaysTrue";
        }

        @Override
        public void write(JsonObject json) {}
    }

    private static class AlwaysFalse implements ICondition {

        @Override
        public boolean isMet(ConditionContext context) {
            return false;
        }

        @Override
        public String getDescription() {
            return "AlwaysFalse";
        }

        @Override
        public void write(JsonObject json) {}
    }
}
