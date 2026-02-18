package ruiseki.omoshiroikamo.api.ids.block.cable;

import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.block.BlockPos;
import ruiseki.omoshiroikamo.api.ids.network.INetwork;
import ruiseki.omoshiroikamo.api.ids.network.INetworkCarrier;
import ruiseki.omoshiroikamo.api.ids.path.IPathElement;

/**
 * Interface for cables that are network-aware.
 * 
 * @author rubensworks
 */
public interface ICableNetwork<N extends INetwork, E extends IPathElement<E>> extends ICable<E>, INetworkCarrier<N> {

    /**
     * (Re-)initialize the network at the given position.
     * 
     * @param world The world.
     * @param pos   The position of this block.
     */
    public void initNetwork(World world, BlockPos pos);

}
