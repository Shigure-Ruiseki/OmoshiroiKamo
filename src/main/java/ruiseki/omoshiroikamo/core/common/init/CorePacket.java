package ruiseki.omoshiroikamo.core.common.init;

import static ruiseki.omoshiroikamo.CommonProxy.NETWORK;

import ruiseki.omoshiroikamo.core.common.network.PacketClientFlight;
import ruiseki.omoshiroikamo.core.common.network.PacketCraftingState;
import ruiseki.omoshiroikamo.core.common.network.PacketEnergy;
import ruiseki.omoshiroikamo.core.common.network.PacketFluidTanks;
import ruiseki.omoshiroikamo.core.common.network.PacketProgress;
import ruiseki.omoshiroikamo.core.common.network.PacketQuickDraw;
import ruiseki.omoshiroikamo.core.common.network.PacketSyncCarriedItem;

public class CorePacket {

    private CorePacket() {}

    public static void init() {

        // Common

        // Client
        NETWORK.register(PacketEnergy.class);
        NETWORK.register(PacketProgress.class);
        NETWORK.register(PacketFluidTanks.class);
        NETWORK.register(PacketClientFlight.class);
        NETWORK.register(PacketCraftingState.class);

        // Server
        NETWORK.register(PacketQuickDraw.class);
        NETWORK.register(PacketSyncCarriedItem.class);

    }

}
