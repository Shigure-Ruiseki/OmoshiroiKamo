package ruiseki.omoshiroikamo.core.common.structure;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.api.structure.core.IStructureLayer;
import ruiseki.omoshiroikamo.api.structure.core.ISymbolMapping;
import ruiseki.omoshiroikamo.api.structure.core.StructureShapeWithMappings;
import ruiseki.omoshiroikamo.api.structure.io.StructureJsonReader;
import ruiseki.omoshiroikamo.api.structure.visitor.StructureValidationVisitor;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.integration.structureLib.StructureCompat;
import ruiseki.omoshiroikamo.core.json.JsonErrorCollector;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

/**
 * Main manager for the custom structure system.
 * Refactored to use IStructureEntry API.
 */
public class StructureManager {

    private static StructureManager INSTANCE;

    /** Cached structure definitions (name -> IStructureEntry). */
    private final Map<String, IStructureEntry> structureEntries = new LinkedHashMap<>();

    /** Default mappings for each file. */
    private final Map<String, Map<Character, ISymbolMapping>> fileDefaultMappings = new HashMap<>();

    /** Custom structures that have recipe groups. */
    private final Map<String, IStructureEntry> customStructures = new LinkedHashMap<>();

    private final StructureErrorCollector errorCollector = StructureErrorCollector.getInstance();
    private File configDir;
    private boolean initialized = false;

    /** Names that have already triggered a warning (prevents log spam). */
    private final Set<String> warnedStructures = new HashSet<>();

    private StructureManager() {}

    public static StructureManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StructureManager();
        }
        return INSTANCE;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean hasErrors() {
        return errorCollector.hasErrors();
    }

    public void initialize(File minecraftDir) {
        if (initialized) return;

        try {
            this.configDir = new File(minecraftDir, "config/" + LibMisc.MOD_ID);
            if (!configDir.exists()) {
                configDir.mkdirs();
            }

            errorCollector.setConfigDir(configDir);
            errorCollector.clear();

            DefaultStructureGenerator.generateAllIfMissing(configDir);

            loadStructureFile("ore_miner");
            loadStructureFile("res_miner");
            loadStructureFile("solar_array");
            loadStructureFile("quantum_beacon");
            loadCustomStructures();

            if (errorCollector.hasErrors()) {
                errorCollector.writeToFile();
            }

            initialized = true;
            Logger.info(
                "StructureManager initialized, " + structureEntries.size()
                    + " structures, "
                    + customStructures.size()
                    + " custom structures");
        } catch (Exception e) {
            errorCollector.collect(StructureException.loadFailed("initialization", e));
            errorCollector.writeToFile();
        }
    }

    public void notifyPlayerIfNeeded(EntityPlayer player) {
        errorCollector.notifyPlayer(player);
        JsonErrorCollector.getInstance()
            .notifyPlayer(player);
    }

    /**
     * Load a single structure JSON file.
     */
    private void loadStructureFile(String name) {
        try {
            File file = new File(configDir, "structures/" + name + ".json");
            if (!file.exists()) return;

            try (FileReader fr = new FileReader(file)) {
                JsonElement root = new JsonParser().parse(fr);
                StructureJsonReader.FileData fileData = StructureJsonReader.readFile(root);

                for (IStructureEntry entry : fileData.structures.values()) {
                    StructureValidationVisitor validator = new StructureValidationVisitor();
                    entry.accept(validator);

                    if (validator.hasErrors()) {
                        for (String error : validator.getErrors()) {
                            errorCollector
                                .collect(StructureException.ErrorType.VALIDATION_ERROR, name + ".json", error);
                        }
                    }
                    structureEntries.put(entry.getName(), entry);
                }
                fileDefaultMappings.put(name, fileData.defaultMappings);
            }
        } catch (Exception e) {
            errorCollector.collect(StructureException.loadFailed(name + ".json", e));
        }
    }

    public String[][] getShape(String fileKey, String structureName) {
        return getInstance().getShapeInternal(fileKey, structureName);
    }

    public static String[][] getSolarArrayShape(int tier) {
        return getInstance().getShape("solar_array", "solarArrayTier" + tier);
    }

    public static String[][] getOreMinerShape(int tier) {
        return getInstance().getShape("ore_miner", "oreExtractorTier" + tier);
    }

    public static String[][] getResMinerShape(int tier) {
        return getInstance().getShape("res_miner", "resExtractorTier" + tier);
    }

    public static String[][] getBeaconShape(int tier) {
        return getInstance().getShape("quantum_beacon", "beaconTier" + tier);
    }

    private String[][] getShapeInternal(String fileKey, String structureName) {
        if (!initialized) return null;

        IStructureEntry entry = structureEntries.get(structureName);
        if (entry == null) {
            warnOnce(fileKey + ":" + structureName, "Structure not found: " + structureName);
            return null;
        }

        List<IStructureLayer> layers = entry.getLayers();
        if (layers.isEmpty()) return null;

        // Convert List of layers to String[][]: [Layer][Row]
        String[][] result = new String[layers.size()][];
        for (int i = 0; i < layers.size(); i++) {
            result[i] = layers.get(i)
                .getRows()
                .toArray(new String[0]);
        }
        return result;
    }

    /**
     * Compatibility bridge for existing code using StructureShapeWithMappings.
     */
    public StructureShapeWithMappings getShapeWithMappings(String fileKey, String structureName) {
        if (!initialized) return null;

        IStructureEntry entry = structureEntries.get(structureName);
        if (entry == null) return null;

        String[][] shape = getShape(fileKey, structureName);
        Map<Character, Object> mappings = new HashMap<>();

        // 1. Defaults
        Map<Character, ISymbolMapping> defaults = fileDefaultMappings.get(fileKey);
        if (defaults != null) {
            mappings.putAll(defaults);
        }

        // 2. Overrides
        mappings.putAll(entry.getMappings());

        return new StructureShapeWithMappings(shape, mappings);
    }

    private void warnOnce(String key, String message) {
        if (!warnedStructures.contains(key)) {
            Logger.warn(message);
            warnedStructures.add(key);
        }
    }

    public void reload() {
        structureEntries.clear();
        fileDefaultMappings.clear();
        customStructures.clear();
        warnedStructures.clear();
        errorCollector.clear();

        loadStructureFile("ore_miner");
        loadStructureFile("res_miner");
        loadStructureFile("solar_array");
        loadStructureFile("quantum_beacon");
        loadCustomStructures();

        StructureCompat.reload();

        if (errorCollector.hasErrors()) {
            errorCollector.writeToFile();
        }
    }

    private void loadCustomStructures() {
        File customDir = new File(configDir, "modular/structures");
        if (!customDir.exists()) {
            customDir.mkdirs();
            return;
        }

        File[] files = customDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) return;

        for (File file : files) {
            try (FileReader fr = new FileReader(file)) {
                JsonElement root = new JsonParser().parse(fr);
                StructureJsonReader.FileData fileData = StructureJsonReader.readFile(root);

                for (IStructureEntry entry : fileData.structures.values()) {
                    // For custom structures, we treat them slightly differently?
                    // The old code checked for 'recipeGroup'.
                    // Since IStructureEntry doesn't have recipeGroup yet, I'll need to add it or
                    // support it.
                    structureEntries.put(entry.getName(), entry);
                    customStructures.put(entry.getName(), entry);
                }
            } catch (Exception e) {
                errorCollector.collect(StructureException.loadFailed(file.getName(), e));
            }
        }
    }

    public IStructureEntry getStructureEntry(String name) {
        return structureEntries.get(name);
    }

    public Set<String> getStructureNames() {
        return structureEntries.keySet();
    }

    public IStructureEntry getCustomStructure(String name) {
        return customStructures.get(name);
    }

    public Set<String> getCustomStructureNames() {
        return customStructures.keySet();
    }

    public boolean hasCustomStructure(String name) {
        return customStructures.containsKey(name);
    }

    public StructureErrorCollector getErrorCollector() {
        return errorCollector;
    }
}
