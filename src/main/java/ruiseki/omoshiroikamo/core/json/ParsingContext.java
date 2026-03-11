package ruiseki.omoshiroikamo.core.json;

import java.io.File;

/**
 * Holds the current context during JSON parsing, such as the file being
 * processed.
 * Uses ThreadLocal to ensure thread safety during potentially parallel parsing.
 */
public class ParsingContext {

    private static final ThreadLocal<File> currentFile = new ThreadLocal<>();

    public static void setCurrentFile(File file) {
        currentFile.set(file);
    }

    public static File getCurrentFile() {
        return currentFile.get();
    }

    public static String getCurrentFileName() {
        File file = currentFile.get();
        return file != null ? file.getName() : "unknown";
    }

    public static void clear() {
        currentFile.remove();
    }
}
