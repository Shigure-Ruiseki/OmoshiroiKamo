package ruiseki.omoshiroikamo.module.cows.common.init;

import cpw.mods.fml.relauncher.Side;
import ruiseki.omoshiroikamo.core.common.network.PacketHandler;
import ruiseki.omoshiroikamo.module.cows.common.network.PacketStall;

public class CowsPackets {

    public static void init() {
        PacketHandler.INSTANCE
            .registerMessage(PacketStall.class, PacketStall.class, PacketHandler.nextID(), Side.CLIENT);
    }
}
