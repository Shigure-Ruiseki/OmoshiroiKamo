package ruiseki.omoshiroikamo.plugin.waila;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.common.event.FMLInterModComms;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import ruiseki.omoshiroikamo.common.entity.cow.EntityCowsCow;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.config.general.CowsConfigs;

public class CowsEntityProvider implements IWailaEntityProvider {

    private static final CowsEntityProvider INSTANCE = new CowsEntityProvider();

    public static void init() {
        String callback = CowsEntityProvider.class.getCanonicalName() + ".load";
        FMLInterModComms.sendMessage("Waila", "register", callback);
    }

    public static void load(IWailaRegistrar registrar) {
        registrar.registerBodyProvider(INSTANCE, EntityCowsCow.class);
    }

    @Override
    public Entity getWailaOverride(IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor,
        IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor,
        IWailaConfigHandler config) {
        EntityCowsCow cow = (EntityCowsCow) entity;
        currenttip.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "entity.tier", cow.getTier()));

        if (cow.getStatsAnalyzed() || CowsConfigs.alwaysShowStats) {
            currenttip.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "entity.growth", cow.getGrowth()));
            currenttip.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "entity.gain", cow.getGain()));
            currenttip.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "entity.strength", cow.getStrength()));
        }

        if (!cow.isChild()) {
            int milkProgress = cow.getMilkProgress();
            if (milkProgress > 0) {
                int totalSeconds = milkProgress / 20;
                int minutes = totalSeconds / 60;
                int seconds = totalSeconds % 60;
                String timeFormatted = String.format("%d:%02d", minutes, seconds);
                currenttip.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "entity.milkProgress", timeFormatted));
            }

            FluidStack stored = cow.getMilkFluid();
            if (!(stored == null || stored.getFluid() == null)) {
                String fluidName = stored.getFluid()
                    .getLocalizedName(stored);
                int amount = stored.amount;
                currenttip.add(
                    String.format(
                        "%s%s : %s (%d %s)",
                        EnumChatFormatting.GRAY,
                        LibMisc.LANG.localize(LibResources.TOOLTIP + "entity.fluid"),
                        fluidName,
                        amount,
                        LibMisc.LANG.localize("fluid.millibucket.abr")));
            }
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor,
        IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, Entity ent, NBTTagCompound tag, World world) {
        return tag;
    }
}
