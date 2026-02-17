package ruiseki.omoshiroikamo.module.backpack.common.init;

import static ruiseki.omoshiroikamo.CommonProxy.NETWORK;

import ruiseki.omoshiroikamo.module.backpack.common.network.PacketBackpackNBT;

public class BackpackPackets {

    public static void init() {
        NETWORK.register(PacketBackpackNBT.class);
    }
}
