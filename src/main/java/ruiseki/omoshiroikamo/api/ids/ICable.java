package ruiseki.omoshiroikamo.api.ids;

import java.util.Collection;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.block.IOKTile;
import ruiseki.omoshiroikamo.api.energy.IOKEnergyIO;
import ruiseki.omoshiroikamo.module.ids.common.network.AbstractCableNetwork;

public interface ICable extends IOKTile, IOKEnergyIO {

    // Part

    ICablePart getPart(ForgeDirection side);

    void setPart(ForgeDirection side, ICablePart part);

    void removePart(ForgeDirection side);

    boolean hasPart(ForgeDirection side);

    Collection<ICablePart> getParts();

    // Endpoint

    ICableEndpoint getEndpoint(ForgeDirection side);

    void setEndpoint(ICableEndpoint endpoint);

    void removeEndpoint(ICableEndpoint endpoint);

    Collection<ICableEndpoint> getEndpoints();

    // Connection

    boolean canConnect(TileEntity other, ForgeDirection side);

    boolean isConnected(ForgeDirection side);

    void updateConnections();

    void disconnect(ForgeDirection side);

    void connect(ForgeDirection side);

    void destroy();

    boolean hasVisualConnection(ForgeDirection side);

    boolean hasCore();

    void setHasCore(boolean hasCore);

    // Blocked Side

    boolean isSideBlocked(ForgeDirection side);

    void blockSide(ForgeDirection side);

    void unblockSide(ForgeDirection side);

    // Events

    boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, ForgeDirection side, float hitX,
        float hitY, float hitZ);

    void onNeighborBlockChange(Block blockId);

    void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ);

    void onBlockRemoved();

    void dirty();

    World getWorld();

    void notifyNeighbors();

    // Network
    Map<Class<? extends ICableNode>, AbstractCableNetwork<?>> getNetworks();

    <T extends ICableNode> AbstractCableNetwork<T> getNetwork(Class<T> partType);

    <T extends ICableNode> void setNetworks(Map<Class<? extends ICableNode>, AbstractCableNetwork<?>> networks);
}
