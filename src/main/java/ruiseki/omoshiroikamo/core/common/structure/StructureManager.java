package ruiseki.omoshiroikamo.core.common.structure;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;

import ruiseki.omoshiroikamo.core.common.structure.StructureDefinitionData.BlockMapping;
import ruiseki.omoshiroikamo.core.common.structure.StructureDefinitionData.StructureEntry;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.integration.structureLib.StructureCompat;
import ruiseki.omoshiroikamo.core.json.JsonErrorCollector;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

/**
 * Main manager for the custom structure system.
 */
public class StructureManager {

    private static StructureManager INSTANCE;

    private final Map<String, StructureJsonLoader> loaders = new HashMap<>();
    private final Map<String, StructureEntry> customStructures = new HashMap<>();
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

    /**
     * Whether initialization is complete.
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Whether any errors were collected.
     */
    public boolean hasErrors() {
        return errorCollector.hasErrors();
    }

    /**
     * Initialize during PreInit.
     */
    public void initialize(File minecraftDir) {
        if (initialized) return;

        try {
            this.configDir = new File(minecraftDir, "config/" + LibMisc.MOD_ID);
            if (!configDir.exists()) {
                configDir.mkdirs();
            }

            // Configure the error collector
            errorCollector.setConfigDir(configDir);
            errorCollector.clear();

            // Generate default JSON files if needed
            DefaultStructureGenerator.generateAllIfMissing(configDir);

            // Load JSON files
            loadStructureFile("ore_miner");
            loadStructureFile("res_miner");
            loadStructureFile("solar_array");
            loadStructureFile("quantum_beacon");

            // Load custom structures from modular/structures/
            loadCustomStructures();

            // Persist errors if any were found
            if (errorCollector.hasErrors()) {
                errorCollector.writeToFile();
            }

            initialized = true;
            Logger.info(
                "StructureManager initialized"
                    + (errorCollector.hasErrors() ? " with " + errorCollector.getErrorCount() + " error(s)" : "")
                    + ", "
                    + customStructures.size()
                    + " custom structure(s)");
        } catch (Exception e) {
            errorCollector.collect(StructureException.loadFailed("initialization", e));
            errorCollector.writeToFile();
        }
    }

    /**
     * Notify a player about configuration errors (called on login).
     */
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
            StructureJsonLoader loader = new StructureJsonLoader();

            if (loader.loadFromFile(file)) {
                loaders.put(name, loader);

                // Validate the structure definitions
                StructureValidator validator = new StructureValidator(loader);
                if (validator.validateAll()) {
                    for (String error : validator.getErrors()) {
                        errorCollector.collect(StructureException.ErrorType.VALIDATION_ERROR, name + ".json", error);
                    }
                }

                // Collect loader errors (parse failures)
                for (String error : loader.getErrors()) {
                    errorCollector.collect(StructureException.ErrorType.PARSE_ERROR, name + ".json", error);
                }
            } else {
                errorCollector.collect(StructureException.loadFailed(name + ".json", null));
            }
        } catch (Exception e) {
            errorCollector.collect(StructureException.loadFailed(name + ".json", e));
        }
    }

    /**
     * Fetch a structure shape.
     *
     * @param fileKey       file key ("ore_miner", "res_miner", ...)
     * @param structureName structure name ("oreExtractorTier1", ...)
     * @return String[][] shape, or null if not found
     */
    public String[][] getShape(String fileKey, String structureName) {
        // Return null before initialization so default shapes are used
        if (!initialized) {
            return null;
        }

        StructureJsonLoader loader = loaders.get(fileKey);
        if (loader == null) {
            warnOnce(fileKey, "Structure loader not found: " + fileKey);
            return null;
        }

        String[][] shape = loader.getShape(structureName);
        if (shape == null) {
            warnOnce(fileKey + ":" + structureName, "Structure not found: " + structureName + " in " + fileKey);
        }

        return shape;
    }

    /**
     * Fetch a structure shape along with all applicable mappings.
     *
     * @param fileKey       file key ("ore_miner", "res_miner", ...)
     * @param structureName structure name ("oreExtractorTier1", ...)
     * @return ShapeWithMappings containing shape and mappings, or null if not found
     */
    public StructureJsonLoader.ShapeWithMappings getShapeWithMappings(String fileKey, String structureName) {
        if (!initialized) {
            return null;
        }

        StructureJsonLoader loader = loaders.get(fileKey);
        if (loader == null) {
            warnOnce(fileKey, "Structure loader not found: " + fileKey);
            return null;
        }

        StructureJsonLoader.ShapeWithMappings result = loader.getShapeWithMappings(structureName);
        if (result == null) {
            warnOnce(fileKey + ":" + structureName, "Structure not found: " + structureName + " in " + fileKey);
        }

        return result;
    }

    /**
     * Prevents repeating the same warning.
     */
    private void warnOnce(String key, String message) {
        if (!warnedStructures.contains(key)) {
            Logger.warn(message);
            warnedStructures.add(key);
        }
    }

    /**
     * Retrieve a block mapping for a symbol.
     */
    public BlockMapping getMapping(String fileKey, String structureName, char symbol) {
        if (!initialized) return null;

        StructureJsonLoader loader = loaders.get(fileKey);
        if (loader == null) {
            return null;
        }
        return loader.getMapping(structureName, symbol);
    }

    /**
     * Reload all structure files.
     */
    public void reload() {
        loaders.clear();
        customStructures.clear();
        warnedStructures.clear();
        errorCollector.clear();

        loadStructureFile("ore_miner");
        loadStructureFile("res_miner");
        loadStructureFile("solar_array");
        loadStructureFile("quantum_beacon");

        // Reload custom structures
        loadCustomStructures();

        if (errorCollector.hasErrors()) {
            errorCollector.writeToFile();
        }

        // Re-register structures with StructureLib using updated shapes
        StructureCompat.reload();

        // Write errors again after StructureCompat.reload() (Q validation errors are
        // added there)
        if (errorCollector.hasErrors()) {
            errorCollector.writeToFile();
        }

        Logger.info(
            "StructureManager reloaded"
                + (errorCollector.hasErrors() ? " with " + errorCollector.getErrorCount() + " error(s)" : "")
                + ", "
                + customStructures.size()
                + " custom structure(s)");
    }

    /**
     * Access the shared error collector.
     */
    public StructureErrorCollector getErrorCollector() {
        return errorCollector;
    }

    // ===== Convenience helpers =====

    /**
     * Get an Ore Miner shape for the given tier.
     */
    public static String[][] getOreMinerShape(int tier) {
        return getInstance().getShape("ore_miner", "oreExtractorTier" + tier);
    }

    /**
     * Get a Resource Miner shape for the given tier.
     */
    public static String[][] getResMinerShape(int tier) {
        return getInstance().getShape("res_miner", "resExtractorTier" + tier);
    }

    /**
     * Get a Solar Array shape for the given tier.
     */
    public static String[][] getSolarArrayShape(int tier) {
        return getInstance().getShape("solar_array", "solarArrayTier" + tier);
    }

    /**
     * Get a Quantum Beacon shape for the given tier.
     */
    public static String[][] getBeaconShape(int tier) {
        return getInstance().getShape("quantum_beacon", "beaconTier" + tier);
    }

    // ===== CustomStructure methods =====

    /**
     * Load custom structures from modular/structures/ directory.
     * Each JSON file represents one custom structure.
     */
    private void loadCustomStructures() {
        File customDir = new File(configDir, "modular/structures");
        Logger.info("StructureManager: Loading custom structures from " + customDir.getAbsolutePath());

        if (!customDir.exists()) {
            customDir.mkdirs();
            Logger.info("StructureManager: Custom structures directory created (empty)");
            return;
        }

        File[] files = customDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) {
            Logger.warn("StructureManager: Could not list files in custom structures directory");
            return;
        }

        Logger.info("StructureManager: Found " + files.length + " JSON file(s)");

        for (File file : files) {
            Logger.info("StructureManager: Processing " + file.getName());
            try {
                StructureJsonLoader loader = new StructureJsonLoader();
                if (loader.loadFromFile(file)) {
                    // Get the first (and only) structure from the file
                    for (String structureName : loader.getStructureNames()) {
                        StructureEntry entry = loader.getStructureEntry(structureName);
                        if (entry != null) {
                            // Check if it has required custom structure fields
                            if (entry.recipeGroup != null) {
                                customStructures.put(entry.name, entry);
                                Logger.info("  Loaded: " + entry.name);
                                Logger.info("    displayName: " + entry.displayName);
                                Logger.info("    recipeGroup: " + entry.recipeGroup);
                                Logger.info("    layers: " + (entry.layers != null ? entry.layers.size() : 0));
                                Logger.info("    mappings: " + (entry.mappings != null ? entry.mappings.size() : 0));
                            } else {
                                Logger.warn("  Skipped '" + structureName + "' - no recipeGroup defined");
                            }
                        }
                    }
                } else {
                    Logger.warn("  Failed to parse " + file.getName());
                }

                // Collect loader errors
                for (String error : loader.getErrors()) {
                    Logger.warn("  Parse error: " + error);
                    errorCollector.collect(StructureException.ErrorType.PARSE_ERROR, file.getName(), error);
                }
            } catch (Exception e) {
                Logger.error("  Exception loading " + file.getName() + ": " + e.getMessage());
                errorCollector.collect(StructureException.loadFailed(file.getName(), e));
            }
        }

        Logger.info("StructureManager: Custom structures loaded - " + customStructures.size() + " total");
    }

    /**
     * Get a custom structure by name.
     */
    public StructureEntry getCustomStructure(String name) {
        return customStructures.get(name);
    }

    /**
     * Get all custom structure names.
     */
    public Set<String> getCustomStructureNames() {
        return new HashSet<>(customStructures.keySet());
    }

    /**
     * Check if a custom structure exists.
     */
    public boolean hasCustomStructure(String name) {
        return customStructures.containsKey(name);
    }
}
