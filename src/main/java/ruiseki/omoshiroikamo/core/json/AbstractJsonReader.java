package ruiseki.omoshiroikamo.core.json;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

/**
 * Base class for JSON readers.
 * Supports reading from a single file or a directory of files.
 * 
 * @param <T> The type of object produced by this reader.
 */
public abstract class AbstractJsonReader<T> {

    protected final File path;
    protected final Gson gson;
    protected T cache;
    protected final Map<String, Object> index = new HashMap<>();

    public AbstractJsonReader(File path) {
        this.path = path;
        this.gson = createGson();
    }

    protected Gson createGson() {
        return new GsonBuilder().setPrettyPrinting()
            .create();
    }

    /**
     * Reads the JSON data and converts it to the ta
     * rn the parsed object(s)
     * 
     * @throws IOException if an I/O error occurs
     */
    public abstract T read() throws IOException;

    /**
     * Reloads the data from the file system.
     */
    public void reload() throws IOException {
        this.cache = read();
        rebuildIndex();
    }

    /**
     * Rebuilds the search index. Subclasses should override this.
     */
    protected void rebuildIndex() {
        index.clear();
    }

    /**
     * Gets an element by its identifier (name or ID) from the index.
     */
    @SuppressWarnings("unchecked")
    public <E> E get(String identifier) {
        return (E) index.get(identifier);
    }

    /**
     * Gets the cached data.
     */
    public T getData() {
        return cache;
    }

    protected JsonElement readJsonElement(File file) throws IOException {
        try (FileReader fileReader = new FileReader(file)) {
            JsonReader reader = new JsonReader(fileReader);
            reader.setLenient(true);
            return new JsonParser().parse(reader);
        }
    }

    /**
     * Helper to list all JSON files in a directory.
     */
    protected List<File> listJsonFiles(File dir) {
        List<File> files = new ArrayList<>();
        listJsonFilesRecursive(dir, files);
        return files;
    }

    private void listJsonFilesRecursive(File dir, List<File> files) {
        if (dir.exists() && dir.isDirectory()) {
            File[] found = dir.listFiles();
            if (found != null) {
                for (File f : found) {
                    if (f.isDirectory()) {
                        listJsonFilesRecursive(f, files);
                    } else if (f.getName()
                        .endsWith(".json")) {
                            files.add(f);
                        }
                }
            }
        }
    }
}
