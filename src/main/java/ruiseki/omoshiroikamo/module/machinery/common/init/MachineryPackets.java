package ruiseki.omoshiroikamo.module.machinery.common.init;

import cpw.mods.fml.relauncher.Side;
import ruiseki.omoshiroikamo.core.common.network.PacketHandler;
import ruiseki.omoshiroikamo.module.machinery.common.network.PacketToggleSide;

public class MachineryPackets {

    public static void init() {
        PacketHandler.INSTANCE
            .registerMessage(PacketToggleSide.class, PacketToggleSide.class, PacketHandler.nextID(), Side.SERVER);
    }
}
