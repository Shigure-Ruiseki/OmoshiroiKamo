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
                if (eat('=')) {
                    op = "==";
                } else {
                    // Check for assignment (single '=')
                    return parseAssignment(x, "=");
                }
            } else if (eat('!')) {
                if (eat('=')) {
                    op = "!=";
                } else {
                    pos--; // Backtrack '!'
                    nextChar();
                    return x;
                }
            } else if (eat('+')) {
                if (eat('=')) {
                    return parseAssignment(x, "+=");
                } else {
                    pos--; // Backtrack '+'
                    nextChar();
                    return x;
                }
            } else if (eat('-')) {
                if (eat('=')) {
                    return parseAssignment(x, "-=");
                } else {
                    pos--; // Backtrack '-'
                    nextChar();
                    return x;
                }
            } else if (eat('*')) {
                if (eat('=')) {
                    return parseAssignment(x, "*=");
                } else {
                    pos--; // Backtrack '*'
                    nextChar();
                    return x;
                }
            } else if (eat('/')) {
                if (eat('=')) {
                    return parseAssignment(x, "/=");
                } else {
                    pos--; // Backtrack '/'
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

            if (!op.isEmpty()) {
                Object y = parseExpression();
                x = new ComparisonCondition(asExpression(x), asExpression(y), op);
            }
        }
    }

    /**
     * Parse assignment operator (=, +=, -=, *=, /=).
     * Left-hand side must be an NbtExpression or DotNotationNBTExpression.
     */
    private Object parseAssignment(Object left, String operation) {
        // Extract NBT key and path segments from left-hand side
        String nbtKey = null;
        List<String> pathSegments = null;

        if (left instanceof NbtExpression) {
            // Legacy nbt('key') syntax (if still supported)
            nbtKey = extractNbtKey((NbtExpression) left);
        } else if (left instanceof DotNotationNBTExpression) {
            // New dot notation: display.Name
            DotNotationNBTExpression dotExpr = (DotNotationNBTExpression) left;
            nbtKey = dotExpr.getFullPath();
            pathSegments = new ArrayList<>(dotExpr.getPathSegments());
        }

        if (nbtKey == null) {
            throw error("Assignment left-hand side must be an NBT path (e.g., display.Name)");
        }

        // Parse right-hand side
        Object right = parseExpression();

        return new NBTAssignmentExpression(nbtKey, pathSegments, asExpression(right), operation);
    }

    /**
     * Extract NBT key from NbtExpression.
     */
    private String extractNbtKey(NbtExpression expr) {
        return expr.getNbtKey();
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
        // Array literals: ['item1', 'item2', ...]
        if (ch == '[') {
            nextChar(); // skip '['
            List<IExpression> elements = new ArrayList<>();

            while (isSpace(ch)) nextChar();

            // Parse array elements
            while (ch != ']' && ch != -1) {
                // Parse element (can be string or expression)
                Object element = parseLogicalOr();
                elements.add(asExpression(element));

                while (isSpace(ch)) nextChar();

                // Check for comma or end of array
                if (ch == ',') {
                    nextChar(); // skip comma
                    while (isSpace(ch)) nextChar();
                } else if (ch != ']') {
                    throw error("Expected ',' or ']' in array literal");
                }
            }

            if (!eat(']')) {
                throw error("Expected closing ']' for array literal");
            }

            return new ArrayLiteralExpression(elements);
        }
        // String literals: 'text' or "text"
        else if (ch == '\'' || ch == '"') {
            char quote = (char) ch;
            nextChar(); // skip opening quote
            int strStart = this.pos;
            while (ch != quote && ch != -1) {
                nextChar();
            }
            if (ch != quote) {
                throw error("Expected closing quote '" + quote + "'");
            }
            String stringValue = input.substring(strStart, this.pos);
            nextChar(); // skip closing quote
            return new StringLiteralExpression(stringValue);
        } else if (eat('(') || eat('{')) { // parentheses or braces
            char close = (input.charAt(pos - 1) == '(') ? ')' : '}';
            Object res = parseLogicalOr();
            if (!eat(close)) throw error("Expected closing '" + close + "'");
            return res;
        } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
            while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
            return new ConstantExpression(Double.parseDouble(input.substring(startPos, this.pos)));
        } else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) { // variables, functions, or NBT paths
            // Parse first segment
            List<String> pathSegments = new ArrayList<>();
            StringBuilder segment = new StringBuilder();

            while ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_') {
                segment.append((char) ch);
                nextChar();
            }
            pathSegments.add(segment.toString());

            // Check for dot notation (NBT path)
            while (ch == '.') {
                nextChar(); // skip '.'
                segment = new StringBuilder();
                while ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_' || (ch >= '0' && ch <= '9')) {
                    segment.append((char) ch);
                    nextChar();
                }
                if (segment.length() == 0) {
                    throw error("Expected identifier after '.'");
                }
                pathSegments.add(segment.toString());
            }

            String name = pathSegments.get(0);

            // Check if this is a function call
            if (eat('(')) {
                // Function call - no longer support nbt() function
                throw error("Unknown function: '" + name + "'");
            }

            // Check if this is a dot notation NBT path (multiple segments)
            if (pathSegments.size() > 1) {
                // Dot notation NBT path: display.Name, ench.lvl, etc.
                String fullPath = String.join(".", pathSegments);
                return new DotNotationNBTExpression(fullPath, pathSegments);
            }

            // Single segment - check for known variables
            if (name.equals("day") || name.equals("total_days")
                || name.equals("time")
                || name.equals("moon_phase")
                || name.equals("moon")) {
                return new WorldPropertyExpression(name.equals("moon") ? "moon_phase" : name);
            } else {
                throw error("Unknown variable: '" + name + "'");
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
