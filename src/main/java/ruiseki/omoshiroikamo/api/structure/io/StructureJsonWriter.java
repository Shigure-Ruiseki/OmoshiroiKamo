package ruiseki.omoshiroikamo.api.structure.io;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.api.structure.core.ISymbolMapping;
import ruiseki.omoshiroikamo.core.json.AbstractJsonWriter;

/**
 * Writer that serializes IStructureEntry objects to JSON files.
 * Extends AbstractJsonWriter for consistency with other JSON writers.
 */
public class StructureJsonWriter extends AbstractJsonWriter<IStructureEntry> {

    public StructureJsonWriter(File path) {
        super(path);
    }

    /**
     * Writes a single structure entry to a JSON file.
     * The output is a single JSON object representing the structure.
     *
     * @param entry the structure to write
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void writeSingle(IStructureEntry entry) throws IOException {
        File targetFile = path.isDirectory() ? new File(path, entry.getName() + ".json") : path;
        JsonObject structureJson = entry.serialize();
        writeObjectToFile(targetFile, structureJson);
    }

    /**
     * Writes a collection of structure entries to JSON file(s).
     * If the path is a file, all structures are written to a single JSON array.
     * If the path is a directory, each structure is written to a separate file.
     *
     * @param collection the structures to write
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void writeCollection(Collection<?> collection) throws IOException {
        if (!path.isDirectory()) {
            // Write all structures to a single JSON array
            JsonArray structuresArray = new JsonArray();
            for (Object item : collection) {
                if (item instanceof IStructureEntry entry) {
                    structuresArray.add(entry.serialize());
                }
            }
            writeObjectToFile(path, structuresArray);
        } else {
            // Write each structure to a separate file in the directory
            for (Object item : collection) {
                if (item instanceof IStructureEntry entry) {
                    File targetFile = new File(path, entry.getName() + ".json");
                    writeObjectToFile(targetFile, entry.serialize());
                }
            }
        }
    }

    /**
     * Writes structures with default mappings to a JSON file.
     * The output is a JSON array containing:
     * 1. A "default" entry with the shared mappings
     * 2. All structure entries
     *
     * This format matches the structure file format used by StructureJsonReader,
     * where structures can inherit from default mappings.
     *
     * @param structures      the structures to write
     * @param defaultMappings the default symbol mappings shared by all structures
     * @throws IOException if an I/O error occurs
     */
    public void writeWithDefaults(Collection<IStructureEntry> structures,
        Map<Character, ISymbolMapping> defaultMappings) throws IOException {

        JsonArray rootArray = new JsonArray();

        // Add default mappings entry if present
        if (defaultMappings != null && !defaultMappings.isEmpty()) {
            JsonObject defaultEntry = new JsonObject();
            defaultEntry.addProperty("name", "default");

            JsonObject mappingsObj = new JsonObject();
            for (Map.Entry<Character, ISymbolMapping> entry : defaultMappings.entrySet()) {
                String key = String.valueOf(entry.getKey());
                mappingsObj.add(
                    key,
                    entry.getValue()
                        .serialize());
            }
            defaultEntry.add("mappings", mappingsObj);

            rootArray.add(defaultEntry);
        }

        // Add all structure entries
        for (IStructureEntry structure : structures) {
            rootArray.add(structure.serialize());
        }

        File targetFile = path.isDirectory() ? new File(path, "structures.json") : path;
        writeObjectToFile(targetFile, rootArray);
    }
}
