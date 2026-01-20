package ruiseki.omoshiroikamo.api.modular;

import net.minecraft.world.IBlockAccess;

import com.gtnewhorizon.gtnhlib.client.model.color.IBlockColor;

/**
 * Extended interface for modular blocks that support dynamic color tinting.
 * 
 * Tinting priority:
 * 1. Structure color (if formed and defined) - highest priority
 * 2. Config default color - fallback
 */
public interface IModularBlockTint extends IBlockColor {

    /**
     * Check if this block is part of a formed structure and should use structure
     * color.
     * 
     * @param world Block access
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     * @return Structure tint color, or null if not part of formed structure
     */
    default Integer getStructureTintColor(IBlockAccess world, int x, int y, int z) {
        return null;
    }
}
