package ruiseki.omoshiroikamo.api.recipe.expression;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

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

    private RecipeScriptException error(String message) {
        return new RecipeScriptException(input, Math.max(0, pos), message);
    }

    public Object parse() {
        nextChar();
        Object x = parseLogicalOr();
        while (isSpace(ch)) nextChar();
        if (pos < input.length()) throw error("Unexpected token: '" + (char) ch + "'");
        return x;
    }

    // 1. OR: x || y
    private Object parseLogicalOr() {
        Object x = parseLogicalAnd();
        while (eat('|')) {
            if (!eat('|')) throw error("Expected '||'");
            Object y = parseLogicalAnd();
            if (x instanceof ICondition && y instanceof ICondition) {
                List<ICondition> children = new ArrayList<>();
                children.add((ICondition) x);
                children.add((ICondition) y);
                x = new OpOr(children);
            } else {
                throw error("OR (||) requires condition operands");
            }
        }
        return x;
    }

    // 2. AND: x && y
    private Object parseLogicalAnd() {
        Object x = parseComparison();
        while (eat('&')) {
            if (!eat('&')) throw error("Expected '&&'");
            Object y = parseComparison();
            if (x instanceof ICondition && y instanceof ICondition) {
                List<ICondition> children = new ArrayList<>();
                children.add((ICondition) x);
                children.add((ICondition) y);
                x = new OpAnd(children);
            } else {
                throw error("AND (&&) requires condition operands");
            }
        }
        return x;
    }

    private IExpression asExpression(Object obj) {
        if (obj instanceof IExpression) return (IExpression) obj;
        if (obj instanceof ICondition) {
            final ICondition cond = (ICondition) obj;
            return new IExpression() {

                @Override
                public double evaluate(ConditionContext context) {
                    return cond.isMet(context) ? 1 : 0;
                }

                @Override
                public String toString() {
                    return cond.toString();
                }
            };
        }
        throw error("Expected numeric expression or condition");
    }

    private ICondition asCondition(Object obj) {
        if (obj instanceof ICondition) return (ICondition) obj;
        if (obj instanceof IExpression) {
            final IExpression expr = (IExpression) obj;
            return new ICondition() {

                @Override
                public boolean isMet(ConditionContext context) {
                    return expr.evaluate(context) != 0;
                }

                @Override
                public String getDescription() {
                    return expr.toString();
                }

                @Override
                public void write(JsonObject json) {}

                @Override
                public String toString() {
                    return expr.toString();
                }
            };
        }
        throw error("Expected condition or numeric expression");
    }

    // 3. Comparison: x == y, x != y, ...
    private Object parseComparison() {
        Object x = parseExpression();
        while (true) {
            String op = "";
            if (eat('=')) {
                if (eat('=')) op = "==";
                else throw error("Expected '=='");
            } else if (eat('!')) {
                if (eat('=')) {
                    op = "!=";
                } else {
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
            x = new ComparisonCondition(asExpression(x), asExpression(y), op);
        }
    }

    // expression = term ( ( "+" | "-" ) term )*
    private Object parseExpression() {
        Object x = parseTerm();
        for (;;) {
            if (eat('+')) x = new ArithmeticExpression(asExpression(x), asExpression(parseTerm()), "+");
            else if (eat('-')) x = new ArithmeticExpression(asExpression(x), asExpression(parseTerm()), "-");
            else return x;
        }
    }

    // term = factor ( ( "*" | "/" | "%" ) factor )*
    private Object parseTerm() {
        Object x = parseFactor();
        for (;;) {
            if (eat('*')) x = new ArithmeticExpression(asExpression(x), asExpression(parseFactor()), "*");
            else if (eat('/')) x = new ArithmeticExpression(asExpression(x), asExpression(parseFactor()), "/");
            else if (eat('%')) x = new ArithmeticExpression(asExpression(x), asExpression(parseFactor()), "%");
            else return x;
        }
    }

    private Object parseFactor() {
        if (eat('+')) return parseFactor(); // unary plus
        if (eat('-')) return new ArithmeticExpression(new ConstantExpression(0), asExpression(parseFactor()), "-"); // unary
                                                                                                                    // minus
        if (eat('!')) {
            return new OpNot(asCondition(parseFactor()));
        }

        int startPos = this.pos;
        if (eat('(') || eat('{')) { // parentheses or braces
            char close = (input.charAt(pos - 1) == '(') ? ')' : '}';
            Object res = parseLogicalOr();
            if (!eat(close)) throw error("Expected closing '" + close + "'");
            return res;
        } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
            while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
            return new ConstantExpression(Double.parseDouble(input.substring(startPos, this.pos)));
        } else if (ch >= 'a' && ch <= 'z') { // variables or functions
            while (ch >= 'a' && ch <= 'z' || ch == '_') nextChar();
            String name = input.substring(startPos, this.pos);
            if (eat('(')) { // function
                List<String> args = new ArrayList<>();
                while (ch != ')' && ch != -1) {
                    while (isSpace(ch)) nextChar();
                    if (eat('\'')) {
                        int s = pos;
                        while (ch != '\'' && ch != -1) nextChar();
                        args.add(input.substring(s, pos));
                        eat('\'');
                    } else if (ch >= '0' && ch <= '9' || ch == '.') {
                        int s = pos;
                        while (ch >= '0' && ch <= '9' || ch == '.') nextChar();
                        args.add(input.substring(s, pos));
                    } else if (ch >= 'A' && ch <= 'Z') { // Symbol char
                        args.add(String.valueOf((char) ch));
                        nextChar();
                    } else if (ch != ')' && ch != ',') {
                        nextChar();
                    }
                    while (isSpace(ch)) nextChar();
                    eat(',');
                }
                eat(')');
                if (name.equals("nbt") && !args.isEmpty()) {
                    if (args.size() >= 2) {
                        // nbt('S', 'key') or nbt(S, 'key')
                        return new NbtExpression(
                            args.get(1),
                            0,
                            args.get(0)
                                .charAt(0));
                    } else {
                        // nbt('key')
                        return new NbtExpression(args.get(0), 0);
                    }
                } else {
                    throw error("Unknown function: '" + name + "' or missing arguments");
                }
            } else {
                // variable
                if (name.equals("day") || name.equals("total_days")
                    || name.equals("time")
                    || name.equals("moon_phase")
                    || name.equals("moon")) {
                    return new WorldPropertyExpression(name.equals("moon") ? "moon_phase" : name);
                } else {
                    throw error("Unknown variable: '" + name + "'");
                }
            }
        } else {
            throw error("Unexpected character: '" + (char) ch + "'");
        }
    }

    public static IExpression parseExpression(String input) {
        Object res = new ExpressionParser(input).parse();
        if (res instanceof IExpression) return (IExpression) res;
        if (res instanceof ICondition) {
            final ICondition cond = (ICondition) res;
            return new IExpression() {

                @Override
                public double evaluate(ConditionContext context) {
                    return cond.isMet(context) ? 1 : 0;
                }

                @Override
                public String toString() {
                    return cond.toString();
                }
            };
        }
        throw new RuntimeException("Input is not a numeric expression: " + input);
    }

    public static ICondition parseCondition(String input) {
        Object res = new ExpressionParser(input).parse();
        if (res instanceof ICondition) return (ICondition) res;
        if (res instanceof IExpression) {
            final IExpression expr = (IExpression) res;
            return new ICondition() {

                @Override
                public boolean isMet(ConditionContext context) {
                    return expr.evaluate(context) != 0;
                }

                @Override
                public String getDescription() {
                    return expr.toString();
                }

                @Override
                public void write(JsonObject json) {
                    // Not needed for dynamic conditions generated during parsing
                }

                @Override
                public String toString() {
                    return expr.toString();
                }
            };
        }
        throw new RuntimeException("Input is not a condition: " + input);
    }
}
