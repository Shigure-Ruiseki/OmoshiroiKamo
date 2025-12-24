package ruiseki.omoshiroikamo.core.common.structure;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockAnyMeta;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;

import com.gtnewhorizon.structurelib.structure.IStructureElement;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.core.common.util.Logger;

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
            return (IStructureElement<T>) ofBlockAnyMeta(result.block, 0);
        } else {
            return (IStructureElement<T>) ofBlock(result.block, result.meta);
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
