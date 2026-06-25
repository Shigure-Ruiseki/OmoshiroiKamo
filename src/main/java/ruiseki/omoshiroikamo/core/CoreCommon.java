package ruiseki.omoshiroikamo.core;

import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.network.PacketHandler;
import ruiseki.okcore.proxy.CommonProxyComponent;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.core.network.PacketClientFlight;
import ruiseki.omoshiroikamo.core.network.PacketCraftingState;
import ruiseki.omoshiroikamo.core.network.PacketEnergy;
import ruiseki.omoshiroikamo.core.network.PacketFluidTanks;
import ruiseki.omoshiroikamo.core.network.PacketProgress;
import ruiseki.omoshiroikamo.core.network.PacketQuickDraw;
import ruiseki.omoshiroikamo.core.network.PacketReloadNEI;
import ruiseki.omoshiroikamo.core.network.PacketSyncCarriedItem;

public class CoreCommon extends CommonProxyComponent {

    @Override
    public ModBase getMod() {
        return OmoshiroiKamo.instance;
    }

    @Override
    public void registerPacketHandlers(PacketHandler packetHandler) {
        super.registerPacketHandlers(packetHandler);

        // Client
        packetHandler.register(PacketEnergy.class);
        packetHandler.register(PacketProgress.class);
        packetHandler.register(PacketFluidTanks.class);
        packetHandler.register(PacketClientFlight.class);
        packetHandler.register(PacketCraftingState.class);

        // Server
        packetHandler.register(PacketQuickDraw.class);
        packetHandler.register(PacketSyncCarriedItem.class);

        packetHandler.register(PacketReloadNEI.class);
    }

    @Override
    public void registerTickHandlers() {
        super.registerTickHandlers();
    }
}
