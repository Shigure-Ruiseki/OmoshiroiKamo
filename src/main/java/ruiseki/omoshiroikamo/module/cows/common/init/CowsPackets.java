package ruiseki.omoshiroikamo.module.cows.common.init;

import static ruiseki.omoshiroikamo.CommonProxy.NETWORK;

import ruiseki.omoshiroikamo.module.cows.common.network.PacketStall;

public class CowsPackets {

    public static void init() {
        NETWORK.register(PacketStall.class);
    }
}
