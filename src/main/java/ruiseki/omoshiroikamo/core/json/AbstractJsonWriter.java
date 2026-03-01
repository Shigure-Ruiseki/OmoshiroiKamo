package ruiseki.omoshiroikamo.core.json;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

/**
 * Base class for JSON writers.
 * Handles serializing objects to JSON files.
 * 
 * @param <T> The type of object to write.
 */
public abstract class AbstractJsonWriter<T> {

    protected final File path;
    protected final Gson gson;

    public AbstractJsonWriter(File path) {
        this.path = path;
        this.gson = createGson();
    }

    protected Gson createGson() {
        return new GsonBuilder().setPrettyPrinting()
            .create();
    }

    /**
     * Writes the given data to the JSON file(s).
     * 
     * @param data the data to write
     * @throws IOException if an I/O error occurs
     */
    public void write(T data) throws IOException {
        if (data instanceof Collection) {
            writeCollection((Collection<?>) data);
        } else {
            writeSingle(data);
        }
    }

    protected void writeSingle(T data) throws IOException {
        File targetFile = path.isDirectory() ? new File(path, "default.json") : path;
        writeObjectToFile(targetFile, data);
    }

    protected void writeCollection(Collection<?> collection) throws IOException {
        if (!path.isDirectory()) {
            writeObjectToFile(path, collection);
            return;
        }
        // Group by source file
        Map<File, JsonArray> filesMap = new HashMap<>();
        for (Object item : collection) {
            if (item instanceof AbstractJsonMaterial) {
                AbstractJsonMaterial material = (AbstractJsonMaterial) item;
                File target = material.getSourceFile();
                if (target == null) target = new File(path, "generated.json");
                filesMap.computeIfAbsent(target, k -> new JsonArray())
                    .add(gson.toJsonTree(material));
            }
        }
        for (Map.Entry<File, JsonArray> entry : filesMap.entrySet()) {
            writeObjectToFile(entry.getKey(), entry.getValue());
        }
    }

    protected void writeObjectToFile(File target, Object data) throws IOException {
        File parent = target.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();
        try (Writer writer = new FileWriter(target)) {
            gson.toJson(data, writer);
        }
    }
}
