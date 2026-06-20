package ruiseki.omoshiroikamo.core.event;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.ids.IDsModule;

/**
 * Handles world-related and connection-related events to manage memory and
 * caches.
 * Ensures that static caches are cleared when worlds are unloaded or when the
 * client disconnects.
 */
public class MemoryEventHandler {

    public static final MemoryEventHandler INSTANCE = new MemoryEventHandler();

    private static final List<WorldUnloadCallback> worldUnloadCallbacks = new ArrayList<>();
    private static final List<Runnable> clientDisconnectCallbacks = new ArrayList<>();

    private MemoryEventHandler() {}

    public static void registerOnWorldUnload(WorldUnloadCallback callback) {
        worldUnloadCallbacks.add(callback);
    }

    public static void registerOnClientDisconnect(Runnable callback) {
        clientDisconnectCallbacks.add(callback);
    }

    public void register() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event.world != null) {
            int dimId = event.world.provider.dimensionId;
            Logger.info("World unload detected for dimension {}. Clearing caches.", dimId);
            for (WorldUnloadCallback cb : worldUnloadCallbacks) {
                cb.onWorldUnload(event.world);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        Logger.info("Client disconnected from server. Clearing all caches.");
        for (Runnable cb : clientDisconnectCallbacks) {
            cb.run();
        }
        if (IDsModule.IDsNetworkTickHandler != null) {
            IDsModule.IDsNetworkTickHandler.clear();
        }
    }
}
