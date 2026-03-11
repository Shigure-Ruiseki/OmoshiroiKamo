package ruiseki.omoshiroikamo.api.recipe.expression;

/**
 * Exception thrown when a recipe script (expression or condition) fails to
 * parse.
 * Provides a visual snippet highlighting the error position.
 */
public class RecipeScriptException extends RuntimeException {

    private final String input;
    private final int pos;
    private final String description;

    public RecipeScriptException(String input, int pos, String description) {
        super(description);
        this.input = input;
        this.pos = pos;
        this.description = description;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(description)
            .append(" at index ")
            .append(pos)
            .append("\n");
        sb.append("Script: ")
            .append(generateSnippet());
        return sb.toString();
    }

    private String generateSnippet() {
        if (input == null || input.isEmpty()) return "[empty]";

        int start = Math.max(0, pos - 20);
        int end = Math.min(input.length(), pos + 20);

        String snippet = input.substring(start, end);

        // Mark the position in the snippet
        StringBuilder sb = new StringBuilder();
        sb.append(snippet)
            .append("\n");

        // Add pointer padding: "Script: " is 8 chars. We need to offset start.
        // Wait, the message starts with "Script: "
        for (int i = 0; i < 8 + (pos - start); i++) {
            sb.append(" ");
        }
        sb.append("^");

        return sb.toString();
    }

    public String getInput() {
        return input;
    }

    public int getPos() {
        return pos;
    }

    public String getDescription() {
        return description;
    }
}
