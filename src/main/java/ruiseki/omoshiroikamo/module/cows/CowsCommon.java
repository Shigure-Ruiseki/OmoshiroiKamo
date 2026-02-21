package ruiseki.omoshiroikamo.module.cows;

import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.network.PacketHandler;
import ruiseki.omoshiroikamo.core.proxy.CommonProxyComponent;
import ruiseki.omoshiroikamo.module.cows.common.network.PacketStall;

public class CowsCommon extends CommonProxyComponent {

    @Override
    public ModBase getMod() {
        return OmoshiroiKamo.instance;
    }

    @Override
    public void registerPacketHandlers(PacketHandler packetHandler) {
        super.registerPacketHandlers(packetHandler);
        packetHandler.register(PacketStall.class);
    }
}
