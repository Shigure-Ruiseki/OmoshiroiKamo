package ruiseki.omoshiroikamo.plugin.waila;

import static ruiseki.omoshiroikamo.plugin.waila.IWailaInfoProvider.BIT_BASIC;
import static ruiseki.omoshiroikamo.plugin.waila.IWailaInfoProvider.BIT_COMMON;
import static ruiseki.omoshiroikamo.plugin.waila.IWailaInfoProvider.BIT_DETAILED;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;

import cpw.mods.fml.common.event.FMLInterModComms;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import ruiseki.omoshiroikamo.common.block.BlockOK;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractTE;
import ruiseki.omoshiroikamo.common.util.Logger;
import ruiseki.omoshiroikamo.common.util.lib.LibMods;

public class WailaCompat implements IWailaDataProvider {

    public static void init() {
        if (!LibMods.Waila.isLoaded()) {
            return;
        }
        ChickensEntityProvider.init();
        CowsEntityProvider.init();
        EntityAnimalProvider.init();

        String callback = WailaCompat.class.getCanonicalName() + ".load";
        FMLInterModComms.sendMessage("Waila", "register", callback);
        Logger.info("Loaded WailaCompat");
    }

    public static final WailaCompat INSTANCE = new WailaCompat();

    public static void load(IWailaRegistrar registrar) {
        registrar.registerBodyProvider(INSTANCE, BlockOK.class);
        registrar.registerNBTProvider(INSTANCE, AbstractTE.class);
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
        TileEntity te = world.getTileEntity(x, y, z);
        Item item = Item.getItemFromBlock(block);

        if (block instanceof IWailaInfoProvider) {
            IWailaInfoProvider info = (IWailaInfoProvider) block;

            if (block instanceof IAdvancedTooltipProvider) {
                int mask = info.getDefaultDisplayMask(world, pos.blockX, pos.blockY, pos.blockZ);
                boolean basic = (mask & BIT_BASIC) == BIT_BASIC;
                boolean common = (mask & BIT_COMMON) == BIT_COMMON;
                boolean detailed = (mask & BIT_DETAILED) == BIT_DETAILED;

                IAdvancedTooltipProvider adv = (IAdvancedTooltipProvider) block;

                if (common) {
                    adv.addCommonEntries(itemStack, player, currenttip, false);
                }

                if (SpecialTooltipHandler.showAdvancedTooltips() && detailed) {
                    adv.addDetailedEntries(itemStack, player, currenttip, false);
                } else if (detailed) {
                    SpecialTooltipHandler.addShowDetailsTooltip(currenttip);
                }

                if (!SpecialTooltipHandler.showAdvancedTooltips() && basic) {
                    adv.addBasicEntries(itemStack, player, currenttip, false);
                }
            } else if (block instanceof IResourceTooltipProvider) {
                SpecialTooltipHandler.INSTANCE
                    .addInformation((IResourceTooltipProvider) block, itemStack, player, currenttip);
            }

            if (currenttip.size() > 0) {
                currenttip.add("");
            }

            info.getWailaInfo(currenttip, player, world, pos.blockX, pos.blockY, pos.blockZ);
        } else {
            if (block instanceof IAdvancedTooltipProvider) {
                SpecialTooltipHandler.INSTANCE
                    .addInformation((IAdvancedTooltipProvider) block, itemStack, player, currenttip, false);
            } else if (item instanceof IAdvancedTooltipProvider) {
                SpecialTooltipHandler.INSTANCE
                    .addInformation((IAdvancedTooltipProvider) item, itemStack, player, currenttip, false);
            } else if (block instanceof IResourceTooltipProvider) {
                SpecialTooltipHandler.INSTANCE
                    .addInformation((IResourceTooltipProvider) block, itemStack, player, currenttip);
            }
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x,
        int y, int z) {
        if (te instanceof IWailaNBTProvider) {
            ((IWailaNBTProvider) te).getData(tag);
        }
        tag.setInteger("x", x);
        tag.setInteger("y", y);
        tag.setInteger("z", z);
        return tag;
    }
}
