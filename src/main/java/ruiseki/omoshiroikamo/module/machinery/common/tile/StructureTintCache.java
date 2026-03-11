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
 * Thread-safe implementation.
 * Optimized to use long keys instead of ChunkCoordinates to reduce GC pressure
 * during rendering.
 */
public class StructureTintCache {

    /**
     * Two-level map: dimension ID -> (packed long coordinates -> color)
     */
    private static final Map<Integer, Map<Long, Integer>> cache = new ConcurrentHashMap<>();

    /**
     * Packs x, y, z into a single long for efficient map key usage.
     */
    private static long pack(int x, int y, int z) {
        return ((long) x & 0x3FFFFFFL) << 38 | ((long) y & 0xFFFL) << 26 | ((long) z & 0x3FFFFFFL);
    }

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
        Map<Long, Integer> dimensionCache = cache.computeIfAbsent(dimension, k -> new ConcurrentHashMap<>());
        dimensionCache.put(pack(x, y, z), color);
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
        Map<Long, Integer> dimensionCache = cache.get(dimension);
        if (dimensionCache != null) {
            dimensionCache.remove(pack(x, y, z));
        }
    }

    /**
     * Clear all cached colors for the specified dimension.
     * Used when switching structures in NEI preview.
     *
     * @param dimensionId Dimension ID to clear
     */
    public static void clearDimension(int dimensionId) {
        Map<Long, Integer> dimensionCache = cache.get(dimensionId);
        if (dimensionCache != null) {
            dimensionCache.clear();
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
            // Prioritize client dimension for rendering consistency
            dimension = getClientDimension();
            if (dimension == Integer.MIN_VALUE) {
                // Fallback: try to get dimension from TileEntity
                TileEntity te = world.getTileEntity(x, y, z);
                if (te != null && te.getWorldObj() != null) {
                    dimension = te.getWorldObj().provider.dimensionId;
                } else {
                    return null;
                }
            }
        }

        Map<Long, Integer> dimensionCache = cache.get(dimension);
        return dimensionCache != null ? dimensionCache.get(pack(x, y, z)) : null;
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
        if (Minecraft.getMinecraft().theWorld == null) return Integer.MIN_VALUE;
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
        Map<Long, Integer> dimensionCache = cache.get(dimension);
        if (dimensionCache != null) {
            for (ChunkCoordinates pos : positions) {
                dimensionCache.remove(pack(pos.posX, pos.posY, pos.posZ));
            }
        }
    }

    /**
     * Clear all cache for a specific dimension.
     *
     * @param world World
     */
    public static void clearDimension(World world) {
        if (world == null) return;

        int dimension = world.provider.dimensionId;
        cache.remove(dimension);
    }

    /**
     * Clear all cached colors for all dimensions.
     */
    public static void clearAll() {
        cache.clear();
    }
}
