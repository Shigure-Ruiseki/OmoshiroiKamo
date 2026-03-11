package ruiseki.omoshiroikamo.core.common.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ruiseki.omoshiroikamo.api.structure.core.BlockMapping;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.api.structure.core.ISymbolMapping;
import ruiseki.omoshiroikamo.api.structure.visitor.StructureValidationVisitor;

/**
 * Validation logic for structure definitions.
 * Refactored to use StructureValidationVisitor.
 */
public class StructureValidator {

    private final List<String> errors = new ArrayList<>();

    public StructureValidator() {}

    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Validate a single structure entry using the visitor.
     */
    public boolean validateStructure(IStructureEntry entry) {
        StructureValidationVisitor visitor = new StructureValidationVisitor();
        entry.accept(visitor);
        if (visitor.hasErrors()) {
            errors.addAll(visitor.getErrors());
            return true;
        }
        return false;
    }

    /**
     * Validate that mapped block IDs resolve correctly.
     * Note: this requires the game to be fully loaded.
     */
    public boolean validateBlockIds(IStructureEntry entry) {
        boolean hasErrors = false;

        // Check mappings
        for (Map.Entry<Character, ISymbolMapping> mapEntry : entry.getMappings()
            .entrySet()) {
            if (!validateSymbolMapping(entry.getName(), mapEntry.getKey(), mapEntry.getValue())) {
                hasErrors = true;
            }
        }

        return hasErrors;
    }

    private boolean validateSymbolMapping(String structureName, char symbol, ISymbolMapping mapping) {
        if (mapping instanceof BlockMapping) {
            BlockMapping bm = (BlockMapping) mapping;
            if (bm.getBlockId() != null) {
                if (BlockResolver.resolve(bm.getBlockId()) == null && !"air".equalsIgnoreCase(bm.getBlockId())) {
                    errors.add(
                        "[" + structureName + "] Invalid block ID for symbol '" + symbol + "': " + bm.getBlockId());
                    return false;
                }
            }
            if (bm.getBlockIds() != null) {
                for (String id : bm.getBlockIds()) {
                    if (BlockResolver.resolve(id) == null && !"air".equalsIgnoreCase(id)) {
                        errors.add("[" + structureName + "] Invalid block ID for symbol '" + symbol + "': " + id);
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
