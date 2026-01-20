package ruiseki.omoshiroikamo.module.machinery.common.tile;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Caches tint colors for structure blocks at the world level.
 * Controllers register/unregister colors on formation/unformation.
 * Port/Casing blocks retrieve colors from this cache.
 * 
 * Thread-safe implementation.
 */
public class StructureTintCache {

    /**
     * Two-level map: dimension ID -> (coordinates -> color)
     */
    private static final Map<Integer, Map<ChunkCoordinates, Integer>> cache = new ConcurrentHashMap<>();

    /**
     * Set color for the specified coordinates
     * 
     * @param world World
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     * @param color ARGB color value
     */
    public static void put(World world, int x, int y, int z, int color) {
        if (world == null) return;

        int dimension = world.provider.dimensionId;
        Map<ChunkCoordinates, Integer> dimensionCache = cache
            .computeIfAbsent(dimension, k -> new ConcurrentHashMap<>());
        dimensionCache.put(new ChunkCoordinates(x, y, z), color);
    }

    /**
     * Remove color for the specified coordinates
     * 
     * @param world World
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     */
    public static void remove(World world, int x, int y, int z) {
        if (world == null) return;

        int dimension = world.provider.dimensionId;
        Map<ChunkCoordinates, Integer> dimensionCache = cache.get(dimension);
        if (dimensionCache != null) {
            dimensionCache.remove(new ChunkCoordinates(x, y, z));
        }
    }

    /**
     * Get color for the specified coordinates
     * 
     * @param world IBlockAccess (World or IBlockAccess implementation)
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     * @return ARGB color value, or null if not in cache
     */
    public static Integer get(IBlockAccess world, int x, int y, int z) {
        if (world == null) return null;

        // No way to get dimension ID from IBlockAccess,
        // so we assume it's a World instance
        if (!(world instanceof World)) return null;

        int dimension = ((World) world).provider.dimensionId;
        Map<ChunkCoordinates, Integer> dimensionCache = cache.get(dimension);
        if (dimensionCache != null) {
            return dimensionCache.get(new ChunkCoordinates(x, y, z));
        }
        return null;
    }

    /**
     * Clear cache for all specified coordinates
     * 
     * @param world     World
     * @param positions Collection of coordinates to clear
     */
    public static void clearAll(World world, Collection<ChunkCoordinates> positions) {
        if (world == null || positions == null) return;

        int dimension = world.provider.dimensionId;
        Map<ChunkCoordinates, Integer> dimensionCache = cache.get(dimension);
        if (dimensionCache != null) {
            for (ChunkCoordinates pos : positions) {
                dimensionCache.remove(pos);
            }
        }
    }

    /**
     * Clear all cache for a specific dimension (for debugging)
     * 
     * @param world World
     */
    public static void clearDimension(World world) {
        if (world == null) return;

        int dimension = world.provider.dimensionId;
        cache.remove(dimension);
    }
}
