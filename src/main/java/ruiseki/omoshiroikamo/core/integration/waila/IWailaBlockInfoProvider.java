package ruiseki.omoshiroikamo.core.integration.waila;

import java.util.List;

import net.minecraft.item.ItemStack;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public interface IWailaBlockInfoProvider {

    default ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    default void getWailaInfo(List<String> tooltip, ItemStack itemStack, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {}

    default void getWailaTail(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {}
}
