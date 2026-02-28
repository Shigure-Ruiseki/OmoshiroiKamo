package ruiseki.omoshiroikamo.api.structure.visitor;

import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.api.structure.core.IStructureLayer;
import ruiseki.omoshiroikamo.api.structure.io.IStructureRequirement;

/**
 * Visitor that validates structure definitions.
 */
public class StructureValidationVisitor implements IStructureVisitor {

    private final List<String> errors = new ArrayList<>();
    private IStructureEntry currentEntry;

    public List<String> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    private void addError(String message) {
        String prefix = currentEntry != null ? "[" + currentEntry.getName() + "] " : "";
        errors.add(prefix + message);
    }

    @Override
    public void visit(IStructureEntry entry) {
        this.currentEntry = entry;
        validateShape(entry);
    }

    @Override
    public void visit(IStructureRequirement requirement) {
        // Validation for requirements can be added here if needed
        if (requirement.getMinCount() < 0) {
            addError("Requirement " + requirement.getType() + " has negative min count");
        }
        if (requirement.getMaxCount() < requirement.getMinCount()) {
            addError("Requirement " + requirement.getType() + " has max count less than min count");
        }
    }

    private void validateShape(IStructureEntry entry) {
        List<IStructureLayer> layers = entry.getLayers();
        if (layers == null || layers.isEmpty()) {
            addError("No layers defined");
            return;
        }

        // Use the first layer as the size baseline
        IStructureLayer firstLayer = layers.get(0);
        if (firstLayer.getRows() == null || firstLayer.getRows()
            .isEmpty()) {
            addError("Layer 0 has no rows");
            return;
        }

        int expectedDepth = firstLayer.getRows()
            .size();
        int expectedWidth = firstLayer.getRows()
            .get(0)
            .length();

        int qCount = 0;

        for (int l = 0; l < layers.size(); l++) {
            IStructureLayer layer = layers.get(l);
            List<String> rows = layer.getRows();

            if (rows == null || rows.isEmpty()) {
                addError("Layer " + l + " has no rows");
                continue;
            }

            if (rows.size() != expectedDepth) {
                addError("Layer " + l + " has " + rows.size() + " rows, expected " + expectedDepth);
            }

            for (int r = 0; r < rows.size(); r++) {
                String row = rows.get(r);
                if (row.length() != expectedWidth) {
                    addError(
                        "Layer " + l + " row " + r + " has length " + row.length() + ", expected " + expectedWidth);
                }

                for (int c = 0; c < row.length(); c++) {
                    char symbol = row.charAt(c);
                    if (symbol == ' ') continue;

                    if (symbol == 'Q') qCount++;

                    if (!entry.getMappings()
                        .containsKey(symbol) && symbol != '_' && symbol != 'Q') {
                        addError("Unknown symbol '" + symbol + "' at Layer " + l + " row " + r + " col " + c);
                    }
                }
            }
        }

        if (qCount == 0) {
            addError("No controller 'Q' found");
        } else if (qCount > 1) {
            addError("Multiple controllers 'Q' found (" + qCount + "), must be exactly 1");
        }
    }
}
