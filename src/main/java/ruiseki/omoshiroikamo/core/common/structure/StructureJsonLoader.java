package ruiseki.omoshiroikamo.core.common.structure;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ruiseki.omoshiroikamo.core.common.structure.StructureDefinitionData.BlockEntry;
import ruiseki.omoshiroikamo.core.common.structure.StructureDefinitionData.BlockMapping;
import ruiseki.omoshiroikamo.core.common.structure.StructureDefinitionData.Layer;
import ruiseki.omoshiroikamo.core.common.structure.StructureDefinitionData.PortRequirement;
import ruiseki.omoshiroikamo.core.common.structure.StructureDefinitionData.Properties;
import ruiseki.omoshiroikamo.core.common.structure.StructureDefinitionData.Requirements;
import ruiseki.omoshiroikamo.core.common.structure.StructureDefinitionData.StructureEntry;
import ruiseki.omoshiroikamo.core.common.util.Logger;

/**
 * Loads and parses structure JSON files.
 */
public class StructureJsonLoader {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
        .create();

    /** Default mappings shared across all structures. */
    private final Map<Character, BlockMapping> defaultMappings = new HashMap<>();

    /** Cached structure definitions (name -> StructureEntry). */
    private final Map<String, StructureEntry> structureCache = new HashMap<>();

    /** Parsed shapes (name -> String[][]). */
    private final Map<String, String[][]> parsedShapes = new HashMap<>();

    /** Collected error list. */
    private final List<String> errors = new ArrayList<>();

    /**
     * Get a copy of the error list.
     */
    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }

    /**
     * Add an error entry.
     */
    private void addError(String error) {
        errors.add(error);
    }

    /**
     * Load a JSON file.
     */
    public boolean loadFromFile(File file) {
        if (!file.exists()) {
            Logger.warn("Structure file not found: " + file.getAbsolutePath());
            return false;
        }

        try (FileReader reader = new FileReader(file)) {
            JsonElement rootElement = new JsonParser().parse(reader);

            // Basic sanity checks
            if (rootElement == null || rootElement.isJsonNull()) {
                Logger.warn("Structure file is empty or null: " + file.getName());
                return false;
            }

            // Support both single object (CustomStructure) and array (existing structures)
            // formats
            if (rootElement.isJsonObject()) {
                // Single object format (CustomStructure)
                JsonObject obj = rootElement.getAsJsonObject();
                if (!obj.has("name") || obj.get("name")
                    .isJsonNull()) {
                    Logger.warn("Structure object missing 'name' field: " + file.getName());
                    return false;
                }
                String name = obj.get("name")
                    .getAsString();
                StructureEntry entry = parseStructureEntry(obj);
                structureCache.put(name, entry);
                Logger.info("Loaded single structure: " + name + " from " + file.getName());
                return true;
            }

            if (!rootElement.isJsonArray()) {
                Logger.warn("Structure file is not a JSON array or object: " + file.getName());
                return false;
            }

            // Array format (existing structures like ore_miner.json)
            JsonArray array = rootElement.getAsJsonArray();
            if (array.size() == 0) {
                Logger.warn("Structure file contains empty array: " + file.getName());
                return false;
            }

            for (JsonElement element : array) {
                if (element == null || !element.isJsonObject()) {
                    Logger.warn("Invalid entry in structure file (not an object): " + file.getName());
                    continue;
                }

                JsonObject obj = element.getAsJsonObject();

                if (!obj.has("name") || obj.get("name")
                    .isJsonNull()) {
                    Logger.warn("Structure entry missing 'name' field: " + file.getName());
                    continue;
                }

                String name = obj.get("name")
                    .getAsString();

                if ("default".equals(name)) {
                    // Parse default mappings
                    parseDefaultMappings(obj);
                } else {
                    // Parse a structure entry
                    StructureEntry entry = parseStructureEntry(obj);
                    structureCache.put(name, entry);
                }
            }

            Logger.info("Loaded structure file: " + file.getName() + " (" + structureCache.size() + " structures)");
            return true;

        } catch (Exception e) {
            Logger.error("Failed to load structure file: " + file.getAbsolutePath(), e);
            return false;
        }
    }

    /**
     * Parse the default mappings.
     */
    private void parseDefaultMappings(JsonObject obj) {
        if (!obj.has("mappings")) return;

        JsonObject mappingsObj = obj.getAsJsonObject("mappings");
        for (Map.Entry<String, JsonElement> entry : mappingsObj.entrySet()) {
            char symbol = entry.getKey()
                .charAt(0);
            BlockMapping mapping = parseBlockMapping(entry.getValue());
            defaultMappings.put(symbol, mapping);
        }
    }

    /**
     * Parse a structure entry.
     */
    private StructureEntry parseStructureEntry(JsonObject obj) {
        StructureEntry entry = new StructureEntry();
        entry.name = obj.get("name")
            .getAsString();

        // Parse displayName (CustomStructure)
        if (obj.has("displayName")) {
            entry.displayName = obj.get("displayName")
                .getAsString();
        }

        // Parse recipeGroup (CustomStructure)
        if (obj.has("recipeGroup")) {
            entry.recipeGroup = obj.get("recipeGroup")
                .getAsString();
        }

        // Parse layers
        if (obj.has("layers")) {
            entry.layers = new ArrayList<>();
            JsonArray layersArray = obj.getAsJsonArray("layers");
            for (JsonElement layerElement : layersArray) {
                Layer layer = parseLayer(layerElement.getAsJsonObject());
                entry.layers.add(layer);
            }
        }

        // Parse mappings (optional)
        if (obj.has("mappings")) {
            entry.mappings = new HashMap<>();
            JsonObject mappingsObj = obj.getAsJsonObject("mappings");
            for (Map.Entry<String, JsonElement> mapEntry : mappingsObj.entrySet()) {
                entry.mappings.put(mapEntry.getKey(), parseBlockMapping(mapEntry.getValue()));
            }
        }

        // Parse requirements (CustomStructure)
        if (obj.has("requirements")) {
            entry.requirements = parseRequirements(obj.getAsJsonObject("requirements"));
        }

        // Parse properties (CustomStructure)
        if (obj.has("properties")) {
            entry.properties = parseProperties(obj.getAsJsonObject("properties"));
        }

        return entry;
    }

    /**
     * Parse a single layer definition.
     */
    private Layer parseLayer(JsonObject obj) {
        Layer layer = new Layer();
        layer.name = obj.has("name") ? obj.get("name")
            .getAsString() : "";
        layer.rows = new ArrayList<>();

        if (obj.has("rows")) {
            JsonArray rowsArray = obj.getAsJsonArray("rows");
            for (JsonElement rowElement : rowsArray) {
                layer.rows.add(rowElement.getAsString());
            }
        }

        return layer;
    }

    /**
     * Parse a block mapping (string, array, or object form).
     */
    private BlockMapping parseBlockMapping(JsonElement element) {
        BlockMapping mapping = new BlockMapping();

        if (element.isJsonPrimitive()) {
            // Simple string form: "mod:block:meta"
            mapping.block = element.getAsString();
        } else if (element.isJsonArray()) {
            // Array form: ["mod:block1:*", "mod:block2:*"]
            mapping.blocks = new ArrayList<>();
            JsonArray array = element.getAsJsonArray();
            for (JsonElement item : array) {
                if (item.isJsonPrimitive()) {
                    BlockEntry entry = new BlockEntry();
                    entry.id = item.getAsString();
                    mapping.blocks.add(entry);
                }
            }
        } else if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();

            // Single block form: { "block": "...", "max": 1 }
            if (obj.has("block")) {
                mapping.block = obj.get("block")
                    .getAsString();
            }

            // Multiple choice form: { "blocks": [...] }
            if (obj.has("blocks")) {
                mapping.blocks = new ArrayList<>();
                JsonArray blocksArray = obj.getAsJsonArray("blocks");
                for (JsonElement blockElement : blocksArray) {
                    BlockEntry blockEntry = parseBlockEntry(blockElement);
                    mapping.blocks.add(blockEntry);
                }
            }

            // min/max (for existing structure compatibility)
            if (obj.has("min")) mapping.min = obj.get("min")
                .getAsInt();
            if (obj.has("max")) mapping.max = obj.get("max")
                .getAsInt();
        }

        return mapping;
    }

    /**
     * Parse an individual block entry.
     */
    private BlockEntry parseBlockEntry(JsonElement element) {
        BlockEntry entry = new BlockEntry();

        if (element.isJsonPrimitive()) {
            // Simple string: "mod:block:meta"
            entry.id = element.getAsString();
        } else if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            entry.id = obj.get("id")
                .getAsString();
            if (obj.has("min")) entry.min = obj.get("min")
                .getAsInt();
            if (obj.has("max")) entry.max = obj.get("max")
                .getAsInt();
        }

        return entry;
    }

    /**
     * Retrieve the String[][] shape for the given structure name.
     */
    public String[][] getShape(String structureName) {
        // Check cache first
        if (parsedShapes.containsKey(structureName)) {
            return parsedShapes.get(structureName);
        }

        StructureEntry entry = structureCache.get(structureName);
        if (entry == null || entry.layers == null || entry.layers.isEmpty()) {
            return null;
        }

        try {
            // Build the String[][] from layers
            String[][] shape = new String[entry.layers.size()][];
            for (int i = 0; i < entry.layers.size(); i++) {
                Layer layer = entry.layers.get(i);
                if (layer == null || layer.rows == null) {
                    // If a malformed layer exists, fall back to an empty array
                    shape[i] = new String[0];
                } else {
                    shape[i] = layer.rows.toArray(new String[0]);
                }
            }

            parsedShapes.put(structureName, shape);
            return shape;
        } catch (Exception e) {
            Logger.error("Failed to parse structure shape: " + structureName, e);
            return null;
        }
    }

    /**
     * Resolve a mapping from a symbol (structure-specific overrides defaults).
     */
    public BlockMapping getMapping(String structureName, char symbol) {
        // Check structure-specific mappings first
        StructureEntry entry = structureCache.get(structureName);
        if (entry != null && entry.mappings != null) {
            Object mapping = entry.mappings.get(String.valueOf(symbol));
            if (mapping instanceof BlockMapping) {
                return (BlockMapping) mapping;
            }
        }

        // Fallback to default mapping
        return defaultMappings.get(symbol);
    }

    /**
     * Write structures back to a JSON file.
     */
    public static void writeToFile(File file, List<StructureEntry> entries) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(entries, writer);
        }
    }

    // ========== Validation helper methods ==========

    /**
     * Get the set of structure names.
     */
    public java.util.Set<String> getStructureNames() {
        return structureCache.keySet();
    }

    /**
     * Get a structure entry by name.
     */
    public StructureEntry getStructureEntry(String name) {
        return structureCache.get(name);
    }

    /**
     * Get a copy of the default mappings.
     */
    public Map<Character, BlockMapping> getDefaultMappings() {
        return new HashMap<>(defaultMappings);
    }

    /**
     * Container for shape data with dynamic mappings.
     */
    public static class ShapeWithMappings {

        public final String[][] shape;
        public final Map<Character, Object> dynamicMappings;

        public ShapeWithMappings(String[][] shape, Map<Character, Object> dynamicMappings) {
            this.shape = shape;
            this.dynamicMappings = dynamicMappings != null ? dynamicMappings : new HashMap<>();
        }
    }

    /**
     * Get the shape along with all applicable dynamic mappings.
     * This includes both default mappings and structure-specific mappings.
     *
     * @param structureName The name of the structure
     * @return ShapeWithMappings containing shape and all mappings, or null if not
     *         found
     */
    public ShapeWithMappings getShapeWithMappings(String structureName) {
        String[][] shape = getShape(structureName);
        if (shape == null) {
            return null;
        }

        // Collect all mappings (default + structure-specific)

        // Add default mappings first
        Map<Character, Object> mappings = new HashMap<>(defaultMappings);

        // Override with structure-specific mappings
        StructureEntry entry = structureCache.get(structureName);
        if (entry != null && entry.mappings != null) {
            for (Map.Entry<String, Object> mapEntry : entry.mappings.entrySet()) {
                if (!mapEntry.getKey()
                    .isEmpty()) {
                    mappings.put(
                        mapEntry.getKey()
                            .charAt(0),
                        mapEntry.getValue());
                }
            }
        }

        return new ShapeWithMappings(shape, mappings);
    }

    // ========== CustomStructure parsing helpers ==========

    /**
     * Parse requirements from JSON.
     */
    private Requirements parseRequirements(JsonObject obj) {
        Requirements req = new Requirements();
        req.itemInput = parsePortRequirement(obj, "itemInput");
        req.itemOutput = parsePortRequirement(obj, "itemOutput");
        req.fluidInput = parsePortRequirement(obj, "fluidInput");
        req.fluidOutput = parsePortRequirement(obj, "fluidOutput");
        req.energyInput = parsePortRequirement(obj, "energyInput");
        req.energyOutput = parsePortRequirement(obj, "energyOutput");
        req.manaInput = parsePortRequirement(obj, "manaInput");
        req.manaOutput = parsePortRequirement(obj, "manaOutput");
        req.gasInput = parsePortRequirement(obj, "gasInput");
        req.gasOutput = parsePortRequirement(obj, "gasOutput");
        req.essentiaInput = parsePortRequirement(obj, "essentiaInput");
        req.essentiaOutput = parsePortRequirement(obj, "essentiaOutput");
        req.visInput = parsePortRequirement(obj, "visInput");
        req.visOutput = parsePortRequirement(obj, "visOutput");
        return req;
    }

    /**
     * Parse a single port requirement.
     */
    private PortRequirement parsePortRequirement(JsonObject parent, String key) {
        if (!parent.has(key)) {
            return null;
        }
        JsonObject obj = parent.getAsJsonObject(key);
        PortRequirement req = new PortRequirement();
        if (obj.has("min")) {
            req.min = obj.get("min")
                .getAsInt();
        }
        if (obj.has("max")) {
            req.max = obj.get("max")
                .getAsInt();
        }
        return req;
    }

    /**
     * Parse properties from JSON.
     */
    private Properties parseProperties(JsonObject obj) {
        Properties props = new Properties();
        if (obj.has("speedMultiplier")) {
            props.speedMultiplier = obj.get("speedMultiplier")
                .getAsFloat();
        }
        if (obj.has("energyMultiplier")) {
            props.energyMultiplier = obj.get("energyMultiplier")
                .getAsFloat();
        }
        if (obj.has("batchMin")) {
            props.batchMin = obj.get("batchMin")
                .getAsInt();
        }
        if (obj.has("batchMax")) {
            props.batchMax = obj.get("batchMax")
                .getAsInt();
        }
        if (obj.has("tintColor")) {
            props.tintColor = obj.get("tintColor")
                .getAsString();
        }
        return props;
    }
}
