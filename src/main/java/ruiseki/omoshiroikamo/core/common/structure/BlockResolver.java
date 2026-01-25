package ruiseki.omoshiroikamo.core.common.structure;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockAnyMeta;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofTileAdder;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.gtnewhorizon.structurelib.structure.IStructureElement;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryBlocks;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;

/**
 * Utility for resolving Block objects from block ID strings
 * and creating StructureLib elements dynamically.
 */
public class BlockResolver {

    /**
     * Resolve a block and metadata from a "mod:block:meta" string.
     *
     * @param blockId identifier such as "minecraft:iron_block:0"
     * @return resolved result, or null on failure
     */
    public static ResolvedBlock resolve(String blockId) {
        if (blockId == null || blockId.isEmpty()) {
            return null;
        }

        // Special handling for "air"
        if ("air".equalsIgnoreCase(blockId)) {
            return new ResolvedBlock(null, 0, true);
        }

        String[] parts = blockId.split(":");
        if (parts.length < 2) {
            return null;
        }

        String modId = parts[0];
        String blockName = parts[1];
        int meta = 0;
        boolean anyMeta = false;

        if (parts.length >= 3) {
            if ("*".equals(parts[2])) {
                anyMeta = true;
            } else {
                try {
                    meta = Integer.parseInt(parts[2]);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }

        Block block = GameRegistry.findBlock(modId, blockName);
        if (block == null) {
            Logger.warn("BlockResolver: Block not found - " + modId + ":" + blockName);
            return null;
        }

        return new ResolvedBlock(block, meta, anyMeta);
    }

    /**
     * Create a StructureLib element for a single block string.
     *
     * @param blockString Format: "mod:blockId:meta" or "mod:blockId:*"
     * @return IStructureElement or null if invalid
     */
    @SuppressWarnings("unchecked")
    public static <T> IStructureElement<T> createElement(String blockString) {
        ResolvedBlock result = resolve(blockString);
        if (result == null || result.isAir) {
            return null;
        }

        if (result.anyMeta) {
            return withTracking((IStructureElement<T>) ofBlockAnyMeta(result.block, 0));
        } else {
            return withTracking((IStructureElement<T>) ofBlock(result.block, result.meta));
        }
    }

    /**
     * Create a chain element from multiple block strings.
     * This allows any of the specified blocks to be valid at this position.
     *
     * @param blockStrings List of block strings
     * @return IStructureElement using ofChain, or null if all invalid
     */
    @SuppressWarnings("unchecked")
    public static <T> IStructureElement<T> createChainElement(List<String> blockStrings) {
        if (blockStrings == null || blockStrings.isEmpty()) {
            return null;
        }

        List<IStructureElement<T>> elements = new ArrayList<>();
        for (String blockString : blockStrings) {
            IStructureElement<T> element = createElement(blockString);
            if (element != null) {
                elements.add(element);
            }
        }

        if (elements.isEmpty()) {
            return null;
        }

        if (elements.size() == 1) {
            return elements.get(0);
        }

        return (IStructureElement<T>) ofChain(elements.toArray(new IStructureElement[0]));
    }

    /**
     * Create a chain element from multiple block strings with TileEntity detection.
     * This allows any of the specified blocks to be valid at this position,
     * and automatically collects IModularPort TileEntities.
     *
     * @param blockStrings List of block strings
     * @return IStructureElement using ofChain with TileAdder, or null if all
     *         invalid
     */
    @SuppressWarnings("unchecked")
    public static IStructureElement<TEMachineController> createChainElementWithTileAdder(List<String> blockStrings) {
        if (blockStrings == null || blockStrings.isEmpty()) {
            return null;
        }

        List<IStructureElement<TEMachineController>> elements = new ArrayList<>();
        Block hintBlock = MachineryBlocks.MACHINE_CASING.getBlock();

        // First, add TileAdder to detect and collect ports
        elements.add(ofTileAdder(BlockResolver::collectPort, hintBlock, 0));

        // Then add block checks for each valid block type
        for (String blockString : blockStrings) {
            IStructureElement<TEMachineController> element = createElement(blockString);
            if (element != null) {
                elements.add(element);
            }
        }

        if (elements.size() <= 1) {
            // Only TileAdder, no valid blocks - return null
            return null;
        }

        return ofChain(elements.toArray(new IStructureElement[0]));
    }

    /**
     * Callback for ofTileAdder to collect IModularPort TileEntities.
     * Called during structure check for each block position.
     *
     * @param controller The machine controller
     * @param tile       The TileEntity at this position (may be null)
     * @return true if the position is valid (port found), false to let block check
     *         handle it
     */
    public static boolean collectPort(TEMachineController controller, TileEntity tile) {
        if (tile == null) return false;

        if (tile instanceof IModularPort port) {
            IPortType.Direction direction = port.getPortDirection();
            switch (direction) {
                case INPUT -> controller.addPortFromStructure(port, true);
                case OUTPUT -> controller.addPortFromStructure(port, false);
                case BOTH -> {
                    controller.addPortFromStructure(port, true);
                    controller.addPortFromStructure(port, false);
                }
                case NONE -> {
                    /* Skip ports with no direction */ }
            }
            return true;
        }

        return false;
    }

    /**
     * Wrap an element to track its position in the controller on success.
     */
    private static <T> IStructureElement<T> withTracking(IStructureElement<T> element) {
        return new IStructureElement<T>() {

            @Override
            public boolean check(T t, World world, int x, int y, int z) {
                if (element.check(t, world, x, y, z)) {
                    if (t instanceof TEMachineController) {
                        ((TEMachineController) t).trackStructureBlock(x, y, z);
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean spawnHint(T t, World world, int x, int y, int z, ItemStack trigger) {
                return element.spawnHint(t, world, x, y, z, trigger);
            }

            @Override
            public boolean placeBlock(T t, World world, int x, int y, int z, ItemStack trigger) {
                return element.placeBlock(t, world, x, y, z, trigger);
            }
        };
    }

    /**
     * Resolved block information.
     */
    public static class ResolvedBlock {

        public final Block block;
        public final int meta;
        public final boolean anyMeta;
        public final boolean isAir;

        public ResolvedBlock(Block block, int meta, boolean anyMeta) {
            this.block = block;
            this.meta = meta;
            this.anyMeta = anyMeta;
            this.isAir = (block == null);
        }
    }
}
