package ruiseki.omoshiroikamo.module.machinery;

import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.network.PacketHandler;
import ruiseki.omoshiroikamo.core.proxy.CommonProxyComponent;
import ruiseki.omoshiroikamo.module.machinery.common.network.PacketStructureTint;
import ruiseki.omoshiroikamo.module.machinery.common.network.PacketToggleSide;

/**
 * Modular Machinery Backport module entry point.
 * Provides a flexible multiblock machine system with JSON-based structure
 * definitions.
 */
public class MachineryCommon extends CommonProxyComponent {

    @Override
    public ModBase getMod() {
        return OmoshiroiKamo.instance;
    }

    @Override
    public void registerPacketHandlers(PacketHandler packetHandler) {
        super.registerPacketHandlers(packetHandler);
        packetHandler.register(PacketToggleSide.class);
        packetHandler.register(PacketStructureTint.class);
    }
}
