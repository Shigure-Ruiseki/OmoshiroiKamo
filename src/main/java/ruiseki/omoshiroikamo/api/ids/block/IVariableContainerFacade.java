package ruiseki.omoshiroikamo.api.ids.block;

import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.block.BlockPos;

/**
 * Facade for a {@link IVariableContainer} at a certain position.
 * Must be implemented on blocks.
 * 
 * @author rubensworks
 */
public interface IVariableContainerFacade {

    /**
     * Get the variable container at a given position.
     * 
     * @param world The world.
     * @param pos   The position.
     * @return The variable container.
     */
    public IVariableContainer getVariableContainer(World world, BlockPos pos);

}
