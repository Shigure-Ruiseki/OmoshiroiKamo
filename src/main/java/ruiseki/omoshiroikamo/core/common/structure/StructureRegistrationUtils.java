package ruiseki.omoshiroikamo.core.common.structure;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.isAir;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import net.minecraft.block.Block;

import com.gtnewhorizon.structurelib.alignment.constructable.IMultiblockInfoContainer;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractMBModifierTE;
import ruiseki.omoshiroikamo.core.common.structure.StructureDefinitionData.BlockEntry;
import ruiseki.omoshiroikamo.core.common.structure.StructureDefinitionData.BlockMapping;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.multiblock.common.block.TieredMultiblockInfoContainer;

public class StructureRegistrationUtils {

    // Reserved symbols that are handled specially and should not be overridden by
    // JSON
    // Q = Controller (must be exactly 1), space = any, _ = mandatory air
    // A = Modifier slot, L = Lens slot (these need ofBlockAdderWithPos)
    // G = Solar Cells
    private static final String RESERVED_SYMBOLS = "Q _ALG";

    /**
     * Registers a single tier of a multiblock structure.
     * <p>
     * This method creates a builder, adds the shape, adds common elements like the
     * controller ('Q')
     * and structure frames ('F'), allows adding custom elements, builds the
     * definition,
     * and registers it with the TieredMultiblockInfoContainer.
     *
     * @param <T>             The TileEntity type.
     * @param tileClass       The class of the TileEntity.
     * @param shape           The shape array (before transposition).
     * @param shapeName       The name of the shape (e.g.,
     *                        SolarArrayShapes.STRUCTURE_TIER_1).
     * @param controllerBlock The block used for the controller ('Q').
     * @param tier            The tier number (1-6). Used for metadata of controller
     *                        and frame blocks.
     * @param elementAdder    A consumer to add structure-specific elements (e.g.,
     *                        'P', 'A', 'L') to the builder.
     * @return The built IStructureDefinition.
     */
    public static <T extends AbstractMBModifierTE> IStructureDefinition<T> registerTier(Class<T> tileClass,
        String[][] shape, String shapeName, Block controllerBlock, int tier,
        Consumer<StructureDefinition.Builder<T>> elementAdder) {

        // Validate: Q must appear exactly once
        int qCount = countSymbolInShape(shape, 'Q');
        if (qCount == 0) {
            Logger.error("Structure " + shapeName + " has no controller 'Q'!");
        } else if (qCount > 1) {
            Logger.error("Structure " + shapeName + " has " + qCount + " controllers 'Q'! Must be exactly 1.");
        }

        StructureDefinition.Builder<T> builder = StructureDefinition.builder();

        // Add Shape
        builder.addShape(shapeName, transpose(shape));

        // Add Controller ('Q') - Meta is usually tier - 1
        builder.addElement('Q', ofBlock(controllerBlock, tier - 1));

        // Add Air element ('_') - mandatory air block
        builder.addElement('_', isAir());

        // Add Custom Elements
        if (elementAdder != null) {
            elementAdder.accept(builder);
        }

        // Build Definition
        IStructureDefinition<T> definition = builder.build();

        // Register with Container
        IMultiblockInfoContainer.registerTileClass(tileClass, new TieredMultiblockInfoContainer<>(definition));

        return definition;
    }

    /**
     * Registers a tier with dynamic mappings from JSON.
     * This overload supports adding elements from JSON mappings in addition to
     * the hardcoded element adder.
     *
     * @param <T>             The TileEntity type.
     * @param tileClass       The class of the TileEntity.
     * @param shape           The shape array (before transposition).
     * @param dynamicMappings Map of character symbols to BlockMapping or simple
     *                        block strings
     * @param shapeName       The name of the shape.
     * @param controllerBlock The block used for the controller ('Q').
     * @param tier            The tier number (1-6).
     * @param elementAdder    A consumer to add structure-specific elements.
     * @return The built IStructureDefinition.
     */
    public static <T extends AbstractMBModifierTE> IStructureDefinition<T> registerTierWithDynamicMappings(
        Class<T> tileClass, String[][] shape, Map<Character, Object> dynamicMappings, String shapeName,
        Block controllerBlock, int tier, Consumer<StructureDefinition.Builder<T>> elementAdder) {

        // Validate: Q must appear exactly once
        int qCount = countSymbolInShape(shape, 'Q');
        if (qCount == 0) {
            String error = "Structure " + shapeName + " has no controller 'Q'!";
            Logger.error(error);
            StructureManager.getInstance()
                .getErrorCollector()
                .collect(StructureException.ErrorType.VALIDATION_ERROR, shapeName, error);
        } else if (qCount > 1) {
            String error = "Structure " + shapeName + " has " + qCount + " controllers 'Q'! Must be exactly 1.";
            Logger.error(error);
            StructureManager.getInstance()
                .getErrorCollector()
                .collect(StructureException.ErrorType.VALIDATION_ERROR, shapeName, error);
        }

        StructureDefinition.Builder<T> builder = StructureDefinition.builder();

        // Add Shape
        builder.addShape(shapeName, transpose(shape));

        // Add Controller ('Q') - Meta is usually tier - 1
        builder.addElement('Q', ofBlock(controllerBlock, tier - 1));

        // Add Air element ('_') - mandatory air block
        builder.addElement('_', isAir());

        // Add dynamic mappings from JSON (skip reserved symbols)
        if (dynamicMappings != null) {
            for (Map.Entry<Character, Object> entry : dynamicMappings.entrySet()) {
                char symbol = entry.getKey();

                // Skip reserved symbols
                if (RESERVED_SYMBOLS.indexOf(symbol) >= 0) {
                    continue;
                }

                IStructureElement<T> element = createElementFromMapping(entry.getValue());
                if (element != null) {
                    builder.addElement(symbol, element);
                } else {
                    Logger.warn("Failed to create element for symbol '" + symbol + "' in structure " + shapeName);
                }
            }
        } else {
            Logger.warn("Structure " + shapeName + ": dynamicMappings is null!");
        }

        // Add Custom Elements (can override dynamic mappings)
        if (elementAdder != null) {
            elementAdder.accept(builder);
        }

        // Build Definition
        IStructureDefinition<T> definition = builder.build();

        // Register with Container
        IMultiblockInfoContainer.registerTileClass(tileClass, new TieredMultiblockInfoContainer<>(definition));

        return definition;
    }

    /**
     * Create a StructureLib element from a mapping object.
     * Supports:
     * - Simple string: "mod:block:meta"
     * - BlockMapping object with blocks list
     * - Map object (from JSON parsing)
     */
    @SuppressWarnings("unchecked")
    private static <T> IStructureElement<T> createElementFromMapping(Object mapping) {
        if (mapping == null) {
            return null;
        }

        // Handle simple string mapping
        if (mapping instanceof String) {
            return BlockResolver.createElement((String) mapping);
        }

        // Handle BlockMapping object
        if (mapping instanceof BlockMapping) {
            BlockMapping blockMapping = (BlockMapping) mapping;

            // Single block
            if (blockMapping.block != null && !blockMapping.block.isEmpty()) {
                return BlockResolver.createElement(blockMapping.block);
            }

            // Multiple blocks (chain)
            if (blockMapping.blocks != null && !blockMapping.blocks.isEmpty()) {
                List<String> blockStrings = new ArrayList<>();
                for (BlockEntry entry : blockMapping.blocks) {
                    if (entry != null && entry.id != null) {
                        blockStrings.add(entry.id);
                    }
                }
                return BlockResolver.createChainElement(blockStrings);
            }
        }

        // Handle raw Map from JSON (Gson LinkedTreeMap)
        if (mapping instanceof Map) {
            Map<String, Object> mapData = (Map<String, Object>) mapping;

            // Single block: { "block": "mod:block:meta" }
            if (mapData.containsKey("block")) {
                Object blockObj = mapData.get("block");
                if (blockObj instanceof String) {
                    return BlockResolver.createElement((String) blockObj);
                }
            }

            // Multiple blocks: { "blocks": [...] }
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
                        return BlockResolver.createChainElement(blockStrings);
                    }
                }
            }
        }

        Logger.warn(
            "Unknown mapping type: " + mapping.getClass()
                .getName());
        return null;
    }

    /**
     * Count occurrences of a symbol in a shape array.
     */
    private static int countSymbolInShape(String[][] shape, char symbol) {
        int count = 0;
        if (shape != null) {
            for (String[] layer : shape) {
                if (layer != null) {
                    for (String row : layer) {
                        if (row != null) {
                            for (int i = 0; i < row.length(); i++) {
                                if (row.charAt(i) == symbol) {
                                    count++;
                                }
                            }
                        }
                    }
                }
            }
        }
        return count;
    }
}
