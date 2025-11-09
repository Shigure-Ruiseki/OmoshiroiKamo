package ruiseki.omoshiroikamo.plugin.waila;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import cpw.mods.fml.common.event.FMLInterModComms;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import ruiseki.omoshiroikamo.common.entity.chicken.EntityChickensChicken;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.config.general.ChickenConfigs;

public class ChickensEntityProvider implements IWailaEntityProvider {

    private static final ChickensEntityProvider INSTANCE = new ChickensEntityProvider();

    public static void init() {
        String callback = ChickensEntityProvider.class.getCanonicalName() + ".load";
        FMLInterModComms.sendMessage("Waila", "register", callback);
    }

    public static void load(IWailaRegistrar registrar) {
        registrar.registerBodyProvider(INSTANCE, EntityChickensChicken.class);
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
        EntityChickensChicken chicken = (EntityChickensChicken) entity;
        currenttip.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "entity.tier", chicken.getTier()));

        if (chicken.getStatsAnalyzed() || ChickenConfigs.alwaysShowStats) {
            currenttip.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "entity.growth", chicken.getGrowth()));
            currenttip.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "entity.gain", chicken.getGain()));
            currenttip.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "entity.strength", chicken.getStrength()));
        }

        if (!chicken.isChild()) {
            int layProgress = chicken.getLayProgress();
            if (layProgress > 0) {
                int totalSeconds = layProgress / 20;
                int minutes = totalSeconds / 60;
                int seconds = totalSeconds % 60;

                String timeFormatted = String.format("%d:%02d", minutes, seconds);

                currenttip.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "entity.layProgress", timeFormatted));
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
