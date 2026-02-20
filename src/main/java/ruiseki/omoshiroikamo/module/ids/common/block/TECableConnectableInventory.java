package ruiseki.omoshiroikamo.module.ids.common.block;

import java.util.Map;

import com.google.common.collect.Maps;

import lombok.experimental.Delegate;
import ruiseki.omoshiroikamo.api.ids.tileentity.ITileCableNetwork;
import ruiseki.omoshiroikamo.core.persist.nbt.NBTPersist;
import ruiseki.omoshiroikamo.core.tileentity.AbstractInventoryTE;
import ruiseki.omoshiroikamo.core.tileentity.TileEntityOK;

public class TECableConnectableInventory extends AbstractInventoryTE
    implements TileEntityOK.ITickingTile, TileCableNetworkComponent.IConnectionsMapProvider {

    @Delegate
    protected final ITickingTile tickingTileComponent = new TickingTileComponent(this);
    @Delegate(types = { ITileCableNetwork.class })
    protected final TileCableNetworkComponent tileCableNetworkComponent = new TileCableNetworkComponent(this);

    @NBTPersist
    private Map<Integer, Boolean> connected = Maps.newHashMap();

    public TECableConnectableInventory(int inventorySize, String inventoryName, int stackSize) {
        super(inventorySize, inventoryName, stackSize);
    }

    @Override
    protected void doUpdate() {
        super.doUpdate();
        tileCableNetworkComponent.doUpdate();
    }

    @Override
    public Map<Integer, Boolean> getConnections() {
        return connected;
    }

    /**
     * Called after the network has been fully initialized
     */
    public void afterNetworkReAlive() {

    }
}
