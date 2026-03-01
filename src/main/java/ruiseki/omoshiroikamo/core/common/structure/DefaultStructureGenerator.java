package ruiseki.omoshiroikamo.core.common.structure;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ruiseki.omoshiroikamo.api.structure.core.BlockMapping;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.api.structure.core.ISymbolMapping;
import ruiseki.omoshiroikamo.api.structure.core.StructureEntryBuilder;
import ruiseki.omoshiroikamo.api.structure.core.StructureLayer;
import ruiseki.omoshiroikamo.api.structure.io.StructureJsonReader;
import ruiseki.omoshiroikamo.api.structure.io.StructureJsonWriter;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumBeacon.QuantumBeaconShapes;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.ore.QuantumOreExtractorShapes;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.res.QuantumResExtractorShapes;
import ruiseki.omoshiroikamo.module.multiblock.common.block.solarArray.SolarArrayShapes;

/**
 * Generates and updates default structure JSON files.
 * Refactored to use StructureJsonWriter instead of direct GSON usage.
 */
public class DefaultStructureGenerator {
    // TODO: Change to modular format

    /**
     * Generate or update all structure JSON files, filling in missing entries.
     */
    public static void generateAllIfMissing(File configDir) {
        File structuresDir = new File(configDir, "structures");
        if (!structuresDir.exists()) {
            structuresDir.mkdirs();
        }

        // Ore Miner
        updateOreMinerJson(new File(structuresDir, "ore_miner.json"));

        // Resource Miner
        updateResMinerJson(new File(structuresDir, "res_miner.json"));

        // Solar Array
        updateSolarArrayJson(new File(structuresDir, "solar_array.json"));

        // Quantum Beacon
        updateBeaconJson(new File(structuresDir, "quantum_beacon.json"));
    }

    /**
     * Update the Ore Miner JSON, adding any missing entries.
     */
    private static void updateOreMinerJson(File file) {
        Map<Character, ISymbolMapping> defaultMappings = createDefaultMappings("oreMiner");
        List<IStructureEntry> requiredStructures = new ArrayList<>();
        requiredStructures.add(createStructureEntry("oreExtractorTier1", QuantumOreExtractorShapes.SHAPE_TIER_1, 1));
        requiredStructures.add(createStructureEntry("oreExtractorTier2", QuantumOreExtractorShapes.SHAPE_TIER_2, 2));
        requiredStructures.add(createStructureEntry("oreExtractorTier3", QuantumOreExtractorShapes.SHAPE_TIER_3, 3));
        requiredStructures.add(createStructureEntry("oreExtractorTier4", QuantumOreExtractorShapes.SHAPE_TIER_4, 4));
        requiredStructures.add(createStructureEntry("oreExtractorTier5", QuantumOreExtractorShapes.SHAPE_TIER_5, 5));
        requiredStructures.add(createStructureEntry("oreExtractorTier6", QuantumOreExtractorShapes.SHAPE_TIER_6, 6));

        updateConfigWithMissing(file, defaultMappings, requiredStructures, "Ore Miner");
    }

    /**
     * Update the Resource Miner JSON.
     */
    private static void updateResMinerJson(File file) {
        Map<Character, ISymbolMapping> defaultMappings = createDefaultMappings("resMiner");
        List<IStructureEntry> requiredStructures = new ArrayList<>();
        requiredStructures.add(createStructureEntry("resExtractorTier1", QuantumResExtractorShapes.SHAPE_TIER_1, 1));
        requiredStructures.add(createStructureEntry("resExtractorTier2", QuantumResExtractorShapes.SHAPE_TIER_2, 2));
        requiredStructures.add(createStructureEntry("resExtractorTier3", QuantumResExtractorShapes.SHAPE_TIER_3, 3));
        requiredStructures.add(createStructureEntry("resExtractorTier4", QuantumResExtractorShapes.SHAPE_TIER_4, 4));
        requiredStructures.add(createStructureEntry("resExtractorTier5", QuantumResExtractorShapes.SHAPE_TIER_5, 5));
        requiredStructures.add(createStructureEntry("resExtractorTier6", QuantumResExtractorShapes.SHAPE_TIER_6, 6));

        updateConfigWithMissing(file, defaultMappings, requiredStructures, "Resource Miner");
    }

    /**
     * Update the Solar Array JSON.
     */
    private static void updateSolarArrayJson(File file) {
        Map<Character, ISymbolMapping> defaultMappings = createDefaultMappings("solarArray");
        List<IStructureEntry> requiredStructures = new ArrayList<>();
        requiredStructures.add(createStructureEntry("solarArrayTier1", SolarArrayShapes.SHAPE_TIER_1, 1));
        requiredStructures.add(createStructureEntry("solarArrayTier2", SolarArrayShapes.SHAPE_TIER_2, 2));
        requiredStructures.add(createStructureEntry("solarArrayTier3", SolarArrayShapes.SHAPE_TIER_3, 3));
        requiredStructures.add(createStructureEntry("solarArrayTier4", SolarArrayShapes.SHAPE_TIER_4, 4));
        requiredStructures.add(createStructureEntry("solarArrayTier5", SolarArrayShapes.SHAPE_TIER_5, 5));
        requiredStructures.add(createStructureEntry("solarArrayTier6", SolarArrayShapes.SHAPE_TIER_6, 6));

        updateConfigWithMissing(file, defaultMappings, requiredStructures, "Solar Array");
    }

    /**
     * Update the Quantum Beacon JSON.
     */
    private static void updateBeaconJson(File file) {
        Map<Character, ISymbolMapping> defaultMappings = createDefaultMappings("beacon");
        List<IStructureEntry> requiredStructures = new ArrayList<>();
        requiredStructures.add(createStructureEntry("beaconTier1", QuantumBeaconShapes.SHAPE_TIER_1, 1));
        requiredStructures.add(createStructureEntry("beaconTier2", QuantumBeaconShapes.SHAPE_TIER_2, 2));
        requiredStructures.add(createStructureEntry("beaconTier3", QuantumBeaconShapes.SHAPE_TIER_3, 3));
        requiredStructures.add(createStructureEntry("beaconTier4", QuantumBeaconShapes.SHAPE_TIER_4, 4));
        requiredStructures.add(createStructureEntry("beaconTier5", QuantumBeaconShapes.SHAPE_TIER_5, 5));
        requiredStructures.add(createStructureEntry("beaconTier6", QuantumBeaconShapes.SHAPE_TIER_6, 6));

        updateConfigWithMissing(file, defaultMappings, requiredStructures, "Quantum Beacon");
    }

    /**
     * Load an existing JSON file and append any missing entries.
     */
    private static void updateConfigWithMissing(File file, Map<Character, ISymbolMapping> defaultMappings,
        List<IStructureEntry> requiredStructures, String typeName) {
        Map<String, IStructureEntry> existingStructures = new HashMap<>();
        Map<Character, ISymbolMapping> existingMappings = new HashMap<>();
        boolean fileExisted = file.exists();
        boolean loadFailed = false;

        // Read the existing file if it is present
        if (fileExisted) {
            try {
                StructureJsonReader reader = new StructureJsonReader(file);
                StructureJsonReader.FileData fileData = reader.read();
                existingStructures.putAll(fileData.structures);
                existingMappings.putAll(fileData.defaultMappings);
            } catch (Exception e) {
                Logger.error("Failed to read existing structure config: " + file.getName(), e);
                loadFailed = true;
                // On failure, treat as a fresh file
            }
        } else {
            // Ensure parent directories exist
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
        }

        // If loading failed and the file existed, back it up before recreating
        if (loadFailed && fileExisted) {
            File backup = new File(file.getAbsolutePath() + ".backup");
            if (file.renameTo(backup)) {
                Logger.warn("Corrupted config backed up to: " + backup.getName());
            }
            fileExisted = false;
            existingStructures.clear();
            existingMappings.clear();
        }

        // Merge default mappings (keep existing, add new)
        Map<Character, ISymbolMapping> mergedMappings = new HashMap<>(defaultMappings);
        mergedMappings.putAll(existingMappings);

        // Add any missing structures
        List<String> addedEntries = new ArrayList<>();
        List<IStructureEntry> finalStructures = new ArrayList<>(existingStructures.values());

        for (IStructureEntry required : requiredStructures) {
            if (!existingStructures.containsKey(required.getName())) {
                finalStructures.add(required);
                addedEntries.add(required.getName());
            }
        }

        // Write back when there are changes
        if (!addedEntries.isEmpty() || !fileExisted) {
            try {
                StructureJsonWriter writer = new StructureJsonWriter(file);
                writer.writeWithDefaults(finalStructures, mergedMappings);
                if (!fileExisted) {
                    Logger.info("Generated " + typeName + " structure file: " + file.getName());
                } else {
                    Logger.info(
                        "Updated " + typeName + " config with missing entries: " + String.join(", ", addedEntries));
                }
            } catch (IOException e) {
                Logger.error("Failed to write structure file: " + file.getAbsolutePath(), e);
            }
        } else {
            Logger.debug("No new entries to add to " + typeName + " config");
        }
    }

    /**
     * Build the default mappings for a given machine type.
     */
    private static Map<Character, ISymbolMapping> createDefaultMappings(String machineType) {
        Map<Character, ISymbolMapping> mappings = new LinkedHashMap<>();

        // Shared mappings (used by all structures)
        mappings.put('_', new BlockMapping('_', "air"));
        mappings.put('F', new BlockMapping('F', "omoshiroikamo:basaltStructure:*"));

        // Machine-specific mappings
        if ("oreMiner".equals(machineType) || "resMiner".equals(machineType)) {
            // Miner-specific: P (machine_base), C (laser_core)
            mappings.put('P', new BlockMapping('P', "omoshiroikamo:machineBase:0"));
            mappings.put('C', new BlockMapping('C', "omoshiroikamo:laserCore:0"));

            // Controller
            String controllerBlock = "oreMiner".equals(machineType) ? "omoshiroikamo:quantumOreExtractor:0"
                : "omoshiroikamo:quantumResExtractor:0";
            mappings.put('Q', new BlockMapping('Q', controllerBlock));

            // Lens (multiple options)
            List<String> lensBlocks = new ArrayList<>();
            lensBlocks.add("omoshiroikamo:lens:0");
            lensBlocks.add("omoshiroikamo:lens:1");
            lensBlocks.add("omoshiroikamo:coloredLens:*");
            mappings.put('L', new BlockMapping('L', lensBlocks));

            // Modifiers (multiple options)
            List<String> modBlocks = new ArrayList<>();
            modBlocks.add("omoshiroikamo:modifierNull:0");
            modBlocks.add("omoshiroikamo:modifierAccuracy:0");
            modBlocks.add("omoshiroikamo:modifierSpeed:0");
            mappings.put('A', new BlockMapping('A', modBlocks));

        } else if ("solarArray".equals(machineType)) {
            // Controller
            mappings.put('Q', new BlockMapping('Q', "omoshiroikamo:solarArray:0"));

            // Solar cells (tiers 0-5)
            List<String> cellBlocks = new ArrayList<>();
            for (int i = 0; i <= 5; i++) {
                cellBlocks.add("omoshiroikamo:solarCell:" + i);
            }
            mappings.put('G', new BlockMapping('G', cellBlocks));

            // Modifiers
            List<String> modBlocks = new ArrayList<>();
            modBlocks.add("omoshiroikamo:modifierNull:0");
            modBlocks.add("omoshiroikamo:modifierPiezo:0");
            mappings.put('A', new BlockMapping('A', modBlocks));

        } else if ("beacon".equals(machineType)) {
            // Beacon does NOT use C
            mappings.put('P', new BlockMapping('P', "omoshiroikamo:machineBase:0"));

            // Controller
            mappings.put('Q', new BlockMapping('Q', "omoshiroikamo:quantumBeacon:0"));

            // Beacon modifiers (all types)
            List<String> modBlocks = new ArrayList<>();
            String[] beaconModifiers = { "modifierNull", "modifierFireResistance", "modifierFlight",
                "modifierNightVision", "modifierWaterBreathing", "modifierStrength", "modifierHaste",
                "modifierRegeneration", "modifierSaturation", "modifierResistance", "modifierJumpBoost",
                "modifierSpeed" };
            for (String mod : beaconModifiers) {
                modBlocks.add("omoshiroikamo:" + mod + ":0");
            }
            mappings.put('A', new BlockMapping('A', modBlocks));
        }

        return mappings;
    }

    /**
     * Build a structure entry from a name and shape.
     */
    private static IStructureEntry createStructureEntry(String name, String[][] shape, int tier) {
        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName(name);
        builder.setTier(tier);

        // Add layers from the shape array
        for (int i = 0; i < shape.length; i++) {
            List<String> rows = new ArrayList<>();
            for (String row : shape[i]) {
                rows.add(row);
            }
            builder.addLayer(new StructureLayer("layer" + i, rows));
        }

        // Add tier-specific frame mapping (basaltStructure:meta matches tier-1)
        builder.addMapping('F', new BlockMapping('F', "omoshiroikamo:basaltStructure:" + (tier - 1)));

        return builder.build();
    }

}
