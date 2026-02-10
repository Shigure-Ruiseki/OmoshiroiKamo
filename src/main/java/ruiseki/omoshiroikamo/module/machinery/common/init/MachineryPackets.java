package ruiseki.omoshiroikamo.module.machinery.common.init;

import cpw.mods.fml.relauncher.Side;
import ruiseki.omoshiroikamo.core.common.network.PacketHandler;
import ruiseki.omoshiroikamo.module.machinery.common.network.PacketStructureTint;
import ruiseki.omoshiroikamo.module.machinery.common.network.PacketToggleSide;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;

public class MachineryPackets {

    public static void init() {
        PacketHandler.INSTANCE
            .registerMessage(PacketToggleSide.class, PacketToggleSide.class, PacketHandler.nextID(), Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(
            PacketStructureTint.Handler.class,
            PacketStructureTint.class,
            PacketHandler.nextID(),
            PacketStructureTint.class,
            PacketHandler.nextID(),
            Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(
            TEMachineController.PacketToggleRedstone.class,
            TEMachineController.PacketToggleRedstone.class,
            PacketHandler.nextID(),
            Side.SERVER);
    }
}
