package ruiseki.omoshiroikamo.core.common.structure;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.isAir;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.gtnewhorizon.structurelib.structure.AutoPlaceEnvironment;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.IStructureElementChain;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import ruiseki.omoshiroikamo.api.modular.IMachineController;
import ruiseki.omoshiroikamo.api.structure.core.BlockMapping;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.api.structure.core.IStructureLayer;
import ruiseki.omoshiroikamo.api.structure.core.ISymbolMapping;
import ruiseki.omoshiroikamo.api.structure.core.TieredBlockMapping;
import ruiseki.omoshiroikamo.core.common.util.Logger;

/**
 * Registers CustomStructures with StructureLib using the new IStructureEntry API.
 */
public class CustomStructureRegistry {

    private static final Map<String, IStructureDefinition<IMachineController>> structureDefinitions = new HashMap<>();
    private static final Map<String, int[]> controllerOffsets = new HashMap<>();

    // Registered by MachineryModule.preInit()
    private static Block controllerBlock = null;
    // Registered by MultiBlockModule (or any module providing a default structure block)
    private static Block defaultStructureBlock = null;

    public static void registerControllerBlock(Block block) {
        controllerBlock = block;
    }

    public static void registerDefaultStructureBlock(Block block) {
        defaultStructureBlock = block;
    }

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

            // Apply rotation based on defaultFacing
            // SOUTH/NORTH etc: standard 180 rotation to align with StructureLib
            // UP/DOWN: transform layers (Y) to depth (Z) so it builds upright
            String[][] processedShape;
            String defaultFacing = entry.getDefaultFacing();
            if (defaultFacing != null
                && (defaultFacing.equalsIgnoreCase("UP") || defaultFacing.equalsIgnoreCase("DOWN"))) {
                processedShape = transformForVertical(shape, defaultFacing);
            } else {
                processedShape = rotate180(shape);
            }

            // Find controller 'Q' position in the PROCESSED shape
            // returns {col, layer, row} -> maps to {A, B, C}
            int[] offset = findControllerOffset(processedShape);
            controllerOffsets.put(entry.getName(), offset);

            StructureDefinition.Builder<IMachineController> builder = StructureDefinition.builder();
            builder.addShape(entry.getName(), transpose(processedShape));

            if (controllerBlock == null) {
                Logger.warn(
                    "CustomStructureRegistry: no controller block registered — call registerControllerBlock() in preInit");
            } else {
                builder.addElement('Q', ofBlock(controllerBlock, 0));
            }
            builder.addElement('_', isAir());

            if (!entry.getMappings()
                .containsKey('F')) {
                if (defaultStructureBlock == null) {
                    Logger.warn(
                        "CustomStructureRegistry: no default structure block registered — 'F' symbol will be skipped");
                } else {
                    builder.addElement('F', wrapTracking('F', ofBlock(defaultStructureBlock, 0)));
                }
            }

            // Add dynamic mappings
            for (Map.Entry<Character, ISymbolMapping> mapEntry : entry.getMappings()
                .entrySet()) {
                char symbol = mapEntry.getKey();
                if (symbol == 'Q' || symbol == '_' || symbol == ' ') continue;

                IStructureElement<IMachineController> element = createElementFromMapping(mapEntry.getValue());
                if (element != null) {
                    builder.addElement(symbol, wrapTracking(symbol, element));
                } else {
                    Logger.warn(
                        "CustomStructureRegistry: Failed to create element for symbol '" + symbol
                            + "' in structure '"
                            + entry.getName()
                            + "'");
                }
            }

            IStructureDefinition<IMachineController> definition = builder.build();
            structureDefinitions.put(entry.getName(), definition);
            Logger.info("CustomStructureRegistry: SUCCESS - Structure '" + entry.getName() + "' registered!");

        } catch (Exception e) {
            Logger.error("CustomStructureRegistry: Failed to register structure '" + entry.getName() + "'", e);
        }
    }

    public static IStructureDefinition<IMachineController> getDefinition(String name) {
        return structureDefinitions.get(name);
    }

    public static boolean hasDefinition(String name) {
        return structureDefinitions.containsKey(name);
    }

    public static int[] getControllerOffset(String name) {
        return controllerOffsets.getOrDefault(name, new int[] { 0, 0, 0 });
    }

    private static IStructureElement<IMachineController> wrapTracking(char symbol,
        IStructureElement<IMachineController> element) {
        return new IStructureElementChain<IMachineController>() {

            @Override
            public boolean check(IMachineController t, World world, int x, int y, int z) {
                t.trackSymbolPosition(symbol, x, y, z);
                boolean result = element.check(t, world, x, y, z);
                if (result) {
                    t.finalizeSymbolPosition(symbol, x, y, z);
                }
                return result;
            }

            @Override
            public boolean spawnHint(IMachineController t, World world, int x, int y, int z, ItemStack trigger) {
                return element.spawnHint(t, world, x, y, z, trigger);
            }

            @Override
            public boolean placeBlock(IMachineController t, World world, int x, int y, int z, ItemStack trigger) {
                return element.placeBlock(t, world, x, y, z, trigger);
            }

            @Override
            public BlocksToPlace getBlocksToPlace(IMachineController t, World world, int x, int y, int z,
                ItemStack trigger, AutoPlaceEnvironment env) {
                return element.getBlocksToPlace(t, world, x, y, z, trigger, env);
            }

            @Override
            @SuppressWarnings("unchecked")
            public IStructureElement<IMachineController>[] fallbacks() {
                return new IStructureElement[] { element };
            }
        };
    }

    private static IStructureElement<IMachineController> createElementFromMapping(ISymbolMapping mapping) {
        if (mapping instanceof BlockMapping) {
            BlockMapping bm = (BlockMapping) mapping;
            if (bm.getBlockId() != null) {
                return BlockResolver.createElement(bm.getBlockId());
            } else if (bm.getBlockIds() != null) {
                return BlockResolver.createChainElementWithTileAdder(bm.getBlockIds());
            }
        } else if (mapping instanceof TieredBlockMapping) {
            TieredBlockMapping tm = (TieredBlockMapping) mapping;
            return BlockResolver.createChainElementWithTileAdder(
                new ArrayList<>(
                    tm.getTiers()
                        .keySet()));
        }
        return null;
    }

    private static String[][] transformForVertical(String[][] shape, String facing) {
        int originalLayers = shape.length;
        if (originalLayers == 0) return shape;

        int originalRows = shape[0].length;
        boolean isDown = "DOWN".equalsIgnoreCase(facing);

        // Transform layers (Y) to rows (Z) to build the structure vertically
        String[][] transformed = new String[originalRows][originalLayers];

        for (int originalZ = 0; originalZ < originalRows; originalZ++) {
            for (int originalY = 0; originalY < originalLayers; originalY++) {
                int targetLayerIndex = originalZ;
                int targetRowIndex = isDown ? (originalLayers - 1 - originalY) : originalY;

                if (shape[originalY].length > originalZ) {
                    transformed[targetLayerIndex][targetRowIndex] = shape[originalY][originalZ];
                } else {
                    transformed[targetLayerIndex][targetRowIndex] = "";
                }
            }
        }
        return transformed;
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
