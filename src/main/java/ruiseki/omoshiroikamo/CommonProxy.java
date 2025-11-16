package ruiseki.omoshiroikamo;

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
import cpw.mods.fml.common.gameevent.TickEvent;
import makamys.mclib.core.MCLib;
import makamys.mclib.core.MCLibModules;
import ruiseki.omoshiroikamo.common.init.ModAchievements;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.init.ModCommands;
import ruiseki.omoshiroikamo.common.init.ModEntity;
import ruiseki.omoshiroikamo.common.init.ModFluids;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.init.ModRecipes;
import ruiseki.omoshiroikamo.common.init.OKWorldGenerator;
import ruiseki.omoshiroikamo.common.network.PacketHandler;
import ruiseki.omoshiroikamo.common.util.Logger;
import ruiseki.omoshiroikamo.common.util.OreDictUtils;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
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
        if (LibMisc.SNAPSHOT_BUILD && !LibMisc.DEV_ENVIRONMENT) {
            Logger.info(
                LibMisc.MOD_ID
                    + " is in snapshot mode. Disabling update checker... Other features may also be different.");
        }
        MCLib.init();
    }

    public void preInit(FMLPreInitializationEvent event) {

        ModFluids.preInit();
        ModBlocks.preInit();
        ModItems.preInit();
        ModEntity.preInit();
        OreDictUtils.preInit();
        ModAchievements.preInit();
        OKWorldGenerator.preInit();

        BaubleExpandedCompat.preInit();

        if (!LibMisc.SNAPSHOT_BUILD && !LibMisc.DEV_ENVIRONMENT) {
            MCLibModules.updateCheckAPI.submitModTask(LibMisc.MOD_ID, LibMisc.VERSION, LibMisc.VERSION_URL);
            Logger.info("Submitting update check for " + LibMisc.MOD_ID + " version " + LibMisc.VERSION);
        }
    }

    public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance()
            .bus()
            .register(tickTimer);

        PacketHandler.init();

        ModRecipes.init();
        ModEntity.init();
        WailaCompat.init();
        NEICompat.init();
        EtFuturumCompat.init();
    }

    public void postInit(FMLPostInitializationEvent event) {
        StructureCompat.postInit();
        BaubleExpandedCompat.postInit();
    }

    public void serverLoad(FMLServerStartingEvent event) {
        ModCommands.init(event);
    }

    public void serverStarted(FMLServerStartedEvent event) {}

    public World getEntityWorld() {
        return MinecraftServer.getServer()
            .getEntityWorld();
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
        public void onTick(TickEvent.ServerTickEvent evt) {
            if (evt.phase == TickEvent.Phase.END) {
                onServerTick();
            }
        }

        @SubscribeEvent
        public void onTick(TickEvent.ClientTickEvent evt) {
            if (evt.phase == TickEvent.Phase.END) {
                onClientTick();
            }
        }
    }
}
