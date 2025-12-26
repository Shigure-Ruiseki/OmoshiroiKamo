package ruiseki.omoshiroikamo.module.backpack.common.init;

import cpw.mods.fml.relauncher.Side;
import ruiseki.omoshiroikamo.core.common.network.PacketHandler;
import ruiseki.omoshiroikamo.module.backpack.common.network.PacketBackpackNBT;
import ruiseki.omoshiroikamo.module.backpack.common.network.PacketQuickDraw;

public class BackpackPackets {

    public static void init() {
        PacketHandler.INSTANCE
            .registerMessage(PacketBackpackNBT.class, PacketBackpackNBT.class, PacketHandler.nextID(), Side.SERVER);
        PacketHandler.INSTANCE
            .registerMessage(PacketQuickDraw.class, PacketQuickDraw.class, PacketHandler.nextID(), Side.SERVER);
    }
}
