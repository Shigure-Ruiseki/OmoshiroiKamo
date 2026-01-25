package ruiseki.omoshiroikamo.module.machinery.common.tile;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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

        int dimension;
        if (world instanceof World) {
            dimension = ((World) world).provider.dimensionId;
        } else {
            // Try to get dimension from TileEntity if world is not a World instance (e.g.
            // ChunkCache)
            TileEntity te = world.getTileEntity(x, y, z);
            if (te != null && te.getWorldObj() != null) {
                dimension = te.getWorldObj().provider.dimensionId;
            } else {
                // Fallback: try to get dimension from client world (for non-TE blocks like
                // Casing)
                dimension = getClientDimension();
                if (dimension == Integer.MIN_VALUE) {
                    return null;
                }
            }
        }

        Map<ChunkCoordinates, Integer> dimensionCache = cache.get(dimension);
        if (dimensionCache != null) {
            return dimensionCache.get(new ChunkCoordinates(x, y, z));
        }
        return null;
    }

    /**
     * Get dimension ID from client world.
     * Returns Integer.MIN_VALUE if not on client side or client world is null.
     */
    private static int getClientDimension() {
        // This method is called during rendering, which is always client-side
        // Use reflection-free approach: FMLCommonHandler for side check
        if (FMLCommonHandler.instance()
            .getSide()
            .isClient()) {
            return getClientDimensionImpl();
        }
        return Integer.MIN_VALUE;
    }

    /**
     * Client-only implementation to get dimension ID.
     * Separated to prevent class loading issues on server.
     */
    @SideOnly(Side.CLIENT)
    private static int getClientDimensionImpl() {
        return Minecraft.getMinecraft().theWorld.provider.dimensionId;
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
