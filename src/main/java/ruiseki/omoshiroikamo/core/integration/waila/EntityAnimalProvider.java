package ruiseki.omoshiroikamo.core.integration.waila;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import cpw.mods.fml.common.event.FMLInterModComms;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;

public class EntityAnimalProvider implements IWailaEntityProvider {

    private static final EntityAnimalProvider INSTANCE = new EntityAnimalProvider();

    public static void init() {
        String callback = EntityAnimalProvider.class.getCanonicalName() + ".load";
        FMLInterModComms.sendMessage("Waila", "register", callback);
    }

    public static void load(IWailaRegistrar registrar) {
        registrar.addConfigRemote(LibMisc.MOD_NAME, LibMisc.MOD_ID + ".breedingTime");
        registrar.addConfigRemote(LibMisc.MOD_NAME, LibMisc.MOD_ID + ".grownTime");
        registrar.registerBodyProvider(INSTANCE, EntityAnimal.class);
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
        EntityAnimal animal = (EntityAnimal) entity;

        if (!animal.isChild() && config.getConfig(LibMisc.MOD_ID + ".breedingTime")) {
            int countDown = animal.getGrowingAge();
            if (countDown > 0) {
                int totalSeconds = countDown / 20;
                int minutes = totalSeconds / 60;
                int seconds = totalSeconds % 60;

                String timeFormatted = String.format("%d:%02d", minutes, seconds);

                currenttip.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "mob.breedCountdown", timeFormatted));
            }
        } else if (config.getConfig(LibMisc.MOD_ID + ".grownTime")) {
            int growUp = -animal.getGrowingAge();
            int totalSeconds = growUp / 20;
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;

            String timeFormatted = String.format("%d:%02d", minutes, seconds);

            currenttip.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "mob.childGrowUp", timeFormatted));
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
