package ruiseki.omoshiroikamo.api.block;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public interface ICable {

    boolean canConnect(ICable connector, ForgeDirection side);

    void updateConnections();

    boolean isConnected(ForgeDirection side);

    void disconnect(ForgeDirection side);

    void reconnect(ForgeDirection side);

    ItemStack getItemStack();

    void destroy();
}
