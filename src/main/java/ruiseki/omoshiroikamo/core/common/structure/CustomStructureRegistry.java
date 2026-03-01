package ruiseki.omoshiroikamo.core.common.structure;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.isAir;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import ruiseki.omoshiroikamo.api.structure.core.BlockMapping;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.api.structure.core.IStructureLayer;
import ruiseki.omoshiroikamo.api.structure.core.ISymbolMapping;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryBlocks;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockBlocks;

/**
 * Registers CustomStructures with StructureLib using the new IStructureEntry
 * API.
 */
public class CustomStructureRegistry {

    private static final Map<String, IStructureDefinition<TEMachineController>> structureDefinitions = new HashMap<>();
    private static final Map<String, int[]> controllerOffsets = new HashMap<>();

    public static void registerAll() {
        structureDefinitions.clear();
        controllerOffsets.clear();

        StructureManager manager = StructureManager.getInstance();
        if (!manager.isInitialized()) {
            Logger.warn("CustomStructureRegistry: StructureManager not initialized!");
            return;
        }

        for (String name : manager.getCustomStructureNames()) {
            IStructureEntry entry = manager.getCustomStructure(name);
            if (entry != null) {
                registerStructure(entry);
            }
        }

        Logger.info("CustomStructureRegistry: Registered " + structureDefinitions.size() + " custom structure(s)");
    }

    private static void registerStructure(IStructureEntry entry) {
        if (entry.getLayers()
            .isEmpty()) {
            Logger.warn("CustomStructureRegistry: Structure '" + entry.getName() + "' has no layers!");
            return;
        }

        try {
            // Convert layers to shape array [Layer][Row]
            List<IStructureLayer> layers = entry.getLayers();
            String[][] shape = new String[layers.size()][];
            for (int i = 0; i < layers.size(); i++) {
                shape[i] = layers.get(i)
                    .getRows()
                    .toArray(new String[0]);
            }

            Logger.info("CustomStructureRegistry: Registering '" + entry.getName() + "'");

            // For now, assume defaultFacing is Southern (or handle it if we add it to
            // IStructureEntry)
            // Existing custom structures might use rotate180 as default to match
            // StructureLib's coordinate system
            String[][] rotatedShape = rotate180(shape);

            // Find controller 'Q' position
            int[] offset = findControllerOffset(rotatedShape);
            controllerOffsets.put(entry.getName(), offset);

            StructureDefinition.Builder<TEMachineController> builder = StructureDefinition.builder();
            builder.addShape(entry.getName(), transpose(rotatedShape));
            builder.addElement('Q', ofBlock(MachineryBlocks.MACHINE_CONTROLLER.getBlock(), 0));
            builder.addElement('_', isAir());

            if (!entry.getMappings()
                .containsKey('F')) {
                builder.addElement('F', ofBlock(MultiBlockBlocks.BASALT_STRUCTURE.getBlock(), 0));
            }

            // Add dynamic mappings
            for (Map.Entry<Character, ISymbolMapping> mapEntry : entry.getMappings()
                .entrySet()) {
                char symbol = mapEntry.getKey();
                if (symbol == 'Q' || symbol == '_' || symbol == ' ') continue;

                IStructureElement<TEMachineController> element = createElementFromMapping(mapEntry.getValue());
                if (element != null) {
                    builder.addElement(symbol, element);
                }
            }

            IStructureDefinition<TEMachineController> definition = builder.build();
            structureDefinitions.put(entry.getName(), definition);
            Logger.info("CustomStructureRegistry: SUCCESS - Structure '" + entry.getName() + "' registered!");

        } catch (Exception e) {
            Logger.error("CustomStructureRegistry: Failed to register structure '" + entry.getName() + "'", e);
        }
    }

    public static IStructureDefinition<TEMachineController> getDefinition(String name) {
        return structureDefinitions.get(name);
    }

    public static boolean hasDefinition(String name) {
        return structureDefinitions.containsKey(name);
    }

    public static int[] getControllerOffset(String name) {
        return controllerOffsets.getOrDefault(name, new int[] { 0, 0, 0 });
    }

    private static IStructureElement<TEMachineController> createElementFromMapping(ISymbolMapping mapping) {
        if (mapping instanceof BlockMapping) {
            BlockMapping bm = (BlockMapping) mapping;
            if (bm.getBlockId() != null) {
                return BlockResolver.createElement(bm.getBlockId());
            } else if (bm.getBlockIds() != null) {
                return BlockResolver.createChainElementWithTileAdder(bm.getBlockIds());
            }
        }
        return null;
    }

    private static String[][] rotate180(String[][] shape) {
        String[][] rotated = new String[shape.length][];
        for (int layer = 0; layer < shape.length; layer++) {
            int numRows = shape[layer].length;
            rotated[layer] = new String[numRows];
            for (int row = 0; row < numRows; row++) {
                int srcRow = numRows - 1 - row;
                rotated[layer][row] = shape[layer][srcRow];
            }
        }
        return rotated;
    }

    private static int[] findControllerOffset(String[][] shape) {
        for (int layer = 0; layer < shape.length; layer++) {
            for (int row = 0; row < shape[layer].length; row++) {
                String rowStr = shape[layer][row];
                for (int col = 0; col < rowStr.length(); col++) {
                    if (rowStr.charAt(col) == 'Q') {
                        return new int[] { col, layer, row };
                    }
                }
            }
        }
        return new int[] { 0, 0, 0 };
    }

    public static Set<String> getRegisteredNames() {
        return new HashSet<>(structureDefinitions.keySet());
    }
}
