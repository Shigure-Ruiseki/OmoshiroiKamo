package ruiseki.omoshiroikamo.core.integration.waila;

import java.util.List;

import net.minecraft.item.ItemStack;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public interface IWailaBlockInfoProvider {

    void getWailaInfo(List<String> tooltip, ItemStack itemStack, IWailaDataAccessor accessor,
        IWailaConfigHandler config);
}
