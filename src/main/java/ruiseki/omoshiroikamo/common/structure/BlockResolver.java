package ruiseki.omoshiroikamo.common.structure;

import net.minecraft.block.Block;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Utility for resolving Block objects from block ID strings.
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
            return null;
        }

        return new ResolvedBlock(block, meta, anyMeta);
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
