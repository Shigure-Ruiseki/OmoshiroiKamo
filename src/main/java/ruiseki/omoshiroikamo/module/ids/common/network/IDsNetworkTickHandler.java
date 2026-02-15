package ruiseki.omoshiroikamo.module.ids.common.network;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import ruiseki.omoshiroikamo.module.ids.common.block.cable.CableUtils;

public class IDsNetworkTickHandler {

    public interface TickListener {

        void tickStart(TickEvent.ServerTickEvent evt);

        void tickEnd(TickEvent.ServerTickEvent evt);
    }

    private final List<TickListener> listeners = new ArrayList<>();
    private final IdentityHashMap<AbstractCableNetwork<?>, Boolean> networks = new IdentityHashMap<>();

    public void addListener(TickListener listener) {
        listeners.add(listener);
    }

    public void removeListener(TickListener listener) {
        listeners.remove(listener);
    }

    public void registerNetwork(AbstractCableNetwork<?> cn) {
        networks.put(cn, Boolean.TRUE);
    }

    public void unregisterNetwork(AbstractCableNetwork<?> cn) {
        networks.remove(cn);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == Phase.START) {
            tickStart(event);
        } else {
            tickEnd(event);
        }
    }

    public void tickStart(TickEvent.ServerTickEvent event) {
        for (TickListener h : listeners) {
            h.tickStart(event);
        }
    }

    public void tickEnd(TickEvent.ServerTickEvent event) {
        CableUtils.processRebuildQueue();

        for (TickListener h : listeners) {
            h.tickEnd(event);
        }
        listeners.clear();
        for (AbstractCableNetwork<?> cn : networks.keySet()) {
            cn.doNetworkTick();
        }
    }
}
