package ruiseki.omoshiroikamo.module.ids.common.block;

import java.util.Map;

import com.google.common.collect.Maps;

import lombok.experimental.Delegate;
import ruiseki.omoshiroikamo.api.ids.tileentity.ITileCableNetwork;
import ruiseki.omoshiroikamo.core.persist.nbt.NBTPersist;
import ruiseki.omoshiroikamo.core.tileentity.TileEntityOK;

public class TECableConnectable extends TileEntityOK
    implements ITileCableNetwork, TileEntityOK.ITickingTile, TileCableNetworkComponent.IConnectionsMapProvider {

    @Delegate
    protected final ITickingTile tickingTileComponent = new TickingTileComponent(this);
    @Delegate(types = { ITileCableNetwork.class })
    protected final TileCableNetworkComponent tileCableNetworkComponent = new TileCableNetworkComponent(this);

    @NBTPersist
    private Map<Integer, Boolean> connected = Maps.newHashMap();

    @Override
    protected void doUpdate() {
        super.doUpdate();
        tileCableNetworkComponent.doUpdate();
    }

    @Override
    public Map<Integer, Boolean> getConnections() {
        return connected;
    }
}
