package ruiseki.omoshiroikamo.api.recipe.context;

import java.util.List;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;

/**
 * Context interface for recipes that interact with structure blocks.
 * Provides access to world, structure information, and block positions.
 */
public interface IRecipeContext {

    /**
     * Get the world where the structure exists.
     */
    World getWorld();

    /**
     * Get the controller's position.
     */
    ChunkCoordinates getControllerPos();

    /**
     * Get the current structure definition.
     */
    IStructureEntry getCurrentStructure();

    /**
     * Get the facing direction of the structure.
     */
    ForgeDirection getFacing();

    /**
     * Get all block positions for a given symbol in the structure.
     * Positions are in world coordinates.
     *
     * @param symbol The mapping symbol (e.g., 'L' for lens)
     * @return List of block positions with that symbol, in world coordinates
     */
    List<ChunkCoordinates> getSymbolPositions(char symbol);
}
