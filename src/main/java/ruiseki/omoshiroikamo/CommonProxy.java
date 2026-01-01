package ruiseki.omoshiroikamo;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import ruiseki.omoshiroikamo.core.CoreCommon;
import ruiseki.omoshiroikamo.core.ModuleManager;
import ruiseki.omoshiroikamo.core.common.command.CommandOK;
import ruiseki.omoshiroikamo.core.common.structure.StructureManager;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.integration.nei.NEICompat;
import ruiseki.omoshiroikamo.core.integration.structureLib.StructureCompat;
import ruiseki.omoshiroikamo.core.integration.waila.WailaCompat;
import ruiseki.omoshiroikamo.module.backpack.BackpackCommon;
import ruiseki.omoshiroikamo.module.cable.CableCommon;
import ruiseki.omoshiroikamo.module.chickens.ChickensCommon;
import ruiseki.omoshiroikamo.module.cows.CowsCommon;
import ruiseki.omoshiroikamo.module.dml.DMLCommon;
import ruiseki.omoshiroikamo.module.machinery.MachineryCommon;
import ruiseki.omoshiroikamo.module.multiblock.MultiBlockCommon;

public class CommonProxy {

    protected long serverTickCount = 0;
    protected long clientTickCount = 0;
    protected final TickTimer tickTimer = new TickTimer();

    public CommonProxy() {}

    public void onConstruction(FMLConstructionEvent event) {
        ModuleManager.onConstruction(event);
    }

    public void preInit(FMLPreInitializationEvent event) {
        Logger.setPhase("PREINIT");

        ModuleManager.register(new CoreCommon());
        ModuleManager.register(new ChickensCommon());
        ModuleManager.register(new CowsCommon());
        ModuleManager.register(new DMLCommon());
        ModuleManager.register(new BackpackCommon());
        ModuleManager.register(new MultiBlockCommon());
        ModuleManager.register(new CableCommon());
        ModuleManager.register(new MachineryCommon());

        ModuleManager.preInitCommon(event);
    }

    public void init(FMLInitializationEvent event) {
        Logger.setPhase("INIT");
        FMLCommonHandler.instance()
            .bus()
            .register(tickTimer);

        ModuleManager.initCommon(event);

        WailaCompat.init();
        NEICompat.init();
    }

    public void postInit(FMLPostInitializationEvent event) {
        Logger.setPhase("POSTINIT");

        ModuleManager.postInitCommon(event);

        StructureCompat.postInit();
    }

    public void serverLoad(FMLServerStartingEvent event) {
        ModuleManager.serverLoad(event);
        event.registerServerCommand(new CommandOK());
    }

    public void serverStarted(FMLServerStartedEvent event) {
        ModuleManager.serverStarted(event);
    }

    public World getEntityWorld() {
        return MinecraftServer.getServer()
            .getEntityWorld();
    }

    public EntityPlayer getClientPlayer() {
        return null;
    }

    public World getClientWorld() {
        return null;
    }

    protected void onServerTick() {
        ++serverTickCount;
    }

    protected void onClientTick() {}

    public long getTickCount() {
        return serverTickCount;
    }

    public double getReachDistanceForPlayer(EntityPlayer entityPlayer) {
        return 5;
    }

    public final class TickTimer {

        @SubscribeEvent
        public void onTick(ServerTickEvent evt) {
            if (evt.phase == Phase.END) {
                onServerTick();
            }
        }

        @SubscribeEvent
        public void onTick(ClientTickEvent evt) {
            if (evt.phase == Phase.END) {
                onClientTick();
            }
        }

        @SubscribeEvent
        public void onPlayerLogin(PlayerLoggedInEvent event) {
            StructureManager.getInstance()
                .notifyPlayerIfNeeded(event.player);
        }
    }
}
