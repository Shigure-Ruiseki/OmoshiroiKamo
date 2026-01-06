package ruiseki.omoshiroikamo.module.cable.common.conduit;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.block.BlockPos;
import ruiseki.omoshiroikamo.module.cable.common.conduit.geom.CollidableComponent;
import ruiseki.omoshiroikamo.module.cable.common.conduit.geom.Offset;

public interface IConduitBundle {

    TileEntity getEntity();

    BlockPos getPos();

    // conduits

    boolean hasType(Class<? extends IConduit> type);

    <T extends IConduit> T getConduit(Class<T> type);

    void addConduit(IConduit conduit);

    void removeConduit(IConduit conduit);

    Collection<IConduit> getConduits();

    Offset getOffset(Class<? extends IConduit> type, ForgeDirection dir);

    // connections

    Set<ForgeDirection> getConnections(Class<? extends IConduit> type);

    boolean containsConnection(Class<? extends IConduit> type, ForgeDirection west);

    Set<ForgeDirection> getAllConnections();

    boolean containsConnection(ForgeDirection dir);

    // geometry

    List<CollidableComponent> getCollidableComponents();

    List<CollidableComponent> getConnectors();

    // events

    void onNeighborBlockChange(Block blockId);

    void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ);

    void onBlockRemoved();

    void dirty();

    World getWorld();
}
