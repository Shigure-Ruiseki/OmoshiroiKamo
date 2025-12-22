package ruiseki.omoshiroikamo.common.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ruiseki.omoshiroikamo.common.structure.StructureDefinitionData.BlockEntry;
import ruiseki.omoshiroikamo.common.structure.StructureDefinitionData.BlockMapping;
import ruiseki.omoshiroikamo.common.structure.StructureDefinitionData.Layer;
import ruiseki.omoshiroikamo.common.structure.StructureDefinitionData.StructureEntry;

/**
 * Validation logic for structure definitions.
 */
public class StructureValidator {

    /** Collected validation errors. */
    private final List<String> errors = new ArrayList<>();

    /** Loader to validate against. */
    private final StructureJsonLoader loader;

    public StructureValidator(StructureJsonLoader loader) {
        this.loader = loader;
    }

    /**
     * Get a copy of the error list.
     */
    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }

    /**
     * Whether any errors were collected.
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Add a validation error.
     */
    private void addError(String error) {
        errors.add(error);
    }

    /**
     * Build a human-readable layer label.
     * e.g., "Layer 0 (controller)" or "Layer 2" if name is missing.
     */
    private String getLayerLabel(int index, String name) {
        if (name != null && !name.isEmpty()) {
            return "Layer " + index + " (\"" + name + "\")";
        }
        return "Layer " + index;
    }

    /**
     * Validate every structure in the loader.
     *
     * @return true if any errors are found
     */
    public boolean validateAll() {
        boolean hasErrors = false;

        for (String structureName : loader.getStructureNames()) {
            StructureEntry entry = loader.getStructureEntry(structureName);
            if (entry != null) {
                if (validateStructure(structureName, entry)) {
                    hasErrors = true;
                }
            }
        }

        return hasErrors;
    }

    /**
     * Validate a single structure entry.
     *
     * @param name  structure name
     * @param entry structure entry
     * @return true if errors are found
     */
    public boolean validateStructure(String name, StructureEntry entry) {
        boolean hasErrors = false;

        if (entry.layers == null || entry.layers.isEmpty()) {
            addError("[" + name + "] No layers defined");
            return true;
        }

        // Use the first layer as the size baseline
        Layer firstLayer = entry.layers.get(0);
        if (firstLayer.rows == null || firstLayer.rows.isEmpty()) {
            String layerLabel = getLayerLabel(0, firstLayer.name);
            addError("[" + name + "] " + layerLabel + " has no rows");
            return true;
        }

        int expectedDepth = firstLayer.rows.size();
        int expectedWidth = firstLayer.rows.get(0)
                .length();

        // Validate each layer
        for (int layerIndex = 0; layerIndex < entry.layers.size(); layerIndex++) {
            Layer layer = entry.layers.get(layerIndex);

            String layerLabel = getLayerLabel(layerIndex, layer.name);

            if (layer.rows == null || layer.rows.isEmpty()) {
                addError("[" + name + "] " + layerLabel + " has no rows");
                hasErrors = true;
                continue;
            }

            // Confirm layer height matches baseline
            if (layer.rows.size() != expectedDepth) {
                addError(
                        "[" + name + "] " + layerLabel + " has " + layer.rows.size()
                                + " rows, expected " + expectedDepth);
                hasErrors = true;
            }

            // Confirm each row length is consistent (rectangular)
            for (int rowIndex = 0; rowIndex < layer.rows.size(); rowIndex++) {
                String row = layer.rows.get(rowIndex);
                if (row.length() != expectedWidth) {
                    addError(
                            "[" + name + "] " + layerLabel + " row " + rowIndex
                                    + " has length " + row.length() + ", expected " + expectedWidth);
                    hasErrors = true;
                }

                // Verify symbol mappings
                for (int charIndex = 0; charIndex < row.length(); charIndex++) {
                    char symbol = row.charAt(charIndex);

                    // Skip spaces
                    if (symbol == ' ') {
                        continue;
                    }

                    // Ensure a mapping exists
                    BlockMapping mapping = loader.getMapping(name, symbol);
                    if (mapping == null) {
                        addError(
                                "[" + name + "] Unknown symbol '" + symbol + "' at "
                                        + layerLabel + " row " + rowIndex + " col " + charIndex);
                        hasErrors = true;
                    }
                }
            }
        }

        return hasErrors;
    }

    /**
     * Validate that mapped block IDs resolve correctly.
     * Note: this requires the game to be fully loaded.
     *
     * @param name structure name
     * @return true if errors are found
     */
    public boolean validateBlockIds(String name) {
        boolean hasErrors = false;
        StructureEntry entry = loader.getStructureEntry(name);
        if (entry == null)
            return false;

        // Validate default mappings
        Map<Character, BlockMapping> defaultMappings = loader.getDefaultMappings();
        for (Map.Entry<Character, BlockMapping> mapEntry : defaultMappings.entrySet()) {
            if (!validateBlockMapping(name, mapEntry.getKey(), mapEntry.getValue())) {
                hasErrors = true;
            }
        }

        // Validate structure-specific mappings
        if (entry.mappings != null) {
            for (Map.Entry<String, Object> mapEntry : entry.mappings.entrySet()) {
                if (mapEntry.getValue() instanceof BlockMapping) {
                    char symbol = mapEntry.getKey()
                            .charAt(0);
                    if (!validateBlockMapping(name, symbol, (BlockMapping) mapEntry.getValue())) {
                        hasErrors = true;
                    }
                }
            }
        }

        return hasErrors;
    }

    /**
     * Validate a block mapping entry.
     */
    private boolean validateBlockMapping(String structureName, char symbol, BlockMapping mapping) {
        if (mapping == null)
            return true;

        // Single-block mappings
        if (mapping.block != null && !mapping.block.isEmpty()) {
            BlockResolver.ResolvedBlock resolved = BlockResolver.resolve(mapping.block);
            if (resolved == null && !"air".equalsIgnoreCase(mapping.block)) {
                addError("[" + structureName + "] Invalid block ID for symbol '" + symbol + "': " + mapping.block);
                return false;
            }
        }

        // Multi-block mappings
        if (mapping.blocks != null) {
            for (BlockEntry blockEntry : mapping.blocks) {
                if (blockEntry.id != null) {
                    BlockResolver.ResolvedBlock resolved = BlockResolver.resolve(blockEntry.id);
                    if (resolved == null && !"air".equalsIgnoreCase(blockEntry.id)) {
                        addError(
                                "[" + structureName + "] Invalid block ID for symbol '" + symbol + "': "
                                        + blockEntry.id);
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
