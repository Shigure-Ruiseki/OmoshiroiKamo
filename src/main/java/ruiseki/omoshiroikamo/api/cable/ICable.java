package ruiseki.omoshiroikamo.api.cable;

import java.util.Collection;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

import ruiseki.omoshiroikamo.api.block.IOKTile;

public interface ICable extends IOKTile, IFluidHandler {

    // ===== Part management =====

    ICablePart getPart(ForgeDirection side);

    void setPart(ForgeDirection side, ICablePart part);

    void removePart(ForgeDirection side);

    boolean hasPart(ForgeDirection side);

    Collection<ICablePart> getParts();

    // ===== Connection management =====

    boolean canConnect(TileEntity other, ForgeDirection side);

    boolean isConnected(ForgeDirection side);

    void updateConnections();

    void disconnect(ForgeDirection side);

    void reconnect(ForgeDirection side);

    void destroy();
}
