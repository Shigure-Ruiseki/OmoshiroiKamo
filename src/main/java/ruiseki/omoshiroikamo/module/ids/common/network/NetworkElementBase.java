package ruiseki.omoshiroikamo.module.ids.common.network;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;

import lombok.Data;
import ruiseki.omoshiroikamo.api.ids.network.INetwork;
import ruiseki.omoshiroikamo.api.ids.network.INetworkElement;

/**
 * Base implementation for a network element.
 * 
 * @author rubensworks
 */
@Data
public abstract class NetworkElementBase<N extends INetwork> implements INetworkElement<N> {

    @Override
    public int getUpdateInterval() {
        return 0;
    }

    @Override
    public boolean isUpdate() {
        return false;
    }

    @Override
    public void update(N network) {

    }

    @Override
    public void beforeNetworkKill(N network) {

    }

    @Override
    public void afterNetworkAlive(N network) {

    }

    @Override
    public void afterNetworkReAlive(N network) {

    }

    @Override
    public void addDrops(List<ItemStack> itemStacks, boolean dropMainElement) {

    }

    @Override
    public boolean onNetworkAddition(N network) {
        return true;
    }

    @Override
    public void onNetworkRemoval(N network) {

    }

    @Override
    public void onPreRemoved(N network) {

    }

    @Override
    public void onNeighborBlockChange(N network, IBlockAccess world, Block neighborBlock) {

    }
}
