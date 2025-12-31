package ruiseki.omoshiroikamo.core.integration.waila;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import cpw.mods.fml.common.event.FMLInterModComms;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import ruiseki.omoshiroikamo.core.common.block.BlockOK;
import ruiseki.omoshiroikamo.core.common.block.TileEntityOK;

public class BlockProvider implements IWailaDataProvider {

    public static void init() {
        String callback = BlockProvider.class.getCanonicalName() + ".load";
        FMLInterModComms.sendMessage("Waila", "register", callback);
    }

    public static final BlockProvider INSTANCE = new BlockProvider();

    public static void load(IWailaRegistrar registrar) {
        registrar.registerBodyProvider(INSTANCE, BlockOK.class);
        registrar.registerNBTProvider(INSTANCE, TileEntityOK.class);
    }

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {

        EntityPlayer player = accessor.getPlayer();
        MovingObjectPosition pos = accessor.getPosition();
        int x = pos.blockX, y = pos.blockY, z = pos.blockZ;
        World world = accessor.getWorld();
        Block block = world.getBlock(x, y, z);
        TileEntity tile = accessor.getTileEntity();

        if (block instanceof IWailaBlockInfoProvider info) {
            info.getWailaInfo(currenttip, itemStack, accessor, config);
        }

        if (tile instanceof IWailaTileInfoProvider info) {
            info.getWailaInfo(currenttip, itemStack, accessor, config);
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x,
        int y, int z) {
        if (tile instanceof IWailaNBTProvider te) {
            te.getData(tag);
        }

        if (tile instanceof IWailaTileInfoProvider te) {
            te.getWailaNBTData(player, tile, tag, world, x, y, z);
        }
        return tag;
    }
}
