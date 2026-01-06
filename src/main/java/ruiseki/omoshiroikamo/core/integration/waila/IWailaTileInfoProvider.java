package ruiseki.omoshiroikamo.core.integration.waila;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public interface IWailaTileInfoProvider {

    default ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    default void getWailaHead(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor,
                              IWailaConfigHandler config) {}

    void getWailaInfo(List<String> tooltip, ItemStack itemStack, IWailaDataAccessor accessor,
        IWailaConfigHandler config);


    default void getWailaTail(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor,
                                     IWailaConfigHandler config) {}

    void getWailaNBTData(final EntityPlayerMP player, final TileEntity tile, final NBTTagCompound tag,
        final World world, int x, int y, int z);

}
