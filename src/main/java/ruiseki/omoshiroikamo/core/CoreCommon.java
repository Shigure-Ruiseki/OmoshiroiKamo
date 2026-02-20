package ruiseki.omoshiroikamo.core;

import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.network.PacketHandler;
import ruiseki.omoshiroikamo.core.network.packet.PacketClientFlight;
import ruiseki.omoshiroikamo.core.network.packet.PacketCraftingState;
import ruiseki.omoshiroikamo.core.network.packet.PacketEnergy;
import ruiseki.omoshiroikamo.core.network.packet.PacketFluidTanks;
import ruiseki.omoshiroikamo.core.network.packet.PacketProgress;
import ruiseki.omoshiroikamo.core.network.packet.PacketQuickDraw;
import ruiseki.omoshiroikamo.core.network.packet.PacketSound;
import ruiseki.omoshiroikamo.core.network.packet.PacketSyncCarriedItem;
import ruiseki.omoshiroikamo.core.proxy.CommonProxyComponent;

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

        packetHandler.register(PacketSound.class);
    }
}
