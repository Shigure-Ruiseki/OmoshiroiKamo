package ruiseki.omoshiroikamo.api.structure.io;

import net.minecraft.world.World;

/**
 * Defines a requirement for a structure to be validly formed (e.g., number of
 * ports).
 */
public interface IStructureRequirement extends IStructureSerializable {

    /**
     * Get the unique type identifier (e.g., "itemInput").
     */
    String getType();

    /**
     * Get the minimum required count.
     */
    int getMinCount();

    /**
     * Get the maximum allowed count.
     */
    int getMaxCount();

    /**
     * Check if the block/TileEntity at the given coordinates matches this
     * requirement.
     * 
     * @param world The world instance.
     * @param x,    y, z The coordinates to check.
     * @return true if the block at the coordinates fulfills this requirement.
     */
    boolean matches(World world, int x, int y, int z);
}
