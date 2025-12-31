package ruiseki.omoshiroikamo.module.backpack.common.init;

import cpw.mods.fml.relauncher.Side;
import ruiseki.omoshiroikamo.core.common.network.PacketHandler;
import ruiseki.omoshiroikamo.module.backpack.common.network.PacketBackpackNBT;

public class BackpackPackets {

    public static void init() {
        PacketHandler.INSTANCE
            .registerMessage(PacketBackpackNBT.class, PacketBackpackNBT.class, PacketHandler.nextID(), Side.SERVER);
    }
}
