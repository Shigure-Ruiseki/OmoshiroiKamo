package ruiseki.omoshiroikamo.module.machinery.common.init;

import static ruiseki.omoshiroikamo.CommonProxy.NETWORK;

import ruiseki.omoshiroikamo.module.machinery.common.network.PacketStructureTint;
import ruiseki.omoshiroikamo.module.machinery.common.network.PacketToggleSide;

public class MachineryPackets {

    public static void init() {
        NETWORK.register(PacketToggleSide.class);

        NETWORK.register(PacketStructureTint.class);

    }
}
