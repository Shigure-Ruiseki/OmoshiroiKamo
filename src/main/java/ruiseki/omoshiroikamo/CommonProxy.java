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
import makamys.mclib.core.MCLib;
import makamys.mclib.core.MCLibModules;
import ruiseki.omoshiroikamo.common.command.CommandOK;
import ruiseki.omoshiroikamo.common.init.MobOreDicts;
import ruiseki.omoshiroikamo.common.init.ModAchievements;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.init.ModRecipes;
import ruiseki.omoshiroikamo.common.init.OKWorldGenerator;
import ruiseki.omoshiroikamo.common.network.PacketHandler;
import ruiseki.omoshiroikamo.common.structure.StructureManager;
import ruiseki.omoshiroikamo.common.util.Logger;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.core.ModuleManager;
import ruiseki.omoshiroikamo.module.backpack.BackpackCommon;
import ruiseki.omoshiroikamo.module.chickens.ChickensCommon;
import ruiseki.omoshiroikamo.module.cows.CowsCommon;
import ruiseki.omoshiroikamo.module.dml.DMLCommon;
import ruiseki.omoshiroikamo.plugin.compat.BaubleExpandedCompat;
import ruiseki.omoshiroikamo.plugin.compat.EtFuturumCompat;
import ruiseki.omoshiroikamo.plugin.nei.NEICompat;
import ruiseki.omoshiroikamo.plugin.structureLib.StructureCompat;
import ruiseki.omoshiroikamo.plugin.waila.WailaCompat;

public class CommonProxy {

    protected long serverTickCount = 0;
    protected long clientTickCount = 0;
    protected final TickTimer tickTimer = new TickTimer();

    public CommonProxy() {}

    public void onConstruction(FMLConstructionEvent event) {
        ModuleManager.onConstruction(event);
        if (LibMisc.SNAPSHOT_BUILD && !LibMisc.DEV_ENVIRONMENT) {
            Logger.info(
                "{} is in snapshot mode. Disabling update checker... Other features may also be different.",
                LibMisc.MOD_ID);
        }
        MCLib.init();
    }

    public void preInit(FMLPreInitializationEvent event) {
        Logger.setPhase("PREINIT");

        ModuleManager.register(new ChickensCommon());
        ModuleManager.register(new CowsCommon());
        ModuleManager.register(new DMLCommon());
        ModuleManager.register(new BackpackCommon());

        // Initialize the custom structure system
        StructureManager.getInstance()
            .initialize(
                event.getModConfigurationDirectory()
                    .getParentFile());

        ModBlocks.preInit();
        ModItems.preInit();
        MobOreDicts.preInit();
        ModAchievements.preInit();
        OKWorldGenerator.preInit();

        ModuleManager.preInitCommon(event);

        BaubleExpandedCompat.preInit();

        if (!LibMisc.SNAPSHOT_BUILD && !LibMisc.DEV_ENVIRONMENT) {
            MCLibModules.updateCheckAPI.submitModTask(LibMisc.MOD_ID, LibMisc.VERSION, LibMisc.VERSION_URL);
            Logger.info("Submitting update check for {} version {}", LibMisc.MOD_ID, LibMisc.VERSION);
        }
    }

    public void init(FMLInitializationEvent event) {
        Logger.setPhase("INIT");
        FMLCommonHandler.instance()
            .bus()
            .register(tickTimer);

        PacketHandler.init();

        ModuleManager.initCommon(event);

        ModRecipes.init();
        WailaCompat.init();
        NEICompat.init();
        EtFuturumCompat.init();
    }

    public void postInit(FMLPostInitializationEvent event) {
        Logger.setPhase("POSTINIT");

        ModuleManager.postInitCommon(event);

        StructureCompat.postInit();
        BaubleExpandedCompat.postInit();
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
