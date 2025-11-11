package ruiseki.omoshiroikamo.plugin.waila;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import cpw.mods.fml.common.event.FMLInterModComms;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.IWailaRegistrar;

public class EntityProvider implements IWailaEntityProvider {

    private static final EntityProvider INSTANCE = new EntityProvider();

    public static void init() {
        String callback = EntityProvider.class.getCanonicalName() + ".load";
        FMLInterModComms.sendMessage("Waila", "register", callback);
        EntityAnimalProvider.init();
    }

    public static void load(IWailaRegistrar registrar) {
        registrar.registerBodyProvider(INSTANCE, EntityLiving.class);
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

        World world = accessor.getWorld();
        EntityPlayer player = accessor.getPlayer();

        if (entity instanceof IWailaEntityInfoProvider info) {
            info.getWailaInfo(currenttip, player, world, entity);
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
        if (ent instanceof IWailaNBTProvider) {
            ((IWailaNBTProvider) ent).getData(tag);
        }
        return tag;
    }
}
