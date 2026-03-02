package ruiseki.omoshiroikamo.api.structure.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.api.structure.core.BlockMapping;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.api.structure.core.IStructureLayer;
import ruiseki.omoshiroikamo.api.structure.core.ISymbolMapping;
import ruiseki.omoshiroikamo.api.structure.io.IStructureRequirement;

/**
 * Visitor that validates structure definitions.
 */
public class StructureValidationVisitor implements IStructureVisitor {

    private final List<String> errors = new ArrayList<>();
    private IStructureEntry currentEntry;
    private Map<Character, ?> externalMappings;

    public void setExternalMappings(Map<Character, ?> mappings) {
        this.externalMappings = mappings;
    }

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

                    boolean hasMapping = entry.getMappings()
                        .containsKey(symbol);
                    if (!hasMapping && externalMappings != null) {
                        hasMapping = externalMappings.containsKey(symbol);
                    }

                    if (!hasMapping && symbol != '_' && symbol != 'Q') {
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

    /**
     * Diagnostic scan to find mismatches in the world.
     */
    public void validateInWorld(World world, int x, int y, int z, ExtendedFacing facing, IStructureEntry entry) {
        this.currentEntry = entry;
        List<IStructureLayer> layers = entry.getLayers();

        // 1. Find Q in the entry to establish relative center
        int qL = -1, qR = -1, qC = -1;
        for (int l = 0; l < layers.size(); l++) {
            List<String> rows = layers.get(l)
                .getRows();
            for (int r = 0; r < rows.size(); r++) {
                int cIndex = rows.get(r)
                    .indexOf('Q');
                if (cIndex != -1) {
                    qL = l;
                    qR = r;
                    qC = cIndex;
                    break;
                }
            }
            if (qL != -1) break;
        }

        if (qL == -1) return;

        // 2. Scan each position
        for (int l = 0; l < layers.size(); l++) {
            List<String> rows = layers.get(l)
                .getRows();
            for (int r = 0; r < rows.size(); r++) {
                String row = rows.get(r);
                for (int c = 0; c < row.length(); c++) {
                    char symbol = row.charAt(c);
                    if (symbol == ' ' || symbol == 'Q' || symbol == '_') continue;

                    // Relative structure coordinates (A, B, C)
                    // A: width, B: height (DOWN), C: depth (BACK)
                    int relA = c - qC;
                    int relB = qL - l; // l=0 is bottom, so if l > qL, relB is negative (UP)
                    int relC = r - qR;

                    int[] relABC = new int[] { relA, relB, relC };
                    int[] worldOffset = new int[3];
                    facing.getWorldOffset(relABC, worldOffset);

                    int wx = x + worldOffset[0];
                    int wy = y + worldOffset[1];
                    int wz = z + worldOffset[2];

                    Block block = world.getBlock(wx, wy, wz);
                    int meta = world.getBlockMetadata(wx, wy, wz);

                    // Map symbol to mapping
                    ISymbolMapping mapping = entry.getMappings()
                        .get(symbol);
                    if (mapping == null && externalMappings != null) {
                        mapping = (ISymbolMapping) externalMappings.get(symbol);
                    }

                    if (mapping instanceof BlockMapping) {
                        if (!isMatch(block, meta, (BlockMapping) mapping)) {
                            addError("Mismatch at " + wx + "," + wy + "," + wz + ": expected '" + symbol + "'");
                        }
                    }
                }
            }
        }
    }

    private boolean isMatch(Block block, int meta, BlockMapping mapping) {
        String currentId = GameRegistry.findUniqueIdentifierFor(block)
            .toString();
        List<String> allowedIds = mapping.getBlockIds();
        if (allowedIds == null) {
            allowedIds = new ArrayList<>();
            if (mapping.getBlockId() != null) allowedIds.add(mapping.getBlockId());
        }

        for (String allowed : allowedIds) {
            String[] parts = allowed.split(":");
            if (parts.length < 2) continue;
            String cleanId = parts[0] + ":" + parts[1];

            if (cleanId.equals(currentId)) {
                if (parts.length < 3 || "*".equals(parts[2])) return true;
                try {
                    if (Integer.parseInt(parts[2]) == meta) return true;
                } catch (NumberFormatException ignored) {}
            }
        }
        return false;
    }
}
