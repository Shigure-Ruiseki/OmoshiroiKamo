package ruiseki.omoshiroikamo.api.ids.block.cable;

import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.ids.path.IPathElement;
import ruiseki.omoshiroikamo.core.datastructure.BlockPos;

/**
 * Interface for cables that can become unreal.
 * This means that for example parts can exist in that block space without the cable being there.
 *
 * @author rubensworks
 */
public interface ICableFakeable<E extends IPathElement<E>> extends ICable<E> {

    /**
     * @param world The world.
     * @param pos   The position of this block.
     * @return If this cable is a real cable, otherwise it is just a holder block for parts without connections.
     */
    public boolean isRealCable(World world, BlockPos pos);

    /**
     * @param world     The world.
     * @param pos       The position of this block.
     * @param realCable If this cable is a real cable, otherwise it is just a holder block for parts without
     *                  connections.
     */
    public void setRealCable(World world, BlockPos pos, boolean realCable);

}
