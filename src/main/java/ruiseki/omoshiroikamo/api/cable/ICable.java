package ruiseki.omoshiroikamo.api.cable;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.block.IOKTile;

public interface ICable extends IOKTile {

    boolean canConnect(ICable connector, ForgeDirection side);

    void updateConnections();

    boolean isConnected(ForgeDirection side);

    void disconnect(ForgeDirection side);

    void reconnect(ForgeDirection side);

    ItemStack getItemStack();

    void destroy();
}
