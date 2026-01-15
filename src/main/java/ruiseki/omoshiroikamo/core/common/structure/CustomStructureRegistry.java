package ruiseki.omoshiroikamo.core.common.structure;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.isAir;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import ruiseki.omoshiroikamo.core.common.structure.StructureDefinitionData.StructureEntry;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryBlocks;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;

/**
 * Registers CustomStructures with StructureLib.
 */
public class CustomStructureRegistry {

    private static final Map<String, IStructureDefinition<TEMachineController>> structureDefinitions = new HashMap<>();

    /**
     * Register all custom structures with StructureLib.
     * Called from StructureCompat.postInit() after StructureManager.initialize().
     */
    public static void registerAll() {
        structureDefinitions.clear();

        StructureManager manager = StructureManager.getInstance();
        if (!manager.isInitialized()) {
            Logger.warn("CustomStructureRegistry: StructureManager not initialized!");
            return;
        }

        for (String name : manager.getCustomStructureNames()) {
            StructureEntry entry = manager.getCustomStructure(name);
            if (entry != null) {
                registerStructure(entry);
            }
        }

        Logger.info("CustomStructureRegistry: Registered " + structureDefinitions.size() + " custom structure(s)");
    }

    /**
     * Register a single custom structure.
     */
    private static void registerStructure(StructureEntry entry) {
        if (entry.layers == null || entry.layers.isEmpty()) {
            Logger.warn("CustomStructureRegistry: Structure '" + entry.name + "' has no layers!");
            return;
        }

        try {
            // Convert layers to shape array
            String[][] shape = new String[entry.layers.size()][];
            for (int i = 0; i < entry.layers.size(); i++) {
                shape[i] = entry.layers.get(i).rows.toArray(new String[0]);
            }

            // Log shape details
            Logger.info("CustomStructureRegistry: Registering '" + entry.name + "'");
            Logger.info("  Layers: " + shape.length);
            for (int i = 0; i < shape.length; i++) {
                Logger.info("    Layer " + i + ": " + Arrays.toString(shape[i]));
            }

            // Validate Q symbol and find its position
            int qCount = countSymbol(shape, 'Q');
            if (qCount == 0) {
                Logger.error("CustomStructureRegistry: Structure '" + entry.name + "' has no controller 'Q'!");
                return;
            } else if (qCount > 1) {
                Logger.error("CustomStructureRegistry: Structure '" + entry.name + "' has " + qCount + " controllers!");
                return;
            }
            Logger.info("  Controller 'Q' found: " + qCount);

            // Calculate controller offset from Q position
            entry.controllerOffset = findControllerOffset(shape);

            // Build structure definition
            StructureDefinition.Builder<TEMachineController> builder = StructureDefinition.builder();

            // Add shape
            builder.addShape(entry.name, transpose(shape));

            // Add Controller ('Q')
            builder.addElement('Q', ofBlock(MachineryBlocks.MACHINE_CONTROLLER.getBlock(), 0));

            // Add Air element ('_')
            builder.addElement('_', isAir());

            // Space means 'any' - skip validation (don't add element for space)
            // StructureLib treats undefined symbols as 'skip' by default

            // Add dynamic mappings from JSON
            int mappingCount = 0;
            if (entry.mappings != null) {
                for (Map.Entry<String, Object> mapEntry : entry.mappings.entrySet()) {
                    if (mapEntry.getKey()
                        .isEmpty()) continue;
                    char symbol = mapEntry.getKey()
                        .charAt(0);

                    // Skip reserved symbols
                    if (symbol == 'Q' || symbol == '_' || symbol == ' ') continue;

                    IStructureElement<TEMachineController> element = createElementFromMapping(
                        mapEntry.getValue(),
                        symbol);
                    if (element != null) {
                        builder.addElement(symbol, element);
                        mappingCount++;
                        Logger.info("  Mapping '" + symbol + "' registered");
                    }
                }
            }
            Logger.info("  Total mappings: " + mappingCount);

            // Build and register
            IStructureDefinition<TEMachineController> definition = builder.build();
            structureDefinitions.put(entry.name, definition);

            Logger.info("CustomStructureRegistry: SUCCESS - Structure '" + entry.name + "' registered!");
            if (entry.recipeGroup != null) {
                Logger.info("  Recipe group: " + entry.recipeGroup);
            }
        } catch (Exception e) {
            Logger.error("CustomStructureRegistry: Failed to register structure '" + entry.name + "'", e);
        }
    }

    /**
     * Get a registered structure definition by name.
     */
    public static IStructureDefinition<TEMachineController> getDefinition(String name) {
        return structureDefinitions.get(name);
    }

    /**
     * Check if a structure is registered.
     */
    public static boolean hasDefinition(String name) {
        return structureDefinitions.containsKey(name);
    }

    /**
     * Get all registered structure names.
     */
    public static Set<String> getRegisteredNames() {
        return new HashSet<>(structureDefinitions.keySet());
    }

    /**
     * Create a StructureLib element from a mapping object.
     */
    @SuppressWarnings("unchecked")
    private static IStructureElement<TEMachineController> createElementFromMapping(Object mapping, char symbol) {
        // Use BlockResolver from StructureRegistrationUtils pattern
        if (mapping instanceof String) {
            return BlockResolver.createElement((String) mapping);
        }

        // Handle direct List (JSON array mapped directly, e.g., "P": ["block1",
        // "block2"])
        if (mapping instanceof List) {
            List<?> listMapping = (List<?>) mapping;
            List<String> blockStrings = new ArrayList<>();
            for (Object item : listMapping) {
                if (item instanceof String) {
                    blockStrings.add((String) item);
                }
            }
            if (!blockStrings.isEmpty()) {
                return BlockResolver.createChainElementWithTileAdder(blockStrings);
            }
        }

        if (mapping instanceof StructureDefinitionData.BlockMapping) {
            StructureDefinitionData.BlockMapping blockMapping = (StructureDefinitionData.BlockMapping) mapping;
            if (blockMapping.block != null && !blockMapping.block.isEmpty()) {
                return BlockResolver.createElement(blockMapping.block);
            }
            if (blockMapping.blocks != null && !blockMapping.blocks.isEmpty()) {
                List<String> blockStrings = new ArrayList<>();
                for (StructureDefinitionData.BlockEntry entry : blockMapping.blocks) {
                    if (entry != null && entry.id != null) {
                        blockStrings.add(entry.id);
                    }
                }
                return BlockResolver.createChainElementWithTileAdder(blockStrings);
            }
        }

        // Handle raw Map from Gson
        if (mapping instanceof Map) {
            Map<String, Object> mapData = (Map<String, Object>) mapping;
            if (mapData.containsKey("block")) {
                Object blockObj = mapData.get("block");
                if (blockObj instanceof String) {
                    return BlockResolver.createElement((String) blockObj);
                }
            }
            if (mapData.containsKey("blocks")) {
                Object blocksObj = mapData.get("blocks");
                if (blocksObj instanceof List) {
                    List<?> blocksList = (List<?>) blocksObj;
                    List<String> blockStrings = new ArrayList<>();
                    for (Object item : blocksList) {
                        if (item instanceof Map) {
                            Map<String, Object> blockItem = (Map<String, Object>) item;
                            Object idObj = blockItem.get("id");
                            if (idObj instanceof String) {
                                blockStrings.add((String) idObj);
                            }
                        } else if (item instanceof String) {
                            blockStrings.add((String) item);
                        }
                    }
                    if (!blockStrings.isEmpty()) {
                        return BlockResolver.createChainElementWithTileAdder(blockStrings);
                    }
                }
            }
        }

        Logger.warn("CustomStructureRegistry: Unknown mapping type for symbol '" + symbol + "'");
        return null;
    }

    /**
     * Count occurrences of a symbol in a shape array.
     */
    private static int countSymbol(String[][] shape, char symbol) {
        int count = 0;
        for (String[] layer : shape) {
            for (String row : layer) {
                for (int i = 0; i < row.length(); i++) {
                    if (row.charAt(i) == symbol) count++;
                }
            }
        }
        return count;
    }

    /**
     * Find the controller (Q) position and return the offset for StructureLib.
     * 
     * JSON format coordinate mapping:
     * - layer index = Y axis (vertical, up is positive)
     * - row index = Z axis (depth, controller facing direction)
     * - col (char) index = X axis (horizontal)
     * 
     * StructureLib buildOrHints expects (offsetA, offsetB, offsetC) which are:
     * - offsetA: horizontal offset (X)
     * - offsetB: vertical offset (Y)
     * - offsetC: depth offset (Z)
     * 
     * The offset represents where the controller is relative to the structure
     * origin (0,0,0).
     */
    private static int[] findControllerOffset(String[][] shape) {
        for (int layer = 0; layer < shape.length; layer++) {
            for (int row = 0; row < shape[layer].length; row++) {
                String rowStr = shape[layer][row];
                for (int col = 0; col < rowStr.length(); col++) {
                    if (rowStr.charAt(col) == 'Q') {
                        // Controller found at (layer, row, col) in JSON coordinates
                        // Map to StructureLib: X=col, Y=layer, Z=row
                        return new int[] { col, layer, row };
                    }
                }
            }
        }
        // Default if not found
        return new int[] { 0, 0, 0 };
    }
}
