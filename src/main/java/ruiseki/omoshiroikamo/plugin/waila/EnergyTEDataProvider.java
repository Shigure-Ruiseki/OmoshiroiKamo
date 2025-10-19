package ruiseki.omoshiroikamo.plugin.waila;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cofh.api.energy.IEnergyHandler;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractTE;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;

public class EnergyTEDataProvider implements IWailaDataProvider {

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
        if (!config.getConfig(LibMisc.MOD_ID + ".energyTE")) {
            return currenttip;
        }

        TileEntity tileEntity = accessor.getTileEntity();
        if (tileEntity instanceof IEnergyHandler handler) {

            AbstractTE te = (AbstractTE) tileEntity;

            if (!config.getConfig("thermalexpansion.energyhandler")) {
                int stored = handler.getEnergyStored(accessor.getSide());
                int maxStored = handler.getMaxEnergyStored(accessor.getSide());

                if (maxStored > 0) {
                    int percent = (int) ((stored * 100.0) / maxStored);
                    currenttip
                        .add(String.format("§7Energy: §a%,d§7 / §a%,d§7 RF §8(§e%d%%§8)", stored, maxStored, percent));

                }
            }

            if (accessor.getPlayer()
                .isSneaking() && te.getMaterial() != null) {
                String voltageTier = te.getMaterial()
                    .getVoltageTier().displayName;
                double voltage = te.getMaterial()
                    .getMaxVoltage();
                int maxTransfer = te.getMaterial()
                    .getMaxPowerTransfer();
                int maxUsage = te.getMaterial()
                    .getMaxPowerUsage();
                currenttip.add(String.format("§7Voltage Tier: §f%s", voltageTier));
                currenttip.add(String.format("§7Voltage: §b%.0f§7 V", voltage));
                currenttip.add(String.format("§7Max Transfer: §b%,d§7 RF/t", maxTransfer));
                currenttip.add(String.format("§7Max Usage: §b%,d§7 RF/t", maxUsage));

            } else if (te.getMaterial() != null) {
                currenttip.add("§7(Hold §eShift§7 for details)");
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
        return tag;
    }
}
