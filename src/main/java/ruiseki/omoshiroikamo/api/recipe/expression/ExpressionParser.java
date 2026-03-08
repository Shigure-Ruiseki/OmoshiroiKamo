package ruiseki.omoshiroikamo.api.recipe.expression;

import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.condition.ComparisonCondition;
import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.condition.ICondition;
import ruiseki.omoshiroikamo.api.condition.OpAnd;
import ruiseki.omoshiroikamo.api.condition.OpNot;
import ruiseki.omoshiroikamo.api.condition.OpOr;

/**
 * A simple recursive descent parser for expressions and conditions.
 * Supports arithmetic, comparison, variables (day, time, moon), and nbt('key')
 * function.
 */
public class ExpressionParser {

    private final String input;
    private int pos = -1, ch;

    public ExpressionParser(String input) {
        this.input = input;
    }

    private void nextChar() {
        ch = (++pos < input.length()) ? input.charAt(pos) : -1;
    }

    private boolean isSpace(int c) {
        return c == ' ' || c == '\n' || c == '\r' || c == '\t';
    }

    private boolean eat(int charToEat) {
        while (isSpace(ch)) nextChar();
        if (ch == charToEat) {
            nextChar();
            return true;
        }
        return false;
    }

    public Object parse() {
        nextChar();
        Object x = parseLogicalOr();
        while (isSpace(ch)) nextChar();
        if (pos < input.length()) throw new RuntimeException("Unexpected index " + pos + ": " + (char) ch);
        return x;
    }

    // 1. OR: x || y
    private Object parseLogicalOr() {
        Object x = parseLogicalAnd();
        while (eat('|')) {
            if (!eat('|')) throw new RuntimeException("Expected '||'");
            Object y = parseLogicalAnd();
            if (x instanceof ICondition && y instanceof ICondition) {
                List<ICondition> children = new ArrayList<>();
                children.add((ICondition) x);
                children.add((ICondition) y);
                x = new OpOr(children);
            } else {
                throw new RuntimeException("OR (||) requires condition operands");
            }
        }
        return x;
    }

    // 2. AND: x && y
    private Object parseLogicalAnd() {
        Object x = parseComparison();
        while (eat('&')) {
            if (!eat('&')) throw new RuntimeException("Expected '&&'");
            Object y = parseComparison();
            if (x instanceof ICondition && y instanceof ICondition) {
                List<ICondition> children = new ArrayList<>();
                children.add((ICondition) x);
                children.add((ICondition) y);
                x = new OpAnd(children);
            } else {
                throw new RuntimeException("AND (&&) requires condition operands");
            }
        }
        return x;
    }

    // 3. Comparison: x == y, x != y, ...
    private Object parseComparison() {
        Object x = parseExpression();
        while (true) {
            String op = "";
            if (eat('=')) {
                if (eat('=')) op = "==";
                else throw new RuntimeException("Expected '=='");
            } else if (eat('!')) {
                if (eat('=')) {
                    op = "!=";
                } else {
                    // Unary NOT (!) handled in parseFactor, but if we are here and see '!',
                    // it might be a start of a new expression if we missed something.
                    // For now, if it's just '!', we back off and let parseFactor handle it
                    // AFTER verifying it's not a comparison.
                    // BUT: '!' is higher priority than comparison.
                    // This parser is top-down (Low -> High), so comparison calls expression,
                    // expression calls term, term calls factor, factor handles '!'.
                    // So we just return here if it's just '!'.
                    pos--; // Backtrack '!'
                    nextChar();
                    return x;
                }
            } else if (eat('>')) {
                if (eat('=')) op = ">=";
                else op = ">";
            } else if (eat('<')) {
                if (eat('=')) op = "<=";
                else op = "<";
            } else {
                return x;
            }

            Object y = parseExpression();
            if (!(x instanceof IExpression) || !(y instanceof IExpression)) {
                throw new RuntimeException("Comparison requires numeric expressions");
            }
            x = new ComparisonCondition((IExpression) x, (IExpression) y, op);
        }
    }

    // expression = term ( ( "+" | "-" ) term )*
    private IExpression parseExpression() {
        IExpression x = parseTerm();
        for (;;) {
            if (eat('+')) x = new ArithmeticExpression(x, parseTerm(), "+");
            else if (eat('-')) x = new ArithmeticExpression(x, parseTerm(), "-");
            else return x;
        }
    }

    // term = factor ( ( "*" | "/" | "%" ) factor )*
    private IExpression parseTerm() {
        IExpression x = parseFactor();
        for (;;) {
            if (eat('*')) x = new ArithmeticExpression(x, parseFactor(), "*");
            else if (eat('/')) x = new ArithmeticExpression(x, parseFactor(), "/");
            else if (eat('%')) x = new ArithmeticExpression(x, parseFactor(), "%");
            else return x;
        }
    }

    // factor = ( "+" | "-" ) factor | "(" comparison ")" | number | function |
    // variable
    private IExpression parseFactor() {
        if (eat('+')) return parseFactor(); // unary plus
        if (eat('-')) return new ArithmeticExpression(new ConstantExpression(0), parseFactor(), "-"); // unary minus
        if (eat('!')) {
            Object res = parseFactor();
            if (res instanceof ICondition) {
                final ICondition cond = (ICondition) res;
                final ICondition notCond = new OpNot(cond);
                return new IExpression() {

                    @Override
                    public double evaluate(ConditionContext context) {
                        return notCond.isMet(context) ? 1 : 0;
                    }

                    @Override
                    public String toString() {
                        return "!" + cond.toString();
                    }
                };
            }
            throw new RuntimeException("Unary '!' requires a condition");
        }

        IExpression x;
        int startPos = this.pos;
        if (eat('(') || eat('{')) { // parentheses or braces
            char close = (input.charAt(pos - 1) == '(') ? ')' : '}';
            Object res = parseLogicalOr();
            if (res instanceof IExpression) {
                x = (IExpression) res;
            } else if (res instanceof ICondition) {
                final ICondition cond = (ICondition) res;
                x = new IExpression() {

                    @Override
                    public double evaluate(ConditionContext context) {
                        return cond.isMet(context) ? 1 : 0;
                    }
                };
            } else {
                throw new RuntimeException("Empty grouping");
            }
            if (!eat(close)) throw new RuntimeException("Expected '" + close + "'");
        } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
            while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
            x = new ConstantExpression(Double.parseDouble(input.substring(startPos, this.pos)));
        } else if (ch >= 'a' && ch <= 'z') { // variables or functions
            while (ch >= 'a' && ch <= 'z' || ch == '_') nextChar();
            String name = input.substring(startPos, this.pos);
            if (eat('(')) { // function
                List<String> args = new ArrayList<>();
                while (ch != ')' && ch != -1) {
                    if (eat('\'')) {
                        int s = pos;
                        while (ch != '\'' && ch != -1) nextChar();
                        args.add(input.substring(s, pos));
                        eat('\'');
                    } else {
                        nextChar();
                    }
                    eat(',');
                }
                eat(')');
                if (name.equals("nbt") && !args.isEmpty()) {
                    if (args.size() >= 2) {
                        // nbt('S', 'key')
                        x = new NbtExpression(
                            args.get(1),
                            0,
                            args.get(0)
                                .charAt(0));
                    } else {
                        // nbt('key')
                        x = new NbtExpression(args.get(0), 0);
                    }
                } else {
                    throw new RuntimeException("Unknown function: " + name);
                }
            } else {
                // variable
                if (name.equals("day") || name.equals("total_days")
                    || name.equals("time")
                    || name.equals("moon_phase")
                    || name.equals("moon")) {
                    x = new WorldPropertyExpression(name.equals("moon") ? "moon_phase" : name);
                } else {
                    throw new RuntimeException("Unknown variable: " + name);
                }
            }
        } else {
            throw new RuntimeException("Unexpected: " + (char) ch);
        }

        return x;
    }

    public static IExpression parseExpression(String input) {
        Object res = new ExpressionParser(input).parse();
        if (res instanceof IExpression) return (IExpression) res;
        throw new RuntimeException("Input is not a numeric expression: " + input);
    }

    public static ICondition parseCondition(String input) {
        Object res = new ExpressionParser(input).parse();
        if (res instanceof ICondition) return (ICondition) res;
        throw new RuntimeException("Input is not a condition: " + input);
    }
}
