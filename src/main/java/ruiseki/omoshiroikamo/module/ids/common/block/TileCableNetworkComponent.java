package ruiseki.omoshiroikamo.module.ids.common.block;

import java.util.Map;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import lombok.Getter;
import lombok.Setter;
import ruiseki.omoshiroikamo.api.ids.block.cable.ICable;
import ruiseki.omoshiroikamo.api.ids.network.IPartNetwork;
import ruiseki.omoshiroikamo.api.ids.tileentity.ITileCableNetwork;
import ruiseki.omoshiroikamo.core.tileentity.TileEntityOK;
import ruiseki.omoshiroikamo.module.ids.common.block.cable.CableNetworkComponent;

/**
 * A convenience component for tiles that require implementation of the {@link ITileCableNetwork} interface.
 * Don't forget to also call the {@link TileCableNetworkComponent#doUpdate()} method when delegating!
 *
 * @author rubensworks
 */
public class TileCableNetworkComponent implements ITileCableNetwork {

    private final TileEntityOK tile;
    private final IConnectionsMapProvider connectionsMapProvider;

    @Getter
    @Setter
    private IPartNetwork network;

    public <T extends TileEntityOK & IConnectionsMapProvider> TileCableNetworkComponent(T tile) {
        this.tile = tile;
        this.connectionsMapProvider = tile;
    }

    public void doUpdate() {
        // If the connection data were reset, update the cable connections
        if (getConnections().isEmpty()) {
            updateConnections();
        }
    }

    protected Map<Integer, Boolean> getConnections() {
        return connectionsMapProvider.getConnections();
    }

    @Override
    public void resetCurrentNetwork() {
        if (network != null) setNetwork(null);
    }

    @Override
    public boolean canConnect(ICable connector, ForgeDirection side) {
        return true;
    }

    @Override
    public void updateConnections() {
        World world = tile.getWorldObj();
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            boolean cableConnected = CableNetworkComponent
                .canSideConnect(world, tile.getPos(), side, (ICable) tile.getBlock());
            getConnections().put(side.ordinal(), cableConnected);
        }
        tile.markDirty();
    }

    @Override
    public boolean isConnected(ForgeDirection side) {
        return getConnections().containsKey(side.ordinal()) && getConnections().get(side.ordinal());
    }

    @Override
    public void disconnect(ForgeDirection side) {

    }

    @Override
    public void reconnect(ForgeDirection side) {

    }

    public static interface IConnectionsMapProvider {

        public Map<Integer, Boolean> getConnections();

    }

}
